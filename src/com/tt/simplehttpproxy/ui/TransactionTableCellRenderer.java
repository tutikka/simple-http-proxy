package com.tt.simplehttpproxy.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class TransactionTableCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    if (!isSelected && column == 3) {
	    	int status = (Integer) value;
	    	// 2xx
	    	if (status >= 200 && status < 300) {
	    		component.setBackground(Color.GREEN);
	    	}
	    	// 3xx
	    	if (status >= 300 && status < 400) {
	    		component.setBackground(Color.YELLOW);
	    	}
	    	// 4xx
	    	if (status >= 400 && status < 500) {
	    		component.setBackground(Color.CYAN);
	    	}
	    	// 5xx
	    	if (status >= 500 && status < 600) {
	    		component.setBackground(Color.RED);
	    	}
	    }
	    return (component);
	}

}
