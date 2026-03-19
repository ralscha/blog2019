package ch.rasc.backup;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Main {
  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String USER_AGENT = "githubbackup";
  private static final String GITHUB_API_VERSION = "2022-11-28";
  private static final int PAGE_SIZE = 100;

  record GitHubRepository(String name, @JsonProperty("clone_url") String cloneUrl) {
  }

  public static void main(String[] args) throws IOException, IllegalStateException,
      InvalidRemoteException, TransportException, GitAPIException, InterruptedException {

    if (args.length == 3) {
      String username = args[0];
      String backupFolder = args[1];
      String archiveFile = args[2];

      Path backupDirectory = Paths.get(backupFolder);
      backup(username, backupDirectory);

      if (!archiveFile.endsWith(".zip")) {
        archiveFile += ".zip";
      }

      Path archive = Paths.get(archiveFile);
      compress(backupDirectory, archive);
    }
    else {
      System.out.println(
          "java -jar githubbackup.jar <git_username> <backup_folder> <archive_file>");
    }
  }

  private static void compress(Path backupDirectory, Path archive) throws IOException {
    try (OutputStream os = Files.newOutputStream(archive);
        ZipOutputStream zipOS = new ZipOutputStream(os);
        Stream<Path> paths = Files.walk(backupDirectory)) {
      zipOS.setLevel(Deflater.BEST_COMPRESSION);

      paths.filter(path -> !Files.isDirectory(path)).sorted().forEach(path -> {
            try {
              ZipEntry zipEntry = new ZipEntry(backupDirectory.relativize(path).toString());
              zipOS.putNextEntry(zipEntry);
              Files.copy(path, zipOS);
              zipOS.closeEntry();
            }
            catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          });
    }
    catch (UncheckedIOException e) {
      throw e.getCause();
    }
  }

  public static void backup(String user, Path backupDirectory) throws IOException,
      InvalidRemoteException, TransportException, IllegalStateException, GitAPIException,
      InterruptedException {

    Files.createDirectories(backupDirectory);

    for (GitHubRepository repo : fetchRepositories(user)) {

      Path repoDir = backupDirectory.resolve(repo.name());

      if (isValidLocalRepository(repoDir)) {
        System.out.println("fetching : " + repo.name());
        try (Git git = Git.open(repoDir.toFile())) {
          git.fetch().call();
        }
      }
      else {
        System.out.println("cloning : " + repo.name());
        try (Git git = Git.cloneRepository().setBare(true).setURI(repo.cloneUrl())
            .setDirectory(repoDir.toFile()).call()) {
        }
      }
    }
  }

  private static List<GitHubRepository> fetchRepositories(String user)
      throws IOException, InterruptedException {
    List<GitHubRepository> repositories = new ArrayList<>();

    for (int page = 1;; page++) {
      String encodedUser = URLEncoder.encode(user, StandardCharsets.UTF_8);
      URI uri = URI.create("https://api.github.com/users/" + encodedUser
          + "/repos?per_page=" + PAGE_SIZE + "&page=" + page);

      HttpRequest request = HttpRequest.newBuilder(uri)
          .header("Accept", "application/vnd.github+json")
          .header("X-GitHub-Api-Version", GITHUB_API_VERSION)
          .header("User-Agent", USER_AGENT).GET().build();

      HttpResponse<String> response = HTTP_CLIENT.send(request,
          HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 404) {
        throw new IOException("GitHub user not found: " + user);
      }
      if (response.statusCode() != 200) {
        throw new IOException(
            "GitHub API request failed with status " + response.statusCode());
      }

      List<GitHubRepository> pageRepositories = OBJECT_MAPPER.readValue(response.body(),
          new TypeReference<List<GitHubRepository>>() {
          });
      repositories.addAll(pageRepositories);

      if (pageRepositories.size() < PAGE_SIZE) {
        return repositories;
      }
    }
  }

  private static boolean isValidLocalRepository(Path repoDir) {
    try (FileRepository fileRepository = new FileRepository(repoDir.toFile())) {
      return fileRepository.getObjectDatabase().exists();
    }
    catch (IOException e) {
      return false;
    }
  }

}
