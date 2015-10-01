package com.tt.simplehttpproxy;

import java.util.ArrayList;
import java.util.List;

public class HttpHead {
	
	protected String version;
	
	protected List<Header> headers = new ArrayList<>();
	
	public HttpHead() {
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}
	
}
