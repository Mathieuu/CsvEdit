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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a row made of String elements for the detailed view
 * @author fhenri
 * @author msavy
 * 
 */
public class AttributeRow {

	/** Splitted line */
	private final ArrayList<String> entries;

	/** Row changes listener */
	private final IAttributeChangesListener listener;

	/**
	 * Constructor
	 * 
	 * @param line
	 * @param listener
	 */
	public AttributeRow(List<String> line, IAttributeChangesListener listener) {
		entries = new ArrayList<String>(line);
		this.listener = listener;
	}

	/**
	 * Constructor
	 * 
	 * @param lineElements
	 * @param listener
	 */
	public AttributeRow(String[] lineElements,
			IAttributeChangesListener listener) {
		this(Arrays.asList(lineElements), listener);
	}

	/**
	 * Create an empty row
	 * 
	 * @param nbOfColumns
	 * @param delimiter
	 * @param listener
	 * @return
	 */
	public static AttributeRow createEmptyLine(int nbOfColumns,
			IAttributeChangesListener listener) {
		List<String> line = new LinkedList<String>();
		for (int i = 0; i < nbOfColumns; i++) {
			line.add("");
		}
		return new AttributeRow(line, listener);
	}

	/**
	 * @return
	 */
	public ArrayList<String> getEntries() {
		return entries;
	}

	/**
	 * @return
	 */
	public String[] getEntriesAsArray() {
		return entries.toArray(new String[entries.size()]);
	}

	/**
	 * @param elementIndex
	 * @param elementString
	 */
	public void setRowEntry(int elementIndex, String elementString) {
		if (entries.get(elementIndex).compareTo(elementString) != 0) {
			entries.set(elementIndex, elementString);
			listener.rowChanged(this, elementIndex);
		}
	}

	/**
	 * return the element at a given index. This method makes sure that if the
	 * current line does not have as many elements as the header, it will not
	 * break and return an empty string
	 * 
	 * @param index
	 * @return the element at a given index
	 */
	public String getElementAt(int index) {
		if (index >= entries.size()) {
			return "";
		}
		return entries.get(index);
	}

	/**
	 * Return the number of elements in this row
	 * 
	 * @return number of elements in this row
	 */
	public int getNumberOfElements() {
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
	public void removeElementAt(int index) {
		entries.remove(index);
	}

	/**
	 * Give the String representation of a CSVRow object.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String result = "";
		for (String s : entries) {
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

	@Override
	public boolean equals(Object anObject) {
		// The commented lines implies that if two rows have the same content,
		// the cell editor will modify
		// the first one found instead of the focused one
		// each row is unique

		/*
		 * //System.out.println("compare:\n[" + this + "] and\n[" + anObject +
		 * "]"); if (!(anObject instanceof AttributeRow)) { return false; }
		 * 
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
