package me.lcardito.ratpack.handlers;

import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;


public class UserRouter implements Action<Chain> {

	@Override
	public void execute(Chain chain) throws Exception {
		chain.path("", this::rootUser);
		chain.prefix(":username", username -> {
			username.get("", this::getUserName);
			username.get("tweets", this::getTweets);
			username.get("friends", this::getFriends);
		});
	}

	private void getFriends(Context ctx) {
		ctx.render("user/" + ctx.getAllPathTokens().get("username") + "/friends");
	}

	private void getTweets(Context ctx) {
		ctx.render("user/" + ctx.getAllPathTokens().get("username") + "/tweets");
	}

	private void getUserName(Context ctx) {
		ctx.render("user/" + ctx.getAllPathTokens().get("username"));
	}

	private void rootUser(Context ctx) throws Exception {
		ctx.byMethod(m -> {
			m.get(() -> ctx.render("user"));
			m.post(() -> ctx.render("user"));
		});
	}

}
