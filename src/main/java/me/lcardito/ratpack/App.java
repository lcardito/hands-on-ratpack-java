package me.lcardito.ratpack;

import me.lcardito.ratpack.handlers.UserRouter;
import me.lcardito.ratpack.handlers.external.ExtFastHandler;
import me.lcardito.ratpack.handlers.external.ExtSlowHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Chain;
import ratpack.handling.RequestLogger;
import ratpack.server.BaseDir;

import static ratpack.server.RatpackServer.start;

public class App {

	private static Logger log = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		start(s -> s
			.serverConfig(c ->
				c.baseDir(BaseDir.find())
					.development(true)
					.threads(1))
			.handlers(chain -> {
				chain.all(RequestLogger.ncsa(log))
					.prefix("user", new UserRouter())
					.prefix("external", external ->
						external.path("slow", new ExtSlowHandler()).path("fast", new ExtFastHandler())
					)
					.path("external", new ExtSlowHandler())
					.prefix("assets", assets -> assets.fileSystem("public", Chain::files))
					.files(f -> f.path("home").dir("pages").indexFiles("index.html"))
					.all(ctx -> ctx.render("Hello Devoxx!"));
			}));
	}
}
