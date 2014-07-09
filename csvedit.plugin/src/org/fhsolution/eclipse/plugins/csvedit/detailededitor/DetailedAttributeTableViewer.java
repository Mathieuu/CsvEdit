/* Copyright 2011 csvedit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fhsolution.eclipse.plugins.csvedit.detailededitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;

/**
 * Class providing methods to add a custom tableViewer to a Composite
 * @author msavy
 * 
 */
public class DetailedAttributeTableViewer {
	
	protected TableViewer tableViewer;
	protected List<AttributeRow> model = new ArrayList<AttributeRow>();
	
	IAttributeChangesListener listener = new IAttributeChangesListener() {
		public void rowChanged(AttributeRow row, int index) {
			tableViewer.refresh();
		}
	};
	
    /**
     * Default constructor
     * Add an empty TableViewer to a Composite view 
     */
	public DetailedAttributeTableViewer(Composite composite) {
		
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION  | SWT.MULTI);
		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.minimumHeight = 100;
		table.setLayoutData(gd);
	}
	
    /**
     * Fill the table
     * @param headers
     * @param content  a List<String[]> where each String[] is a column of the table
     */
	public void fillTable(List<String> headers, List<String[]> content){
		
		for (int i = 0; i < headers.size(); i++) {
			final TableViewerColumn column = new TableViewerColumn(tableViewer,
					SWT.LEFT);

			column.getColumn().setText(headers.get(i));
			column.getColumn().setWidth(200);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
			
			final int index = i;
			
			column.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((AttributeRow)element).getElementAt(index);
				}
			});
		}
			
		model = new ArrayList<AttributeRow>();
		
		if(content.size() > 0){
			//We first look for the biggest column, it will be the reference to generate all AttributeRow
			int biggestColumn = 0;
			
			for(int i = 0; i < content.size(); i++){
				if(content.get(biggestColumn).length < content.get(i).length){
					biggestColumn = i;
				}
			}
			
			//For each row of the biggest column (String[]), we look for the rows
			//at the same level in other columns and we build a AttributeRow with those data
			for(int i = 0; i < content.get(biggestColumn).length; i++){
				List<String> attributes = new ArrayList<String>();
				for(int j = 0; j < content.size(); j++){
					if(i < content.get(j).length){
						attributes.add(content.get(j)[i]);
					} else {
						attributes.add("");
					}
				}
				model.add(new AttributeRow(attributes, listener));
			}
		}
		
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(model);

		defineCellEditing(tableViewer, headers.size());
		addRightClickItems(tableViewer);
	}
	
    /**
     * Make cells of the tableViewer editable
     */
	private void defineCellEditing(TableViewer tableViewer, int numberOfColumns) {
		String[] columnProperties = new String[numberOfColumns];
		CellEditor[] cellEditors = new CellEditor[numberOfColumns];

		for (int i = 0; i < numberOfColumns; i++) {
			columnProperties[i] = Integer.toString(i);
			cellEditors[i] = new TextCellEditor(tableViewer.getTable());
		}

		tableViewer.setColumnProperties(columnProperties);

		tableViewer.setCellEditors(cellEditors);
		tableViewer.setCellModifier(new AttributeEditorCellModifier());

	}
	
    /**
     * Describe the behavior of right click on the tableViewer
     */
	private void addRightClickItems(final TableViewer tableViewer){
		
		// create menu item to delete column
		final Menu tableHeaderMenu = new Menu(tableViewer.getTable());
		
		// create menu item to insert column
		final MenuItem insertColumnItem = new MenuItem(tableHeaderMenu,
				SWT.PUSH);
		insertColumnItem.setText("Add row");
		insertColumnItem.setSelection(false);
		insertColumnItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// call insert/add column page
				AttributeRow row = (AttributeRow) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
				
				IAttributeChangesListener listener = new IAttributeChangesListener() {
					public void rowChanged(AttributeRow row, int index) {
						// TODO Auto-generated method stub
						tableViewer.refresh();
					}
				};
				
				List<String> emptyAttributes = new ArrayList<String>();
				for(int i = 0; i < tableViewer.getTable().getColumnCount(); i++){
					emptyAttributes.add("#TO_FILL#");
				}
				
				AttributeRow emptyRow = new AttributeRow(emptyAttributes, listener);
				
				if (row != null) {
					model.add(model.indexOf(row) + 1, emptyRow);
				} else {
					model.add(emptyRow);
				}
				
				tableViewer.refresh();
			}
		});
				
		final MenuItem deleteColumnItem = new MenuItem(tableHeaderMenu,
				SWT.PUSH);
		deleteColumnItem.setText("Delete Row");
		deleteColumnItem.setSelection(false);
		deleteColumnItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				
				AttributeRow row = (AttributeRow) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
				
				while(row != null){
					row = (AttributeRow) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
					if (row != null) {
						model.remove(model.indexOf(row));
						tableViewer.refresh();
					}
				}
			}
		});
		
		//Link to the tableViewer
		tableViewer.getTable().addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				tableViewer.getTable().setMenu(tableHeaderMenu);
				tableViewer.refresh();
			}
		});
	}
	
	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public List<AttributeRow> getModel() {
		return model;
	}

	public void setModele(List<AttributeRow> model) {
		this.model = model;
	}
}
