/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.jmol.business;

import java.util.List;

import net.bioclipse.jmol.model.IJmolMolecule;
import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.Recorded;
import net.bioclipse.core.api.domain.IMolecule;
import net.bioclipse.core.api.managers.IBioclipseManager;
import net.bioclipse.core.api.managers.PublishedClass;
import net.bioclipse.core.api.managers.PublishedMethod;
import net.bioclipse.core.api.managers.TestClasses;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

@TestClasses("net.bioclipse.jmol.test.JmolManagerTest")
@PublishedClass(
    value="This manager contains methods for controlling jmol",
    doi="10.1038/npre.2007.50.1"
)
public interface IJmolManager extends IBioclipseManager{

    /**
     * Execute a script in Jmol. If editor active, run script there. 
     * In the future, if JmolView active, there too.
     * @param script The script command to run
     */
    @PublishedMethod(
        methodSummary = "Execute the given String as a " +
            "script in the active Jmol editor.",
        params = "String script"
    )
    @Recorded
    public void run(String script);

    
    /**
     * Load jmoo with a file
     * @param path Path to file, relative workspace
     * @throws CoreException 
     */
    @PublishedMethod( methodSummary = "Load a file into the active Jmol editor.",
                      params = "Path to file" )
    @Recorded
    public void load(String path) throws CoreException;

    /**
     * @param file
     * @throws CoreException 
     */
    public void load(IFile file) throws CoreException;
    
    @Recorded
    @PublishedMethod(
        methodSummary = "Loads an IMolecule into a new Jmol editor.",
        params = "IMolecule file"
    )
    public void load(IMolecule file) throws BioclipseException;
    
    @Recorded
    @PublishedMethod( methodSummary = "Export as image to path",
                      params = "String filepath" )
    public void snapshot(String filepath);

    /**
     * Runs "spin on" in jmol
     */
    @Recorded
    @PublishedMethod( methodSummary = "Causes active jmol to " +
    		                              "spin molecule")
    public void spinOn();
    
    /**
     * Runs "spin off" in jmol
     */
    @Recorded
    @PublishedMethod( methodSummary = "Causes active jmol to stop " +
    		                              "spinning molecule" )
    public void spinOff();

    @Recorded
    @PublishedMethod(methodSummary = "Optimizes the geometry of the structure" +
    		" in the active JmolEditor (but not back to file)" )
    public void minimize();
 
    @PublishedMethod(
        methodSummary="Returns a List<IMolecule> from the current Jmol editor."
    )
    public List<IJmolMolecule> getMolecules();

    @PublishedMethod( params = "String s", 
                      methodSummary = "Prints the given string to the Jmol " +
                      		            "console" )
    public void print(String s);
    
    public boolean selectionIsEmpty();
    
    /**
     * Append a file to the open editor, effectively opening multiple files 
     * on top of eachother.
     * 
     * @param file to be appended
     */
    @PublishedMethod( methodSummary = "Append a file to the open editor, " +
                                      "effectively opening multiple files on " +
                                      "top of eachother",
                      params = "Path to file" )
    @Recorded
    public void append(String file);
    
    /**
     * Append a file to the open editor, effectively opening multiple files 
     * on top of eachother.
     * 
     * @param file to be appended
     */
    public void append(IFile file);

    @Recorded
    @PublishedMethod(
        methodSummary = "Append an IMolecule to the open editor, " +
            "effectively opening multiple files on " +
            "top of each other",
        params = "IMolecule molecule"
    )
    public void append(IMolecule molecule);
}
