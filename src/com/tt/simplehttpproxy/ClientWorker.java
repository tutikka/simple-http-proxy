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
		Transaction transaction = null;
		HttpRequestHead requestHead = null;
		HttpResponseHead responseHead = null;
		HttpURLConnection conn = null;
		long total = -1;
		try {
			// start transaction
			transaction = new Transaction(id);
			transaction.setStart(System.currentTimeMillis());
			BufferedInputStream in = null;
			BufferedOutputStream out = null;
			byte[] buffer = null;
			long start;
			long end;
			int read;
			// parse request head
			requestHead = HttpRequestHead.parse(id, socket.getInputStream());
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
			for (Header header : requestHead.getHeaders()) {
				String name = header.getName();
				String value = header.getValue();
				conn.setRequestProperty(name, value);
				transaction.getRequestHeaders().add(header);
				Log.i(id, "Proxy -> Destination " + name + ": " + value);
			}
			// send content to server
			if (conn.getDoOutput()) {
				start = System.currentTimeMillis();
				File file = new File("/tmp", "cache.input." + id);
				in = new BufferedInputStream(socket.getInputStream());
				out = new BufferedOutputStream(new FileOutputStream(file));
				buffer = new byte[32 * 1024];
				total = 0;
				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
					total += read;
				}
				out.flush();
				out.close();
				in.close();
				end = System.currentTimeMillis();
				Log.i(id, "Source -> Proxy " + total + " bytes in " + (end - start) + " milliseconds");
				start = System.currentTimeMillis();
				in = new BufferedInputStream(new FileInputStream(file));
				out = new BufferedOutputStream(conn.getOutputStream());
				buffer = new byte[32 * 1024];
				total = 0;
				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
					total += read;
				}
				out.flush();
				out.close();
				in.close();
				end = System.currentTimeMillis();
				Log.i(id, "Proxy -> Destination " + total + " bytes in " + (end - start) + " milliseconds");
			}
			// get status from server
			int status = conn.getResponseCode();
			Log.i(id, "got status " + status + " from destination " + requestHead.getUri());
			// get response headers from server
			responseHead = HttpResponseHead.parse(id, conn.getHeaderFields());
			// send response headers to client
			socket.getOutputStream().write((responseHead.getVersion() + " " + responseHead.getStatus() + " " + responseHead.getMessage() + "\r\n").getBytes("UTF-8"));
			for (Header header : responseHead.getHeaders()) {
				String name = header.getName();
				String value = header.getValue();
				transaction.getResponseHeaders().add(header);
				if ("Transfer-Encoding".equalsIgnoreCase(name) && "chunked".equalsIgnoreCase(value)) {
					// handle content length below in this case...
				} else {
					socket.getOutputStream().write((name + ": " + value + "\r\n").getBytes("UTF-8"));
					Log.i(id, "Proxy -> Source " + name + ": " + value);
				}
			}
			// send content to client
			start = System.currentTimeMillis();
			File file = new File("/tmp", "cache.output." + id);
			in = new BufferedInputStream(conn.getInputStream());
			out = new BufferedOutputStream(new FileOutputStream(file));
			buffer = new byte[32 * 1024];
			total = 0;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
				total += read;
			}
			out.flush();
			out.close();
			in.close();
			end = System.currentTimeMillis();
			Log.i(id, "Destination -> Proxy " + total + " bytes in " + (end - start) + " milliseconds");
			// ensure content length
			socket.getOutputStream().write(("Content-Length: " + total + "\r\n").getBytes("UTF-8"));
			socket.getOutputStream().write("\r\n".getBytes("UTF-8"));
			in = new BufferedInputStream(new FileInputStream(file));
			out = new BufferedOutputStream(socket.getOutputStream());
			buffer = new byte[32 * 1024];
			total = 0;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
				total += read;
			}
			out.flush();
			out.close();
			in.close();
			end = System.currentTimeMillis();
			Log.i(id, "Proxy -> Source " + total + " bytes in " + (end - start) + " milliseconds");	
			// disconnect
			conn.disconnect();
		} catch (SocketException e) {
			Log.e(id, "socket exception when handling client connection", e);
		} catch (Exception e) {
			Log.e(id, "exception when handling client connection", e);
		} finally {
			// end transaction
			transaction.setParameters(requestHead.getParameters());
			transaction.setSource(socket.getInetAddress().getHostAddress());
			transaction.setDestination(requestHead.getUri());
			transaction.setMethod(requestHead.getMethod());
			transaction.setStatus(responseHead.getStatus());
			transaction.setContentType(Utils.getHeaderValue(responseHead.getHeaders(), "Content-Type"));
			transaction.setLength(total);
			transaction.setEnd(System.currentTimeMillis());
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
