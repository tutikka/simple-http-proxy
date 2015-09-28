package com.tt.simplehttpproxy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class HttpHead {

	private String method;
	
	private String uri;
	
	private String version;
	
	private Map<String, String> headers;
	
	public static HttpHead parse(InputStream in) throws Exception {
		if (in == null) {
			throw new Exception("input stream is null");
		}
		HttpHead head = new HttpHead();
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
	
	public HttpHead() {
		this.headers = new HashMap<>();
	}

	public String getHeaderValue(String name) {
		Set<String> keys = headers.keySet();
		for (Iterator<String> i = keys.iterator(); i.hasNext(); ) {
			String key = i.next();
			if (name.toLowerCase().equals(key.toLowerCase())) {
				return (headers.get(key));
			}
		}
		return (null);
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
}
