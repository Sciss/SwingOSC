/*
 *  Application.java
 *  (SwingOSC)
 *
 *  Copyright (c) 2005-2012 Hanns Holger Rutz. All rights reserved.
 *
 *	This software is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either
 *	version 2, june 1991 of the License, or (at your option) any later version.
 *
 *	This software is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *	General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public
 *	License (gpl.txt) along with this software; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *	For further information, please contact Hanns Holger Rutz at
 *	contact@sciss.de
 */

package de.sciss.swingosc;

//import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.plaf.MenuBarUI;
//import javax.swing.KeyStroke;

import de.sciss.app.AbstractApplication;
import de.sciss.app.Document;
import de.sciss.app.DocumentHandler;
import de.sciss.app.DocumentListener;
import de.sciss.common.BasicApplication;
import de.sciss.common.BasicMenuFactory;
import de.sciss.common.BasicWindowHandler;
//import de.sciss.gui.MenuGroup;
//import de.sciss.gui.MenuItem;

public class Application
extends BasicApplication
{
	private final boolean	lafDeco;
	private final boolean	internalFrames;
	private final boolean	floating;

    private MenuBarUI aquaMenuBarUI;

	public Application()
	{
		this( false, false, false );
	}
	
	public Application( boolean lafDeco, boolean internalFrames, boolean floating )
	{
		super( Application.class, "SwingOSC" );
		
		this.lafDeco		= lafDeco;
		this.internalFrames	= internalFrames;
		this.floating		= floating;

        if( SwingOSC.isMacOS() && Boolean.parseBoolean( System.getProperty( "apple.laf.useScreenMenuBar", "false" ))) {
            try {
                final Class aquaClazz = Class.forName( "com.apple.laf.AquaMenuBarUI" );
                aquaMenuBarUI = (MenuBarUI) aquaClazz.newInstance();
            } catch( Exception e1 ) {
                // ignore
            }
        }

		init();
	}
	
	public static void ensure()
	{
		ensure( false, false, false );
	}
	
	public static void ensure( boolean lafDeco, boolean internalFrames, boolean floating )
	{
		BasicApplication app = (BasicApplication) AbstractApplication.getApplication(); 
		if( app == null ) {
			app = new Application( lafDeco, internalFrames, floating );
		}
		SwingOSC.getInstance().getCurrentClient().locals.put(
			"menuRoot", app.getMenuBarRoot() );
	}
	
	public double getVersion() { return SwingOSC.VERSION; }
	public String getMacOSCreator() { return "????"; };
	
	protected BasicWindowHandler createWindowHandler()
	{
		return new BasicWindowHandler( this, lafDeco, internalFrames, floating ) {
            @Override
            public boolean usesScreenMenuBar() {
                return aquaMenuBarUI != null;
            }
        };
	}
	
	protected BasicMenuFactory createMenuFactory()
	{
		return new BasicMenuFactory( this ) {
			public void addMenuItems() {
//				MenuGroup	mg;
//				MenuItem	mi;
				
				remove( get( "file" ));
//				mg = new MenuGroup( "file", getResourceString( "menuFile" ));
//				add( mg, 0 );
//				mi = new MenuItem( "close", getResourceString( "menuClose" ),
//				   KeyStroke.getKeyStroke( KeyEvent.VK_W, MENU_SHORTCUT ));
//				mg.add( mi );
				remove( get( "edit" ));
//				mi = new MenuItem( "minimize", getResourceString( "menuMinimize" ),
//					KeyStroke.getKeyStroke( KeyEvent.VK_M, MENU_SHORTCUT ));
//				mg = (MenuGroup) get( "window" );
//				mg.add( mi, 0 );
				remove( get( "window" ));
				remove( get( "help" ));
			}
			
			public void showPreferences() { /* none */ }
			public void openDocument( File f ) { /* none */ }
			public Action getOpenAction() { return null; }

            @Override
            protected JComponent createComponent( Action a ) {
                final JMenuBar mb = new JMenuBar();
                if( aquaMenuBarUI != null ) {
                    mb.setUI( aquaMenuBarUI );
                }
                return mb;
           	}
		};
	}
	
	protected DocumentHandler createDocumentHandler()
	{
		return new DocumentHandler() {
			public void addDocument( Object source, Document doc ) { /* nothing */ }
			public void removeDocument( Object source, Document doc ) { /* nothing */ }
			public void setActiveDocument( Object source, Document doc ) { /* nothing */ }
			public void addDocumentListener( DocumentListener l ) { /* nothing */ }
			public void removeDocumentListener( DocumentListener l ) { /* nothing */ }
			public Document getActiveDocument() { return null; }
			public Document getDocument( int i ) { return null; }
			public int getDocumentCount() { return 0; }
			public boolean isMultiDocumentApplication() { return false; }
		};
	}
}
