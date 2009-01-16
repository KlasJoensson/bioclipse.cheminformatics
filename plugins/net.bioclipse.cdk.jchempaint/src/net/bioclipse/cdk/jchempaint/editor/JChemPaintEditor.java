/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Arvid Berg
 *
 ******************************************************************************/
package net.bioclipse.cdk.jchempaint.editor;

import java.awt.Color;
import java.util.Iterator;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.jchempaint.outline.CDKChemObject;
import net.bioclipse.cdk.jchempaint.outline.JCPOutlinePage;
import net.bioclipse.cdk.jchempaint.widgets.JChemPaintEditorWidget;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.controller.IControllerModel;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

public class JChemPaintEditor extends EditorPart implements ISelectionListener{

    Logger logger = Logger.getLogger( JChemPaintEditor.class );

    private JCPOutlinePage fOutlinePage;

    boolean dirty=false;
    ICDKMolecule model;
    JChemPaintEditorWidget widget;
    IControllerModel c2dm;
    SWTMouseEventRelay relay;
    Menu menu;

    public JChemPaintEditorWidget getWidget() {
    	return widget;
    }

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {

	    setSite(site);
        setInput(input);
        ICDKMolecule cModel = (ICDKMolecule)input.getAdapter( ICDKMolecule.class );
        if(cModel == null){
            IFile file = (IFile) input.getAdapter(IFile.class);
            if(file != null)
		            cModel=(ICDKMolecule)  file.getAdapter(ICDKMolecule.class);
        }
		if(cModel != null ){



		setPartName(input.getName());
		model=cModel;
		model.getAtomContainer().addListener(new IChemObjectListener(){
		   public void stateChanged(IChemObjectChangeEvent event) {

		       if(!isDirty()){
		           dirty=true;
		           Display.getDefault().syncExec( new Runnable() {
		               public void run() {
		                   firePropertyChange(IEditorPart.PROP_DIRTY);
		               }
		           });

		       }
		    }
		});
		}
//		widget.setAtomContainer(model.getMoleculeSet().getAtomContainer(0));
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
	    //  create widget
		widget=new JChemPaintEditorWidget(parent,SWT.NONE);
		IAtomContainer atomContainer=null;
		if(model!=null)
		    atomContainer=model.getAtomContainer();


		MenuManager menuMgr = new MenuManager();
	  menuMgr.add( new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	  getSite().registerContextMenu( menuMgr, widget);
	  //getSite().registerContextMenu( "net.bioclipse.cdk.ui.editors.jchempaint.menu",
	    //                             menuMgr, widget);

	  //Control control = lViewer.getControl();
	  menu = menuMgr.createContextMenu(widget);
	  widget.setMenu(menu);


		// setup hub
		getSite().setSelectionProvider( widget );
		getSite().getPage().addSelectionListener(this);
		widget.setAtomContainer( atomContainer );

		parent.addDisposeListener( new DisposeListener () {

            public void widgetDisposed( DisposeEvent e ) {
                disposeControll( e );
            }
		});

	}

    @Override
	public void setFocus() {
		widget.setFocus();
	}

    public ControllerHub getControllerHub() {
        return widget.getControllerHub();
    }

    public IControllerModel getControllerModel() {
        return c2dm;
    }

    public void update() {
        widget.redraw();
    }

    public void setInput( Object element ) {
        widget.setInput( element );
        widget.redraw();
    }

    public ICDKMolecule getCDKMolecule() {
        return model;
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        if (IContentOutlinePage.class.equals(adapter)) {
            if (fOutlinePage == null) {
                fOutlinePage= new JCPOutlinePage(getEditorInput(), this);
            }
            return fOutlinePage;
        }
        if (IAtomContainer.class.equals( adapter )) {
            return model.getAtomContainer();
        }
        return super.getAdapter(adapter);
    }

    public void doAddAtom() {

        logger.debug( "Executing 'Add atom' action" );
    }
    public void doChageAtom() {
        logger.debug( "Executing 'Chage atom' action" );
    }

    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
        if(part != null && part.equals( this )) return;
        if(selection instanceof IStructuredSelection) {
            Object structSel = ((IStructuredSelection)selection).getFirstElement();

            if (structSel instanceof CDKChemObject && ((CDKChemObject)structSel).getChemobj() instanceof IChemObject) {
                IAtomContainer container = ((CDKChemObject)structSel).getChemobj().getBuilder().newAtomContainer();
                for(Iterator<?> iter =((IStructuredSelection)selection).iterator();iter.hasNext();) {
                    Object c = iter.next();
                    if(! (c instanceof CDKChemObject)) {
                        continue;
                    }
                    IChemObject o = ((CDKChemObject)c).getChemobj();
                    if(o instanceof IAtom) {
                        container.addAtom( (IAtom)o );
                    }else if( o instanceof IBond) {
                        container.addBond( (IBond)o);
                    }

                }
                widget.getRenderer2DModel().setExternalHighlightColor( Color.ORANGE );
                widget.getRenderer2DModel().setExternalSelectedPart( container );
                widget.redraw();
            }
        }

    }

    private void disposeControll(DisposeEvent e) {
        // TODO remove regiistration?
        //getSite().registerContextMenu( "net.bioclipse.cdk.ui.editors.jchempaint.menu",
        //                               menuMgr, widget);

        getSite().setSelectionProvider( null );
        getSite().getPage().removeSelectionListener( this );

        widget.dispose();
        menu.dispose();
    }
}
