package ch.rasc.capupload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;

@Controller
public class UploadController {

  private static final Logger logger = LoggerFactory
      .getLogger(UploadController.class);

  private final TusFileUploadService tusFileUploadService;

  private final Path uploadDirectory;

  private final Path tusUploadDirectory;

  public UploadController(TusFileUploadService tusFileUploadService,
      AppProperties appProperties) {
    this.tusFileUploadService = tusFileUploadService;

    this.uploadDirectory = Path.of(appProperties.getAppUploadDirectory());
    try {
      Files.createDirectories(this.uploadDirectory);
    }
    catch (IOException e) {
      Application.logger.error("create upload directory", e);
    }

    this.tusUploadDirectory = Path.of(appProperties.getTusUploadDirectory());
  }

  @CrossOrigin
  @PostMapping("/uploadAll")
  @ResponseBody
  public boolean uploadAll(@RequestParam("file") MultipartFile file) {

    try {
      try (InputStream inputStream = file.getInputStream()) {
        saveUpload(this.uploadDirectory, UUID.randomUUID().toString(), inputStream);
      }
      return true;
    }
    catch (IOException e) {
      logger.error("uploadAll", e);
      return false;
    }

  }

  @CrossOrigin(exposedHeaders = { "Location", "Upload-Offset" })
  @RequestMapping(value = { "/upload", "/upload/**" }, method = { RequestMethod.POST,
      RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.DELETE, RequestMethod.GET,
      RequestMethod.OPTIONS })
  public void upload(HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) throws IOException {
    this.tusFileUploadService.process(servletRequest, servletResponse);

    String uploadURI = servletRequest.getRequestURI();

    UploadInfo uploadInfo = null;
    try {
      uploadInfo = this.tusFileUploadService.getUploadInfo(uploadURI);
    }
    catch (IOException | TusException e) {
      Application.logger.error("get upload info", e);
    }

    if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
      try (InputStream is = this.tusFileUploadService.getUploadedBytes(uploadURI)) {
        saveUpload(this.uploadDirectory, uploadURI, is);
      }
      catch (IOException | TusException e) {
        Application.logger.error("get uploaded bytes", e);
        servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
      }

      try {
        this.tusFileUploadService.deleteUpload(uploadURI);
      }
      catch (IOException | TusException e) {
        Application.logger.error("delete upload", e);
      }
    }
  }

  @Scheduled(fixedDelayString = "PT24H")
  @SuppressWarnings("unused")
  private void cleanup() {
    Path locksDir = this.tusUploadDirectory.resolve("locks");
    if (Files.exists(locksDir)) {
      try {
        this.tusFileUploadService.cleanup();
      }
      catch (IOException e) {
        Application.logger.error("error during cleanup", e);
      }
    }
  }


  static void saveUpload(Path directory, String uploadId, InputStream input) throws IOException {
    Path destination = directory.resolve(UUID.nameUUIDFromBytes(
        uploadId.getBytes(StandardCharsets.UTF_8)).toString());
    if (Files.exists(destination)) {
      return;
    }
    Path temporary = Files.createTempFile(directory, ".upload-", ".tmp");
    try {
      Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING);
      try {
        Files.move(temporary, destination);
      }
      catch (FileAlreadyExistsException e) {
        // Another finalization request already saved this upload.
      }
    }
    finally {
      Files.deleteIfExists(temporary);
    }
  }

}
