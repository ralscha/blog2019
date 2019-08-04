package ch.rasc.jgit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

public class Branch {

  public static void main(String[] args)
      throws IllegalStateException, GitAPIException, IOException {
    Path repoPath = Paths.get("./my_project_branch");

    try (Git git = Git.init().setDirectory(repoPath.toFile()).call()) {

      Files.writeString(repoPath.resolve("file.md"), "Hello World");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("initial commit").setAuthor("author", "author@email.com")
          .call();

      // new branch
      git.branchCreate().setName("new_feature").call();
      git.checkout().setName("new_feature").call();

      // or
      git.checkout().setName("new_feature").setCreateBranch(true).call();

      System.out.println(git.getRepository().getFullBranch());

      Files.writeString(repoPath.resolve("file.md"), "Hello Earth");
      Files.writeString(repoPath.resolve("branch_file.md"), "New Feature");
      git.add().addFilepattern(".").call();
      git.commit().setMessage("implementing new feature")
          .setAuthor("author", "author@email.com").call();

      git.checkout().setName("master").call();

      ObjectId branchObjectId = git.getRepository().resolve("new_feature");
      MergeResult mergeResult = git.merge()
          // .setFastForward(MergeCommand.FastForwardMode.NO_FF)
          // .setCommit(false)
          // .setMessage("merge")
          .include(branchObjectId).call();

      System.out.println(mergeResult);
      if (mergeResult.getConflicts() != null) {
        for (Map.Entry<String, int[][]> entry : mergeResult.getConflicts().entrySet()) {
          System.out.println("Key: " + entry.getKey());
          for (int[] arr : entry.getValue()) {
            System.out.println("value: " + Arrays.toString(arr));
          }
        }
      }

      // list branches
      List<Ref> branches = git.branchList().setListMode(ListMode.ALL).call();
      for (Ref branch : branches) {
        System.out.println(branch.getName());
      }

      // rename branch
      git.branchRename().setOldName("new_feature").setNewName("amazing_feature").call();

      System.out.println("- after rename");
      branches = git.branchList().setListMode(ListMode.ALL).call();
      for (Ref branch : branches) {
        System.out.println(branch.getName());
      }

      // delete branch
      git.checkout().setName("master").call();
      git.branchDelete().setBranchNames("amazing_feature").setForce(true).call();

      System.out.println("- after delete");
      branches = git.branchList().setListMode(ListMode.ALL).call();
      for (Ref branch : branches) {
        System.out.println(branch.getName());
      }
    }
  }
}
