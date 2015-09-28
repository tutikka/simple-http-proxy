package com.tt.simplehttpproxy;

public class Proxy {
	
	public static void main(String[] args) throws Exception {
		HttpServer server = new HttpServer(9999, 1);
		server.run();
	}
	
}
