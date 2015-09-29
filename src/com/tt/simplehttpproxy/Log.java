package com.tt.simplehttpproxy;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	public static void i(String message) {
		i(null, message);
	}
	
	public static void i(String id, String message) {
		StringBuilder sb = new StringBuilder();
		sb.append("[INFO   ] ");
		sb.append(DATE_FORMAT.format(new Date()));
		sb.append(" ");
		if (id != null) {
			sb.append(formatId(id));
		}
		sb.append(message);
		System.out.println(sb.toString());
	}
	
	public static void w(String message, Exception exception) {
		w(null, message, exception);
	}
	
	public static void w(String id, String message, Exception exception) {
		StringBuilder sb = new StringBuilder();
		sb.append("[WARNING] ");
		sb.append(DATE_FORMAT.format(new Date()));
		sb.append(" ");
		if (id != null) {
			sb.append(formatId(id));
		}
		sb.append(message);
		System.out.println(sb.toString());
	}
	
	public static void e(String message, Exception exception) {
		e(null, message, exception);
	}
	
	public static void e(String id, String message, Exception exception) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ERROR  ] ");
		sb.append(DATE_FORMAT.format(new Date()));
		sb.append(" ");
		if (id != null) {
			sb.append(formatId(id));
		}
		sb.append(message);
		System.out.println(sb.toString());
	}
	
	private static String formatId(String id) {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		int length = 4 - sb.length();
		for (int i = 0; i < length; i++) {
			sb.insert(0, " ");
		}
		sb.insert(0, "(");
		sb.append(") ");
		return (sb.toString());
	}
	
}
