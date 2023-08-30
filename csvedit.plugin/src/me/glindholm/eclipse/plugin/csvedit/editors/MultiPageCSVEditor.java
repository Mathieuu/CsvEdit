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
package me.glindholm.eclipse.plugin.csvedit.editors;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

import me.glindholm.eclipse.plugin.csvedit.detailededitor.DetailedEditor;
import me.glindholm.eclipse.plugin.csvedit.editors.text.CSVTextEditor;
import me.glindholm.eclipse.plugin.csvedit.filter.CSVTableFilter;
import me.glindholm.eclipse.plugin.csvedit.model.AbstractCSVFile;
import me.glindholm.eclipse.plugin.csvedit.model.CSVRow;
import me.glindholm.eclipse.plugin.csvedit.model.ICsvFileModelListener;
import me.glindholm.eclipse.plugin.csvedit.page.DeleteColumnPage;
import me.glindholm.eclipse.plugin.csvedit.page.InsertColumnPage;
import me.glindholm.eclipse.plugin.csvedit.providers.CSVContentProvider;
import me.glindholm.eclipse.plugin.csvedit.providers.CSVLabelProvider;
import me.glindholm.eclipse.plugin.csvedit.sorter.CSVTableSorter;

/**
 *
 * @author fhenri
 * @author msavy
 *
 */
public abstract class MultiPageCSVEditor extends MultiPageEditorPart implements IResourceChangeListener {

    private boolean isPageModified;

    /** index of the source page */
    public static final int indexSRC = 1;
    /** index of the table page */
    public static final int indexTBL = 0;

    /** The text editor used in page 0. */
    protected TextEditor editor;

    /** The table viewer used in page 1. */
    protected TableViewer tableViewer;

    private CSVTableSorter tableSorter;

    private Menu tableHeaderMenu;

    private final AbstractCSVFile model;

    /**
     *
     */
    private final ICsvFileModelListener csvFileListener = (row, rowIndex) -> tableModified();

