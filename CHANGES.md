## SwingOSC Version History

### v0.70 (apr 2012)

 - new components: JSCWebView, based on JxBrowser; JSCLevelIndicator (still very plain); native JSCKnob
 - fixes: JSCButton modifiers; s.meter returns window; JSCWindow full screen and alpha; initial JSCNumberBox space; sub-pixel coordinates in user view, envelope view and multislider view; unicodes in JSCTextField despite sclang's broken Char/String interaction; JSCView bounds argument is copied (fixes mutability)
 - updates: speech works with newer freetts, PDF example uses newer iTextPDF
 - adds various methods required for smooth operation with SC 3.5: JSCFont; shutdown hooks (thanks to james harkins); SwingDialog.openPanel; JSCWindow.drawHook deprecated; JPen.lineDash accepts empty array; JSCView has mouseWheel, mouseEnter, mouseLeave, onMove and onResize methods
 - uses Nimbus look-and-feel for default components, and Nimbus resembling style for custom components. Add --nimbus switch to command line.
 - more consistent component naming: JFont becomes JSCFont, JSpeech becomes JSCSpeech, JStethoscope becomes JSCStethoscope, JTexturePaint becomes JSCTexturePaint, JFreqScope becomes JSCFreqScopeWindow, JSC2DSlider becomes JSCSlider2D
 - slight changes: JSCWindow appears centred on screen by default
 - regression: the native tablet view is currently disabled
 - switched from SourceForge/svn to GitHub/git
 - switched from Eclipse/ant to IntelliJ IDEA/sbt for development
 - now includes JMF (for JSCMovieView)
 - SwingOSC.jar now in base directory

### v0.65 (jul 2010 - SVN rev. 252)

 - SC classes: JSCTextField and JSCNumberBox (added `borderless`, `setNormalBorder`, `setLineBorder`), JSCMultiSlider (added `steady`, `precision` - thanks to Tim Blechmann), JSCPeakMeter (added `orientation`), JSCImage (added *color, setPixels, SwingGUI.image), JFont (available faces are now stored in archive, so can be accessed synchronously), Server (doWhenBooted forks functions now, fixed a problem with Model/ dependants)
 - Added missing images in Den Haag tutorial
 - Added Debian packaging info by Dan Stowell

### v0.64 (feb 2010 - SVN rev. 235)

 - SC classes: JSCTextView (typo in `caret`, support for RTF, various fixes, `htmlString`, better tab size, undo/redo, `selectedString` fix, paragraph attributes), JSCSlider (adaptive knob size on OS X look-and-feel), JSCListView (fix for big bundles), JSCPopUpMenu (fix for big bundles), JSCNumberBox (helpfile)
 - Ensuring bundles are only made up of Arrays not Lists, fixing a problem with Linux and Windows

### v0.63 (oct 2009 - SVN rev. 218)

 - SC classes: JSCPeakMeter (added setRefreshRate, more CPU efficient peak metering), JSCScrollView (faster mouse wheel scrolling on osx and windows), JSCMovieView (mouse and keyboard events now enabled)
 - Tutorial: updating Spinner class
 - Improved support for remote servers

