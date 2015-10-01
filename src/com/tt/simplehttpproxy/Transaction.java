package com.tt.simplehttpproxy;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

	private String id;
	
	private long start;
	
	private long end;
	
	private String source;
	
	private String destination;
	
	private String method;
	
	private int status;
	
	private String contentType;
	
	private long length;
	
	private List<Parameter> parameters;
	
	private List<Header> requestHeaders;
	
	private List<Header> responseHeaders;
	
	public Transaction(String id) {
		this.id = id;
		parameters = new ArrayList<>();
		requestHeaders = new ArrayList<>();
		responseHeaders = new ArrayList<>();
	}

	public long getTime() {
		return (end - start);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public List<Header> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(List<Header> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public List<Header> getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(List<Header> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}
	
}
