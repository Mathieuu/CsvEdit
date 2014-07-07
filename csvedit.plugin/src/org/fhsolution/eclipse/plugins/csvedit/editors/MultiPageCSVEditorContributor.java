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
package org.fhsolution.eclipse.plugins.csvedit.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * This is the contributor class defines in the Editor ContributorClass attribute
 * of the pluging which adds new actions to the workbench menu and toolbar,
 * reflecting the features of the editor type
 *
 * @author fhenri
 *
 */
public class MultiPageCSVEditorContributor extends MultiPageEditorActionBarContributor {

    /**
     * Creates a multi-page contributor.
     */
    public MultiPageCSVEditorContributor () {
        super();
    }

    /**
     * Returns the action registered with the given text editor.
     * @return IAction or null if editor is null.
     */
    protected IAction getAction (ITextEditor editor, String actionID) {
        return (editor == null ? null : editor.getAction(actionID));
    }

    /**
     *
     * @see org.eclipse.ui.part.MultiPageEditorActionBarContributor#setActivePage(org.eclipse.ui.IEditorPart)
     */
    public void setActivePage (IEditorPart part) {
    }

    /**
     *
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager)
     */
    public void contributeToMenu (IMenuManager manager) {
    }

    /**
     *
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    public void contributeToToolBar (IToolBarManager manager) {
    }
}