### v0.62 (jul 2009 - SVN rev. 207)

 - SC classes: JSCView (mouse coordinates now relative to view's left-top, added scheme), JSCUserView (added clearDrawing, refresh is &quot;lazy&quot;, mousePosition initially reports 0@0), JSCImage (renamed from JImage), JPen (added fillAxialGradient, fillStroke uses draw( 3 )), JKnob (added `resize`), JSCPeakMeter finally works
 - Bug fixes: 2809848, 2809958, 2001007, 2820992, 1901530
 - Help files: referring to ViewRedirect classes

### v0.61 (may 2009 - SVN rev. 181)

 - Server: performance improvement, improved booting from SC
 - SC classes: moved OSX specific stuff to separate folder, adding (still experimental) JSCMenuNode, JPen (`joinStyle`, `lineDash`, `alpha`), adding JavaObjectD, SwingGUI (stringBounds), JSCTextView (open, linkAction), JSCContainerView (relativeOrigin), JSCWindow (initAction, toFrontAction, endFrontAction), JSCView (absoluteBounds, focusGainedAction, focusLostAction), JSCUserView (relativeOrigin true), JSCPeakMeter added, JSCEnvelopeView (all curve shapes should work now), keyboard value scrolling (for JSCSlider etc.)
 - Refactoring: using ScissLib (application framework with window handler customization), now JCollider (for peakmeter, in future version for scope etc.)
 - Bug fixes: 2037842, 2482337
 - New features: 1849186

### v0.60 (may 2008 - SVN rev. 77)

 - sc classes: fixes for control rate scoping, JSCView (handleKeyUp/DownBubbling, `focusColor` dummy,), JPen (addArc, addWedge, addAnnularWedge, setSmoothing), JSCListView (bounds), JSCTextView (bounds, open), adding coarse String-bounds estimator
 - synced with new NetUtil and common classes version
 - fixed loopback for -h rendezvous option (addresses problems on Linux and Windows Vista)
 - Mac OS X tablet library now universal binary

### v0.59 (feb 2008 - SVN rev. 56)

 - sc classes: JavaObject (added double underscore syntax, calling prRemove from JSCPlug(Container)View), SwingOSC (separate java field, faster booting, bootSync fix), SwingOptions (javaOptions field), SwingGUI (updated), JSCMultiSlider (`valueAction` fix), JSCPopUpMenu (`value` fix), JSCListView (`value` fix), JKnob (was broken), removed JEZSlider, JEZNumber.
 - java classes: DummyTransferHandler sends file-lists

### v0.58 (jan 2008 - SVN rev. 47)

 - sc classes: SwingOSC (aliveThread has longer timeout, improved isLocal detection), JavaObject (added print, isNull, notNull methods), extSwingOSC (fixed asSwingArg implementations, removed jinspect, jplot, jmakeWindow, jscope, jbrowse)
 - sc gui classes: JSCWindow (added `resizable`, redefined `drawHook` refresh policy, cocoa-bounds translation look-and-feel independant, added `scroll` argument to contructor), JSCNumberBox (drag'n'drop fixed), JSCTextField (drag'n'drop fixed), JSCUserView (added `mousePosition`, `clearOnRefresh` methods), JSCPlug(Container)View (improved doesNotUnderstand), JSCView (proper layout manager validation with `bounds` and `visible`, added `addAction` and `removeAction` methods), SwingGUI (added tabletView, scrollView), extCocoaCompat (removed methods that have been added to the standard library), JSCTextView (fixed property changes when document is empty), removed JSCScrollPane, JStartRow, added JSCScrollView, JSCScrollTopView, JSCTabletView
 - sc helper classes improvements: Collapse (function call error protection), Insets (added leftTop method), added UpdateListener
 - sc help files: completed, partly reformatted
 - java classes: SwingOSC (fixed checkMethodArgs type priorities)
 - java gui classes: ActionResponder (bug fix), WindowResponder (bounds-to-cocoa translation), ColliderLayout (to work with scrollview), MouseResponder and ComponentResponder (to work with relativeOrigin parents), ContentPane (extracted from Frame), DummyTransferHandler (preserve clipboard cut/copy/paste actions), Frame (scroll support, cocoa-bounds translation, setAlpha method), NumberField (String based setNumber method to avoid roundoff errors), Pen (fixed stroke shape transform when shearing, support for relativeOrigin, bug fixes), RangeSlider (more pretty knob scaling)
 - java helper classes: AudioFile (added retrieveType method)

### v0.56 (oct 2007)

 - sc and java classes: bug fixes (alpha tranparency, FlowView support)

### v0.55 (sep 2007)

 - added tutorial from the den haag symposium
 - sc classes: bug fixes, enhancements in JavaObject
 - java classes: updates and compatibility with Forest/Timebased project

### v0.54 (jul 2007)

 - osc commands: added remove and update commands to "/classes"
 - sc classes: added JSCScrollBar, updated SwingGUI, SwingOSC, caching option for JSCSoundFileView, curve shapes for JSCEnvelopeView

### v0.53 (jul 2007)

 - updated NetUtil, assuring UTF-8 string encoding
 - fixed -h option
 - sc classes: updated SwingGUI, crucial support, various fixes

### v0.52 (apr 2007)

 - bug fixes: JSCWindow-bounds
 - new classes: JKeyState, JStartRow, SwingGUI
 - help files converted to HTML (c. 2/3 still need proper clean-up though)

### v0.513 (apr 2007)

 - fixes a number of known issues (`JSCView.bounds`, `JSCEnvelopeView.value`, `JSCPopUpMenu.item`, `JSCListView.item`)

### v0.51 (mar 2007)
 - includes NetUtil 0.32 (= fixes umlaut problems)
 - using Apache Ant now for building
 - removed non-breaking-spaces from help files (= avoids PsyCollider bug)
 - bug fixes in SC classes: continuous text/number updates while typing in JSCTextField, JSCNumberBox ; graphics glitches with JSCSoundFileView and JSCEnvelopeView on Linux/Windows ; bounds updates when parent views move

### v0.50 (feb 2007)

 - java classes: fixed opacity and background colour issues. added layout managers for supercollider, added AbstractMultiSlider, EnvelopeView, TextView, DocumentResponder, ActionMessenger. lot's of fixes and improvement (MovieView, RangeSlider, SoundFileView, Pen, MouseResponder, Frame)
 - sc classes: added JSCEnvelopeView, finished JSCTextView, fixed JSCCompositeView, JSCHLayoutView, JSCVLayoutView, added support for elastic views. Dozens of fixes and improvements (JPen, JSlider, JRangeSlider, JSCSoundFileView, JSCMovieView, JSCWindow, JFont, JEZSlider, JEZNumber, probably others)
 - .sh shell scripts now specify Gtk+ look-and-feel. Edit them if you want to return to metal look-and-feel.

### v0.44 (dec 2006)

 - new sc classes: JSCSoundFileView, JSCMovieView, JSCTabbedPane, JSCCheckBox
 - fixes: JFont, JSCTextField, JPen

### v0.43 (oct 2006)

 - sc classes: JSCMultiSliderView

### v0.42 (oct 2006)

 - adds dynamic class loading and "/classes" OSC command
 - uses updated NetUtil, and hence has TCP server mode (TCP is default now)
 - renamed `OSCClient.java` to `SwingClient.java`
 - sc classes: fixes (keyboard actions in JSC2DSlider, opacity in JSCCompositeView, focus in JSCSlider), missing classes (SwingDialog), modifications (string operations in JPen)
 - error messages are printed to `System.out` not `System.err`, so they are visible in SuperCollider's post window
 - v0.421 makes `new()` and `start()` in `SwingOSC.java` public (e.g. to be accessible by Eisenkraut)

### v0.41 (aug 2006)

 - fixes for JSCCompositeView, completions for JSCWindow
 - includes JSCPlugView, JSCPlugContainerView, JavaObject

### v0.40 (apr 2006)

 - OSC syntax change: using nested blob'ed OSC messages is deprecated ; instead nesting is achieved by using special string arguments. this makes SwingOSC really usable in other clients such as Pure Data.
 - added first examples for Pure Data
 - sc classes: all modified to use the new OSC syntax. added JPen, JSCUserView, JSCTextView, fixed JSCHLayoutView and JSCVLayoutView, added mouse, component resize and focus traversal tracking, modified the JMouseBase gui, optimized performance, fixed a lot of bugs.
 - MultiStateButton was moved to the `de.sciss.gui` package and is slightly modified.

### v0.39 (mar 2006)

 - sc classes: `SwingOSC.sc` : booting now works. Help file updated

### v0.38 (feb 2006)

 - sc classes: finished JStethoscope / JSCScope / `.jscope` plusGUI
 - sc classes: converted FreqScope by Lance Putnam
 - added `-h` option

### v0.37 (feb 2006)

 - cleanup and slight performance improvisation
 - added `/field` and `/fieldr` commands
 - messages whose arguments contain nested messages are evaluated strictly sequentially, making side effects much more stringent
 - bug fixes: `/free`
 - removed `"toolkit"` object assignment which caused the `-i` option to be ineffective. you can restore the original binding by sending a message `[ "/local", \toolkit, [ "/method", "java.awt.Toolkit", \getDefaultToolkit ]]`

### v0.36 (dec 2005)

 - added JSCDragSource/Sink/Both supercollider classes
 - added keyboard + drag'n'drop support in supercollider classes
 - optimization and fixes in supercollider classes

### v0.35 (dec 2005)

 - switched to Eclipse for development
 - added RangeSlider and Slider2D classes (+ related SC classes)
 - added `JMacUGens.sc` supercollider class
 - added `/import` package lookup mechanism (not yet accessible though)

### v0.3 (nov 2005)

 - reworked (new) OSC command structure
 - prelimary SC classes

### v0.2 (aug 2005)

 - includes example of the possibility to instantiate applets (nice for integrating Processing)
