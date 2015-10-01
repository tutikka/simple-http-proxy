package com.tt.simplehttpproxy.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.tt.simplehttpproxy.Transaction;
import com.tt.simplehttpproxy.Utils;

@SuppressWarnings("serial")
public class TransactionDetailsDialog extends JDialog implements ActionListener {

	private static final int WIDTH = 640;
	
	private static final int HEIGHT = 480;
	
	private Transaction transaction;
	
	public TransactionDetailsDialog(JFrame parent, Transaction transaction) {
		super(parent);
		this.transaction = transaction;
		setTitle("Transaction " + transaction.getId());
		setSize(WIDTH, HEIGHT);
		int x = parent.getX() + parent.getWidth() / 2 - WIDTH / 2;
		int y = parent.getY() + parent.getHeight() / 2 - HEIGHT / 2;
		setLocation(x, y);
		setModal(true);
		setLayout(new BorderLayout());
		add(createContent(), BorderLayout.CENTER);
		add(createFooter(), BorderLayout.SOUTH);
		setVisible(true);
	}

	private JComponent createContent() {
		JTabbedPane jtp = new JTabbedPane();
		jtp.addTab("Parameters", createParametersContent());
		jtp.addTab("Request", createRequestContent());
		jtp.addTab("Response", createResponseContent());
		jtp.addTab("Input", createInputContent());
		jtp.addTab("Output", createOutputContent());
		jtp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return (jtp);
	}
	
	private JComponent createParametersContent() {
		JTable table = new JTable(new ParameterTableModel(transaction.getParameters()));
		table.setBorder(BorderFactory.createEmptyBorder());
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		JScrollPane jsp = new JScrollPane(table);
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(1, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(jsp);
		return (panel);
	}
	
	private JComponent createRequestContent() {
		JTable table = new JTable(new HeaderTableModel(transaction.getRequestHeaders()));
		table.setBorder(BorderFactory.createEmptyBorder());
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		JScrollPane jsp = new JScrollPane(table);
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(1, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(jsp);
		return (panel);
	}
	
	private JComponent createResponseContent() {
		JTable table = new JTable(new HeaderTableModel(transaction.getResponseHeaders()));
		table.setBorder(BorderFactory.createEmptyBorder());
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		JScrollPane jsp = new JScrollPane(table);
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(1, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(jsp);
		return (panel);
	}
	
	private JComponent createInputContent() {
		final JTextArea jta = new JTextArea();
		jta.setEditable(false);
		final File file = new File("/tmp", "cache.input." + transaction.getId());
		if (file.exists() && file.canRead()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					BufferedInputStream in = null;
					try {
						// content type
						String type = Utils.getHeaderValue(transaction.getRequestHeaders(), "Content-Type");
						if (type == null) {
							jta.setText("-- No content type found from response --");
							return;
						}
						if (!type.toLowerCase().startsWith("text/")) {
							jta.setText("-- Content type '" + type + "' not supported --");
							return;
						}
						// encoding
						in = new BufferedInputStream(new FileInputStream(file));
						byte[] buffer = new byte[1024];
						int read;
						while ((read = in.read(buffer)) != -1) {
							jta.append(new String(buffer, 0, read, "UTF-8"));
						}
						in.close();
					} catch (Exception e) {
					} finally {
						try {
							if (in != null) {
								in.close();
							}
						} catch (Exception e) {
						}
					}
				}
			});
		}
		JScrollPane jsp = new JScrollPane(jta);
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(1, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(jsp);
		return (panel);
	}
	
	private JComponent createOutputContent() {
		final JTextArea jta = new JTextArea();
		jta.setEditable(false);
		final File file = new File("/tmp", "cache.output." + transaction.getId());
		if (file.exists() && file.canRead()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					BufferedInputStream in = null;
					try {
						// content type
						String type = Utils.getHeaderValue(transaction.getResponseHeaders(), "Content-Type");
						if (type == null) {
							jta.setText("-- No content type found from response --");
							return;
						}
						if (!type.toLowerCase().startsWith("text/")) {
							jta.setText("-- Content type '" + type + "' not supported --");
							return;
						}
						// encoding
						String encoding = Utils.getHeaderValue(transaction.getResponseHeaders(), "Content-Encoding");
						if ("gzip".equalsIgnoreCase(encoding)) {
							in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)));
						} else if ("deflate".equalsIgnoreCase(encoding)) {
							in = new BufferedInputStream(new DeflaterInputStream(new FileInputStream(file)));
						} else {
							in = new BufferedInputStream(new FileInputStream(file));
						}
						byte[] buffer = new byte[1024];
						int read;
						while ((read = in.read(buffer)) != -1) {
							jta.append(new String(buffer, 0, read, "UTF-8"));
						}
						in.close();
					} catch (Exception e) {
					} finally {
						try {
							if (in != null) {
								in.close();
							}
						} catch (Exception e) {
						}
					}
				}
			});
		}
		JScrollPane jsp = new JScrollPane(jta);
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(1, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(jsp);
		return (panel);
	}
	
	private JComponent createFooter() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton close = new JButton("Close");
		close.setActionCommand("close");
		close.addActionListener(this);
		panel.add(close);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		return (panel);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("close".equals(e.getActionCommand())) {
			dispose();
		}
	}
	
}
