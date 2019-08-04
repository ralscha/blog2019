package ch.rasc.jgit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Remote {

  public static void main(String[] args)
      throws IllegalStateException, GitAPIException, IOException, URISyntaxException {

    Path repoPath = Paths.get("./test_repo");
    try (Git git = Git.init().setDirectory(repoPath.toFile()).call()) {

      Files.writeString(repoPath.resolve("README.md"), "# test_repo");
      git.add().addFilepattern("README.md").call();
      git.commit().setMessage("first commit").setAuthor("author", "author@email.com")
          .call();

      // git remote add origin git@github.com:ralscha/test_repo.git
      RemoteAddCommand remoteAddCommand = git.remoteAdd();
      remoteAddCommand.setName("origin");
      remoteAddCommand.setUri(new URIish("git@github.com:ralscha/test_repo.git"));
      remoteAddCommand.call();

      // git push -u origin master
      PushCommand pushCommand = git.push();
      pushCommand.add("master");
      pushCommand.setRemote("origin");
      pushCommand.call();

      // pushCommand.setCredentialsProvider(new
      // UsernamePasswordCredentialsProvider("username", "password"));

      SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
        @Override
        protected void configure(Host host, Session session) {
          // do nothing
        }

        @Override
        protected JSch createDefaultJSch(FS fs) throws JSchException {
          JSch defaultJSch = super.createDefaultJSch(fs);
          defaultJSch.addIdentity("c:/path/to/my/private_key");

          // if key is protected with passphrase
          // defaultJSch.addIdentity("c:/path/to/my/private_key", "my_passphrase");

          return defaultJSch;
        }
      };

      pushCommand = git.push();
      pushCommand.setTransportConfigCallback(transport -> {
        SshTransport sshTransport = (SshTransport) transport;
        sshTransport.setSshSessionFactory(sshSessionFactory);
      });
      pushCommand.add("master");
      pushCommand.setRemote("origin");
      pushCommand.call();

    }

  }

}
