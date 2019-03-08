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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.rf2.naming.RF2FileName;
import com.b2international.rf2.naming.RF2NameElement;
import com.b2international.rf2.naming.file.RF2ContentSubType;
import com.b2international.rf2.naming.file.RF2ContentType;
import com.b2international.rf2.naming.file.RF2CountryNamespace;
import com.b2international.rf2.naming.file.RF2FileType;
import com.b2international.rf2.naming.file.RF2VersionDate;

/**
 * @since 0.1
 */
public class RF2FileNameTest {

	@Test
	public void unrecognized() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("unrecognized");
		assertTrue(rf2FileName.getExtension().isEmpty());
		assertThat(rf2FileName.getElements())
			.contains(RF2NameElement.unrecognized("unrecognized"));
	}
	
	@Test
	public void fileType_sct() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("sct");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				new RF2FileType("", "sct", "")
			);
	}
	
	@Test
	public void fileType_sct1() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("sct1");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.SCT1
			);
	}
	
	@Test
	public void fileType_sct2() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("sct2");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.SCT2
			);
	}
	
	@Test
	public void fileType_xsct2() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("xsct2");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				new RF2FileType("x", "sct", "2")
			);
	}
	
	@Test
	public void fileType_zdoc() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("zdoc");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				new RF2FileType("z", "doc", "")
			);
	}
	
	@Test
	public void fileType_Unrecognized() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("adoc");
		assertThat(rf2FileName.getElements())
			.contains(RF2NameElement.unrecognized("adoc"));
	}
	
	@Test
	public void contentType_sct2_Concept() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("sct2_Concept");
		assertThat(rf2FileName.getElements()).
			containsOnly(
				RF2FileType.SCT2,
				RF2ContentType.CONCEPT
			);
	}
	
	@Test
	public void contentType_sct2_Relationship() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("sct2_Relationship");
		assertThat(rf2FileName.getElements()).
			containsOnly(
				RF2FileType.SCT2,
				RF2ContentType.RELATIONSHIP
			);
	}
	
	@Test
	public void contentType_sct2_Description() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("sct2_Description");
		assertThat(rf2FileName.getElements()).
			containsOnly(
				RF2FileType.SCT2,
				RF2ContentType.DESCRIPTION
			);
	}
	
	@Test
	public void contentSubType_Full() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("sct2_Concept_Full");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.SCT2,
				RF2ContentType.CONCEPT,
				new RF2ContentSubType("", "Full", "")
			);
	}
	
	@Test
	public void contentSubType_Delta() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("sct2_Description_Delta");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.SCT2,
				RF2ContentType.DESCRIPTION,
				new RF2ContentSubType("", "Delta", "")
			);
	}
	
	@Test
	public void contentSubType_Snapshot() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("sct2_Relationship_Snapshot");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.SCT2,
				RF2ContentType.RELATIONSHIP,
				new RF2ContentSubType("", "Snapshot", "")
			);
	}
	
	@Test
	public void contentSubType_AttributeValueDelta() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_AttributeValueDelta");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("AttributeValue", "Delta", "")
			);
	}
	
	@Test
	public void contentSubType_LanguageDelta_en() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_LanguageDelta-en");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("Language", "Delta", "en")
			);
	}
	
	@Test
	public void contentSubType_LanguageDelta_en_sg() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_LanguageDelta-en-sg");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("Language", "Delta", "en-sg")
			);
	}
	
	@Test
	public void countryNamespace_INT() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_LanguageDelta-en-sg_INT");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("Language", "Delta", "en-sg"),
				RF2CountryNamespace.INT
			);
	}
	
	@Test
	public void countryNamespace_INTWithNamespace() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_LanguageDelta-en-sg_INT1000000");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("Language", "Delta", "en-sg"),
				new RF2CountryNamespace("INT", "1000000")
			);
	}
	
	@Test
	public void countryNamespace_NRC() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_LanguageDelta-en-sg_AU");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("Language", "Delta", "en-sg"),
				new RF2CountryNamespace("AU", "")
			);
	}
	
	@Test
	public void countryNamespace_NRCWithNamespace() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_LanguageDelta-en-sg_AU1000001");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("Language", "Delta", "en-sg"),
				new RF2CountryNamespace("AU", "1000001")
			);
	}
	
	@Test
	public void countryNamespace_NamespaceOnly() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_LanguageDelta-en-sg_1000001");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("Language", "Delta", "en-sg"),
				new RF2CountryNamespace("", "1000001")
			);
	}
	
	@Test
	public void countryNamespace_Unrecognized() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_LanguageDelta-en-sg_10001");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("Language", "Delta", "en-sg"),
				RF2NameElement.unrecognized("10001")
			);
		assertThat(rf2FileName.getMissingElements()).contains(RF2CountryNamespace.class);
	}
	
	@Test
	public void versionDate_20190131() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_LanguageDelta-en_INT_20190131");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("Language", "Delta", "en"),
				RF2CountryNamespace.INT,
				new RF2VersionDate("20190131")
			);
	}
	
	@Test
	public void versionDate_Unrecognized() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_LanguageDelta-en_INT_0131");
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("Language", "Delta", "en"),
				RF2CountryNamespace.INT,
				RF2NameElement.unrecognized("0131")
			);
		assertThat(rf2FileName.getMissingElements()).contains(RF2VersionDate.class);
	}
	
	@Test
	public void completeRF2FileName() throws Exception {
		RF2FileName rf2FileName = new RF2FileName("der2_cRefSet_LanguageDelta-en_INT_20190131.txt");
		assertThat(rf2FileName.hasUnrecognizedElement());
		assertThat(rf2FileName.getElements())
			.containsOnly(
				RF2FileType.DER2,
				new RF2ContentType("cRefSet"),
				new RF2ContentSubType("Language", "Delta", "en"),
				RF2CountryNamespace.INT,
				new RF2VersionDate("20190131")
			);
		assertEquals("txt", rf2FileName.getExtension());
	}
	
}
