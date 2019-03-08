/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.rf2.naming;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.b2international.rf2.model.RF2File;

/**
 * @since 0.1
 */
public abstract class RF2FileNameBase {

	public static final String ELEMENT_SEPARATOR = "_";
	public static final String FILE_EXT_SEPARATOR = ".";
	
	private final String fileName;
	private final String extension;
	private final List<RF2NameElement> elements;
	private final List<Class<?>> missingElements;
	
	public RF2FileNameBase(String fileName, Class<?>...expectedRF2NameElements) {
		if (fileName == null || fileName.isBlank()) {
			throw new IllegalArgumentException("FileName argument cannot be null or empty");
		}
		
		final int lastExtSeparatorIndex = fileName.lastIndexOf(FILE_EXT_SEPARATOR);
		if (lastExtSeparatorIndex == -1) {
			this.extension = "";
			this.fileName = fileName;
		} else {
			this.extension = fileName.substring(lastExtSeparatorIndex + 1);
			this.fileName = fileName.substring(0, lastExtSeparatorIndex);
		}
		
		final Iterator<String> actualElements;
		
		if (expectedRF2NameElements.length == 1 && expectedRF2NameElements[0] == RF2NameElement.AcceptAll.class) {
			this.elements = new ArrayList<>(expectedRF2NameElements.length);
			this.missingElements = Collections.emptyList();
			
			actualElements = Arrays.asList(this.fileName).iterator();
		} else {
			this.elements = new ArrayList<>(expectedRF2NameElements.length);
			this.missingElements = new ArrayList<>(expectedRF2NameElements.length);

			actualElements = Arrays.asList(this.fileName.split(ELEMENT_SEPARATOR)).iterator();
		}
		
		final Iterator<Class<?>> expectedElements = Arrays.asList(expectedRF2NameElements).iterator();
		
		// consume both iterators in order, since an RF2 file has a strict specification
		while (actualElements.hasNext() && expectedElements.hasNext()) {
			parse(actualElements.next(), expectedElements.next());
		}

		// if at this point we still have actualElements, report them as unrecognized
		while (actualElements.hasNext()) {
			elements.add(RF2NameElement.unrecognized(actualElements.next()));
		}
		
		// if at this point we still have expectedElements, report them as missing
		while (expectedElements.hasNext()) {
			missingElements.add(expectedElements.next());
		}
	}

	private void parse(String actualElement, Class<?> expectedElement) {
		Matcher matcher = RF2NameElement.getNamingPattern(expectedElement).matcher(actualElement);
		if (matcher.matches()) {
			Object[] args = new String[matcher.groupCount()];
			for (int i = 0; i < matcher.groupCount(); i++) {
				args[i] = matcher.group(i + 1);
			}
			try {
				elements.add((RF2NameElement) expectedElement.getConstructors()[0].newInstance(args));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			elements.add(RF2NameElement.unrecognized(actualElement));
			missingElements.add(expectedElement);
		}
	}

	public final String getFileName() {
		return fileName;
	}

	public final String getExtension() {
		return extension;
	}
	
	public final List<RF2NameElement> getElements() {
		return Collections.unmodifiableList(elements);
	}
	
	/**
	 * Returns the first (and the only) occurrence of an RF2 name element
	 * @param nameType
	 * @return
	 */
	public final <T extends RF2NameElement> Optional<T> getElement(Class<T> nameType) {
		return getElements().stream()
				.filter(nameType::isInstance)
				.map(nameType::cast)
				.findFirst();
	}
	
	public final List<RF2NameElement.Unrecognized> getUnrecognizedElements() {
		return getElements().stream()
				.filter(RF2NameElement.Unrecognized.class::isInstance)
				.map(RF2NameElement.Unrecognized.class::cast)
				.collect(Collectors.toUnmodifiableList());
	}
	
	public final List<Class<?>> getMissingElements() {
		return Collections.unmodifiableList(missingElements);
	}

	/**
	 * @return whether this RF2 File Name has any unrecognized parts in the given {@link #getFileName() fileName}.
	 */
	public final boolean hasUnrecognizedElement() {
		return !getUnrecognizedElements().isEmpty();
	}
	
	/**
	 * Subclasses must specify the cases when an RF2 file name considered unrecognized.
	 * 
	 * @return whether this RF2 file name is unrecognized or not
	 */
	public abstract boolean isUnrecognized();
	
	/**
	 * Creates an {@link RF2File} based on the described RF2 File Name elements.
	 * 
	 * @param parent - the parent path where the file is currently located or will be located 
	 * @return an {@link RF2File} instance
	 */
	public abstract RF2File createRF2File(Path parent);
	
	@Override
	public final String toString() {
		return String.format("%s%s%s", elements.stream().map(Object::toString).collect(Collectors.joining(ELEMENT_SEPARATOR)), extension.isEmpty() ? "" : FILE_EXT_SEPARATOR, extension);
	}
	
}
