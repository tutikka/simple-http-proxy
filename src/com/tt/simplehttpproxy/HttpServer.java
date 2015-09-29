package com.tt.simplehttpproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer implements Runnable {

	private int port = 9999;
	
	private int maxParallelClients = 1;
	
	private ServerSocket serverSocket;
	
	private ExecutorService executorService;
	
	private Set<TransactionListener> listeners;
	
	public HttpServer() throws Exception {
		init();
	}
	
	public HttpServer(int port, int maxParallelClients) throws Exception {
		this.port = port;
		this.maxParallelClients = maxParallelClients;
		init();
	}
	
	public void addTransactionListener(TransactionListener listener) {
		listeners.add(listener);
	}
	
	public void removeTransactionListener(TransactionListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
	
	private void init() throws Exception {
		executorService = Executors.newFixedThreadPool(maxParallelClients);
		serverSocket = new ServerSocket(port);
		listeners = new HashSet<>();
	}
	
	@Override
	public void run() {
		try {
			Log.i("waiting for connections on port " + port + "...");
			while (!Thread.currentThread().isInterrupted()) {
				executorService.submit(new ClientWorker(serverSocket.accept(), listeners));
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
