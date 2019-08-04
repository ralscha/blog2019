package ch.rasc.jgit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;

public class Log {

  public static void main(String[] args)
      throws IOException, NoHeadException, GitAPIException {

    Path repoPath = Paths.get("./my_project");

    try (Git git = Git.open(repoPath.toFile())) {
      // all
      Iterable<RevCommit> logs = git.log().all().call();
      for (RevCommit rev : logs) {
        System.out.print(Instant.ofEpochSecond(rev.getCommitTime()));
        System.out.print(": ");
        System.out.print(rev.getFullMessage());
        System.out.println();
        System.out.println(rev.getId().getName());
        System.out.print(rev.getAuthorIdent().getName());
        System.out.println(rev.getAuthorIdent().getEmailAddress());
        System.out.println("-------------------------");
      }

      System.out.println("======================");

      // particular file
      logs = git.log().addPath("file1.md").call();
      for (RevCommit rev : logs) {
        System.out.print(Instant.ofEpochSecond(rev.getCommitTime()));
        System.out.print(": ");
        System.out.print(rev.getFullMessage());
        System.out.println();
        System.out.println(rev.getId().getName());
        System.out.print(rev.getAuthorIdent().getName());
        System.out.println(rev.getAuthorIdent().getEmailAddress());
        System.out.println("-------------------------");
      }

    }
  }

}
