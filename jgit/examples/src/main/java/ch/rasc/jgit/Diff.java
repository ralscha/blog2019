package ch.rasc.jgit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class Diff {

  public static void main(String[] args) throws IOException, GitAPIException {
    Path repoPath = Paths.get("./my_project_tag");
    try (Git git = Git.open(repoPath.toFile());
        ObjectReader reader = git.getRepository().newObjectReader()) {

      ObjectId oldObject = git.getRepository().resolve("v1.0^{tree}");
      ObjectId newObject = git.getRepository().resolve("v1.1^{tree}");

      CanonicalTreeParser oldIter = new CanonicalTreeParser();
      oldIter.reset(reader, oldObject);
      CanonicalTreeParser newIter = new CanonicalTreeParser();
      newIter.reset(reader, newObject);

      List<DiffEntry> diffs = git.diff().setNewTree(newIter).setOldTree(oldIter).call();

      for (DiffEntry entry : diffs) {
        System.out.println("type: " + entry.getChangeType());
        System.out.println("old : " + entry.getOldPath());
        System.out.println("new : " + entry.getNewPath());
      }
    }

    repoPath = Paths.get("./my_project_diff");
    try (Git git = Git.init().setDirectory(repoPath.toFile()).call();
        ObjectReader reader = git.getRepository().newObjectReader()) {
      Files.writeString(repoPath.resolve("file1.md"), "Hello World 1");
      Files.writeString(repoPath.resolve("file2.md"), "Hello World 2");
      Files.writeString(repoPath.resolve("file3.md"), "Hello World 3");
      Files.writeString(repoPath.resolve("file4.md"), "Hello World 4");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("initial commit").setAuthor("author", "author@email.com")
          .call();

      Files.writeString(repoPath.resolve("file1.md"), "Hello Earth 1");
      Files.delete(repoPath.resolve("file4.md"));
      Files.move(repoPath.resolve("file2.md"), repoPath.resolve("file22.md"));
      git.add().addFilepattern(".").call();
      git.rm().addFilepattern("file2.md").call();
      git.rm().addFilepattern("file4.md").call();

      git.commit().setMessage("update").setAuthor("author", "author@email.com").call();

      ObjectId oldObject = git.getRepository().resolve("HEAD~^{tree}");
      ObjectId newObject = git.getRepository().resolve("HEAD^{tree}");

      CanonicalTreeParser oldIter = new CanonicalTreeParser();
      oldIter.reset(reader, oldObject);
      CanonicalTreeParser newIter = new CanonicalTreeParser();
      newIter.reset(reader, newObject);

      List<DiffEntry> diffs = git.diff().setNewTree(newIter).setOldTree(oldIter).call();

      for (DiffEntry entry : diffs) {
        System.out.println("type: " + entry.getChangeType());
        System.out.println("old : " + entry.getOldPath());
        System.out.println("new : " + entry.getNewPath());
      }

    }
  }

}
