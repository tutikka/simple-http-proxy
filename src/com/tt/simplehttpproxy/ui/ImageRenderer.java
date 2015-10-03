package com.tt.simplehttpproxy.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ImageRenderer extends ContentRenderer {

	@Override
	public JComponent render(File file, String contentEncoding) {
		try {
			ImagePanel panel = new ImagePanel(ImageIO.read(file));
			panel.setBackground(Color.WHITE);
			JScrollPane jsp = new JScrollPane(panel);
			return (jsp);
		} catch (Exception e) {
			return (null);
		}
	}

	@SuppressWarnings("serial")
	private class ImagePanel extends JPanel {
		
		private Image image;
		
		private ImagePanel(Image image) {
			this.image = image;
		}
		
	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        g.drawImage(image, 0, 0, null);           
	    }
		
	}
	
}
