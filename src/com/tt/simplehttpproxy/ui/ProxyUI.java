package com.tt.simplehttpproxy.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.tt.simplehttpproxy.HttpServer;
import com.tt.simplehttpproxy.Transaction;
import com.tt.simplehttpproxy.TransactionListener;

@SuppressWarnings("serial")
public class ProxyUI extends JFrame implements ActionListener, TransactionListener {

	private static final int WIDTH = 1024;
	
	private static final int HEIGHT = 768;
	
	private TransactionTableModel tableModel = new TransactionTableModel();
	
	private HttpServer server;
	
	private ProxyUI() {
		setTitle("Simple HTTP Proxy");
		setSize(WIDTH, HEIGHT);
		setLayout(new BorderLayout());
		initMenu();
		initTable();
		setVisible(true);
	}
	
	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem start = new JMenuItem("Start");
		start.setActionCommand("start");
		start.addActionListener(this);
		JMenuItem stop = new JMenuItem("Stop");
		stop.setActionCommand("stop");
		stop.addActionListener(this);
		JMenuItem clear = new JMenuItem("Clear");
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
		table.setFillsViewportHeight(true);
		JScrollPane jsp = new JScrollPane(table);
		add(jsp, BorderLayout.CENTER);
	}
	
	private void startServer() {
		try {
			server = new HttpServer(9999, 1);
			server.addTransactionListener(this);
			server.run();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	private void stopServer() {
		
	}
	
	public static void main(String[] args) {
		new ProxyUI();
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
			dispose();
		}
	}
	
}
