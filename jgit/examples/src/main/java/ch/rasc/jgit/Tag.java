package ch.rasc.jgit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.Ref;

public class Tag {
  public static void main(String[] args)
      throws NoFilepatternException, GitAPIException, IOException {
    Path repoPath = Paths.get("./my_project_tag");

    try (Git git = Git.init().setDirectory(repoPath.toFile()).call()) {

      Files.writeString(repoPath.resolve("file1.md"), "Hello World 1");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("initial commit").setAuthor("author", "author@email.com")
          .call();

      git.tag().setName("v1.0").setMessage("version 1.0").call();

      Files.writeString(repoPath.resolve("file2.md"), "Hello World 2");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("new feature").setAuthor("author", "author@email.com")
          .call();

      git.tag().setName("v1.0.1").setMessage("version 1.0.1").call();
      git.tagDelete().setTags("v1.0.1").call();

      git.tag().setName("v1.1").setMessage("version 1.1").call();

      List<Ref> tags = git.tagList().call();
      for (Ref tag : tags) {
        System.out.println(tag.getName());
      }
    }

  }
}
