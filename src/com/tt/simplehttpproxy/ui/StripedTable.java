package com.tt.simplehttpproxy.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class StripedTable extends JTable {

	public StripedTable(ParameterTableModel tableModel) {
		super(tableModel);
	}
	
	public StripedTable(HeaderTableModel tableModel) {
		super(tableModel);
	}
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
	    Component component = super.prepareRenderer( renderer, row, column );
	    if (!isRowSelected(row)) {
	    	component.setBackground(row % 2 == 0 ? getBackground() : new Color(238, 238, 255));
	    }
	    return (component);
	}

}
