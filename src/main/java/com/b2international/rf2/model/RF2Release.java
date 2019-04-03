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
package com.b2international.rf2.model;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

import com.b2international.rf2.Constants;
import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.naming.RF2ReleaseName;

/**
 * @since 0.1 
 */
public final class RF2Release extends RF2File {

	public RF2Release(Path parent, RF2ReleaseName fileName) {
		super(parent, fileName);
	}

	@Override
	public void visit(Consumer<RF2File> visitor) throws IOException {
		if (!Files.exists(getPath())) {
			throw new IllegalStateException("Cannot visit non-existing RF2 Release: " + getPath());
		}
		
		visitor.accept(this);
		try (FileSystem zipfs = openZipfs(false)) {
			for (Path root : zipfs.getRootDirectories()) {
				Files.walk(root, 1).forEach(path -> {
					if (!RF2Directory.ROOT_PATH.equals(path.toString())) {
						try {
							RF2File.detect(path).visit(visitor);
						} catch (IOException e) {
							throw new RuntimeException("Couldn't visit path: " + path, e);
						}
					}
				});
			}
		}
	}
	
	private FileSystem openZipfs(boolean create) throws IOException {
		return FileSystems.newFileSystem(URI.create("jar:" + getPath().toUri()), Map.of("create", String.valueOf(create)));
	}
	
	@Override
	public String getType() {
		return "Release";
	}

	@Override
	public void create(RF2CreateContext context) throws IOException {
		if (Files.exists(getPath())) {
			throw new IllegalStateException("Cannot overwrite and create RF2 Release at path: " + getPath());
		}
		
		try (FileSystem zipfs = openZipfs(true)) {
			// root folder with same name
			RF2Directory rootDir = RF2Directory.create(zipfs.getPath("/"), getFileName().getFileName());
			rootDir.create(context);
			
			for (String contentSubType : context.getContentSubTypes()) {
				RF2Directory contentSubTypeDir = RF2Directory.create(rootDir.getPath(), contentSubType);
				contentSubTypeDir.create(context);
				
				createTerminologyContent(context, contentSubType, contentSubTypeDir);
				createRefsetContent(context, contentSubType, contentSubTypeDir);
			}
			
		}
	}

	private void createTerminologyContent(RF2CreateContext context, String contentSubType, RF2Directory contentSubTypeDir) throws IOException {
		// create Terminology directory
		RF2Directory terminologyDir = RF2Directory.create(contentSubTypeDir.getPath(), "Terminology");
		terminologyDir.create(context);
		
		RF2ConceptFile conceptFile = RF2ConceptFile.create(terminologyDir.getPath(), contentSubType, context);
		conceptFile.create(context);
		
		RF2DescriptionFile descriptionFile = RF2DescriptionFile.create(terminologyDir.getPath(), contentSubType, context);
		descriptionFile.create(context);
		
		RF2DescriptionFile textDefinitionFile = RF2DescriptionFile.createTextDefinition(terminologyDir.getPath(), contentSubType, context);
		textDefinitionFile.create(context);
		
		RF2RelationshipFile relationshipFile = RF2RelationshipFile.create(terminologyDir.getPath(), contentSubType, context);
		relationshipFile.create(context);
		
		RF2RelationshipFile statedRelationshipFile = RF2RelationshipFile.createStated(terminologyDir.getPath(), contentSubType, context);
		statedRelationshipFile.create(context);
		
		RF2RefsetFile owlExpressionRefsetFile = RF2RefsetFile.createOwlExpressionRefset(terminologyDir.getPath(), contentSubType, context);
		owlExpressionRefsetFile.create(context);
		
		RF2IdentifierFile identifierFile = RF2IdentifierFile.create(terminologyDir.getPath(), contentSubType, context);
		identifierFile.create(context);
	}
	
