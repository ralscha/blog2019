package ch.rasc.httpclient;

import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

public class Client {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		var client = HttpClient.newBuilder()
						.authenticator(Authenticator.getDefault())
						.connectTimeout(Duration.ofSeconds(30))
						.cookieHandler(CookieHandler.getDefault())
						.executor(Executors.newFixedThreadPool(2))
						.followRedirects(Redirect.NEVER)
						.priority(1) //HTTP/2 priority
						.proxy(ProxySelector.getDefault())
						.sslContext(SSLContext.getDefault())
						.version(Version.HTTP_2)
						.sslParameters(new SSLParameters())
						.build();

		client = HttpClient.newHttpClient();
		// equivalent
		client = HttpClient.newBuilder().build();
	}

}
