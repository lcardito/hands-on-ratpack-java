package me.lcardito.ratpack.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;
import ratpack.handling.Handler;

public class UserRouter implements Action<Chain> {

	private static Logger log = LoggerFactory.getLogger(UserRouter.class);

	@Override
	public void execute(Chain chain) throws Exception {
		chain.path("", new UsersHandler());
		chain.prefix(":username", new UserNameRouter());
	}

	class UsersHandler implements Handler {

		@Override
		public void handle(Context ctx) throws Exception {
			ctx.byMethod(m -> {
				m.get(() -> {
					log.info("Getting user details");
					log.info(Thread.currentThread().getName());
					ctx.render(String.valueOf(System.currentTimeMillis()));
				});
				m.post(() -> ctx.render("user"));
			});
		}
	}

	class UserNameRouter implements Action<Chain> {

		@Override
		public void execute(Chain username) throws Exception {
			username.get("", ctx -> ctx.render("user/" + ctx.getAllPathTokens().get("username")));
			username.get("tweets", new UserNameHandler());
			username.get("friends", ctx -> {
				log.info("Got some friends!");
				ctx.render("user/" + ctx.getAllPathTokens().get("username") + "/friends");
			});
		}
	}

	class UserNameHandler implements Handler {

		@Override
		public void handle(Context ctx) throws Exception {
			ctx.render("user/" + ctx.getAllPathTokens().get("username") + "/tweets");
		}
	}
}
