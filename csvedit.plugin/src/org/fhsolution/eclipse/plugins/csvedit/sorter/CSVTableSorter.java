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
package org.fhsolution.eclipse.plugins.csvedit.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.fhsolution.eclipse.plugins.csvedit.model.CSVRow;

/**
 *
 * @author fhenri
 *
 */
public class CSVTableSorter extends ViewerComparator {

    private int propertyIndex;
    private static final int DESCENDING = 1;
    private static final int ASCENDING = 0;

    private int direction = DESCENDING;

    private boolean noSort = true;

    /**
     * Public Constructor
     */
    public CSVTableSorter() {
        propertyIndex = -1;
        direction = DESCENDING;
    }

    /**
     * Set the column on which the user wants to sort table.
     *
     * @param column columnId selected by the user.
     */
    public void setColumn(final int column, final int dir) {
        if (dir == SWT.NONE) {
            noSort = true;
            return;
        }
        noSort = false;
        if (column != propertyIndex) {
            // New column; do an ascending sort
            propertyIndex = column;
        }
        direction = dir == SWT.UP ? ASCENDING : DESCENDING;
    }

    /**
     * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final Viewer viewer, final Object e1, final Object e2) {
        // this is necessary at opening of csv file so column are not sorted.
        if (propertyIndex == -1 || noSort) {
            return 0;
        }

        final String row1 = nvl(((CSVRow) e1).getElementAt(propertyIndex));
        final String row2 = nvl(((CSVRow) e2).getElementAt(propertyIndex));

        int rc = row1.compareTo(row2);

        // If descending order, flip the direction
        if (direction == DESCENDING) {
            rc = -rc;
        }
        return rc;
    }

    private static final String nvl(final String string) {
        return string != null ? string : "";
    }
}
