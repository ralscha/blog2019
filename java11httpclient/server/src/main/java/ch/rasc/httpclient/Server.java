package ch.rasc.httpclient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Server {

	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}

	@GetMapping("/helloworld")
	public String handle(HttpServletRequest request,
			@RequestParam(value = "query", required = false) String query) {
		System.out.println("/helloworld");
		printHeaders(request);
		return "Hello World" + (query != null ? ", " + query : "");
	}

	@GetMapping("/one")
	public String one() {
		return "ONE";
	}

	@GetMapping("/two")
	public String two() {
		return "TWO";
	}

	@GetMapping("/three")
	public String three() {
		return "THREE";
	}

	@GetMapping("/four")
	public String four() {
		return "FOUR";
	}

	@GetMapping("/secret")
	public ResponseEntity<String> secret(@RequestHeader(name = "Authorization",
			required = false) String authorization) {
		System.out.println(authorization);
		if (authorization != null && !authorization.isBlank()) {
			String decoded = new String(
					Base64.getDecoder().decode(authorization.substring(6)),
					StandardCharsets.UTF_8);
			if (decoded.equals("user:password")) {
				return new ResponseEntity<>("the secret message", HttpStatus.OK);
			}
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("WWW-Authenticate", "Basic realm=\"Access to the secret endpoint\"");
		return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
	}

	@GetMapping("/secret2")
	public String secret2(@RequestHeader(name = "Authorization",
			required = false) String authorization) {
		if (authorization != null && !authorization.isBlank()) {
			return "another secret message";
		}
		return "no authorization header";
	}

	@GetMapping("/setCookie")
	public String setCookie(HttpServletResponse response) {
		Cookie biscuitCookie = new Cookie("biscuits", "ChocolateChipCookies");
		biscuitCookie.setMaxAge(1000);
		response.addCookie(biscuitCookie);

		return "set cookie";
	}

	@GetMapping("/secondCookieRequest")
	public String secondCookieRequest(
			@CookieValue(name = "biscuits", required = false) String biscuits) {
		System.out.println(biscuits);
		return "read cookie: " + biscuits;
	}

	@GetMapping("/headers")
	public ResponseEntity<String> headers(HttpServletRequest request) {
		System.out.println("/headers");

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Custom-Header", "from");
		headers.add("X-Custom-Header", "hello world");
		headers.add("X-Time", String.valueOf(System.currentTimeMillis() / 1000));

		printHeaders(request);
		return new ResponseEntity<>("sent you some headers", headers, HttpStatus.OK);
	}

	@GetMapping("/user")
	public User user() {
		return new User(1, "username");
	}

	@PostMapping("/saveUser")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void saveUser(@RequestBody User user) {
		System.out.println("Save User");
		System.out.println(user);
	}

	@PostMapping("/uppercase")
	public String uppercase(@RequestBody String string) {
		return string.toUpperCase();
	}

	@PostMapping("/formdata")
	public String formdata(@RequestParam("id") int id, @RequestParam("name") String name,
			@RequestParam("ts") long ts) {
		return id + ":" + name + ":" + ts;
	}

	@GetMapping("/redirect")
	public ResponseEntity<Void> redirect(HttpServletRequest request) {
		System.out.println("/redirect");
		printHeaders(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", "/helloworld");
		return new ResponseEntity<>(headers, HttpStatus.PERMANENT_REDIRECT);
	}

	private static void printHeaders(HttpServletRequest request) {
		System.out.println("Headers:");
		Enumeration<String> en = request.getHeaderNames();
		while (en.hasMoreElements()) {
			String header = en.nextElement();
			System.out.printf("%s = %s\n", header, request.getHeader(header));
		}
	}
}
