package ch.rasc.jgit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.TrackingRefUpdate;

public class Pull {

  public static void main(String[] args)
      throws IOException, InvalidRemoteException, TransportException, GitAPIException {
    Path repoPath = Paths.get("./test_repo");
    try (Git git = Git.open(repoPath.toFile())) {

      Files.writeString(repoPath.resolve("THIRD.md"), "# third file");
      git.add().addFilepattern("THIRD.md").call();
      git.commit().setMessage("third commit").setAuthor("author", "author@email.com")
          .call();

      Iterable<PushResult> pushResults = git.push().call();
      for (PushResult pushResult : pushResults) {
        for (RemoteRefUpdate update : pushResult.getRemoteUpdates()) {
          System.out.println(update.getStatus());
        }
      }

      PullResult result = git.pull().call();
      FetchResult fetchResult = result.getFetchResult();
      MergeResult mergeResult = result.getMergeResult();
      MergeStatus mergeStatus = mergeResult.getMergeStatus();

      for (TrackingRefUpdate update : fetchResult.getTrackingRefUpdates()) {
        System.out.println(update.getLocalName());
        System.out.println(update.getRemoteName());
        System.out.println(update.getResult());
      }

      System.out.println(mergeResult);
      System.out.println(mergeStatus);

      pushResults = git.push().call();
      for (PushResult pushResult : pushResults) {
        for (RemoteRefUpdate update : pushResult.getRemoteUpdates()) {
          System.out.println(update.getStatus());
        }
      }
    }
  }

}