    /**
     * Creates a multi-page editor example.
     */
    public MultiPageCSVEditor() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
        model = createCSVFile();
    }

    /**
     * Create the CSV file object. Class that extends the MultiPageCSVEditor <i>must</i> implement this
     * class.
     *
     * @return an {@link AbstractCSVFile} object which provides the contents as well as some formatting
     *         information such as the delimiter and extra meta information
     */
    protected abstract AbstractCSVFile createCSVFile();

    /**
     * Creates the pages of the multi-page editor.
     *
     * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
     */
    @Override
    protected void createPages() {
        try {
            createTablePage();
            createSourcePage();
            updateTitle();
            populateTablePage();
        } catch (final Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    /**
     * Creates page 0 of the multi-page editor, which contains a text editor.
     */
    private void createSourcePage() {
        try {
            editor = new CSVTextEditor(model.getCustomDelimiter());
            addPage(editor, getEditorInput());
            setPageText(indexSRC, "CSV Source");
        } catch (final PartInitException e) {
            ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
        }
    }

    /**
     *
     */
    private void createTablePage() {
        final Composite parent = getContainer();

        // XXX move all the creation into its own component
        final Canvas canvas = new Canvas(parent, SWT.None);

        final GridLayout layout = new GridLayout(6, false);
        canvas.setLayout(layout);

        // create the header part with the search function and Add/Delete rows
        final Label searchLabel = new Label(canvas, SWT.NONE);
        searchLabel.setText("Filter: ");
        final Text searchText = new Text(canvas, SWT.BORDER | SWT.SEARCH);
        searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        // Create and configure the buttons
        final Button duplicate = new Button(canvas, SWT.PUSH | SWT.CENTER);
        duplicate.setText("Duplicate");
        duplicate.setToolTipText("Duplicate the current row");
        final GridData buttonDuplicateGridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        buttonDuplicateGridData.widthHint = 80;
        duplicate.setLayoutData(buttonDuplicateGridData);
        duplicate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final CSVRow row = (CSVRow) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
                if (row != null) {
                    model.duplicateRow(row);
                    tableModified();
                }
            }
        });

        final Button insert = new Button(canvas, SWT.PUSH | SWT.CENTER);
        insert.setText("Insert Row");
        insert.setToolTipText("Insert a new row before the current one");
        final GridData buttonInsertGridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        buttonInsertGridData.widthHint = 80;
        insert.setLayoutData(buttonInsertGridData);
        insert.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final CSVRow row = (CSVRow) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
                if (row != null) {
                    model.addRowAfterElement(row);
                    tableModified();
                }
            }
        });
        /*
         * insert.addKeyListener(new KeyAdapter() { public void keyPressed(KeyEvent e) { //if(((e.stateMask
         * & SWT.CTRL) != 0) & (e.keyCode == 'd')) { //if (e.stateMask == SWT.CTRL && e.keyCode == 'd') { if
         * (e.character == SWT.DEL) { CSVRow row = (CSVRow) ((IStructuredSelection)
         * tableViewer.getSelection()).getFirstElement(); if (row != null) { model.addLineAfterElement(row);
         * tableViewer.refresh(); tableModified(); } } } });
         */

        final Button add = new Button(canvas, SWT.PUSH | SWT.CENTER);
        add.setText("Add Row");
        add.setToolTipText("Add a new row at the end of the file");
        final GridData buttonAddGridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        buttonAddGridData.widthHint = 80;
        add.setLayoutData(buttonAddGridData);
        add.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                model.addRow();
                tableModified();
            }
        });

        final Button delete = new Button(canvas, SWT.PUSH | SWT.CENTER);
        delete.setText("Delete Row");
        delete.setToolTipText("Delete the current row");
        final GridData buttonDelGridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        buttonDelGridData.widthHint = 80;
        delete.setLayoutData(buttonDelGridData);
        delete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                CSVRow row = (CSVRow) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();

                while (row != null) {
                    row = (CSVRow) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
                    if (row != null) {
                        model.removeRow(row);
                        tableModified();
                    }
                }
            }
        });
        /*
         * insert.addKeyListener(new KeyAdapter() { public void keyPressed(KeyEvent e) { if (e.stateMask ==
         * SWT.CTRL && e.keyCode == 'd') { CSVRow row = (CSVRow) ((IStructuredSelection)
         * tableViewer.getSelection()).getFirstElement(); if (row != null) { model.removeLine(row);
         * tableViewer.refresh(); tableModified(); } } } });
         */
        /*
         *
         * // manage 1st line - should only be visible if global option is set if
         * (pref.getUseFirstLineAsHeader()) { Label encodingLineLabel = new Label(canvas, SWT.NONE);
         * encodingLineLabel.setText("Display 1st line"); final Button encodingLineBtn = new Button(canvas,
         * SWT.CHECK); encodingLineBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
         * encodingLineBtn.setSelection(true); encodingLineBtn.addSelectionListener(new SelectionAdapter() {
         * public void widgetSelected(SelectionEvent e) {
         * model.displayFirstLine(encodingLineBtn.getSelection()); updateTableFromTextEditor(); } }); }
         * sensitiveBtn.addSelectionListener(new SelectionAdapter() { public void
         * widgetSelected(SelectionEvent e) { tableFilter.setSearchText(searchText.getText(),
         * sensitiveBtn.getSelection()); labelProvider.setSearchText(searchText.getText());
         * tableViewer.refresh(); } });
         */
        tableViewer = new TableViewer(canvas, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        tableViewer.setUseHashlookup(true);
        final Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        // set the sorter for the table
        tableSorter = new CSVTableSorter();
        tableViewer.setComparator(tableSorter);

        // set a table filter
        final CSVTableFilter tableFilter = new CSVTableFilter();
        tableViewer.addFilter(tableFilter);

        // add the filtering and coloring when searching specific elements.
        searchText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent ke) {
                tableFilter.setSearchText(searchText.getText(), model.getSensitiveSearch());
                final String filterText = searchText.getText();
                for (int i = 0; i < tableViewer.getColumnProperties().length; i++) {
                    final CellLabelProvider labelProvider = tableViewer.getLabelProvider(i);
                    if (labelProvider != null) {
                        ((CSVLabelProvider) labelProvider).setSearchText(filterText);
                    }
                }
                tableViewer.refresh();
            }
        });

        /*
         * // create a TableCursor to navigate around the table final TableCursor cursor = new
         * TableCursor(table, SWT.NONE); // create an editor to edit the cell when the user hits "ENTER" //
         * while over a cell in the table final ControlEditor editor = new ControlEditor(cursor);
         * editor.grabHorizontal = true; editor.grabVertical = true;
         *
         * cursor.addSelectionListener(new SelectionAdapter() { // This is called as the user navigates
         * around the table public void widgetSelected(SelectionEvent e) { // Select the row in the table
         * where the TableCursor is table.setSelection(new TableItem[] {cursor.getRow()}); }
         *
         * // when the user hits "ENTER" in the TableCursor, // pop up a text editor so that user can change
         * the text of the cell public void widgetDefaultSelected(SelectionEvent e) { // Begin an editing
         * session final Text text = new Text(cursor, SWT.NONE);
         *
         * // Copy the text from the cell to the Text int column = cursor.getColumn();
         * text.setText(cursor.getRow().getText(column));
         *
         * // Add a handler to detect key presses text.addKeyListener(new KeyAdapter() { public void
         * keyPressed(KeyEvent e) { // tab will save & move to the next column if (e.character == SWT.TAB) {
         * TableItem row = cursor.getRow(); int column = cursor.getColumn(); row.setText(column,
         * text.getText()); text.dispose(); cursor.setSelection(row, column+1); tableModified(); } // close
         * the text editor and copy the data over // when the user hits "ENTER" if (e.character == SWT.CR) {
         * TableItem row = cursor.getRow(); row.setText(cursor.getColumn(), text.getText());
         * tableModified(); text.dispose(); } // close the text editor when the user hits "ESC" if
         * (e.character == SWT.ESC) { text.dispose(); } } }); // close the text editor when the user tabs
         * away text.addFocusListener(new FocusAdapter() { public void focusLost(FocusEvent e) {
         * text.dispose(); } }); editor.setEditor(text); text.setFocus(); } });
         *
         * /* // Hide the TableCursor when the user hits the "CTRL" or "SHIFT" key. // This allows the user
         * to select multiple items in the table. cursor.addKeyListener(new KeyAdapter() { public void
         * keyPressed(KeyEvent e) {
         *
         * // delete line if (e.character == SWT.DEL) { TableItem row = cursor.getRow(); tableModified();
         * row.dispose(); //table.showItem(row); //cursor.setSelection(row, 0); }
         *
         * // insert line if (e.character == (char) SWT.F8) { TableItem row = cursor.getRow();
         * row.dispose(); }
         *
         * // add line
         *
         * cursor.setVisible(true); cursor.setFocus();
         *
         * if (e.keyCode == SWT.CTRL || e.keyCode == SWT.SHIFT || (e.stateMask & SWT.CONTROL) != 0 ||
         * (e.stateMask & SWT.SHIFT) != 0) { cursor.setVisible(false); return; } } });
         *
         * // When the user double clicks in the TableCursor, pop up a text editor so that // they can
         * change the text of the cell. cursor.addMouseListener(new MouseAdapter() { public void
         * mouseDown(MouseEvent e) { final Text text = new Text(cursor, SWT.NONE); TableItem row =
         * cursor.getRow(); int column = cursor.getColumn(); text.setText(row.getText(column));
         * text.addKeyListener(new KeyAdapter() { public void keyPressed(KeyEvent e) { // close the text
         * editor and copy the data over // when the user hits "ENTER" if (e.character == SWT.CR) {
         * TableItem row = cursor.getRow(); int column = cursor.getColumn(); row.setText(column,
         * text.getText()); tableModified(); text.dispose(); } // close the text editor when the user hits
         * "ESC" if (e.character == SWT.ESC) { text.dispose(); } } }); // close the text editor when the
         * user clicks away text.addFocusListener(new FocusAdapter() { public void focusLost(FocusEvent e) {
         * text.dispose(); } }); editor.setEditor(text); text.setFocus(); } });
         *
         * // Show the TableCursor when the user releases the "SHIFT" or "CTRL" key. // This signals the end
         * of the multiple selection task. table.addKeyListener(new KeyAdapter() { public void
         * keyReleased(KeyEvent e) {
         *
         * if (e.keyCode == SWT.CONTROL && (e.stateMask & SWT.SHIFT) != 0) return; if (e.keyCode ==
         * SWT.SHIFT && (e.stateMask & SWT.CONTROL) != 0) return; if (e.keyCode != SWT.CONTROL &&
         * (e.stateMask & SWT.CONTROL) != 0) return; if (e.keyCode != SWT.SHIFT && (e.stateMask & SWT.SHIFT)
         * != 0) return;
         *
         * TableItem[] selection = table.getSelection(); TableItem row = (selection.length == 0) ?
         * table.getItem(table.getTopIndex()) : selection[0]; table.showItem(row); cursor.setSelection(row,
         * 0); cursor.setVisible(true); cursor.setFocus(); } });
         */

        /*
         * tableViewer.addDoubleClickListener(new IDoubleClickListener() {
         *
         * public void doubleClick(DoubleClickEvent event) {
         *
         * CSVRow row = (CSVRow) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
         * DetailedView input = new DetailedView(Display.getDefault(), model.getHeader(), row);
         *
         * input.open();
         *
         * } });
         */

        // Layout the viewer
        final GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 6;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        tableViewer.getControl().setLayoutData(gridData);

        addPage(canvas);
        setPageText(indexTBL, "CSV Table");
    }

    /**
     * Set Name of the file to the tab
     */
    private void updateTitle() {
        final IEditorInput input = getEditorInput();
        setPartName(input.getName());
        setTitleToolTip(input.getToolTipText());
    }

    /**
     * @throws Exception
     */
    private void populateTablePage() throws Exception {
        tableViewer.setContentProvider(new CSVContentProvider());

        // make the selection available
        getSite().setSelectionProvider(tableViewer);

        tableViewer.getTable().getDisplay().asyncExec(this::updateTableFromTextEditor);
    }

    /**
     *
     */
    public void tableModified() {
        tableViewer.refresh();
        final boolean wasPageModified = isPageModified;
        isPageModified = true;
        if (!wasPageModified) {
            firePropertyChange(IEditorPart.PROP_DIRTY);
            editor.validateEditorInputState(); // will invoke:
                                               // FileModificationValidator.validateEdit()
                                               // (expected by some repository
                                               // providers)
        }
    }

    /**
     *
     */
    private void updateTableFromTextEditor() {

        tableHeaderMenu = new Menu(tableViewer.getTable());

        // PropertyFile propertyFile = (PropertyFile) treeViewer.getInput();
        model.removeModelListener(csvFileListener);

        model.setInput(editor.getDocumentProvider().getDocument(editor.getEditorInput()).get());

        final MenuItem detailedEditItem = new MenuItem(tableHeaderMenu, SWT.PUSH, 0);
        detailedEditItem.setText("Edit");
        detailedEditItem.setSelection(false);
        detailedEditItem.addListener(SWT.Selection, event -> {

            final CSVRow row = (CSVRow) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();

            if (row != null) {
                final DetailedEditor input = new DetailedEditor(Display.getDefault(), model.getHeader(), row, model.getInCellDelimiter(),
                        model.getRegexTableMarker());
                input.open();
            }
        });

        new MenuItem(tableHeaderMenu, SWT.SEPARATOR, 1);

        final TableColumn[] columns = tableViewer.getTable().getColumns();
        if (columns.length > 0) { // if table header columns already created
            // update column header text
            for (int i = 0; i < model.getHeader().size(); i++) {
                if (i < columns.length) {
                    columns[i].setText(model.getHeader().get(i));
                    final int index = i;
                    addMenuItemToColumn(columns[i], index + 2);
                }
            }
        } else {
            // create columns
            for (int i = 0; i < model.getHeader().size(); i++) {
                final TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.LEFT);
                final int index = i;
                column.getColumn().setText(model.getHeader().get(i));
                column.getColumn().setWidth(100);
                column.getColumn().setResizable(true);
                column.getColumn().setMoveable(true);
                column.setLabelProvider(new CSVLabelProvider());
                // +2 because the first two items are Edit and a separator
                addMenuItemToColumn(column.getColumn(), index + 2);
            }
        }

        if (model.isFirstLineHeader()) {
            new MenuItem(tableHeaderMenu, SWT.SEPARATOR);

            // create menu item to delete column
            final MenuItem deleteColumnItem = new MenuItem(tableHeaderMenu, SWT.PUSH);
            deleteColumnItem.setText("Delete Column");
            deleteColumnItem.setSelection(false);
            deleteColumnItem.addListener(SWT.Selection, event -> {
                // call delete column page
                final DeleteColumnPage dcPage = new DeleteColumnPage(getSite().getShell(), model.getArrayHeader());
                if (dcPage.open() == Window.OK) {
                    final String[] colToDelete = dcPage.getColumnSelected();
                    for (final String column : colToDelete) {
                        final int colIndex = findColumnForName(column);
                        tableViewer.getTable().getColumn(colIndex).dispose();
                        // +2 because the first two items are Edit and a separator
                        tableHeaderMenu.getItem(colIndex + 2).dispose();
                        model.removeColumn(column);
                    }
                    tableModified();
                }
            });

            // create menu item to insert column
            final MenuItem insertColumnItem = new MenuItem(tableHeaderMenu, SWT.PUSH);
            insertColumnItem.setText("Add Column");
            insertColumnItem.setSelection(false);
            insertColumnItem.addListener(SWT.Selection, event -> {
                // call insert/add column page
                final InsertColumnPage acPage = new InsertColumnPage(getSite().getShell(), model.getArrayHeader());
                if (acPage.open() == Window.OK) {
                    final String colToInsert = acPage.getColumnNewName();
                    model.addColumn(colToInsert);

                    tableViewer.setInput(model);
                    final TableColumn column = new TableColumn(tableViewer.getTable(), SWT.LEFT);
                    column.setText(colToInsert);
                    column.setWidth(100);
                    column.setResizable(true);
                    column.setMoveable(true);

                    addMenuItemToColumn(column, model.getColumnCount() - 1);
                    defineCellEditing();

                    tableModified();
                }
            });
        }

        tableViewer.setInput(model);
        model.addModelListener(csvFileListener);

        tableViewer.getTable().addListener(SWT.MenuDetect, event -> tableViewer.getTable().setMenu(tableHeaderMenu));

        defineCellEditing();
    }

    /**
     *
     */
    private void defineCellEditing() {
        final String[] columnProperties = new String[model.getColumnCount()];
        final CellEditor[] cellEditors = new CellEditor[model.getColumnCount()];

        for (int i = 0; i < model.getColumnCount(); i++) {
            columnProperties[i] = Integer.toString(i);
            cellEditors[i] = new TextCellEditor(tableViewer.getTable());
        }

        tableViewer.setColumnProperties(columnProperties);

        // XXX can be replaced by tableViewer.setEditingSupport()
        tableViewer.setCellEditors(cellEditors);
        tableViewer.setCellModifier(new CSVEditorCellModifier());

    }

    /**
     * Find a column in the Table by its name
     *
     * @param columnName
     * @return the index of the Column indicated by its name
     */
    private int findColumnForName(final String columnName) {
        final int index = -1;
        final TableColumn[] tableColumns = tableViewer.getTable().getColumns();
        for (int i = 0; i < tableColumns.length; i++) {
            final TableColumn column = tableColumns[i];
            if (columnName.equalsIgnoreCase(column.getText())) {
                return i;
            }
        }
        return index;
    }

    /**
     * @param column
     * @param index
     */
    private void addMenuItemToColumn(final TableColumn column, final int index) {

        // create menu item
        final MenuItem itemName = new MenuItem(tableHeaderMenu, SWT.CHECK, index);
        itemName.setText(column.getText());
        itemName.setSelection(column.getResizable());
        itemName.addListener(SWT.Selection, event -> {
            if (itemName.getSelection()) {
                column.setWidth(100);
                column.setResizable(true);
            } else {
                column.setWidth(0);
                column.setResizable(false);
            }
        });

        // Setting the right sorter
        column.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                int dir = tableViewer.getTable().getSortDirection();
                switch (dir) {
                case SWT.UP:
                    dir = SWT.DOWN;
                    break;
                case SWT.DOWN:
                    dir = SWT.NONE;
                    break;
                case SWT.NONE:
                    dir = SWT.UP;
                    break;
                }
                tableSorter.setColumn(index, dir);
                tableViewer.getTable().setSortDirection(dir);
                if (dir == SWT.NONE) {
                    tableViewer.getTable().setSortColumn(null);
                } else {
                    tableViewer.getTable().setSortColumn(column);
                }
                tableViewer.refresh();
            }
        });

    }

    /**
     * The <code>MultiPageEditorPart</code> implementation of this <code>IWorkbenchPart</code> method
     * disposes all nested editors. This method is automatically called when the editor is closed and
     * marks the end of the editor's life cycle. It cleans up any platform resources, such as images,
     * clipboard, and so on, which were created by this class.
     *
     * @see org.eclipse.ui.part.MultiPageEditorPart#dispose()
     */
    @Override
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        super.dispose();
    }

    /**
     * Saves the multi-page editor's document. If the save is successful, the part should fire a
     * property changed event (PROP_DIRTY property), reflecting the new dirty state. If the save is
     * canceled via user action, or for any other reason, the part should invoke setCanceled on the
     * IProgressMonitor to inform the caller
     *
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(final IProgressMonitor monitor) {
        if (getActivePage() == indexTBL && isPageModified) {
            updateTextEditorFromTable();
        } else {
            updateTableFromTextEditor();
        }

        isPageModified = false;
        editor.doSave(monitor);
    }

    /**
     * Returns whether the "Save As" operation is supported by this part.
     *
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    /**
     * Saves the multi-page editor's document as another file. Also updates the text for page 0's tab,
     * and updates this multi-page editor's input to correspond to the nested editor's.
     *
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        if (getActivePage() == indexTBL && isPageModified) {
            updateTextEditorFromTable();
        } else {
            updateTableFromTextEditor();
        }

        isPageModified = false;

        editor.doSaveAs();
        setInput(editor.getEditorInput());
        updateTitle();
    }

    /**
     * Initializes this editor with the given editor site and input. This method is automatically called
     * shortly after editor construction; it marks the start of the editor's lifecycle.
     *
     * The <code>MultiPageEditorExample</code> implementation of this method checks that the input is an
     * instance of <code>IFileEditorInput</code>.
     *
     * @see org.eclipse.ui.part.MultiPageEditorPart#init(org.eclipse.ui.IEditorSite,
     *      org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(final IEditorSite site, final IEditorInput editorInput) throws PartInitException {
        /*
         * String message = "Input is " + editorInput + " of instance " + editorInput.getClass().getName();
         * IStatus status = new Status(IStatus.ERROR, "csvedit", IStatus.ERROR, message, null);
         * Activator.getDefault().getLog().log(status);
         */

        /*
         * if (!(editorInput instanceof IFileEditorInput)) throw new
         * PartInitException("Invalid Input: Must be IFileEditorInput");
         */
        super.init(site, editorInput);
    }

    /**
     * @see org.eclipse.ui.part.MultiPageEditorPart#handlePropertyChange(int)
     */
    @Override
    protected void handlePropertyChange(final int propertyId) {
        if (propertyId == IEditorPart.PROP_DIRTY) {
            isPageModified = isDirty();
        }
        super.handlePropertyChange(propertyId);
    }

    /**
     * @see org.eclipse.ui.part.MultiPageEditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return isPageModified || super.isDirty();
    }

    /**
     * Calculates the contents of page 2 when the it is activated.
     *
     * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
     */
    @Override
    protected void pageChange(final int newPageIndex) {
        switch (newPageIndex) {
        case indexSRC:
            if (isDirty()) {
                updateTextEditorFromTable();
            }
            break;
        case indexTBL:
            if (isDirty()) {
                updateTableFromTextEditor();
            }
            break;
        }
        isPageModified = false;
        super.pageChange(newPageIndex);
    }

    /**
     *
     */
    private void updateTextEditorFromTable() {
        editor.getDocumentProvider().getDocument(editor.getEditorInput()).set(((AbstractCSVFile) tableViewer.getInput()).getTextRepresentation());
    }

    /**
     * When the focus shifts to the editor, this method is called; it must then redirect focus to the
     * appropriate editor based on which page is currently selected.
     *
     * @see org.eclipse.ui.part.MultiPageEditorPart#setFocus()
     */
    @Override
    public void setFocus() {
        switch (getActivePage()) {
        case indexSRC:
            editor.setFocus();
            break;
        case indexTBL:
            tableViewer.getTable().setFocus();
            break;
        }
    }

    /**
     * Closes all project files on project close.
     *
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
        if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
            Display.getDefault().asyncExec(() -> {
                final IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
                for (final IWorkbenchPage page : pages) {
                    if (((FileEditorInput) editor.getEditorInput()).getFile().getProject().equals(event.getResource())) {
                        final IEditorPart editorPart = page.findEditor(editor.getEditorInput());
                        page.closeEditor(editorPart, true);
                    }
                }
            });
        }
    }
}
