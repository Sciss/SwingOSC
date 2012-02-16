/*
 *  WebView.java
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

import com.teamdev.jxbrowser.Browser;
import com.teamdev.jxbrowser.BrowserFactory;
import com.teamdev.jxbrowser.events.NavigationEvent;
import com.teamdev.jxbrowser.events.NavigationFinishedEvent;
import com.teamdev.jxbrowser.events.NavigationListener;
import com.teamdev.jxbrowser.events.NavigationType;

import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.AWTEventMulticaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WebView extends JPanel {
    public static final boolean VERBOSE         = true;

    public static final String ACTION_LOADED    = "loaded";

    private static boolean initialized = false;

    private final Browser browser = BrowserFactory.createBrowser();
    private final List hyperlinkListeners   = new ArrayList();
    private ActionListener actionListener = null;

    private String nonManualURL = "";

    private final NavigationListener navListener = new NavigationListener() {
        public void navigationStarted( NavigationEvent e ) {
            final String url = e.getUrl();
            if( VERBOSE ) System.out.println( "Navigation started " + url );
            if( (e.getNavigationType() == NavigationType.NAVIGATE) && !url.equals( nonManualURL )) {
                try {
                    nonManualURL = "";
//                    dispatchLinkActivated( new URI( url ).toURL() );
                } catch( MalformedURLException e2 ) {
                    e2.printStackTrace();
                } catch( URISyntaxException e2 ) {
                    e2.printStackTrace();
                }
            }
        }

        public void navigationFinished( NavigationFinishedEvent e ) {
            if( VERBOSE ) System.out.println( "Navigation finished" );
            dispatchAction( ACTION_LOADED );
        }
    };

    public synchronized void addHyperlinkListener( HyperlinkListener l ) {
        hyperlinkListeners.add( l );
    }

    public synchronized void removeHyperlinkListener( HyperlinkListener l ) {
        hyperlinkListeners.remove(l);
    }

    public synchronized void addActionListener( ActionListener l ) {
        actionListener = AWTEventMulticaster.add( actionListener, l );
    }

    public synchronized void removeActionListener( ActionListener l ) {
        actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

    private void dispatchLinkActivated( URL url ) {
        if( !EventQueue.isDispatchThread() ) System.out.println( "--Ooops. not event thread" );

        final HyperlinkEvent e = new HyperlinkEvent( this, HyperlinkEvent.EventType.ACTIVATED, url );
        final Iterator iter = hyperlinkListeners.iterator();
        while( iter.hasNext() ) {
            final HyperlinkListener l = (HyperlinkListener) iter.next();
            try {
                l.hyperlinkUpdate( e );
            } catch( Exception ex ) {
                ex.printStackTrace();
            }
        }
    }

    private void dispatchAction( String command ) {
        final ActionListener l = actionListener;
        if( l != null ) {
            try {
                l.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, command ));
            } catch( Exception ex ) {
                ex.printStackTrace();
            }
        }
    }

    private static synchronized void init() throws Exception {
        if( !initialized ) {
//            final PlatformInit pi = PlatformInit.getInstance();
////            pi.initOtherProperties();
////            pi.initSecurity();
////            pi.initProtocols();
//////            pi.initHTTP();
////            pi.initExtensions();
//            pi.init( false, false );
            initialized = true;
        }
    }

    public static WebView create() throws Exception {
        init();
        return new WebView();
    }

    public String getURL() {
//        final NavigationEntry ne = getCurrentNavigationEntry();
//        final URL u = ne == null ? null : ne.getUrl();
//        return u == null ? "" : u.toString();
        return "";
    }

    public String getTitle() {
//        final NavigationEntry ne = getCurrentNavigationEntry();
//        final String t = ne == null ? null : ne.getTitle();
//        return t == null ? "" : t;
        return "";
    }

    public void setHtml( String html ) throws IOException {
        final File f    = File.createTempFile( "tmp", ".html" );
        f.deleteOnExit();
        final URL url   = f.toURI().toURL();
        final FileWriter w = new FileWriter( f );
        w.write( html );
        w.close();
        navigate( url );
    }

    public void navigate( String url ) throws MalformedURLException {
        if( VERBOSE ) System.out.println( "navigate: " + url );

//        final int i = url.indexOf( ':' );
//        final String proto = url.substring( 0, i );
//        final String addr  = url.substring( i + 1 );
        try {
//        final URI uri = new URI( proto, addr, "" );
            final URI uri = new URI( url );
            navigate( uri.toURL() );

        } catch( URISyntaxException e ) {
            throw new MalformedURLException( url );
        }
//        browser.navigate(url);
    }

    public void navigate( URL url ) {
//        browser.removeNavigationListener( navListener );
//        try {
        final String urlString = url.toString();
        nonManualURL = urlString;
            browser.navigate( urlString );
//        } finally {
//            browser.addNavigationListener( navListener );
//        }
    }

    public void forward() {
        browser.goForward();
    }

    public void back() {
        browser.goBack();
    }

    public void reload() {
        browser.refresh();
    }

    private WebView() {
        super( new BorderLayout() );
        add( browser.getComponent(), BorderLayout.CENTER );

//        browser.
//
        browser.addNavigationListener( navListener );

//        addContentListener( new ContentListener() {
//            public void contentSet( ContentEvent e ) {
////                final ComponentContent c = getComponentContent();
//                dispatchAction( ACTION_LOADED );
////                final String descr = c.getDescription();
////                final NavigationEntry ne = getCurrentNavigationEntry();
//            }
//        });
//
//        addNavigationListener( new NavigationListener() {
//            public void beforeNavigate( NavigationEvent e ) throws NavigationVetoException {
//                if( e.getTargetType() != TargetType.SELF ) throw new NavigationVetoException();
//            }
//
//            public void beforeLocalNavigate( NavigationEvent e ) throws NavigationVetoException {
//                if( e.isFromClick() ) {
////System.out.println( "beforeLocalNavigate : " + e.getMethod() + " : " + e.getURL() + " : " + e.getTargetType() + " : " + e.getLinkObject() + " : " + e.getParamInfo() + " : " + e.getRequestType() );
//                    dispatchLinkActivated(e.getURL());
//                    throw new NavigationVetoException( "Client handles navigation" );
//                }
//            }
//
//            public void beforeWindowOpen( NavigationEvent e ) throws NavigationVetoException {
//                throw new NavigationVetoException();
//            }
//        });
    }

//    public void readURL( String url ) throws MalformedURLException {
//        navigate( url );
//    }
}
