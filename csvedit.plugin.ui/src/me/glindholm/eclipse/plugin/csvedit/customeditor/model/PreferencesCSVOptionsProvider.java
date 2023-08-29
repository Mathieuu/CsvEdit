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
package me.glindholm.eclipse.plugin.csvedit.customeditor.model;

import me.glindholm.eclipse.plugin.csvedit.Activator;
import me.glindholm.eclipse.plugin.csvedit.customeditor.preferences.PreferenceConstants;
import me.glindholm.eclipse.plugin.csvedit.model.ICsvOptionsProvider;

/**
 *
 * @author fhenri, msavy
 *
 */
public class PreferencesCSVOptionsProvider implements ICsvOptionsProvider {

    private final boolean useFirstLineAsHeader;
    private final boolean sensitiveSearch;
    private final boolean useQualifier;
    private final String customDelimiter;
    private final String commentChar;
    private final String defaultPage;
    private final String textQualifier;
    private final String inCellDelimiter;
    private final String regexTableMarker;

    public PreferencesCSVOptionsProvider() {
        useFirstLineAsHeader = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.USE_FIRST_LINE_AS_HEADER);
        customDelimiter = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.CUSTOM_DELIMITER);
        sensitiveSearch = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CASE_SENSITIVE_SEARCH);
        commentChar = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.COMMENT_CHAR);
        textQualifier = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.TEXT_QUALIFIER);
        useQualifier = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.USE_QUALIFIER);
        defaultPage = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.DEFAULT_VIEW_PAGE);
        inCellDelimiter = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.INCELL_DELIMITER);
        regexTableMarker = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.REGEX_HEADER_TABLE);
    }

    @Override
    public String getCustomDelimiter() {
        return customDelimiter;
    }

    @Override
    public boolean getUseFirstLineAsHeader() {
        return useFirstLineAsHeader;
    }

    @Override
    public boolean getSensitiveSearch() {
        return sensitiveSearch;
    }

    @Override
    public String getCommentChar() {
        return commentChar;
    }

    public int getDefaultPage() {
        return Integer.parseInt(defaultPage);
    }

    @Override
    public String getTextQualifier() {
        return textQualifier;
    }

    @Override
    public boolean useTextQualifier() {
        return useQualifier;
    }

    @Override
    public String getRegexTableMarker() {
        return regexTableMarker;
    }

    @Override
    public String getInCellDelimiter() {
        return inCellDelimiter;
    }
}
