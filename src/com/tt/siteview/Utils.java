package com.tt.siteview;

import java.io.File;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

public class Utils {

	private static final String UNKNOWN = "Unknown";
	
	private static final UserAgentStringParser userAgentStringParser = UADetectorServiceFactory.getResourceModuleParser();
	
	private static final Cache<String, ReadableUserAgent> useragentCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(2, TimeUnit.HOURS).build();
	
	private static final File countryDatabase = new File("etc/GeoIP2-City.mmdb");
	
	public static String parseUriFromReferer(String referer) {
		if (referer == null) {
			return (UNKNOWN);
		}
		if (referer.toLowerCase().startsWith("http://")) {
			return (referer.substring(referer.indexOf("/", 7), referer.length()));
		}
		if (referer.toLowerCase().startsWith("https://")) {
			return (referer.substring(referer.indexOf("/", 8), referer.length()));
		}
		return (UNKNOWN);
	}
	
	public static String parseCountryFromHost(String host) {
		if (host == null) {
			return (UNKNOWN);
		}
		try {
			DatabaseReader databaseReader = new DatabaseReader.Builder(countryDatabase).withCache(new CHMCache()).build();
			InetAddress ip = InetAddress.getByName(host);
			CountryResponse countryResponse = databaseReader.country(ip);
			return (countryResponse.getCountry().getName());
		} catch (Exception e) {
			return (UNKNOWN);
		}
	}
	
	public static String parseLanguageFromAcceptLanguage(String acceptLanguage) {
		if (acceptLanguage == null) {
			return (UNKNOWN);
		}
		String[] s = acceptLanguage.split(",");
		if (s == null || s.length == 0) {
			return (UNKNOWN);
		}
		String preferred = s[0].trim().replace("-", "_");
		Locale locale = null;
		if (preferred.indexOf("_") != -1) {
			locale = new Locale(preferred.split("_")[0], preferred.split("_")[1]);
		} else {
			locale = new Locale(preferred);
		}
		return (locale.getDisplayLanguage());
	}
	
	public static String parseOperatingSystemFromUserAgent(String userAgent) {
		if (userAgent == null) {
			return (UNKNOWN);
		}
		ReadableUserAgent readableUserAgent = useragentCache.getIfPresent(userAgent);
		if (readableUserAgent == null) {
			readableUserAgent = userAgentStringParser.parse(userAgent);
			useragentCache.put(userAgent, readableUserAgent);
		}
		if (readableUserAgent == null || readableUserAgent.getOperatingSystem() == null) {
			return (UNKNOWN);
		}
		return (readableUserAgent.getOperatingSystem().getName());
	}
	
	public static String parseBrowserFromUserAgent(String userAgent) {
		if (userAgent == null) {
			return (UNKNOWN);
		}
		ReadableUserAgent readableUserAgent = useragentCache.getIfPresent(userAgent);
		if (readableUserAgent == null) {
			readableUserAgent = userAgentStringParser.parse(userAgent);
			useragentCache.put(userAgent, readableUserAgent);
		}
		if (readableUserAgent == null) {
			return (UNKNOWN);
		}
		return (readableUserAgent.getName());
	}
	
	public static Map<String, Long> sortedView(Map<String, Long> original, int numItems) {
		List<Map.Entry<String, Long>> values = new LinkedList<Map.Entry<String,Long>>(original.entrySet());
		Collections.sort(values, new Comparator<Map.Entry<String, Long>>() {
            public int compare( Map.Entry<String, Long> o1, Map.Entry<String, Long> o2 ) {
            	int i = o1.getValue().compareTo(o2.getValue());
                return (-i);
            }
		});
		Map<String, Long> view = new LinkedHashMap<String, Long>();
		for (int i = 0; i < values.size() && i < numItems; i++) {
			Map.Entry<String, Long> entry = values.get(i);
			view.put( entry.getKey(), entry.getValue() );
		}
	    return (view);
	}
	
	
	
}
