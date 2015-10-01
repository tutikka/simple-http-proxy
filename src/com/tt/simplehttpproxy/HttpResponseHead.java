package com.tt.simplehttpproxy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpResponseHead extends HttpHead {

	private int status;
	
	private String message;

	private static String formatValues(List<String> values) {
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
	
	public static HttpResponseHead parse(String id, Map<String, List<String>> in) throws Exception {
		if (in == null) {
			throw new Exception("input is null");
		}
		HttpResponseHead head = new HttpResponseHead();
		for (Iterator<String> i = in.keySet().iterator(); i.hasNext(); ) {
			String name = i.next();
			String value = formatValues(in.get(name));
			if (name == null) {
				StringTokenizer st = new StringTokenizer(value, " ");
				if (st.countTokens() >= 2) {
					head.version = st.nextToken();
					head.status = Integer.parseInt(st.nextToken());
					StringBuilder sb = new StringBuilder();
					int index = 0;
					while (st.hasMoreTokens()) {
						if (index > 0) {
							sb.append(" ");
						}
						sb.append(st.nextToken());
					}
					if (sb.length() > 0) {
						head.message = sb.toString();
					}
					Log.i(id, "Destination -> Proxy " + value);
				}
			} else {
				head.headers.put(name, value);
				Log.i(id, "Destination -> Proxy " + name + ": " + value);
			}
		}
		if (head.version == null) {
			throw new Exception("did not find version");
		}
		if (head.status == -1) {
			throw new Exception("did not find status");
		}
		return (head);
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
