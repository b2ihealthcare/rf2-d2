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

import com.b2international.rf2.Constants;
import com.b2international.rf2.RF2;
import com.b2international.rf2.model.RF2Release;
import com.b2international.rf2.naming.release.RF2Product;
import com.b2international.rf2.naming.release.RF2ReleaseDate;
import com.b2international.rf2.naming.release.RF2ReleaseInitial;
import com.b2international.rf2.naming.release.RF2ReleaseStatus;
import com.b2international.rf2.spec.RF2Specification;

/**
 * @since 0.1
 */
public final class RF2ReleaseName extends RF2FileName {

	public RF2ReleaseName(String fileName) {
		super(fileName, 
			RF2ReleaseInitial.class,
			RF2Product.class,
			RF2ReleaseStatus.class,
			RF2ReleaseDate.class
		);
	}
	
	/**
	 * An RF2 Release Name is unrecognizable when either:
	 * - none of the required name elements were found in the current file
	 * - the extension is not equal to {@link Constants#ZIP zip} 
	 * 
	 * @return whether this {@link RF2Release} name is unrecognizable based on the current {@link RF2#RF2_VERSION RF2 version}. 
	 */
	@Override
	public boolean isUnrecognized() {
		return !Constants.ZIP.equals(getExtension()) || getElements().stream().allMatch(RF2NameElement.Unrecognized.class::isInstance);
	}
	
	@Override
	public RF2Release createRF2File(Path parent, RF2Specification specification) {
		return new RF2Release(parent, this, specification);
	}

}
