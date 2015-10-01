package com.tt.simplehttpproxy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class HttpRequestHead extends HttpHead {

	private String method;
	
	private String uri;

	private List<Parameter> parameters = new ArrayList<>();
	
	public static HttpRequestHead parse(String id, InputStream in) throws Exception {
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
					head.method = st.nextToken();
					head.uri = URLDecoder.decode(st.nextToken(), "UTF-8");
					int index = head.uri.indexOf("?");
					if (index != -1) {
						StringTokenizer st2 = new StringTokenizer(head.uri.substring(index + 1), "&");
						while (st2.hasMoreTokens()) {
							String[] parts = st2.nextToken().split("=");
							if (parts != null && parts.length == 2) {
								head.parameters.add(new Parameter(parts[0], parts[1]));
							}
						}
					}
					head.version = st.nextToken();
					Log.i(id, "Source -> Proxy " + line);
				}
			} else {
				int index = line.indexOf(":");
				if (index > 0) {
					String name = line.substring(0, index).trim();
					String value = line.substring(index + 1).trim();
					head.headers.add(new Header(name, value));
					Log.i(id, "Source -> Proxy " + name + ": " + value);
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

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
}
