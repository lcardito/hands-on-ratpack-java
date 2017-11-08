package me.lcardito.ratpack.handlers.external;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.client.HttpClient;
import ratpack.http.client.ReceivedResponse;

import java.net.URI;
import java.time.Duration;

import static java.lang.String.format;

public class ExtSlowHandler implements Handler {
	private static Logger log = LoggerFactory.getLogger(ExtSlowHandler.class);

	private final HttpClient httpClient;

	public ExtSlowHandler() throws Exception {
		httpClient = HttpClient.of(rs -> rs.readTimeout(Duration.ofMinutes(2)));
	}

	@Override
	public void handle(Context ctx) throws Exception {
		log.info("Calling a slow service...");
		Promise<ReceivedResponse> tweets = httpClient.get(new URI("http://localhost:9081/slow"))
			.mapIf(res -> res.getStatusCode() == 200, res -> res)
			.mapIf(res -> res.getStatusCode() == 404, res -> {
				log.warn("Response return not found");
				throw new NotFoundException("Resource not found");
			})
			.onError(NotFoundException.class, e -> ctx.notFound())
			.onError(e -> {
				log.error("Some error occurred");
				ctx.clientError(503);
			});

		tweets.then(response -> {
			log.info(format("Got my tweets %s!", String.valueOf(response.getBody().getText())));
			log.info(Thread.currentThread().getName());
			ctx.render(String.valueOf(System.currentTimeMillis()));
		});
	}

}
