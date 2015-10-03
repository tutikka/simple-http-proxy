package com.tt.simplehttpproxy.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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
		StripedTable table = new StripedTable(new ParameterTableModel(transaction.getParameters()));
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
		StripedTable table = new StripedTable(new HeaderTableModel(transaction.getRequestHeaders()));
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
		StripedTable table = new StripedTable(new HeaderTableModel(transaction.getResponseHeaders()));
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
		final JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(1, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				File file = new File(Utils.getCacheDirectory(), "input." + transaction.getId());
				String contentType = Utils.getHeaderValue(transaction.getRequestHeaders(), "Content-Type");
				ContentRenderer cr = ContentRenderer.getRenderer(contentType);
				if (!file.exists() || !file.canRead() || file.length() == 0) {
					JTextArea jta = new JTextArea("-- Content not found --");
					jta.setEditable(false);
					panel.add(jta);
				} else if (cr == null) {
					JTextArea jta = new JTextArea("-- Content type '" + contentType + "' not supported --");
					jta.setEditable(false);
					panel.add(jta);
				} else {
					panel.add(ContentRenderer.getRenderer(contentType).render(file, null));
				}
			}
		});
		return (panel);
	}
	
	private JComponent createOutputContent() {
		final JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridLayout(1, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				File file = new File(Utils.getCacheDirectory(), "output." + transaction.getId());
				String contentType = Utils.getHeaderValue(transaction.getResponseHeaders(), "Content-Type");
				String contentEncoding = Utils.getHeaderValue(transaction.getResponseHeaders(), "Content-Encoding");
				ContentRenderer cr = ContentRenderer.getRenderer(contentType);
				if (!file.exists() || !file.canRead() || file.length() == 0) {
					JTextArea jta = new JTextArea("-- Content not found --");
					jta.setEditable(false);
					panel.add(jta);
				} else if (cr == null) {
					JTextArea jta = new JTextArea("-- Content type '" + contentType + "' not supported --");
					jta.setEditable(false);
					panel.add(jta);
				} else {
					panel.add(ContentRenderer.getRenderer(contentType).render(file, contentEncoding));
				}
			}
		});
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
