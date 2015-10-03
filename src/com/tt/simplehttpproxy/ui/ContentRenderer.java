package com.tt.simplehttpproxy.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

public abstract class ContentRenderer {

	private static final Map<String, ContentRenderer> renderers = new HashMap<String, ContentRenderer>();
	
	private static TextRenderer text = new TextRenderer();
	
	private static ImageRenderer image = new ImageRenderer();
	
	static {
		// text
		renderers.put("text/plain", text);
		renderers.put("text/html", text);
		renderers.put("text/css", text);
		renderers.put("text/javascript", text);
		renderers.put("application/javascript", text);
		
		// image
		renderers.put("image/jpg", image);
		renderers.put("image/jpeg", image);
		renderers.put("image/png", image);
		renderers.put("image/gif", image);
	}
	
	public static ContentRenderer getRenderer(String contentType) {
		if (contentType == null) {
			return (null);
		}
		for (String key : renderers.keySet()) {
			if (contentType.toLowerCase().startsWith(key)) {
				return (renderers.get(key));
			}
		}
		return (null);
	}
	
	public abstract JComponent render(File file, String contentEncoding);
	
}
