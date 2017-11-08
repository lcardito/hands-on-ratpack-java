package me.lcardito.ratpack.handlers.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;

public class ExtFastHandler implements Handler {
	private static Logger log = LoggerFactory.getLogger(ExtFastHandler.class);

	public ExtFastHandler() throws Exception {
	}

	@Override
	public void handle(Context ctx) throws Exception {
		log.info("Calling a fast service...");
		ctx.render(String.valueOf(System.currentTimeMillis()));
	}

}
