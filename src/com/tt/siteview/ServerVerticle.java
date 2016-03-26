package com.tt.siteview;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class ServerVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(ServerVerticle.class);
	
	private static final Buffer buffer = Buffer.buffer(new byte[]{71, 73, 70, 56, 57, 97, 1, 0, 1, 0, 0, 0, 0, 33, -7, 4, 1, 0, 0, 0, 0, 44, 0, 0, 0, 0, 1, 0, 1, 0, 0, 2});
	
	private HttpServer httpServer;
	
	private EventBus eventBus;
	
	private AtomicInteger hits = new AtomicInteger(0);
	
	private Map<String, Long> uris = new HashMap<String, Long>();
	
	private Map<String, Long> countries = new HashMap<String, Long>();
	
	private Map<String, Long> languages = new HashMap<String, Long>();
	
	private Map<String, Long> operatingSystems = new HashMap<String, Long>();
	
	private Map<String, Long> browsers = new HashMap<String, Long>();
	
	@Override
	public void start() throws Exception {
		logger.trace("start");
		
		Router router = Router.router(vertx);
		
	    // handle events to clients
		BridgeOptions opts = new BridgeOptions().addOutboundPermitted(new PermittedOptions().setAddress("event.to.client"));
	    SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);
	    router.route("/event/*").handler(ebHandler);
		
	    // handle static html
	    router.route("/html/*").handler(StaticHandler.create());
	    
	    // handle tracking image requests
		router.get("/img/*").handler(this::handleImg);
		
		httpServer = vertx.createHttpServer();
		httpServer.requestHandler(router::accept).listen(9090);
	
		eventBus = vertx.eventBus();
		
		new Thread() {
			
			@Override
			public void run() {
				logger.trace("run");
				while (true) {
					try {
						sleep(1000);
						Map<String, Object> obj = new HashMap<String, Object>();
						obj.put("time", "" + System.currentTimeMillis());
						obj.put("hits", "" + hits.intValue());
						obj.put("uris", Utils.sortedView(uris, 10));
						obj.put("countries", Utils.sortedView(countries, 10));
						obj.put("languages", Utils.sortedView(languages, 10));
						obj.put("operatingSystems", Utils.sortedView(operatingSystems, 10));
						obj.put("browsers", Utils.sortedView(browsers, 10));
						eventBus.publish("event.to.client", Json.encode(obj));
						hits.set(0);
					} catch (Exception e) {
						logger.error("error publishing event to clients: " + e.getMessage());
					}
				}
			}
			
		}.start();
	}

	@Override
	public void stop() throws Exception {
		logger.trace("stop");
		httpServer.close();
	}

	private void handleImg(RoutingContext routingContext) {
		logger.trace("handleImg");
		
		HttpServerRequest request = routingContext.request();
		
		String uri = Utils.parseUriFromReferer(request.getHeader("Referer"));
		logger.debug("uri = " + uri);
		
		String country = Utils.parseCountryFromHost(request.remoteAddress().host());
		logger.debug("country = " + country);
		
		String language = Utils.parseLanguageFromAcceptLanguage(request.getHeader("Accept-Language"));
		logger.debug("language = " + language);
		
		String operatingSystem = Utils.parseOperatingSystemFromUserAgent(request.getHeader("User-Agent"));
		logger.debug("operatingSystem = " + operatingSystem);
		
		String browser = Utils.parseBrowserFromUserAgent(request.getHeader("User-Agent"));
		logger.debug("browser = " + browser);
		
		hits.incrementAndGet();
		uris.put(uri, uris.get(uri) == null ? 1 : 1 + uris.get(uri));
		countries.put(country, countries.get(country) == null ? 1 : 1 + countries.get(country));
		languages.put(language, languages.get(language) == null ? 1 : 1 + languages.get(language));
		operatingSystems.put(operatingSystem, operatingSystems.get(operatingSystem) == null ? 1 : 1 + operatingSystems.get(operatingSystem));
		browsers.put(browser, browsers.get(browser) == null ? 1 : 1 + browsers.get(browser));

		HttpServerResponse response = routingContext.response();
		response.setStatusCode(200);
		response.headers()
			.add("Content-Length", "" + buffer.length())
			.add("Content-Type", "img/gif")
			.add("Cache-Control", "private");
		response.write(buffer);
		response.end();
	}
	
}
