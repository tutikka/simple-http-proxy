package com.tt.simplehttpproxy;

import java.util.HashMap;
import java.util.Map;

public class HttpHead {
	
	protected String version;
	
	protected Map<String, String> headers;
	
	public HttpHead() {
		this.headers = new HashMap<>();
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
