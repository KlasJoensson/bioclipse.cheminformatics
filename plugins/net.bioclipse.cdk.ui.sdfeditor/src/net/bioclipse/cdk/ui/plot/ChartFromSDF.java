/*******************************************************************************
 * Copyright (c) 2013  Klas J�nsson <klas.joensson@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.cdk.ui.plot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.sdfeditor.editor.MolTableSelection;
import net.bioclipse.cdk.ui.sdfeditor.editor.MoleculesEditor;
import net.bioclipse.cdk.ui.sdfeditor.editor.MultiPageMoleculesEditorPart;
import net.bioclipse.chart.ChartConstants;
import net.bioclipse.chart.ChartPoint;
import net.bioclipse.chart.ChartUtils;
import net.bioclipse.chart.IChartDescriptor;
import net.bioclipse.chart.ui.business.IChartManager;
import net.bioclipse.chart.ui.business.IJavaChartManager;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule.Property;
import net.bioclipse.model.ChartDescriptor;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This class abstracts (selected) data from the MolTableViewer and presents it
 * as a scatter pot in the chart view.
 *  
 * @author Klas J�nsson (klas.joensson@gmail.com)
 *
 */
public class ChartFromSDF extends AbstractHandler {

    private Logger logger = Logger.getLogger( ChartFromSDF.class );
    private ICDKManager cdk = Activator.getDefault().getJavaCDKManager();

