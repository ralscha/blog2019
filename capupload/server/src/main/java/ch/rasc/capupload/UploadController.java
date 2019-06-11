package ch.rasc.capupload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;

@Controller
public class UploadController {

	private final TusFileUploadService tusFileUploadService;

	private final Path uploadDirectory;

	private final Path tusUploadDirectory;

	public UploadController(TusFileUploadService tusFileUploadService,
			AppProperties appProperties) {
		this.tusFileUploadService = tusFileUploadService;

		this.uploadDirectory = Paths.get(appProperties.getAppUploadDirectory());
		try {
			Files.createDirectories(this.uploadDirectory);
		}
		catch (IOException e) {
			Application.logger.error("create upload directory", e);
		}

		this.tusUploadDirectory = Paths.get(appProperties.getTusUploadDirectory());
	}

	@CrossOrigin
	@PostMapping("/uploadAll")
	@ResponseBody
	public boolean uploadAll(@RequestParam("file") MultipartFile file) {

		try {
			Path downloadedFile = this.uploadDirectory.resolve(Paths.get(file.getOriginalFilename()));
			Files.deleteIfExists(downloadedFile);
			Files.copy(file.getInputStream(), downloadedFile);
			return true;
		}
		catch (IOException e) {
			LoggerFactory.getLogger(this.getClass()).error("uploadAll", e);
			return false;
		}

	}

	@CrossOrigin(exposedHeaders = { "Location", "Upload-Offset" })
	@RequestMapping(value = { "/upload", "/upload/**" },
			method = { RequestMethod.POST, RequestMethod.PATCH, RequestMethod.HEAD,
					RequestMethod.DELETE, RequestMethod.GET })
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
				Path output = this.uploadDirectory.resolve(uploadInfo.getFileName());
				Files.copy(is, output, StandardCopyOption.REPLACE_EXISTING);
			}
			catch (IOException | TusException e) {
				Application.logger.error("get uploaded bytes", e);
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
}
