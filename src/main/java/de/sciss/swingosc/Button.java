/*
 *  Button.java
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
 *
 *
 *  Changelog:
 *		15-Apr-06	created from SwingOSC ; slightly modified look ;
 *					new focus ; setNumColumns ; option to use gradient colours
 *		04-May-06	added autoStep option
 *		17-Apr-07	added icon support, ActionEvents have modifiers
 *		02-Jul-07	added getLastModifiers
 */
 
package de.sciss.swingosc;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIDefaults;

/**
 *	A button with a variable number of states.
 *	Each state is defined by a label and colours,
 *	so as to mimic the functionality of SuperCollider's SCButton.
 */
public class Button
extends JButton {
	private int	state			= -1;
	private int	numStates		= 0;
	private int lastModifiers	= 0;
	
	private final List collStateViews	= new ArrayList();	// element class: StateView

	private int numColumns = 0;
	
	private boolean autoStep = true;

	public Button() {
		super();

        // cf. http://stackoverflow.com/questions/8764602/how-to-override-nimbus-button-margins-for-a-single-button
        final UIDefaults defaults = new UIDefaults();
        defaults.put( "Button.contentMargins", new Insets( 0, 0, 0, 0 ));
        putClientProperty( "Nimbus.Overrides", defaults );
	}

    @Override protected void fireActionPerformed( ActionEvent e ) {
        if( autoStep && (numStates > 0) ) {
            state = (state + 1) % numStates;
            stateUpdate();
        }
        lastModifiers = e.getModifiers();
        super.fireActionPerformed( e );
    }
	
	public void setNumColumns( int num )
	{
		if( num != numColumns ) {
			numColumns = num;
			recalcPrefSize();
		}
	}
	
	public int getNumColumns()
	{
		return numColumns;
	}
	
	public void setAutoStep( boolean onOff )
	{
		autoStep = onOff;
	}
	
	public boolean getAutoStep()
	{
		return autoStep;
	}
	
	private void recalcPrefSize()
	{
	}
	
	private void configureTextColor( StateView sv, Color c ) {
		sv.colrLabel    = c; // new Color( argb, true );
	}
		
	private void configureBgColor( StateView sv ) {
		sv.isClear		= true;
	}

	private void configureBgColor( StateView sv, Color c ) {
        sv.colrBack = c;
        sv.isClear  = c.getAlpha() == 0;
	}

	public void setNumItems( int num ) {
		StateView sv;
	
		if( (num >= 0) && (num != numStates ) ) {
		
			if( state >= num ) {
				setSelectedIndex( num - 1 );
			}
			while( numStates > num ) {
				collStateViews.remove( numStates - 1 );
				numStates--;
			}
			while( numStates < num ) {
				sv = new StateView();
				collStateViews.add( sv );
				numStates++;
				configureTextColor( sv, Color.black );
//				configureBgColor( sv, Color.lightGray );
				configureBgColor( sv );
			}
		
			if( numColumns == 0 ) recalcPrefSize();
		}
	}
	
	public void removeAllItems()
	{
		setNumItems( 0 );
	}
	
	public void setItemText( int configureState, String text )
	{
		final StateView sv;
		
		if( configureState < numStates ) {
			sv = (StateView) collStateViews.get( configureState );
			sv.text = text;
			if( numColumns == 0 ) recalcPrefSize();
			if( configureState == state ) {
                stateUpdate();
//                repaint();
            }
		}
	}

    private void stateUpdate() {
        if( state < 0 || state >= collStateViews.size() ) return;

        final StateView sv = (StateView) collStateViews.get( state );
        final Color fg = getForeground();
        final Color bg = getBackground();
        final Color sfg = sv.colrLabel;
        final Color sbg = sv.colrBack;

        if( sv.icon != getIcon() ) setIcon( sv.icon );
        if( !sv.text.equals( getText() )) setText( sv.text );
        if( (sfg == null && fg != null) || (sfg != null && !sfg.equals( fg ))) {
            setForeground( sfg );
        }
        if( sv.isClear ) {
            if( bg != null ) setBackground( null );
//        } else if( sv.isGradient ) {
//
        } else if( bg == null || !bg.equals( sbg )) {
            final Color c = sbg == null ? null : (sbg.getAlpha() == 0xFF ? sbg :
                NimbusHelper.mixColorWithAlpha(
                    NimbusHelper.adjustColor( NimbusHelper.getBaseColor(),
                        5.1498413e-4f, -0.43866998f, 0.24705881f, 0 ), sbg )
            );
            setBackground( c );
        }

//        repaint();
    }
	
	public void setItemIcon( int configureState, Icon icon )
	{
		final StateView sv;
		
		if( configureState < numStates ) {
			sv = (StateView) collStateViews.get( configureState );
			sv.icon = icon;
			if( numColumns == 0 ) recalcPrefSize();
			if( configureState == state ) {
                stateUpdate();
//                repaint();
            }
		}
	}
	
	public void setItemTextColor( int configureState, Color c )
	{
		if( configureState < numStates ) {
			final StateView sv = (StateView) collStateViews.get( configureState );
			configureTextColor( sv, c == null ? Color.black : c );
			if( configureState == state ) {
                stateUpdate();
//                repaint();
            }
		}
	}
	
	public void setItemBgColor( int configureState, Color c )
	{
		if( configureState < numStates ) {
			final StateView sv = (StateView) collStateViews.get( configureState );
			if( c == null ) {
				configureBgColor( sv );
			} else {
				configureBgColor( sv, c );
			}
			if( configureState == state ) {
                stateUpdate();
//                repaint();
            }
		}
	}

	public void setItemBgColor( int configureState, Color c1, Color c2 )
	{
        setItemBgColor( configureState, c1 );
	}

	public void setItem( int configureState, String text, Color clrText, Color clrBg )
	{
		if( configureState < numStates ) {
			final StateView sv = (StateView) collStateViews.get( configureState );
			sv.text = text;
			configureTextColor( sv, clrText == null ? Color.black : clrText );
			if( clrBg == null ) {
				configureBgColor( sv );
			} else {
				configureBgColor( sv, clrBg );
			}
			if( numColumns == 0 ) recalcPrefSize();
			if( configureState == state ) {
                stateUpdate();
//                repaint();
            }
		}
	}
	
	public void setItem( int configureState, String text, Color clrText, Color clrBg1, Color clrBg2 )
	{
        setItem( configureState, text, clrText, clrBg1 );
	}

	public void addItem( Object text )
	{
		addItem( text.toString(), Color.black, null );
	}

	public void addItem( String text, Color clrText, Color clrBg )
	{
		final int		configureState	= numStates;
		final StateView	sv				= new StateView();
		
		collStateViews.add( sv );
		numStates++;
		sv.text = text;
		configureTextColor( sv, clrText == null ? Color.black : clrText );
		if( clrBg == null ) {
			configureBgColor( sv );
		} else {
			configureBgColor( sv, clrBg );
		}
		if( numColumns == 0 ) recalcPrefSize();
		if( configureState == 0 ) setSelectedIndex( configureState );
	}

	public void addItem( String text, Color clrText, Color clrBg1, Color clrBg2 )
	{
        addItem( text, clrText, clrBg1 );
	}

	public void setSelectedIndex( int state ) {
		if( this.state != state ) {
			this.state = state;
            stateUpdate();
		}
	}
	
	public int getSelectedIndex()
	{
		return state;
	}
	
	public int getLastModifiers()
	{
		return lastModifiers;
	}

	// ---------------- internal classes ----------------

	private static class StateView {
		protected String	text;
		protected Color	    colrLabel;
		protected boolean	isClear;
		protected Color     colrBack;
		protected Icon		icon;
		
		protected StateView() {}
	}
}