package com.b2international.rf2.naming;

import java.nio.file.Path;

import com.b2international.rf2.model.RF2Directory;
import com.b2international.rf2.model.RF2File;

/**
 * @since 
 */
public final class RF2DirectoryName extends RF2FileNameBase {

	public RF2DirectoryName(String fileName) {
		super(fileName, RF2NameElement.AcceptAll.class);
	}

	@Override
	public boolean isUnrecognized() {
		return false;
	}

	@Override
	public RF2File createRF2File(Path parent) {
		return new RF2Directory(parent, this);
	}

}
