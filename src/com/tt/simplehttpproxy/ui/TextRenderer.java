package com.tt.simplehttpproxy.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextRenderer extends ContentRenderer {

	@Override
	public JComponent render(File file, String contentEncoding) {
		JTextArea content = new JTextArea();
		BufferedReader br = null;
		try {
			if ("gzip".equalsIgnoreCase(contentEncoding)) {
				br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
			} else if ("deflate".equalsIgnoreCase(contentEncoding)) {
				br = new BufferedReader(new InputStreamReader(new DeflaterInputStream(new FileInputStream(file))));
			} else {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			}
			String line;
			while ((line = br.readLine()) != null) {
				content.append(line);
				content.append("\r\n");
			}
		} catch (Exception e) {
			
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
			}
		}
		JScrollPane jsp = new JScrollPane(content);
		return (jsp);
	}

}
