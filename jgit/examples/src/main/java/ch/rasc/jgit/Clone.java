package ch.rasc.jgit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class Clone {

  public static void main(String[] args)
      throws InvalidRemoteException, TransportException, GitAPIException, IOException {

    Path localPath = Paths.get("./my_other_test_repo");
    try (Git git = Git.cloneRepository().setURI("git@github.com:ralscha/test_repo.git")
        .setDirectory(localPath.toFile()).call()) {

      Files.writeString(localPath.resolve("SECOND.md"), "# another file");
      git.add().addFilepattern("SECOND.md").call();
      git.commit().setMessage("second commit").setAuthor("author", "author@email.com")
          .call();

      git.push().call();
    }

  }

}
