package me.lcardito.ratpack;

import me.lcardito.ratpack.handlers.UserChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Chain;
import ratpack.handling.RequestLogger;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

import static ratpack.server.RatpackServer.start;

public class App {

	private static Logger log = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		createServer();
	}

	static RatpackServer createServer() throws Exception {
		return start(s -> s
			.serverConfig(c -> c.baseDir(BaseDir.find()))
			.handlers(chain -> {
				chain.all(RequestLogger.ncsa(log))
					.prefix("user", new UserChain())
					.prefix("assets", assets -> assets.fileSystem("public", Chain::files))
					.files(f -> f.path("home").dir("pages").indexFiles("index.html"))
					.all(ctx -> ctx.render("Hello Devoxx!"));
			}));
	}
}
