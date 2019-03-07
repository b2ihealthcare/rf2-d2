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
package superrf2.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import superrf2.check.RF2IssueAcceptor;
import superrf2.naming.RF2FileName;
import superrf2.naming.file.RF2ContentType;

/**
 * @since 0.1
 */
public final class RF2RefsetFile extends RF2ContentFile {

	public static final String COMPONENT_TYPE = "Refset";
	
	private final String[] rf2HeaderSpec;
	
	public RF2RefsetFile(Path path, RF2FileName fileName, String[] rf2HeaderSpec) {
		super(path, fileName);
		this.rf2HeaderSpec = Objects.requireNonNull(rf2HeaderSpec);
	}
	
	@Override
	protected String[] getRF2HeaderSpec() {
		return rf2HeaderSpec;
	}
	
	@Override
	protected void validateRows(RF2IssueAcceptor acceptor, Stream<String[]> rows) {
		rows.forEach(row -> {
			var id = row[0];
			try {
				UUID.fromString(id);
			} catch (IllegalArgumentException e) {
				acceptor.error("Member id is not a valid uuid: %s", id);
			}
		});
	}
	
	public static RF2File detect(Path parent, RF2FileName fileName) {
		return fileName.getElement(RF2ContentType.class)
			.map(contentType -> {
				if (contentType.getContentType().endsWith("Refset")) {
					try {
						String[] header = Files.lines(parent.resolve(fileName.toString())).findFirst().orElse("N/A").split(TAB);
						if (KNOWN_REFSET_HEADERS.stream().anyMatch(knownHeader -> Arrays.deepEquals(knownHeader, header))) {
							return new RF2RefsetFile(parent, fileName, header);
						}					
					} catch (Exception e) {
						// ignore unreadable, etc. files here 
					}
				}
				return new RF2UnrecognizedFile(parent, fileName);
			})
			.orElse(new RF2UnrecognizedFile(parent, fileName));
	}
	
	// KNOWN RF2 RefSet Headers
	
	public static final String[] SIMPLE_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID
	};
	
	public static final String[] ORDERED_COMPONENT_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.ORDER
	};
	
	public static final String[] ATTRIBUTE_VALUE_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.VALUE_ID
	};
	
	public static final String[] LANGUAGE_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.ACCEPTABILITY_ID
	};
	
	public static final String[] ASSOCIATION_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.TARGET_COMPONENT_ID
	};
	
	public static final String[] ORDERED_ASSOCIATION_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.TARGET_COMPONENT_ID,
		RF2Columns.ORDER
	};
	
	public static final String[] ANNOTATION_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.ANNOTATION
	};
	
	public static final String[] QUERY_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.QUERY
	};	
	
	public static final String[] SIMPLE_MAP_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.MAP_TARGET
	};
	
	public static final String[] COMPLEX_MAP_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.MAP_GROUP,
		RF2Columns.MAP_PRIORITY,
		RF2Columns.MAP_RULE,
		RF2Columns.MAP_ADVICE,
		RF2Columns.MAP_TARGET,
		RF2Columns.CORRELATION_ID
	};
	
	public static final String[] EXTENDED_MAP_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.MAP_GROUP,
		RF2Columns.MAP_PRIORITY,
		RF2Columns.MAP_RULE,
		RF2Columns.MAP_ADVICE,
		RF2Columns.MAP_TARGET,
		RF2Columns.CORRELATION_ID,
		RF2Columns.MAP_CATEGORY_ID
	};
	
	public static final String[] REFSET_DESCRIPTOR_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.ATTRIBUTE_DESCRIPTION,
		RF2Columns.ATTRIBUTE_TYPE,
		RF2Columns.ATTRIBUTE_ORDER
	};

	public static final String[] MODULE_DEPENDENCY_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.SOURCE_EFFECTIVE_TIME,
		RF2Columns.TARGET_EFFECTIVE_TIME
	};
	
	public static final String[] DESCRIPTION_FORMAT_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.DESCRIPTION_FORMAT,
		RF2Columns.DESCRIPTION_LENGTH
	};
	
	public static final String[] MAP_CORRELATION_ORIGIN_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.MAP_TARGET,
		RF2Columns.ATTRIBUTE_ID,
		RF2Columns.CORRELATION_ID,
		RF2Columns.CONTENT_ORIGIN_ID
	};
	
	public static final String[] MRCM_DOMAIN_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.DOMAIN_CONSTRAINT,
		RF2Columns.PARENT_DOMAIN,
		RF2Columns.PROXIMAL_PRIMITIVE_CONSTRAINT,
		RF2Columns.PROXIMAL_PRIMITIVE_REFINEMENT,
		RF2Columns.DOMAIN_TEMPLATE_FOR_PRECOORDINATION,
		RF2Columns.DOMAIN_TEMPLATE_FOR_POSTCOORDINATION,
		RF2Columns.GUIDE_URL
	};
	
	public static final String[] MRCM_ATTRIBUTE_DOMAIN_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.DOMAIN_ID,
		RF2Columns.GROUPED,
		RF2Columns.ATTRIBUTE_CARDINALITY,
		RF2Columns.ATTRIBUTE_IN_GROUP_CARDINALITY,
		RF2Columns.RULE_STRENGTH_ID,
		RF2Columns.CONTENT_TYPE_ID
	};
	
	public static final String[] MRCM_ATTRIBUTE_RANGE_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.RANGE_CONSTRAINT,
		RF2Columns.ATTRIBUTE_RULE,
		RF2Columns.RULE_STRENGTH_ID,
		RF2Columns.CONTENT_TYPE_ID
	};
	
	public static final String[] MRCM_MODULE_SCOPE_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.RULE_REFSET_ID
	};
	
	public static final String[] OWL_EXPRESSION_REFSET_HEADER = new String[] {
		RF2Columns.ID,
		RF2Columns.EFFECTIVE_TIME,
		RF2Columns.ACTIVE,
		RF2Columns.MODULE_ID,
		RF2Columns.REFSET_ID,
		RF2Columns.REFERENCED_COMPONENT_ID,
		RF2Columns.OWL_EXPRESSION
	};
		
	private static final List<String[]> KNOWN_REFSET_HEADERS = List.<String[]>of(
		SIMPLE_REFSET_HEADER,
		ORDERED_COMPONENT_REFSET_HEADER,
		ATTRIBUTE_VALUE_REFSET_HEADER,
		LANGUAGE_REFSET_HEADER,
		ASSOCIATION_REFSET_HEADER,
		ORDERED_ASSOCIATION_REFSET_HEADER,
		ANNOTATION_REFSET_HEADER,
		QUERY_REFSET_HEADER,
		SIMPLE_MAP_REFSET_HEADER,
		COMPLEX_MAP_REFSET_HEADER,
		EXTENDED_MAP_REFSET_HEADER,
		REFSET_DESCRIPTOR_REFSET_HEADER,
		MODULE_DEPENDENCY_REFSET_HEADER,
		DESCRIPTION_FORMAT_REFSET_HEADER,
		MAP_CORRELATION_ORIGIN_REFSET_HEADER,
		MRCM_DOMAIN_REFSET_HEADER,
		MRCM_ATTRIBUTE_DOMAIN_REFSET_HEADER,
		MRCM_ATTRIBUTE_RANGE_REFSET_HEADER,
		MRCM_MODULE_SCOPE_REFSET_HEADER,
		OWL_EXPRESSION_REFSET_HEADER
	);

}
