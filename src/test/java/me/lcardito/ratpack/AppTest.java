package me.lcardito.ratpack;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.vavr.control.Try;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import ratpack.test.MainClassApplicationUnderTest;
import ratpack.test.ServerBackedApplicationUnderTest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnitParamsRunner.class)
public class AppTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(9081);

	private static ServerBackedApplicationUnderTest serverBackedApplicationUnderTest;

	private JerseyClient jerseyClient;

	@BeforeClass
	public static void startServer() {
		serverBackedApplicationUnderTest = new MainClassApplicationUnderTest(App.class);
	}

	@Before
	public void setUp() throws Exception {
		serverBackedApplicationUnderTest.getAddress();

		ClientConfig config = new ClientConfig();
		config.property(ClientProperties.READ_TIMEOUT, 20000);
		jerseyClient = new JerseyClientBuilder().withConfig(config).build();
	}

	@Test
	public void shouldRespondToRootPath() throws Exception {
		Response response = jerseyClient.target(new URI(serverBackedApplicationUnderTest.getAddress().toString())).request().get();

		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("Hello Devoxx!"));
	}

	@Test
	public void shouldResponseToGetUser() throws Exception {
		Response response = jerseyClient.target(new URI(serverBackedApplicationUnderTest.getAddress().toString()))
			.path("user")
			.request()
			.get();

		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("user"));
	}

	@Test
	@Parameters({"luigi", "gigi"})
	public void shouldRespondToGetUserOnUserNames(String name) throws Exception {
		Response response = jerseyClient.target(new URI(serverBackedApplicationUnderTest.getAddress().toString()))
			.path("user/" + name)
			.request()
			.get();

		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("user/" + name));
	}

	@Test
	@Parameters({"luigi", "gigi"})
	public void shouldRespondToGetUserTweets(String name) throws Exception {
		Response response = jerseyClient.target(new URI(serverBackedApplicationUnderTest.getAddress().toString()))
			.path("user/" + name + "/tweets")
			.request()
			.get();

		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("user/" + name + "/tweets"));
	}

	@Test
	@Parameters({"luigi", "gigi"})
	public void shouldRespondToGetUserFriends(String name) throws Exception {
		Response response = jerseyClient.target(new URI(serverBackedApplicationUnderTest.getAddress().toString()))
			.path("user/" + name + "/friends")
			.request()
			.get();

		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("user/" + name + "/friends"));
	}

	@Test
	public void shouldRespondToPostUser() throws Exception {
		Response response = jerseyClient.target(new URI(serverBackedApplicationUnderTest.getAddress().toString()))
			.path("user")
			.request()
			.post(Entity.entity("{name: 'Luigi'}", MediaType.APPLICATION_JSON_TYPE));


		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("user"));
	}

	@Test
	public void shouldMethodNotAllowedToPutUsers() throws Exception {
		Response response = jerseyClient.target(new URI(serverBackedApplicationUnderTest.getAddress().toString()))
			.path("user")
			.request()
			.put(Entity.entity("{name: 'Luigi'}", MediaType.APPLICATION_JSON_TYPE));


		assertThat(response.getStatus(), is(405));
	}

	@Test
	public void shouldServeStaticFile() throws Exception {
		Response response = jerseyClient.target(new URI(serverBackedApplicationUnderTest.getAddress().toString()))
			.path("assets/js/app.js")
			.request()
			.get();

		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("var message = 'Hello Devoxx!';\n"));
	}

	@Test
	public void shouldNotBlockWhenUsingIO() throws Exception {
		CountDownLatch lock = new CountDownLatch(2);

		wireMockRule.stubFor(get(urlMatching(".*"))
			.willReturn(aResponse()
				.withStatus(200)
				.withFixedDelay(2000)
				.withBody("My long tweets")
			));

		CompletableFuture<Response> extFuture = CompletableFuture.supplyAsync(() ->
			jerseyClient.target(Try.of(() -> new URI(serverBackedApplicationUnderTest.getAddress().toString())).get())
				.path("external")
				.request()
				.get());

		extFuture.thenAccept(response -> {
			System.out.println("Done with external");
			lock.countDown();
		});

		Thread.sleep(1000);

		CompletableFuture<Response> userFuture = CompletableFuture.supplyAsync(() ->
			jerseyClient.target(Try.of(() -> new URI(serverBackedApplicationUnderTest.getAddress().toString())).get())
				.path("user")
				.request()
				.get());

		userFuture.thenAccept(response -> {
			System.out.println("Done with user");
			lock.countDown();
		});

		assertThat(lock.await(2500, TimeUnit.MILLISECONDS), is(true));;
		wireMockRule.verify(1, getRequestedFor(urlPathMatching(".*")));

	}
}
