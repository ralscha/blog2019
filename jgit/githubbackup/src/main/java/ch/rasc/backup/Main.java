package ch.rasc.backup;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;

public class Main {
  public static void main(String[] args) throws IOException, IllegalStateException,
      InvalidRemoteException, TransportException, GitAPIException {

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
        ZipOutputStream zipOS = new ZipOutputStream(os)) {
      zipOS.setLevel(Deflater.BEST_COMPRESSION);

      Files.walk(backupDirectory).filter(path -> !Files.isDirectory(path))
          .forEach(path -> {
            try {
              ZipEntry zipEntry = new ZipEntry(
                  backupDirectory.relativize(path).toString());
              zipOS.putNextEntry(zipEntry);
              Files.copy(path, zipOS);
              zipOS.closeEntry();
            }
            catch (IOException e) {
              e.printStackTrace();
            }
          });
    }
  }

  public static void backup(String user, Path backupDirectory) throws IOException,
      InvalidRemoteException, TransportException, IllegalStateException, GitAPIException {

    Files.createDirectories(backupDirectory);

    RepositoryService service = new RepositoryService();
    for (Repository repo : service.getRepositories(user)) {

      Path repoDir = backupDirectory.resolve(repo.getName());
      Files.createDirectories(repoDir);

      if (isValidLocalRepository(repoDir)) {
        System.out.println("fetching : " + repo.getName());
        Git.open(repoDir.toFile()).fetch().call();
      }
      else {
        System.out.println("cloning : " + repo.getName());
        Git.cloneRepository().setBare(true).setURI(repo.getCloneUrl())
            .setDirectory(repoDir.toFile()).call();
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
