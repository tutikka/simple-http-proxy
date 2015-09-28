package com.tt.simplehttpproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer implements Runnable {

	private int port = 9999;
	
	private int maxParallelClients = 1;
	
	private ServerSocket serverSocket;
	
	private ExecutorService executorService;
	
	public HttpServer() throws Exception {
		init();
	}
	
	public HttpServer(int port, int maxParallelClients) throws Exception {
		this.port = port;
		this.maxParallelClients = maxParallelClients;
		init();
	}
	
	private void init() throws Exception {
		executorService = Executors.newFixedThreadPool(maxParallelClients);
		serverSocket = new ServerSocket(port);
	}
	
	@Override
	public void run() {
		try {
			Log.i("waiting for connections on port " + port + "...");
			while (!Thread.currentThread().isInterrupted()) {
				executorService.submit(new ClientWorker(serverSocket.accept()));
			}
		} catch (IOException e) {
			Log.e("error waiting for connections", e);
		} finally {
			close();
		}
	}
	
	private void close() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			Log.e("error closing server", e);
		}
	}
	
}
