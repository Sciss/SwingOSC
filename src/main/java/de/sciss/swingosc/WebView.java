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

import com.teamdev.jxbrowser.*;
import com.teamdev.jxbrowser.events.*;

import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.AWTEventMulticaster;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WebView extends JPanel {
    public static final boolean MOZILLA         = false;

    public static final boolean VERBOSE         = false;

    public static final String ACTION_LOADED    = "loaded";
    public static final String ACTION_FAILED    = "failed";

    private static boolean initialized = false;

    private final Browser browser = MOZILLA ? BrowserFactory.createBrowser( BrowserType.Mozilla ) : BrowserFactory.createBrowser();
    private final List hyperlinkListeners   = new ArrayList();
    private ActionListener actionListener = null;

    private final String dummyURLString = escape( "file:" + new File( "" ).getAbsolutePath() );
    private final URL dummyURL;

    volatile private URL nonManualURL = null;
//    private String nonManualURLString = null;

    volatile private boolean vetoEnabled = false;

//    private String nextURL;
    private String lastURL;
    private String result = "";

    private final WebPolicyDelegate veto = new DefaultWebPolicyDelegate() {
        @Override
        public boolean allowNavigation( NavigationEvent e ) {
            if( !vetoEnabled ) return true;

            if( VERBOSE ) System.out.println( "allowNavigation: " + e.getUrl() + " ; type = " + e.getNavigationType() );
            if( e.getNavigationType() == NavigationType.REDIRECT ) return true;
            try {
                final URL url = new URL( e.getUrl() );
                if( url.equals( dummyURL )) return true;
                // the comparison never works in all cases :-(
                final boolean sameURL = nonManualURL != null; // url.equals( nonManualURL );
                if( VERBOSE ) System.out.println( "allowNavigation: is " + url.toString() + " same as " + (nonManualURL == null ? null : nonManualURL.toString()) + " ? " + sameURL );
                if( !sameURL ) {
                    defer( new Runnable() { public void run() { dispatchLinkActivated( url );}});
                } else {
                    nonManualURL = null;
                }
                return sameURL;
            } catch( MalformedURLException e2 ) {
                e2.printStackTrace();
                return true;
            }
        }
    };

    private void defer( Runnable r ) {
        EventQueue.invokeLater( r );
    }

    private final NavigationListener navListener = new NavigationListener() {
        public void navigationStarted( NavigationEvent _e ) {
            if( VERBOSE ) System.out.println( "navigationStarted " + _e.getUrl() );
            final NavigationEvent e = _e;
//            defer( new Runnable() {
//                public void run() {
//                    try {
//                        final URL url = new URL( e.getUrl() );
//                        if( url.equals( dummyURL )) return;
//                        final boolean sameURL = url.equals( nonManualURL );
//                        if( VERBOSE ) System.out.println( "Navigation started " + url.toString() + " / same? " + sameURL );
//                        if( (e.getNavigationType() == NavigationType.NAVIGATE) && !sameURL ) {
//                            nonManualURL = null;
//                            dispatchLinkActivated( url );
//                        }
//                    } catch( MalformedURLException e2 ) {
//                        e2.printStackTrace();
//        //            } catch( URISyntaxException e2 ) {
//        //                e2.printStackTrace();
//                    }
//                }
//            });
        }

        public void navigationFinished( NavigationFinishedEvent e ) {
            if( VERBOSE ) System.out.println( "navigationFinished " + e.getUrl() + " ; status = " + e.getStatusCode() );
//            final boolean ok = e.getStatusCode().equals(NavigationStatusCode.OK );
            final boolean ok = e.getStatusCode().getValue() == 0L;
            final String url = e.getUrl();
            defer( new Runnable() {
                public void run() {
                    lastURL = url;
                    dispatchAction( ok ? ACTION_LOADED : ACTION_FAILED );
                }
            });
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

//    public float getZoom() { return browser.getServices(). ... }

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
        result = command;
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
            // disable Mozilla profile creation in ~/.JxBrowser:
            // http://www.teamdev.com/downloads/jxbrowser/docs/JxBrowser-PGuide.html#properties
            // --- unfortunately that still extracts the XUL runner
            System.setProperty( "jxbrowser.xulrunner.use.profile", "false" );
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

    public boolean getVetoEnabled() { return vetoEnabled; }

    public void setVetoEnabled( boolean b ) {
        vetoEnabled = b;
    }

    public String getURL() {
        final String u = lastURL; //  browser.getCurrentLocation();
//        final NavigationEntry ne = getCurrentNavigationEntry();
//        final URL u = ne == null ? null : ne.getUrl();
        return u == null ? "" : u; // .toString();
    }

    public String getTitle() {
        final String t = browser.getTitle();
//        final NavigationEntry ne = getCurrentNavigationEntry();
//        final String t = ne == null ? null : ne.getTitle();
        return t == null ? "" : t;
    }

    public String getResult() {
        return result;
    }

    public void setHtml( String html ) throws IOException {
//        browser.setContent( html );
        nonManualURL = dummyURL;
//        nonManualURLString = dummyURLString;
        browser.setContent( html, dummyURLString );
//        final File f    = File.createTempFile( "tmp", ".html" );
//        f.deleteOnExit();
//        final URL url   = f.toURI().toURL();
//        final FileWriter w = new FileWriter( f );
//        w.write( html );
//        w.close();
//        navigate( url );
    }

    public void navigate( String url ) throws MalformedURLException {
//        if( VERBOSE ) System.out.println( "navigate: " + url );

//        final int i = url.indexOf( ':' );
//        final String proto = url.substring( 0, i );
//        final String addr  = url.substring( i + 1 );
//        try {
//        final URI uri = new URI( proto, addr, "" );
//            final URI uri = new URI( url );
            final String url1 = escape( url );
            if( VERBOSE ) System.out.println( "navigate string " + url1 );
//            final URI uri = new URI( url1 );
            navigate( new URL( url1 ));

//        } catch( URISyntaxException e ) {
//            throw new MalformedURLException( url );
//        } catch( UnsupportedEncodingException e ) {
//            throw new MalformedURLException( url );
//        }
//        browser.navigate(url);
    }

    // XXX ought to use apache commons-httpclient, but this adds another 500K at least
    private String escape( String url ) throws MalformedURLException {
        final String url1;
        if( url.startsWith( "file:/" ) && url.length() > 6 && url.charAt( 6 ) != '/' ) {
            url1 = "file://" + url.substring( 5 );
        } else {
            url1 = url;
        }
        return URLUtil.encodeFuckApache( url1, "UTF-8" );
//        return url.replace( " ", "%20" );
//        final int i = url.indexOf( ':' );
//        if( i >= 0 ) {
//            final String proto = url.substring( 0, i );
//            final String path  = url.substring( i + 1 );
//            return proto + ":" + URLEncoder.encode( path, "UTF-8" );
//        } else {
//            return URLEncoder.encode( url, "UTF-8" );
//        }
    }

    public void navigate( URL url ) {
//        browser.removeNavigationListener( navListener );
//        try {
        final String urlString = url.toString();
        if( VERBOSE ) System.out.println( "navigate: " + urlString );
        nonManualURL = url;
//        nonManualURLString = urlString;
        browser.stop();
//        vetoEnabled = false;
//        try {
            browser.navigate( urlString );
//        browser.navigate( urlString + "?programmatic=true" );
//        } finally {
//            vetoEnabled = true;
//        }
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

    private WebView() throws MalformedURLException {
        super( new BorderLayout() );
        dummyURL = new URL( dummyURLString );
        add( browser.getComponent(), BorderLayout.CENTER );

//        browser.
//
        browser.addNavigationListener( navListener );

        browser.getServices().setWebPolicyDelegate( veto );

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
