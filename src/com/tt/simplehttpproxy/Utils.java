package com.tt.simplehttpproxy;

import java.io.File;
import java.util.List;

public class Utils {

	public static String getHeaderValue(List<Header> headers, String name) {
		for (Header header : headers) {
			if (name.equalsIgnoreCase(header.getName())) {
				return (header.getValue());
			}
		}
		return (null);
	}
	
	public static String getCacheDirectory() {
		File file = new File(System.getProperty("user.home"), "simple-http-proxy-cache");
		if (!file.exists()) {
			file.mkdirs();
		}
		return (file.getAbsolutePath());
	}
	
}
