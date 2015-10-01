package com.tt.simplehttpproxy.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.tt.simplehttpproxy.HttpServer;
import com.tt.simplehttpproxy.Transaction;
import com.tt.simplehttpproxy.TransactionListener;

@SuppressWarnings("serial")
public class ProxyUI extends JFrame implements ActionListener, TransactionListener, MouseListener {

	private static final int WIDTH = 800;
	
	private static final int HEIGHT = 600;
	
	private TransactionTableModel tableModel = new TransactionTableModel();
	
	private HttpServer server;
	
	private JMenuItem start;
	
	private JMenuItem stop;
	
	private JLabel status;
	
	private JLabel summary;
	
	private int summaryItems = 0;
	
	private long summaryLength = 0;
	
	private ProxyUI() {
		setTitle("Simple HTTP Proxy");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dimension.width / 2 - WIDTH / 2, dimension.height / 2 - HEIGHT / 2);
		setLayout(new BorderLayout());
		setJMenuBar(createMenu());
		add(createContent(), BorderLayout.CENTER);
		add(createFooter(), BorderLayout.SOUTH);
		setVisible(true);
	}
	
	private JMenuBar createMenu() {
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
		return (menuBar);
	}
	
	private JComponent createContent() {
		JTable table = new JTable(tableModel);
		table.setBorder(BorderFactory.createEmptyBorder());
		table.setDefaultRenderer(Integer.class, new TransactionTableCellRenderer());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(this);
		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);
		JScrollPane jsp = new JScrollPane(table);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 1));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(jsp);
		return (panel);
	}
	
	private JComponent createFooter() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		status = new JLabel("Server: stopped", SwingConstants.LEFT);
		panel.add(status);
		summary = new JLabel("0 items | 0 bytes", SwingConstants.RIGHT);
		panel.add(summary);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		return (panel);
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
		summaryItems++;
		summaryLength += transaction.getLength() > 0 ? transaction.getLength() : 0;
		updateSummary();
	}
	
	private void updateSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append(summaryItems);
		sb.append(summaryItems == 1 ? " item | " : " items | ");
		sb.append(summaryLength);
		sb.append(summaryLength == 1 ? " byte" : " bytes");
		summary.setText(sb.toString());
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
			summaryItems = 0;
			summaryLength = 0;
			updateSummary();
		}
		if ("exit".equals(e.getActionCommand())) {
			stopServer();
			dispose();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			JTable table = (JTable) e.getSource();
			Transaction transaction = tableModel.getTransaction(table.rowAtPoint(e.getPoint()));
			if (transaction != null && e.getClickCount() == 2) {
				new TransactionDetailsDialog(this, transaction);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
}
