package com.tt.simplehttpproxy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.StringTokenizer;

public class HttpRequestHead extends HttpHead {

	private String method;
	
	private String uri;

	public static HttpRequestHead parse(InputStream in) throws Exception {
		if (in == null) {
			throw new Exception("input is null");
		}
		HttpRequestHead head = new HttpRequestHead();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		int lineNumber = 1;
		while ((line = br.readLine()) != null) {
			if (line.isEmpty()) {
				break;
			}
			if (lineNumber == 1) {
				StringTokenizer st = new StringTokenizer(line, " ");
				if (st.countTokens() == 3) {
					head.method = URLDecoder.decode(st.nextToken(), "UTF-8");
					head.uri = URLDecoder.decode(st.nextToken(), "UTF-8");
					head.version = URLDecoder.decode(st.nextToken(), "UTF-8");
					Log.i("\tc -> p " + line);
				}
			} else {
				int index = line.indexOf(":");
				if (index > 0) {
					String name = line.substring(0, index).trim();
					String value = line.substring(index + 1).trim();
					head.headers.put(name, value);
					Log.i("\tc -> p " + name + ": " + value);
				}
			}
			lineNumber++;
		}
		if (head.method == null) {
			throw new Exception("did not find method");
		}
		if (head.uri == null) {
			throw new Exception("did not find uri");
		}
		if (head.version == null) {
			throw new Exception("did not find version");
		}
		return (head);
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
}
