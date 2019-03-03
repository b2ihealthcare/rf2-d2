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
package superrf2.naming;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import superrf2.naming.release.RF2Product;
import superrf2.naming.release.RF2ReleaseDate;
import superrf2.naming.release.RF2ReleaseInitial;
import superrf2.naming.release.RF2ReleaseStatus;

/**
 * @since 0.1
 */
public class RF2ReleaseNameTest {

	@Test
	public void releaseInitial() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT");
		assertThat(rf2ReleaseName.getElements()).contains(new RF2ReleaseInitial());
	}
	
	@Test
	public void releaseInitial_Unrecognized() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCTSomething");
		assertThat(rf2ReleaseName.getElements())
			.contains(RF2NameElement.unrecognized("SnomedCTSomething"))
			.doesNotContain(new RF2ReleaseInitial());
	}
	
	@Test
	public void product_International() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_International");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("International", "", "")
		);
	}
	
	@Test
	public void product_InternationalRF2() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_InternationalRF2");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("International", "", "RF2")
		);
	}
	
	@Test
	public void product_InternationalEdition() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_InternationalEdition");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("International", "Edition", "")
		);
	}
	
	@Test
	public void product_InternationalEditionRF2() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_InternationalEditionRF2");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("International", "Edition", "RF2")
		);
	}
	
	@Test
	public void product_AustralianExtensionRF2() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_AustralianExtensionRF2");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("Australian", "Extension", "RF2")
		);
	}
	
	@Test
	public void product_Unrecognized() throws Exception {
		// Since Product part can be anything RF2Product will always recognize the given word but puts everything into the actual product part of RF2Product
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_SomethingEditiRF3");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("SomethingEditiRF3", "", "")
		);
	}
	
	@Test
	public void releaseStatus_Alpha() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_InternationalRF2_ALPHA");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("International", "", "RF2"),
			new RF2ReleaseStatus("ALPHA")
		);
	}
	
	@Test
	public void releaseStatus_Beta() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_InternationalRF2_BETA");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("International", "", "RF2"),
			new RF2ReleaseStatus("BETA")
		);
	}
	
	@Test
	public void releaseStatus_Production() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_InternationalRF2_PRODUCTION");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("International", "", "RF2"),
			new RF2ReleaseStatus("PRODUCTION")
		);
	}
	
	@Test
	public void releaseStatus_Unrecognized() throws Exception {
		// MEMBER is actually not a valid 
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_InternationalRF2_MEMBER");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("International", "", "RF2"),
			RF2NameElement.unrecognized("MEMBER")
		);
		assertThat(rf2ReleaseName.getMissingElements()).contains(RF2ReleaseStatus.class);
	}
	
	@Test
	public void releaseDate_20190131T120000Z() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_InternationalRF2_PRODUCTION_20190131T120000Z");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("International", "", "RF2"),
			new RF2ReleaseStatus("PRODUCTION"),
			new RF2ReleaseDate("20190131", "120000")
		);
	}
	
	@Test
	public void releaseDate_Unrecognized() throws Exception {
		RF2ReleaseName rf2ReleaseName = new RF2ReleaseName("SnomedCT_InternationalRF2_PRODUCTION_20190131T120000");
		assertThat(rf2ReleaseName.getElements()).contains(
			new RF2ReleaseInitial(),
			new RF2Product("International", "", "RF2"),
			new RF2ReleaseStatus("PRODUCTION"),
			RF2NameElement.unrecognized("20190131T120000")
		);
		assertThat(rf2ReleaseName.getMissingElements()).contains(RF2ReleaseDate.class);
	}
	
}
