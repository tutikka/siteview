package com.tt.siteview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;

public class Server {

	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	private Server() {
		logger.trace("Server");
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new ServerVerticle());
	}
	
	public static void main(String[] args) {
		logger.trace("main");
		new Server();
	}
	
}
