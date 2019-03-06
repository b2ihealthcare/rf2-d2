package superrf2.model;

import java.nio.file.Path;

import superrf2.RF2CreateContext;
import superrf2.naming.RF2FileName;

/**
 * @since 0.1
 */
public final class RF2RelationshipFile extends RF2ContentFile {

	public RF2RelationshipFile(Path path, RF2FileName fileName) {
		super(path, fileName);
	}
	
	@Override
	protected String[] getRF2HeaderSpec() {
		return new String[] {
			RF2Columns.ID,
			RF2Columns.EFFECTIVE_TIME,
			RF2Columns.ACTIVE,
			RF2Columns.MODULE_ID,
			RF2Columns.SOURCE_ID,
			RF2Columns.DESTINATION_ID,
			RF2Columns.RELATIONSHIP_GROUP,
			RF2Columns.TYPE_ID,
			RF2Columns.CHARACTERISTIC_TYPE_ID,
			RF2Columns.MODIFIER_ID,
		};
	}

	public static RF2RelationshipFile create(Path parent, String contentSubType, RF2CreateContext context) {
		final String fileName = String.format("sct2_Relationship_%s_%s%s_%s.%s", contentSubType, context.getCountry(), context.getNamespace(), context.getReleaseDate(), TXT);
		return new RF2RelationshipFile(parent, new RF2FileName(fileName));
	}

}
