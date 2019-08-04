package ch.rasc.jgit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitStatus {
  public static void main(String[] args)
      throws IllegalStateException, GitAPIException, IOException {

    Path repoPath = Paths.get("./my_project_status");

    try (Git git = Git.init().setDirectory(repoPath.toFile()).call()) {

      Files.writeString(repoPath.resolve("ignored_file.md"), "Ignore me");
      Files.writeString(repoPath.resolve(".gitignore"), "ignored_file.md");

      Files.writeString(repoPath.resolve("file1.md"), "Hello World 1");
      Files.writeString(repoPath.resolve("file2.md"), "Hello World 2");
      Files.writeString(repoPath.resolve("file3.md"), "Hello World 3");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("create files").setAuthor("author", "author@email.com")
          .call();

      Files.writeString(repoPath.resolve("file4.md"), "Hello World 4");
      Files.writeString(repoPath.resolve("file5.md"), "Hello World 5");
      git.add().addFilepattern("file5.md").call();

      Files.writeString(repoPath.resolve("file2.md"), "Hello Earth 2");
      git.add().addFilepattern("file2.md").call();

      // Files.deleteIfExists(repoPath.resolve("file1.md"));
      git.rm().addFilepattern("file1.md").call();

      Files.deleteIfExists(repoPath.resolve("file3.md"));

      Files.createDirectory(repoPath.resolve("new_directory"));

      Status status = git.status().call();
      System.out.println("Added              : " + status.getAdded());
      System.out.println("Changed            : " + status.getChanged());
      System.out.println("Removed            : " + status.getRemoved());
      System.out.println("Uncommitted Changes: " + status.getUncommittedChanges());
      System.out.println("Untracked          : " + status.getUntracked());
      System.out.println("Untracked Folders  : " + status.getUntrackedFolders());
      System.out.println("Ignored Not Index  : " + status.getIgnoredNotInIndex());
      System.out.println("Conflicting        : " + status.getConflicting());
      System.out.println("Missing            : " + status.getMissing());
    }
  }
}
