package com.tt.simplehttpproxy.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.tt.simplehttpproxy.Parameter;

@SuppressWarnings("serial")
public class ParameterTableModel extends AbstractTableModel {

	private static final String[] columns = new String[]{"Name", "Value"};

	private List<Parameter> rows = new ArrayList<>();
	
	public ParameterTableModel(List<Parameter> rows) {
		this.rows = rows;
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
		default : return (null);
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Parameter parameter = rows.get(rowIndex);
		if (parameter != null) {
			switch (columnIndex) {
			case 0 : return (parameter.getName());
			case 1 : return (parameter.getValue());
			default : return (null);
			}
		} else {
			return (null);
		}
	}
	
}
