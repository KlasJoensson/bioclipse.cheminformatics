/*******************************************************************************
 * Copyright (c) 2010  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.opsin.business;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.api.managers.IBioclipseManager;
import net.bioclipse.core.api.managers.PublishedClass;
import net.bioclipse.core.api.managers.PublishedMethod;

@PublishedClass(
    value="Manager for the OPSIN functionality to convert IUPAC names into " +
    		"chemical structures."
)
public interface IOpsinManager extends IBioclipseManager {

	@PublishedMethod(
		params="String iupacName",
		methodSummary="Converts an IUPAC name into a chemical structure"
	)
	public ICDKMolecule parseIUPACName(String iupacName);

	@PublishedMethod(
		params="String iupacName",
		methodSummary="Converts an IUPAC name into a CML document"
	)
	public String parseIUPACNameAsCML(String iupacName);

}
