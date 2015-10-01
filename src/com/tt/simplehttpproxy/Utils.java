package com.tt.simplehttpproxy;

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
	
}
