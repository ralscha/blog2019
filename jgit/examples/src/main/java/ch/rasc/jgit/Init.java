package ch.rasc.jgit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

public class Init {

  public static void main(String[] args)
      throws IllegalStateException, GitAPIException, IOException {

    Path repoPath = Paths.get("./my_project");

    try (Git git = Git.init().setDirectory(repoPath.toFile()).call()) {

    }

    InitCommand init = Git.init();
    init.setDirectory(repoPath.toFile());
    try (Git git = init.call()) {

    }

    try (Git git = Git.open(repoPath.toFile())) {

    }

  }

}
