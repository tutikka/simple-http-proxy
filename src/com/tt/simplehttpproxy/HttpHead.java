package com.tt.simplehttpproxy;

import java.nio.ByteBuffer;
import java.util.Map;

public class HttpHead {

	private String method;
	
	private String uri;
	
	private String version;
	
	private Map<String, String> headers;
	
	public HttpHead parse(ByteBuffer data) throws Exception {
		HttpHead head = new HttpHead();
		return (head);
	}
	
	public HttpHead() {
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
