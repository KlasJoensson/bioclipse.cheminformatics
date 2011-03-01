/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.pubchem.business;

import java.io.IOException;
import java.util.List;

import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.Recorded;
import net.bioclipse.core.api.domain.IMolecule;
import net.bioclipse.core.api.jobs.BioclipseJobUpdateHook;
import net.bioclipse.core.api.jobs.IBioclipseJob;
import net.bioclipse.core.api.managers.IBioclipseManager;
import net.bioclipse.core.api.managers.PublishedMethod;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPubChemManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
        params = "int PubChem Compound ID, String path to save the content too", 
        methodSummary = "Loads the PubChem Compound XML with the given number" +
        		"to the given path"
    )
    public String loadCompound(int cid, String target)
        throws IOException, BioclipseException, CoreException;

    public IFile loadCompound(int cid, IFile target, IProgressMonitor monitor )
        throws IOException, BioclipseException, CoreException;

    public IBioclipseJob<IFile> loadCompound(int cid, 
                                         IFile target, 
                                         BioclipseJobUpdateHook<IFile> hook )
        throws IOException, BioclipseException, CoreException;

    @Recorded
    @PublishedMethod(
        params = "int PubChem Compound ID, String path to save the content too",
        methodSummary = "Loads the PubChem Compound 3D MDL molfile with the" +
            " given number to the given path"
    )
    public String loadCompound3d(int cid, String target)
        throws IOException, BioclipseException, CoreException;

    public IFile loadCompound3d(int cid, IFile target, IProgressMonitor monitor )
        throws IOException, BioclipseException, CoreException;

    public IBioclipseJob<IFile> loadCompound3d(int cid, 
                                            IFile target, 
                                            BioclipseJobUpdateHook<IFile> hook )
        throws IOException, BioclipseException, CoreException;

    @Recorded
    @PublishedMethod(
        params = "Integer cid", 
        methodSummary = "Loads the PubChem Compound XML with the given " +
                "compound identifier into a IMolecule."
    )
    public IMolecule download(Integer cid)
        throws IOException, BioclipseException, CoreException;

    @Recorded
    @PublishedMethod(
        params = "Integer cid", 
        methodSummary = "Loads the PubChem Compound 3D MDL molfile with the " +
        		"given compound identifier into a IMolecule."
    )
    public IMolecule download3d(Integer cid)
        throws IOException, BioclipseException, CoreException;

    @Recorded
    @PublishedMethod(
        params = "Integer cid", 
        methodSummary = "Loads the PubChem Compound XML with the given " +
                "compound identifier into a String."
    )
    public String downloadAsString(Integer cid)
        throws IOException, BioclipseException, CoreException;

    @Recorded
    @PublishedMethod(
        params = "Integer cid", 
        methodSummary = "Loads the PubChem Compound 3D MDL molfile with the " +
                "given compound identifier into a String."
    )
    public String download3dAsString(Integer cid)
        throws IOException, BioclipseException, CoreException;

    @Recorded
    @PublishedMethod(
        params = "List<Integer> cids", 
        methodSummary = "Loads the PubChem Compound XMLs for the given " +
                "list of compound identifiers into a List<IMolecule>."
    )
    public List<IMolecule> download(List<Integer> cids)
        throws IOException, BioclipseException, CoreException;

    @Recorded
    @PublishedMethod(
        params = "List<Integer> cids", 
        methodSummary = "Loads the PubChem Compound 3D MDL molfiles for the " +
                "given list of compound identifiers into a List<IMolecule>."
    )
    public List<IMolecule> download3d(List<Integer> cids)
        throws IOException, BioclipseException, CoreException;

    @Recorded
    @PublishedMethod(
        params = "String query against PubChem", 
        methodSummary = "Returns a List of matching compound CIDs."
    )
    public List<Integer> search(String query)
        throws IOException, BioclipseException, CoreException;

    public List<Integer> search(String query, IProgressMonitor monitor )
        throws IOException, BioclipseException, CoreException;

    public IBioclipseJob<IFile> search(String query,
                                      BioclipseJobUpdateHook<IFile> hook)
          throws IOException, BioclipseException, CoreException;

}