	private void createRefsetContent(RF2CreateContext context, String contentSubType, RF2Directory contentSubTypeDir) throws IOException {
		// create Refset directory
		RF2Directory refSetDir = RF2Directory.create(contentSubTypeDir.getPath(), "Refset");
		refSetDir.create(context);
		
		// create Content directory
		{
			RF2Directory contentDir = RF2Directory.create(refSetDir.getPath(), "Content");
			contentDir.create(context);
	
			RF2RefsetFile simpleRefsetFile = RF2RefsetFile.createSimpleRefset(contentDir.getPath(), contentSubType, context);
			simpleRefsetFile.create(context);
			
			RF2RefsetFile associationRefsetFile = RF2RefsetFile.createAssociationRefset(contentDir.getPath(), contentSubType, context);
			associationRefsetFile.create(context);
			
			RF2RefsetFile attributeValueRefsetFile = RF2RefsetFile.createAttributeValueRefset(contentDir.getPath(), contentSubType, context);
			attributeValueRefsetFile.create(context);
		}
		
		// Language
		{
			RF2Directory languageDir = RF2Directory.create(refSetDir.getPath(), "Language");
			languageDir.create(context);
		
			RF2RefsetFile languageRefsetFile = RF2RefsetFile.createLanguageRefset(languageDir.getPath(), contentSubType, context);
			languageRefsetFile.create(context);
		}
		
		// Map
		{
			RF2Directory mapDir = RF2Directory.create(refSetDir.getPath(), "Map");
			mapDir.create(context);
			
			RF2RefsetFile simpleMapRefsetFile = RF2RefsetFile.createSimpleMapRefset(mapDir.getPath(), contentSubType, context);
			simpleMapRefsetFile.create(context);
			
			RF2RefsetFile complexMapRefsetFile = RF2RefsetFile.createComplexMapRefset(mapDir.getPath(), contentSubType, context);
			complexMapRefsetFile.create(context);
			
			RF2RefsetFile extendedMapRefsetFile = RF2RefsetFile.createExtendedMapRefset(mapDir.getPath(), contentSubType, context);
			extendedMapRefsetFile.create(context);
		}
		
		// Metadata
		{
			RF2Directory metadataDir = RF2Directory.create(refSetDir.getPath(), "Metadata");
			metadataDir.create(context);
			
			RF2RefsetFile refsetDescriptorRefsetFile = RF2RefsetFile.createRefsetDescriptorRefset(metadataDir.getPath(), contentSubType, context);
			refsetDescriptorRefsetFile.create(context);
			
			RF2RefsetFile descriptionTypeRefsetFile = RF2RefsetFile.createDescriptionTypeRefset(metadataDir.getPath(), contentSubType, context);
			descriptionTypeRefsetFile.create(context);
			
			RF2RefsetFile mrcmAttributeDomainRefsetFile = RF2RefsetFile.createMRCMAttributeDomainRefset(metadataDir.getPath(), contentSubType, context);
			mrcmAttributeDomainRefsetFile.create(context);
			
			RF2RefsetFile mrcmModuleScopeRefsetFile = RF2RefsetFile.createMRCMModuleScopeRefset(metadataDir.getPath(), contentSubType, context);
			mrcmModuleScopeRefsetFile.create(context);
			
			RF2RefsetFile mrcmAttributeRangeRefsetFile = RF2RefsetFile.createMRCMAttributeRangeRefset(metadataDir.getPath(), contentSubType, context);
			mrcmAttributeRangeRefsetFile.create(context);
			
			RF2RefsetFile mrcmDomainRefsetFile = RF2RefsetFile.createMRCMDomainRefset(metadataDir.getPath(), contentSubType, context);
			mrcmDomainRefsetFile.create(context);
			
			RF2RefsetFile moduleDependencyRefsetFile = RF2RefsetFile.createModuleDependencyRefset(metadataDir.getPath(), contentSubType, context);
			moduleDependencyRefsetFile.create(context);
		}
	}
	
	public static RF2Release create(Path parent, String product, String releaseStatus, String releaseDate, String releaseTime) {
		String releaseName = String.format("SnomedCT_%sRF2_%s_%sT%sZ.%s", product, releaseStatus, releaseDate, releaseTime, Constants.ZIP);
		return new RF2Release(parent, new RF2ReleaseName(releaseName));
	}
	
}
