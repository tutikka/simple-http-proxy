package com.tt.simplehttpproxy.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.tt.simplehttpproxy.HttpServer;
import com.tt.simplehttpproxy.Transaction;
import com.tt.simplehttpproxy.TransactionListener;

@SuppressWarnings("serial")
public class ProxyUI extends JFrame implements ActionListener, TransactionListener {

	private static final int WIDTH = 800;
	
	private static final int HEIGHT = 600;
	
	private TransactionTableModel tableModel = new TransactionTableModel();
	
	private HttpServer server;
	
	private JMenuItem start;
	
	private JMenuItem stop;
	
	private JLabel status;
	
	private ProxyUI() {
		setTitle("Simple HTTP Proxy");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dimension.width / 2 - WIDTH / 2, dimension.height / 2 - HEIGHT / 2);
		setLayout(new BorderLayout());
		initMenu();
		initTable();
		initFooter();
		setVisible(true);
	}
	
	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		start = new JMenuItem("Start proxy server");
		start.setActionCommand("start");
		start.addActionListener(this);
		stop = new JMenuItem("Stop proxy server");
		stop.setEnabled(false);
		stop.setActionCommand("stop");
		stop.addActionListener(this);
		JMenuItem clear = new JMenuItem("Clear history");
		clear.setActionCommand("clear");
		clear.addActionListener(this);
		JMenuItem exit = new JMenuItem("Exit");
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		file.add(start);
		file.add(stop);
		file.addSeparator();
		file.add(clear);
		file.addSeparator();
		file.add(exit);
		menuBar.add(file);
		setJMenuBar(menuBar);
	}
	
	private void initTable() {
		JTable table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);
		JScrollPane jsp = new JScrollPane(table);
		add(jsp, BorderLayout.CENTER);
	}
	
	private void initFooter() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		status = new JLabel("Server: stopped");
		panel.add(status);
		add(panel, BorderLayout.SOUTH);
	}
	
	private void startServer() {
		try {
			server = new HttpServer(9999, 10);
			server.addTransactionListener(this);
			server.start();
			start.setEnabled(false);
			stop.setEnabled(true);
			status.setText("Server: running");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	private void stopServer() {
		try {
			if (server != null && server.isAlive()) {
				server.close();
			}
			start.setEnabled(true);
			stop.setEnabled(false);
			status.setText("Server: stopped");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		Runnable ui = new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
				new ProxyUI();
			}
		};
		SwingUtilities.invokeLater(ui);
	}

	@Override
	public void onTransaction(Transaction transaction) {
		tableModel.addTransaction(transaction);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("start".equals(e.getActionCommand())) {
			startServer();
		}
		if ("stop".equals(e.getActionCommand())) {
			stopServer();
		}
		if ("clear".equals(e.getActionCommand())) {
			tableModel.clear();
		}
		if ("exit".equals(e.getActionCommand())) {
			stopServer();
			dispose();
		}
	}
	
}
