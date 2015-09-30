package com.tt.simplehttpproxy.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.tt.simplehttpproxy.Transaction;

@SuppressWarnings("serial")
public class TransactionTableModel extends AbstractTableModel {

	private static final String[] columns = new String[]{"Source", "Destination", "Method", "Status", "Content Type", "Length (bytes)", "Time (ms)"};

	private List<Transaction> rows = new ArrayList<>();
	
	public void addTransaction(Transaction transaction) {
		rows.add(transaction);
		fireTableDataChanged();
	}
	
	public void clear() {
		rows.clear();
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return (rows.size());
	}

	@Override
	public int getColumnCount() {
		return (columns.length);
	}

	@Override
	public String getColumnName(int column) {
		return (columns[column]);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0 : return (String.class);
		case 1 : return (String.class);
		case 2 : return (String.class);
		case 3 : return (Integer.class);
		case 4 : return (String.class);
		case 5 : return (Long.class);
		case 6 : return (Long.class);
		default : return (null);
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Transaction transaction = rows.get(rowIndex);
		if (transaction != null) {
			switch (columnIndex) {
			case 0 : return (transaction.getSource());
			case 1 : return (transaction.getDestination());
			case 2 : return (transaction.getMethod());
			case 3 : return (transaction.getStatus());
			case 4 : return (transaction.getContentType());
			case 5 : return (transaction.getLength());
			case 6 : return (transaction.getTime());
			default : return (null);
			}
		} else {
			return (null);
		}
	}
	
}
