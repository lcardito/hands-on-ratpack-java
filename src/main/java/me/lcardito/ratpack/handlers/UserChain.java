package me.lcardito.ratpack.handlers;

import ratpack.func.Action;
import ratpack.handling.Chain;

public class UserChain implements Action<Chain> {

	@Override
	public void execute(Chain chain) throws Exception {
		chain.path("", ctx -> ctx.byMethod(m -> {
			m.get(() -> ctx.render("user"));
			m.post(() -> ctx.render("user"));
		}));

		chain.prefix(":username", username -> {
			username.get("", ctx -> ctx.render("user/" + ctx.getAllPathTokens().get("username")));
			username.get("tweets", ctx -> ctx.render("user/" + ctx.getAllPathTokens().get("username") + "/tweets"));
			username.get("friends", ctx -> ctx.render("user/" + ctx.getAllPathTokens().get("username") + "/friends"));

		});

	}

}
