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

package me.glindholm.eclipse.plugin.csvedit.detailededitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import me.glindholm.eclipse.plugin.csvedit.model.CSVRow;

/**
 * Class providing methods to add a custom tableViewer to a Composite
 *
 * @author msavy
 * 
 */
public class DetailedEditor {

    Shell shell;
    protected List<Object> componentList = new ArrayList<>();

    protected CSVRow row; // The row to modify
    protected List<String> headerList; // A list of the headers for each column
                                       // of the row

    protected String inCellDelimiter;
    protected String regexTableMarker;

    final static String UNKNOWN_URL_FORMAT = "_UNKNOWN_URL_FORMAT:";

    public Shell getShell() {
        return shell;
    }

    public void setShell(final Shell shell) {
        this.shell = shell;
    }

    /**
     * Default constructor, open a new window
     * 
     */
    public DetailedEditor(final Display display, final List<String> headerList, final CSVRow row, final String inCellDelimiter, final String regexTableMarker) {

        shell = new Shell(display);
        shell.setLayout(new FillLayout());
        shell.setText("Detailed CSV edition");

        shell.setSize(800, 600);

        // If there is more columns for the header than there is for the column,
        // we had empty columns in the row
        // It happen when the row's last columns are empty and separators are
        // forgotten.
        // The CSV reader can't guess there is a column to create
        if (row.getNumberOfElements() > 0 && headerList.size() > row.getNumberOfElements()) {
            for (int i = row.getNumberOfElements() - 1; i < headerList.size(); i++) {
                row.addElement("");
            }
        }

        this.headerList = headerList;
        this.row = row;

        this.inCellDelimiter = inCellDelimiter;
        this.regexTableMarker = regexTableMarker;

        final ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        final Composite composite = new Composite(scrolledComposite, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));

        generateComponents(composite);
        new Label(composite, SWT.NONE);

        final Composite compositeBtn = new Composite(composite, SWT.NONE);
        final GridData gd_compositeBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_compositeBtn.heightHint = 55;
        gd_compositeBtn.widthHint = 220;
        compositeBtn.setLayoutData(gd_compositeBtn);

        final Button btnApply = new Button(compositeBtn, SWT.NONE);
        btnApply.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
            }
        });
        btnApply.setBounds(19, 14, 75, 30);
        btnApply.addListener(SWT.Selection, e -> {
            switch (e.type) {
            case SWT.Selection:
                updateRow();
                shell.dispose();
                break;
            }
        });
        btnApply.setText("Apply");

        final Button btnCancel = new Button(compositeBtn, SWT.NONE);
        btnCancel.setBounds(141, 14, 75, 30);
        btnCancel.addListener(SWT.Selection, e -> {
            switch (e.type) {
            case SWT.Selection:
                shell.dispose();
                break;
            }
        });
        btnCancel.setText("Cancel");

        shell.setMinimumSize(400, 200);

        scrolledComposite.setContent(composite);
        scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // The components must be filled only after the scrolled composite size
        // has been set
        // Otherwise the size of the scrolled composite would adapt its size to
        // the content of its components
        fillComponents();
    }

    /**
     * Generates components of the DetailedView depending of their type
     * 
     */
    private void generateComponents(final Composite composite) {

        for (int i = 0; i < headerList.size() && !headerList.get(i).equals(""); i++) {

            final Label label = new Label(composite, SWT.NONE);
            label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

            if (headerList.get(i).matches(regexTableMarker)) {
                final DetailedAttributeTableViewer tableViewer = new DetailedAttributeTableViewer(composite);
                componentList.add(tableViewer);

            } else {
                label.setText(headerList.get(i));

                final Text text = new Text(composite, SWT.BORDER);
                text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
                componentList.add(text);
            }
        }
    }

    /**
     * Fills components according to data provided by the field attribute "row"
     * 
     */
    private void fillComponents() {

        for (int i = 0; i < componentList.size(); i++) {

            // Fill text components
            if (componentList.get(i) instanceof Text) {
                ((Text) componentList.get(i)).setText(row.getElementAt(i));

                // Fill table viewer components
            } else if (componentList.get(i) instanceof DetailedAttributeTableViewer) {

                final List<String> headers = new ArrayList<>();
                headers.add(headerList.get(i));

                final String column1[] = row.getElementAt(i).split("[^|]\\|[^|]");
                for (int j = 0; j < column1.length; j++) {
                    column1[j] = column1[j].trim();
                }

                final List<String[]> content = new ArrayList<>();
                if (!(column1.length == 1 && column1[0].equals(""))) {
                    content.add(column1);
                }
                ((DetailedAttributeTableViewer) componentList.get(i)).fillTable(headers, content);
            }
        }
    }

    private void updateRow() {

        for (int i = 0; i < headerList.size() && !headerList.get(i).equals(""); i++) {

            if (componentList.get(i) instanceof Text) {
                row.setRowEntry(i, ((Text) componentList.get(i)).getText());

                // if update source is a table viewer
            } else if (componentList.get(i) instanceof DetailedAttributeTableViewer) {

                final List<AttributeRow> model = ((DetailedAttributeTableViewer) componentList.get(i)).getModel();
                final StringBuilder updatedStr = new StringBuilder();

                for (final AttributeRow atts : model) {

                    if (atts.getElementAt(0).equals("")) {
                        continue;
                    }

                    updatedStr.append(atts.getElementAt(0).trim());
                    updatedStr.append(" | ");
                }

                String finalStr = updatedStr.toString();
                if (finalStr.length() > 0 && finalStr.substring(finalStr.length() - 3).equals(" | ")) {
                    finalStr = finalStr.substring(0, finalStr.length() - 3);
                }

                row.setRowEntry(i, finalStr);
            }
        }
    }

    public void open() {
        shell.open();
    }
}