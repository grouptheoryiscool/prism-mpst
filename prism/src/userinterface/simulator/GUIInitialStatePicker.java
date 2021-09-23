//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Andrew Hinton <ug60axh@cs.bham.ac.uk> (University of Birmingham)
//	* Dave Parker <david.parker@comlab.ox.ac.uk> (University of Oxford, formerly University of Birmingham)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================

package userinterface.simulator;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import parser.Values;
import parser.ast.Declaration;
import parser.ast.Module;
import parser.ast.ModulesFile;
import parser.type.TypeBool;
import parser.type.TypeInt;
import userinterface.GUIPrism;

@SuppressWarnings("serial")
public class GUIInitialStatePicker extends javax.swing.JDialog implements KeyListener
{
	//STATICS    
	public static final int NO_VALUES = 0;
	public static final int VALUES_DONE = 1;
	public static final int CANCELLED = 2;

	//ATTRIBUTES    
	private boolean cancelled = true;

	private JTable initValuesTable;
	private DefineValuesTable initValuesModel;

	Values initialState;

	private GUIPrism gui;

	private ModulesFile mf;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel allPanel;
	private javax.swing.JPanel bottomPanel;
	private javax.swing.JPanel buttonPanel;
	private javax.swing.JButton cancelButton;
	private javax.swing.JPanel innerPanel;
	private javax.swing.JButton okayButton;
	private javax.swing.JCheckBox optionCheckBox;
	private javax.swing.JPanel topPanel;

	// End of variables declaration//GEN-END:variables

