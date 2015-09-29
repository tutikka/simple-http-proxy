package com.tt.simplehttpproxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

public class ClientWorker implements Runnable {

	private Socket socket;
	
	private String id;
	
	public ClientWorker(Socket socket) {
		this.socket = socket;
		this.id = "" + Thread.currentThread().getId();
		Log.i(id, "new connection from " + socket.getInetAddress().getHostAddress());
	}
	
	@Override
	public void run() {
		
		HttpRequestHead requestHead;
		HttpResponseHead responseHead;
		
		try {
			BufferedInputStream in = null;
			BufferedOutputStream out = null;
			byte[] buffer = null;
			long start;
			long end;
			long total;
			int read;

			// parse request head
			requestHead = HttpRequestHead.parse(id, socket.getInputStream());
			
			// create connection to server
			Log.i(id, "connecting to server " + requestHead.getUri());
			URL url = new URL(requestHead.getUri());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(requestHead.getMethod().toUpperCase());
			conn.setDoInput(true);
			if ("PUT".equalsIgnoreCase(requestHead.getMethod()) || "POST".equalsIgnoreCase(requestHead.getMethod())) {
				conn.setDoOutput(true);
			} else {
				conn.setDoOutput(false);
			}
			
			// send request headers to server
			Set<String> requestHeaders = requestHead.getHeaders().keySet();
			for (Iterator<String> i = requestHeaders.iterator(); i.hasNext(); ) {
				String name = i.next();
				String value = requestHead.getHeaders().get(name);
				conn.setRequestProperty(name, value);
				Log.i(id, "p -> s " + name + ": " + value);
			}
			
			// send content to server
			if (conn.getDoOutput()) {
				start = System.currentTimeMillis();
				in = new BufferedInputStream(socket.getInputStream());
				out = new BufferedOutputStream(conn.getOutputStream());
				buffer = new byte[32 * 1024];
				total = 0;
				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
					total += read;
				}
				out.flush();
				end = System.currentTimeMillis();
				Log.i(id, "c -> s " + total + " bytes in " + (end - start) + " milliseconds");
			}
			
			// get status from server
			int status = conn.getResponseCode();
			Log.i(id, "server responded with status " + status);
			
			// get response headers from server
			responseHead = HttpResponseHead.parse(id, conn.getHeaderFields());

			// send response headers to client
			socket.getOutputStream().write((responseHead.getVersion() + " " + responseHead.getStatus() + " " + responseHead.getMessage() + "\n").getBytes("UTF-8"));
			Set<String> responseHeaders = responseHead.getHeaders().keySet();
			for (Iterator<String> i = responseHeaders.iterator(); i.hasNext(); ) {
				String name = i.next();
				String value = responseHead.getHeaders().get(name);
				socket.getOutputStream().write((name + ": " + value + "\n").getBytes("UTF-8"));
				Log.i(id, "p -> c " + name + ": " + value);
			}
			socket.getOutputStream().write("\n".getBytes("UTF-8"));
			
			// send content to client
			start = System.currentTimeMillis();
			in = new BufferedInputStream(conn.getInputStream());
			out = new BufferedOutputStream(socket.getOutputStream());
			buffer = new byte[32 * 1024];
			total = 0;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
				total += read;
			}
			out.flush();
			end = System.currentTimeMillis();
			Log.i(id, "s -> c " + total + " bytes in " + (end - start) + " milliseconds");
			
			// disconnect
			conn.disconnect();
			
		} catch (Exception e) {
			Log.e(id, "error handling client connection", e);
			e.printStackTrace(System.err);
		} finally {
			close();
		}
	}
	
	private void close() {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			Log.e(id, "error closing client", e);
		}
	}
	
}
