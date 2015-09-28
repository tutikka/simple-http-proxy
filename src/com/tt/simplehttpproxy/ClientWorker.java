package com.tt.simplehttpproxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientWorker implements Runnable {

	private Socket socket;
	
	public ClientWorker(Socket socket) {
		this.socket = socket;
		Log.i("new connection from " + socket.getInetAddress().getHostAddress());
	}
	
	@Override
	public void run() {
		
		HttpHead head;
		try {
			head = HttpHead.parse(socket.getInputStream());
		} catch (Exception e) {
			Log.e("error parsing request head", e);
			return;
		}
		
		try {
			BufferedInputStream in = null;
			BufferedOutputStream out = null;
			byte[] buffer = null;
			long start;
			long end;
			long total;
			int read;

			Log.i("connecting to server " + head.getUri());
			URL url = new URL(head.getUri());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(head.getMethod().toUpperCase());
			conn.setDoInput(true);
			if ("PUT".equalsIgnoreCase(head.getMethod()) || "POST".equalsIgnoreCase(head.getMethod())) {
				conn.setDoOutput(true);
			} else {
				conn.setDoOutput(false);
			}
			
			// request headers
			Set<String> requestHeaders = head.getHeaders().keySet();
			for (Iterator<String> i = requestHeaders.iterator(); i.hasNext(); ) {
				String name = i.next();
				String value = head.getHeaders().get(name);
				conn.setRequestProperty(name, value);
				Log.i("\tp -> s " + name + ": " + value);
			}
			
			// content
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
				Log.i("\tc -> p -> s " + total + " bytes in " + (end - start) + " milliseconds");
			}
			
			// status
			int status = conn.getResponseCode();
			Log.i("server responded with status " + status);
			
			// response headers
			Map<String, List<String>> responseHeaders = conn.getHeaderFields();
			for (Iterator<String> i = responseHeaders.keySet().iterator(); i.hasNext(); ) {
				String name = i.next();
				String value = formatValues(responseHeaders.get(name));
				if (name == null) {
					socket.getOutputStream().write((value + "\n").getBytes("UTF-8"));
					Log.i("\ts -> p -> c " + value);
				} else {
					socket.getOutputStream().write((name + ": " + value + "\n").getBytes("UTF-8"));
					Log.i("\ts -> p -> c " + name + ": " + value);
				}
			}
			socket.getOutputStream().write("\n".getBytes("UTF-8"));
			
			// content
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
			Log.i("\ts -> p -> c " + total + " bytes in " + (end - start) + " milliseconds");
			
			conn.disconnect();
			
		} catch (Exception e) {
			Log.e("error handling client connection", e);
			e.printStackTrace(System.err);
		} finally {
			close();
		}
	}

	private String formatValues(List<String> values) {
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for (String value : values) {
			if (index > 0) {
				sb.append(",");
			}
			sb.append(value);
			index++;
		}
		return (sb.toString());
	}
	
	private void close() {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			Log.e("error closing client connection", e);
		}
	}
	
}
