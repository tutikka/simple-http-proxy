package com.tt.simplehttpproxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

public class ClientWorker implements Runnable {

	private static int counter = 0;
	
	private Socket socket;
	
	private Set<TransactionListener> listeners;
	
	private String id;
	
	public ClientWorker(Socket socket, Set<TransactionListener> listeners) {
		this.socket = socket;
		this.listeners = listeners;
		this.id = "" + counter++;
		Log.i(id, "new connection from " + socket.getInetAddress().getHostAddress());
	}
	
	@Override
	public void run() {
		HttpRequestHead requestHead = null;
		HttpResponseHead responseHead = null;
		HttpURLConnection conn = null;
		long txStart = System.currentTimeMillis();
		long txEnd = -1;
		int status = -1;
		long total = -1;
		try {
			BufferedInputStream in = null;
			BufferedOutputStream out = null;
			byte[] buffer = null;
			long start;
			long end;
			int read;
			// parse request head
			requestHead = HttpRequestHead.parse(id, socket.getInputStream());
			// create connection to server
			Log.i(id, "connecting to destination " + requestHead.getUri());
			URL url = new URL(requestHead.getUri().replace(" ", "%20"));
			conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);
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
			status = conn.getResponseCode();
			Log.i(id, "got status " + status + " for destination " + requestHead.getUri());
			// get response headers from server
			responseHead = HttpResponseHead.parse(id, conn.getHeaderFields());
			if (responseHead.isChunkedEncoding()) {
				// send response headers to client
				socket.getOutputStream().write((responseHead.getVersion() + " " + responseHead.getStatus() + " " + responseHead.getMessage() + "\r\n").getBytes("UTF-8"));
				Set<String> responseHeaders = responseHead.getHeaders().keySet();
				for (Iterator<String> i = responseHeaders.iterator(); i.hasNext(); ) {
					String name = i.next();
					String value = responseHead.getHeaders().get(name);
					if ("Transfer-Encoding".equalsIgnoreCase(name) && "chunked".equalsIgnoreCase(value)) {
						// handle content length below...
					} else {
						socket.getOutputStream().write((name + ": " + value + "\r\n").getBytes("UTF-8"));
						Log.i(id, "p -> c " + name + ": " + value);
					}
				}
				// send content to client
				start = System.currentTimeMillis();
				in = new BufferedInputStream(conn.getInputStream());
				out = new BufferedOutputStream(new FileOutputStream(new File("/tmp", "cache." + id)));
				buffer = new byte[32 * 1024];
				total = 0;
				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
					total += read;
				}
				out.flush();
				socket.getOutputStream().write(("Content-Length: " + total + "\r\n").getBytes("UTF-8"));
				socket.getOutputStream().write("\r\n".getBytes("UTF-8"));
				in = new BufferedInputStream(new FileInputStream(new File("/tmp", "cache." + id)));
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
			} else {
				// send response headers to client
				socket.getOutputStream().write((responseHead.getVersion() + " " + responseHead.getStatus() + " " + responseHead.getMessage() + "\r\n").getBytes("UTF-8"));
				Set<String> responseHeaders = responseHead.getHeaders().keySet();
				for (Iterator<String> i = responseHeaders.iterator(); i.hasNext(); ) {
					String name = i.next();
					String value = responseHead.getHeaders().get(name);
					socket.getOutputStream().write((name + ": " + value + "\r\n").getBytes("UTF-8"));
					Log.i(id, "p -> c " + name + ": " + value);
				}
				socket.getOutputStream().write("\r\n".getBytes("UTF-8"));
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
			}
			// disconnect
			conn.disconnect();
			// transaction end
			txEnd = System.currentTimeMillis();
		} catch (SocketException e) {
			Log.e(id, "socket exception when handling client connection", e);
		} catch (Exception e) {
			Log.e(id, "exception when handling client connection", e);
		} finally {
			// transaction
			txEnd = System.currentTimeMillis();
			Transaction transaction = new Transaction();
			transaction.setSource(socket.getInetAddress().getHostAddress());
			transaction.setDestination(requestHead.getUri());
			transaction.setMethod(requestHead.getMethod());
			transaction.setStatus(status);
			transaction.setContentType(responseHead.getHeaderValue("content-type"));
			transaction.setLength(total);
			transaction.setTime(txEnd - txStart);
			if (listeners != null) {
				for (TransactionListener listener : listeners) {
					listener.onTransaction(transaction);
				}
			}
			// clean up
			if (conn != null) {
				conn.disconnect();
			}
			close();
		}
	}
	
	private void close() {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			Log.e(id, "io exception when closing client", e);
		}
	}
	
}
