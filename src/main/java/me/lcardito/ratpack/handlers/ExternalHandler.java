package me.lcardito.ratpack.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;

import static java.lang.String.format;

public class ExternalHandler implements Handler {
	private static Logger log = LoggerFactory.getLogger(ExternalHandler.class);

	private final HttpClient httpClient;

	public ExternalHandler() throws Exception {
		httpClient = HttpClient.of(rs -> rs.readTimeout(Duration.ofMinutes(2)));
	}

	@Override
	public void handle(Context ctx) throws Exception {
		log.info("Calling a slow service...");
		Promise<String> tweets = httpClient.get(new URI("http://localhost:9081/slow")).map(res -> String.valueOf(res.getBody().getText()));

		tweets.then(string -> {
			log.info(format("Got my tweets %s!", string));
			ctx.render(format("Got my tweets %s!", string));
		});
	}

}
