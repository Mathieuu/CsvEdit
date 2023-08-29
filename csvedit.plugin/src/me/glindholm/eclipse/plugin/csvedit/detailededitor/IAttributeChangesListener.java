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

/**
 * Class providing methods to add a custom tableViewer to a Composite
 *
 * @author fhenri
 * @author msavy
 * 
 */
public interface IAttributeChangesListener {

    /**
     * Element at the given index position has changes in
     *
     * @param row   the {@link AttributeRow} which changed
     * @param index the index position
     */
    void rowChanged(AttributeRow row, int index);
}