	/** Creates new form GUIConstantsPicker */
	public GUIInitialStatePicker(GUIPrism parent, Values defaultInitial, ModulesFile mf)
	{
		super(parent, "Initial State for Simulation", true);

		this.gui = parent;
		this.mf = mf;

		//setup tables
		initValuesModel = new DefineValuesTable();
		initValuesTable = new JTable();

		initValuesTable.setModel(initValuesModel);
		initValuesTable.setSelectionMode(DefaultListSelectionModel.SINGLE_INTERVAL_SELECTION);
		initValuesTable.setCellSelectionEnabled(true);
		initValuesTable.setRowHeight(getFontMetrics(initValuesTable.getFont()).getHeight() + 4);

		this.initialState = defaultInitial;

		//initialise
		initComponents();
		this.getRootPane().setDefaultButton(okayButton);
		initTable();
		initValues();

		super.setBounds(new Rectangle(550, 300));
		setResizable(true);
		setLocationRelativeTo(getParent()); // centre

		//this.askOption = gui.getPrism().getSettings().getBoolean(PrismSettings.SIMULATOR_NEW_PATH_ASK_INITIAL);
		//optionCheckBox.setSelected(this.askOption);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents()
	{
		allPanel = new javax.swing.JPanel();
		bottomPanel = new javax.swing.JPanel();
		buttonPanel = new javax.swing.JPanel();
		okayButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		optionCheckBox = new javax.swing.JCheckBox();
		topPanel = new javax.swing.JPanel();
		innerPanel = new javax.swing.JPanel();

		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				closeDialog(evt);
			}
		});

		allPanel.setLayout(new java.awt.BorderLayout());

		allPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 5, 5, 5)));
		bottomPanel.setLayout(new java.awt.BorderLayout());

		buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

		okayButton.setText("Okay");
		okayButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				okayButtonActionPerformed(evt);
			}
		});

		buttonPanel.add(okayButton);

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				cancelButtonActionPerformed(evt);
			}
		});

		buttonPanel.add(cancelButton);

		bottomPanel.add(buttonPanel, java.awt.BorderLayout.EAST);

		optionCheckBox.setText("Always prompt for initial state on path creation");
		optionCheckBox.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				optionCheckBoxActionPerformed(evt);
			}
		});

		//bottomPanel.add(optionCheckBox, java.awt.BorderLayout.WEST);
		optionCheckBox.getAccessibleContext().setAccessibleName("optionCheckBox");

		allPanel.add(bottomPanel, java.awt.BorderLayout.SOUTH);

		topPanel.setLayout(new java.awt.BorderLayout());

		topPanel.setBorder(new javax.swing.border.TitledBorder("Initial state"));
		innerPanel.setLayout(new java.awt.BorderLayout());

		innerPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 5, 5, 5)));
		topPanel.add(innerPanel, java.awt.BorderLayout.CENTER);

		allPanel.add(topPanel, java.awt.BorderLayout.CENTER);

		getContentPane().add(allPanel, java.awt.BorderLayout.CENTER);

	}

	// </editor-fold>//GEN-END:initComponents

	private void optionCheckBoxActionPerformed(java.awt.event.ActionEvent evt)
	{//GEN-FIRST:event_optionCheckBoxActionPerformed
	// TODO add your handling code here:
	}//GEN-LAST:event_optionCheckBoxActionPerformed

	public static double log(double base, double x)
	{
		return Math.log(x) / Math.log(base);
	}

	private void initTable()
	{
		JScrollPane sp = new JScrollPane();

		sp.setViewportView(initValuesTable);
		innerPanel.add(sp);

		innerPanel.setPreferredSize(new Dimension(300, 300));
	}

	private void initValues()
	{

		Value v;
		if (initialState == null) {

			int n, i, j, n2;
			Declaration decl;
			Module module;

			// first add all globals
			n = mf.getNumGlobals();
			for (i = 0; i < n; i++) {
				decl = mf.getGlobal(i);
				v = new Value(decl.getName(), decl.getType(), "");
				initValuesModel.addValue(v);
			}
			// then add all module variables
			n = mf.getNumModules();
			for (i = 0; i < n; i++) {
				module = mf.getModule(i);
				n2 = module.getNumDeclarations();
				for (j = 0; j < n2; j++) {
					decl = module.getDeclaration(j);
					v = new Value(decl.getName(), decl.getType(), "");
					initValuesModel.addValue(v);
				}
			}

		} else {
			for (int i = 0; i < initialState.getNumValues(); i++) {
				v = new Value(initialState.getName(i), initialState.getType(i), initialState.getValue(i));
				initValuesModel.addValue(v);
			}
		}
	}

	/** Call this static method to construct a new GUIValuesPicker to define
	 *  initialState.  If you don't want any default values, then pass in null for
	 *  initDefaults
	 */
	public static Values defineInitalValuesWithDialog(GUIPrism parent, Values initDefaults, ModulesFile mf)
	{
		return new GUIInitialStatePicker(parent, initDefaults, mf).defineValues();
	}

	public Values defineValues()
	{
		setVisible(true);
		if (cancelled)
			return null;
		else
			return initialState;
	}

	private void okayButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okayButtonActionPerformed
	{//GEN-HEADEREND:event_okayButtonActionPerformed
		if (initValuesTable.getCellEditor() != null)
			initValuesTable.getCellEditor().stopCellEditing();

		String parameter = "";
		try {
			Values newInitState = new Values();
			// check each variable value
			for (int i = 0; i < initValuesModel.getNumValues(); i++) {
				parameter = initValuesModel.getValue(i).name;
				Object parameterValue = null;
				if (initValuesModel.getValue(i).type instanceof TypeBool) {
					String bool = initValuesModel.getValue(i).value.toString();
					if (!(bool.equals("true") || bool.equals("false")))
						throw new NumberFormatException();
					parameterValue = new Boolean(bool);
				} else if (initValuesModel.getValue(i).type instanceof TypeInt) {
					parameterValue = Integer.valueOf(initValuesModel.getValue(i).value.toString());
				} else {
					throw new NumberFormatException();
				}
				newInitState.addValue(parameter, parameterValue);
			}
			initialState = newInitState;
			cancelled = false;
			dispose();
		} catch (NumberFormatException e) {
			gui.errorDialog("Invalid number value entered for " + parameter + " parameter");
		}
	}//GEN-LAST:event_okayButtonActionPerformed

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
	{//GEN-HEADEREND:event_cancelButtonActionPerformed
		dispose();
	}//GEN-LAST:event_cancelButtonActionPerformed

	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)//GEN-FIRST:event_closeDialog
	{
		setVisible(false);
		dispose();
	}//GEN-LAST:event_closeDialog

	public void keyPressed(KeyEvent e)
	{
	}

	public void keyReleased(KeyEvent e)
	{

	}

	public void keyTyped(KeyEvent e)
	{

	}

	class DefineValuesTable extends AbstractTableModel
	{
		ArrayList<Value> values;

		public DefineValuesTable()
		{
			values = new ArrayList<Value>();
		}

		public void addValue(Value v)
		{
			values.add(v);
			fireTableRowsInserted(values.size() - 1, values.size() - 1);
		}

		public int getNumValues()
		{
			return values.size();
		}

		public Value getValue(int i)
		{
			return values.get(i);
		}

		public int getColumnCount()
		{
			return 3;
		}

		public int getRowCount()
		{
			return values.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{

			Value v = values.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return v.name;
			case 1:
				return v.type.getTypeString();
			case 2:
				return v.value.toString();
			default:
				return "";
			}
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex) {
			case 0:
				return "Name";
			case 1:
				return "Type";
			case 2:
				return "Value";
			default:
				return "";
			}
		}

		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			if (columnIndex == 2)
				return true;
			else
				return false;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (columnIndex == 2) {
				Value v = values.get(rowIndex);
				String s = (String) aValue;
				v.value = s;
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}

		public String toString()
		{
			String str = "";
			for (int i = 0; i < values.size(); i++) {
				Value c = values.get(i);
				str += c.toString();
				if (i != values.size() - 1)
					str += ",";
			}
			return str;
		}

	}

	class Value
	{
		String name;
		parser.type.Type type;
		Object value;

		public Value(String name, parser.type.Type type, Object value)
		{
			this.name = name;
			this.type = type;
			this.value = value;
		}

		public String toString()
		{
			return name + "=" + value.toString();
		}
	}
}
