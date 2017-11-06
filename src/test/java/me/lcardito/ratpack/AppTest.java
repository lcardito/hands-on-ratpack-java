package me.lcardito.ratpack;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import ratpack.test.embed.EmbeddedApp;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static ratpack.test.embed.EmbeddedApp.fromServer;

@RunWith(JUnitParamsRunner.class)
public class AppTest {

	private static EmbeddedApp testApp;

	@BeforeClass
	public static void setUp() throws Exception {
		testApp = fromServer(
			App.createServer()
		);
	}

	@Test
	public void shouldRespondToRootPath() throws Exception {
		testApp.test(testHttpClient -> assertThat(testHttpClient.get("/").getBody().getText(), is("Hello Devoxx!")));
	}

	@Test
	public void shouldResponseToGetUser() throws Exception {
		testApp.test(client -> assertThat(client.get("/user").getBody().getText(), is("user")));
	}

	@Test
	@Parameters({"luigi", "gigi"})
	public void shouldRespondToGetUserOnUserNames(String name) throws Exception {
		testApp.test(client -> assertThat(client.get("/user/" + name).getBody().getText(), is("user/" + name)));
	}

	@Test
	@Parameters({"luigi", "gigi"})
	public void shouldRespondToGetUserTweets(String name) throws Exception {
		testApp.test(client -> assertThat(client.get("/user/" + name + "/tweets").getBody().getText(), is("user/" + name + "/tweets")));
	}

	@Test
	@Parameters({"luigi", "gigi"})
	public void shouldRespondToGetUserFriends(String name) throws Exception {
		testApp.test(client -> assertThat(client.get("/user/" + name + "/friends").getBody().getText(), is("user/" + name + "/friends")));
	}

	@Test
	public void shouldRespondToPostUser() throws Exception {
		testApp.test(client -> assertThat(client.post("user").getBody().getText(), is("user")));
	}

	@Test
	public void shouldMethodNotAllowedToPutUsers() throws Exception {
		testApp.test(client -> assertThat(client.put("user").getStatusCode(), is(405)));
	}

	@Test
	public void shouldServeStaticFile() throws Exception {
		testApp.test(client -> assertThat(client.get("assets/js/app.js").getBody().getText(), is("var message = 'Hello Devoxx!';\n")));
	}
}
