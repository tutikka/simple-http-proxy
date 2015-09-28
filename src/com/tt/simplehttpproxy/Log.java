package com.tt.simplehttpproxy;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	public static void i(String message) {
		StringBuilder sb = new StringBuilder();
		sb.append("[INFO   ] ");
		sb.append(DATE_FORMAT.format(new Date()));
		sb.append(" ");
		sb.append(message);
		System.out.println(sb.toString());
	}
	
	public static void w(String message, Exception exception) {
		StringBuilder sb = new StringBuilder();
		sb.append("[WARNING] ");
		sb.append(DATE_FORMAT.format(new Date()));
		sb.append(" ");
		sb.append(message);
		System.out.println(sb.toString());
	}
	
	public static void e(String message, Exception exception) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ERROR  ] ");
		sb.append(DATE_FORMAT.format(new Date()));
		sb.append(" ");
		sb.append(message);
		System.out.println(sb.toString());
	}
	
}
