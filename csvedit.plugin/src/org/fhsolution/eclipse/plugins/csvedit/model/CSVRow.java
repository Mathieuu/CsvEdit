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
package org.fhsolution.eclipse.plugins.csvedit.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a row made of String elements
 *
 * @author fhenri
 *
 */
public class CSVRow {

    /** Splitted line */
    private final ArrayList<String> entries;

    /** Row changes listener */
    private final IRowChangesListener listener;

    /** track of commented line */
    private boolean isCommentLine;

    private boolean isHeader;

    /**
     * Constructor
     * @param line
     * @param listener
     */
    public CSVRow(List<String> line, IRowChangesListener listener) {
        entries = new ArrayList<String>(line);
        this.listener = listener;
    }

    /**
     * Constructor
     * @param row
     * @param listener
     */
    public CSVRow(CSVRow row, IRowChangesListener listener){
		
    	this.entries = (ArrayList<String>)row.entries.clone();
		this.listener = listener;
		this.isCommentLine = row.isCommentLine;
		this.isHeader = row.isHeader;
    }
    
    /**
     * Constructor
     * @param lineElements
     * @param listener
     */
    public CSVRow(String[] lineElements, IRowChangesListener listener) {
        this(Arrays.asList(lineElements), listener);
    }

    /**
     * Create an empty row
     * @param nbOfColumns
     * @param delimiter
     * @param listener
     * @return
     */
    public static CSVRow createEmptyLine (int nbOfColumns, IRowChangesListener listener) {
        List<String> line = new LinkedList<String>();
        for (int i=0; i<nbOfColumns; i++) {
            line.add("");
        }
        return new CSVRow(line, listener);
    }

    /**
     * @return
     */
    public ArrayList<String> getEntries () {
        return entries;
    }

    /**
     * @return
     */
    public String[] getEntriesAsArray () {
        return entries.toArray(new String[entries.size()]);
    }

    /**
     * @param elementIndex
     * @param elementString
     */
    public void setRowEntry (int elementIndex, String elementString) {
        if (entries.get(elementIndex).compareTo(elementString) != 0)  {
            entries.set(elementIndex, elementString);
            listener.rowChanged(this, elementIndex);
        }
    }

    /**
     * return the element at a given index.
     * This method makes sure that if the current line does not have as many
     * elements as the header, it will not break and return an empty string
     *
     * @param index
     * @return the element at a given index
     */
    public String getElementAt (int index) {
        if (index >= entries.size()) {
            return "";
        }
        return entries.get(index);
    }

    /**
     * Return the number of elements in this row
     * @return number of elements in this row
     */
    public int getNumberOfElements () {
        return entries.size();
    }

    /**
     * @param element
     */
    public void addElement(String element) {
        entries.add(element);
    }

    /**
     * Remove an element of the row represented by its index
     *
     * @param index
     */
    public void removeElementAt (int index) {
        entries.remove(index);
    }

    public void setCommentLine (boolean comment) {
        isCommentLine = comment;
    }

    public boolean isCommentLine () {
        return isCommentLine;
    }

    public void setHeader (boolean header) {
        isHeader = header;
    }

    public boolean isHeader () {
        return isHeader;
    }

    public String getComment () {
        return entries.get(0).substring(1);
    }

    /**
     * Give the String representation of a CSVRow object.
     *
     * @see java.lang.Object#toString()
     */
    public String toString () {
        String result = "";
        for (String s:entries) {
            // FIXME get preferences here
            result = result.concat(s).concat(",");
        }
        return result;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entries == null) ? 0 : entries.hashCode());
        return result;
    }

    /**
     * A CSVRow is equal to another one if all element are equals.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
   @Override
	public boolean equals(Object anObject) {
	   
		// The commented lines implies that if two rows have the same content,
		// the cell editor will modify
		// the first one found instead of the focused one
		// each row should be considered as unique even if they have the same content

		/*
		 * AttributeRow thisRow = (AttributeRow) anObject; for (int i=0;
		 * i<getNumberOfElements(); i++) { if
		 * (!(getElementAt(i).equals(thisRow.getElementAt(i)))) { return false;
		 * } } return true;
		 */
	   
		if (this == anObject)
			return true;
	
		else
			return false;
	}

}
