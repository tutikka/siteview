package com.tt.siteview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

	private static final Logger logger = LoggerFactory.getLogger(Config.class);
	
	private static final String PORT_KEY = "siteview.port";
	
	private static final int PORT_DEFAULT_VALUE = 9090;
	
	private static final String INTERVAL_KEY = "siteview.interval";
	
	private static final int INTERVAL_DEFAULT_VALUE = 1000;
	
	public static int port() {
		int value = PORT_DEFAULT_VALUE;
		try {
			value = Integer.parseInt(System.getProperty(PORT_KEY, "" + PORT_DEFAULT_VALUE));
		} catch (Exception e) {
			logger.warn("error parsing configuration value for key " + PORT_KEY + ": " + e.getMessage());
		}
		logger.debug("config: " + PORT_KEY + " = " + value);
		return (value);
	}
	
	public static int interval() {
		int value = INTERVAL_DEFAULT_VALUE;
		try {
			value = Integer.parseInt(System.getProperty(INTERVAL_KEY, "" + INTERVAL_DEFAULT_VALUE));
		} catch (Exception e) {
			logger.warn("error parsing configuration value for key " + INTERVAL_KEY + ": " + e.getMessage());
		}
		logger.debug("config: " + INTERVAL_KEY + " = " + value);
		return (value);
	}
	
}
