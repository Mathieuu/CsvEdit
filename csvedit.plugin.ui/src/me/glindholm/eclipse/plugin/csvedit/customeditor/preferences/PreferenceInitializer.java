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
package me.glindholm.eclipse.plugin.csvedit.customeditor.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import me.glindholm.eclipse.plugin.csvedit.Activator;

/**
 * Class used to initialize default preference values.
 *
 * @author fhenri
 * @author msavy
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     *
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(PreferenceConstants.USE_FIRST_LINE_AS_HEADER, true);
        store.setDefault(PreferenceConstants.CASE_SENSITIVE_SEARCH, false);
        store.setDefault(PreferenceConstants.CUSTOM_DELIMITER, ",");
        store.setDefault(PreferenceConstants.TEXT_QUALIFIER, "\"");
        store.setDefault(PreferenceConstants.USE_QUALIFIER, false);

        store.setDefault(PreferenceConstants.DEFAULT_VIEW_PAGE, "0");
    }

}