    /* The column that shows the 2D-structure don't have any property, if that
     * selected the mass of the molecule will be calculated. */ 
    private final static String MOL_STRUCTURE_COLUMN = "2D-structure";
    private IChartManager chart = 
            ChartUtils.getManager( IJavaChartManager.class );
    
    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
      
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);        
        if ( editorPart!=null && 
                editorPart instanceof MultiPageMoleculesEditorPart ) {
            MultiPageMoleculesEditorPart mpmep = (MultiPageMoleculesEditorPart)
                    editorPart;
            MoleculesEditor editor = (MoleculesEditor) mpmep
                    .getAdapter( MoleculesEditor.class );
            ISelection sel = editor.getSelection();
            double yValues[];
            double xValues[];
            String yLabel;
            if (sel instanceof MolTableSelection) {
                MolTableSelection selectedMols = (MolTableSelection) sel;
                Iterator<ICDKMolecule> itr = selectedMols.iterator();
                List<Integer> selRows = selectedMols.getSelectedRows();
                Point[] originRows = new Point[selRows.size()];
                List<String> selectedProerties = selectedMols.getPropertyNames();
                int index = 0;
                for (int row:selRows)
                    originRows[index++] = new Point( 0, row+1 );
                if (selectedMols.getFirstElement() == null) {
                    /* For some reason are the one extra selection in front of 
                     * the selected molecules. This only happens when a 
                     * selection is made for the first time a SD-file.
                     * This part of the if-statement is a work around, and 
                     * (hopefully) it can be removed when bug 3521 is solved*/
                    yValues = new double[selectedMols.size()-1];
                    xValues = new double[selectedMols.size()-1];
                    itr.next();
                } else {                
                    yValues = new double[selectedMols.size()];
                    xValues = new double[selectedMols.size()];
                }

                /* As it is now; if there's more than one column selected 
                 * lets calculate the mass or if the first column was selected*/
                boolean twoColSelected = (selectedProerties.size() == 2);

                int i = 0;
                ICDKMolecule mol;
                while (itr.hasNext()) {
                    mol = itr.next();
                    if (twoColSelected) {
                        xValues[i] = getValue(mol, selectedProerties.get( 0 ));
                        yValues[i] = getValue(mol, selectedProerties.get( 1 ));
                    } else {
                        xValues[i] = i + selRows.get( 0 ) + 1;
                        yValues[i] = getValue(mol, selectedProerties.get( 0 ));
                    }
                    
                    i++;
                }
                /*For some reason the iterator skip the last molecule. WHY!!:(*/
                mol = itr.next();
                if (twoColSelected) {
                    xValues[i] = getValue(mol, selectedProerties.get( 0 ));
                    yValues[i] = getValue(mol, selectedProerties.get( 1 ));
                } else {
                    xValues[i] = i + selRows.get( 0 ) + 1;
                    yValues[i] = getValue(mol, selectedProerties.get( 0 ));
                }
                
                
                String xLabel, title;
                if (twoColSelected) {
                    xLabel = selectedProerties.get( 0 );
                    if (xLabel.equals( MOL_STRUCTURE_COLUMN )) 
                        xLabel = ChartConstants.MOL_MASS;
                    
                    yLabel = selectedProerties.get( 1 );
                    if (yLabel.equals( MOL_STRUCTURE_COLUMN )) 
                        yLabel = ChartConstants.MOL_MASS;
                        
                    title = xLabel + " against " + yLabel;
                } else {
                    xLabel = ChartConstants.ROW_NUMBER;
                    yLabel = selectedProerties.get( 0 );
                    if (yLabel.equals( MOL_STRUCTURE_COLUMN )) 
                        yLabel = ChartConstants.MOL_MASS;
                    title = yLabel + " for the selected molecules";
                }
               
                IChartDescriptor descriptor = new ChartDescriptor( editor, 
                                                                   ChartConstants.plotTypes.SCATTER_PLOT, 
                                                                   xLabel, xValues, yLabel, yValues, 
                                                                   originRows, 
                                                                   title ) {
                    @Override
                    public List<ChartPoint> handleEvent( ISelection selection ) {
                        List<ChartPoint> chartValues;
                        if (selection instanceof MolTableSelection) {
                            MolTableSelection mtSel = (MolTableSelection) selection;
                            chartValues = new ArrayList<ChartPoint>(mtSel.size());
                            List<Double> xValues = getValues( mtSel, this.getXLabel() );
                            List<Double> yValues = getValues( mtSel, this.getYLabel() );
                            for (int i = 0;i<mtSel.size();i++) {
                                chartValues.add( new ChartPoint( xValues.get( i ), yValues.get( i ) ) );
                            }
                        } else {
                            chartValues = new ArrayList<ChartPoint>();
                        }

                        return chartValues;
                    }

                    private List<Double> getValues(MolTableSelection mtSel, String property) {
                        List<Double> values = new ArrayList<Double>(mtSel.size());
                        if (property.equals( ChartConstants.ROW_NUMBER ))
                            for(Integer i:mtSel.getSelectedRows())
                                values.add( i.doubleValue() + 1 );
                        else {
                            ICDKMolecule mol;
                            Iterator<ICDKMolecule> mtSelItr = mtSel.iterator();
                            while (mtSelItr.hasNext()) {
                                mol = (ICDKMolecule) mtSelItr.next();
                                if (property.equals( ChartConstants.MOL_MASS )) {
                                    values.add( getValue( mol, MOL_STRUCTURE_COLUMN ) );
                                } else
                                    values.add( getValue( mol, property ) );
                            }
                            mol = (ICDKMolecule) mtSelItr.next();
                            values.add( getValue( mol, property ) );
                        }
                        return values;
                    }

                };
             
                chart.plot( descriptor );

            }
        }

        return null;
    }

    /**
     * Creates a x- or y-value for the plot. If it, by some, reason can't get a 
     * value it will return <code>Double.NaN</code>.
     *   
     * @param mol The molecule that has the wanted property
     * @param property The name of the wanted property
     * @return The value of the wanted property, or <code>Double.NaN</code> if 
     *      it by some reason couldn't be determined
     */
    private double getValue( ICDKMolecule mol, String property ) {
        double value = Double.NaN;
        Object obj = null;
        try {
            if (property.equals( MOL_STRUCTURE_COLUMN ))
                value = cdk.calculateMass(mol);
            else {
                obj = mol.getProperty( property, Property.USE_CACHED_OR_CALCULATED);
                value = Double.parseDouble( obj.toString() );
            }
        } catch ( BioclipseException e ) {
            logger.error( "Could not calculate the molecular mass." );         
        } catch (NumberFormatException e) {
            logger.error( "Not a number: "+obj.toString() ); 
        } 
        return value;
    }

}
