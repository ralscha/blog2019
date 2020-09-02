package ch.rasc.upload;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.tus.java.client.ProtocolException;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusExecutor;
import io.tus.java.client.TusURLMemoryStore;
import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;

public class Client {

  public static void main(String args[])
      throws IOException, ProtocolException, InterruptedException {
    var httpClient = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build();

    // Download test file
    Path testFile = Paths.get("test.jpg");
    if (!Files.exists(testFile)) {
      var request = HttpRequest.newBuilder()
          .uri(URI.create("https://picsum.photos/id/970/2000/2000.jpg")).build();
      httpClient.send(request, BodyHandlers.ofFile(testFile));
    }

    // Upload file to server
    var client = new TusClient();
    client.setUploadCreationURL(URI.create("http://localhost:8080/upload").toURL());
    client.enableResuming(new TusURLMemoryStore());

    TusUpload upload = new TusUpload(testFile.toFile());

    var executor = new TusExecutor() {

      @Override
      protected void makeAttempt() throws ProtocolException, IOException {
        TusUploader uploader = client.resumeOrCreateUpload(upload);
        uploader.setChunkSize(1024);

        do {
          long totalBytes = upload.getSize();
          long bytesUploaded = uploader.getOffset();
          double progress = (double) bytesUploaded / totalBytes * 100;

          System.out.printf("Upload at %6.2f %%.\n", progress);
        }
        while (uploader.uploadChunk() > -1);

        uploader.finish();
      }

    };

    boolean success = executor.makeAttempts();

    if (success) {
      System.out.println("Upload successful");
    }
    else {
      System.out.println("Upload interrupted");
    }
    
  }
}