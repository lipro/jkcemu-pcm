/*
 * (c) 2008-2012 Jens Mueller
 *
 * Kleincomputer-Emulator
 *
 * Basisklasse fuer Swing-Frames
 */

package jkcemu.base;

import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;


public class BasicFrm extends JFrame implements
					ActionListener,
					MouseListener,
					KeyListener,
					WindowListener
{
  protected BasicFrm()
  {
    setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
    addWindowListener( this );
  }


  /*
   * Manchmal funktioniert die pack()-Methode nicht richtig,
   * wenn vorher setResizable(...) aufgerufen wurde.
   * Haeufig wird die pack()-Methode aufgerufen,
   * wenn es keine Einstellungen zu der Fenstergroesse und -position gibt.
   * Demzufolge darf die setResizable(...)-Methode erst nach dem erstmaligen
   * Aufruf dieser Methode erfolgen.
   * Damit nun aber diese Methode weiss, ob das Fenster
   * in der Groesse veraenderbar ist,
   * muss dies explizit mit einem Paramter uebergeben werden.
   *
   * Rueckgabewert: true, wenn Fensterposition bzw. -groesse geaendert wurde
   */
  public boolean applySettings( Properties props, boolean resizable )
  {
    return ((props != null) && !isVisible()) ?
			EmuUtil.applyWindowSettings( props, this, resizable )
			: false;
  }


  /*
   * Diese Methoden erzeugen einen Knopf mit einem Bild.
   * Als ActionListner wird das Frame eingetragen.
   */
  protected JButton createImageButton( String imgName, String text )
  {
    JButton btn = EmuUtil.createImageButton( imgName, text );
    btn.addActionListener( this );
    return btn;
  }


  /*
   * Diese folgenden Methoden erzeugen ein JMenuItem.
   * Als ActionListner wird das Frame eingetragen.
   */
  protected JMenuItem createJMenuItem( String text )
  {
    JMenuItem item = new JMenuItem( text );
    item.addActionListener( this );
    return item;
  }


  protected JMenuItem createJMenuItem( String text, String actionCmd )
  {
    JMenuItem item = createJMenuItem( text );
    item.setActionCommand( actionCmd );
    return item;
  }


  protected JMenuItem createJMenuItem(
				String text,
				int    keyCode,
				int    modifiers )
  {
    return createJMenuItem(
			text,
			KeyStroke.getKeyStroke( keyCode, modifiers ) );
  }


  protected JMenuItem createJMenuItem(
				String    text,
				KeyStroke keyStroke )
  {
    JMenuItem item = createJMenuItem( text );
    item.setAccelerator( keyStroke );
    return item;
  }


  protected JMenuItem createJMenuItem(
				String    text,
				String    actionCmd,
				KeyStroke keyStroke )
  {
    JMenuItem item = createJMenuItem( text, actionCmd );
    item.setAccelerator( keyStroke );
    return item;
  }


  protected JRadioButtonMenuItem createJRadioButtonMenuItem(
					ButtonGroup grp,
					String      text,
					String      actionCmd,
					boolean     selected,
					KeyStroke   keyStroke )
  {
    JRadioButtonMenuItem item = new JRadioButtonMenuItem( text, selected );
    grp.add( item );
    item.setActionCommand( actionCmd );
    item.addActionListener( this );
    item.setAccelerator( keyStroke );
    return item;
  }


  /*
   * Die Methode fordert das Schliessen des Dialoges an und
   * liefert true zurueck, wenn der Dialog auch tatsaechlich
   * geschlossen wurde.
   */
  public boolean doClose()
  {
    setVisible( false );
    dispose();
    return true;
  }


  public void fireRepaint()
  {
    EventQueue.invokeLater(
		new Runnable()
		{
		  public void run()
		  {
		    repaint();
		  }
		} );
  }


  public String getSettingsPrefix()
  {
    return getClass().getName();
  }


  /*
   * Die Methode wird aufgerufen,
   * nachdem sich das Erscheinungsbild geaendert hat.
   * Alle Komponenten, die sich in der Komponentenhierarchie
   * des Frames befinden, werden automatisch aktualisiert.
   *
   * Diese Methode muss dann ueberschrieben werden,
   * wenn Komponenten aktualisiert werden muessen,
   * die sich gerade nicht in in der Komponentenhierarchie befinden
   * (z.B. Popup-Menus).
   */
  public void lookAndFeelChanged()
  {
    // leer
  }


  public void putSettingsTo( Properties props )
  {
    if( props != null ) {
      String prefix = getSettingsPrefix();

      Point location = getLocation();
      if( location != null ) {
	props.setProperty(
			prefix + ".window.x",
			String.valueOf( location.x ) );
	props.setProperty(
			prefix + ".window.y",
			String.valueOf( location.y ) );
      }

      if( isResizable() ) {
	Dimension size = getSize();
	if( size != null ) {
	  if( (size.width > 0) && (size.height > 0) ) {
	    props.setProperty(
			prefix + ".window.width",
			String.valueOf( size.width ) );
	    props.setProperty(
			prefix + ".window.height",
			String.valueOf( size.height ) );
	  }
	}
      }
    }
  }


  public void resetFired()
  {
    // leer
  }


  /*
   * Die Methode setzt die Groesse und Position des Fensters
   * auf einen Standardwert.
   */
  public void setBoundsToDefaults()
  {
    Dimension screenSize = getToolkit().getScreenSize();
    if( screenSize != null ) {
      setBounds(
	screenSize.width  / 4,
	screenSize.height / 4,
	screenSize.width  / 2,
	screenSize.height / 2 );
    } else {
      setSize( 200, 200 );
    }
  }


  /*
   * Die Methode zentriert das Frame auf dem Bildschirm.
   */
  public void setScreenCentered()
  {
    Dimension screenSize = getToolkit().getScreenSize();
    if( screenSize != null ) {
      Dimension mySize = getSize();
      int       x      = (screenSize.width - mySize.width) / 2;
      int       y      = (screenSize.height - mySize.height) / 2;
      setLocation( x >= 0 ? x : 0, y >= 0 ? y : 0 );
    }
  }


  /*
   * Die Methode schaltet den Warte-Cursor ein bzw. aus.
   */
  public void setWaitCursor( boolean state )
  {
    Component c = getGlassPane();
    if( c != null ) {
      c.setVisible( state );
      c.setCursor( state ?
	Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) : null );
    }
  }


	/* --- ActionListener --- */

  @Override
  public void actionPerformed( ActionEvent e )
  {
    doActionInternal( e );
  }


	/* --- MouseListener --- */

  @Override
  public void mouseClicked( MouseEvent e )
  {
    if( showPopupInternal( e ) ) {
      e.consume();
    } else if( e.getClickCount() > 1 ) {
      if( doActionInternal( e ) ) {
	e.consume();
      }
    }
  }

  @Override
  public void mouseEntered( MouseEvent e )
  {
    // leer
  }

  @Override
  public void mouseExited( MouseEvent e )
  {
    // leer
  }

  @Override
  public void mousePressed( MouseEvent e )
  {
    if( showPopupInternal( e ) )
      e.consume();
  }

  @Override
  public void mouseReleased( MouseEvent e )
  {
    if( showPopupInternal( e ) )
      e.consume();
  }


	/* --- KeyListener --- */

  @Override
  public void keyPressed( KeyEvent e )
  {
    if( e != null ) {
      if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
	if( doActionInternal( e ) ) {
	  e.consume();
	}
      }
    }
  }

  @Override
  public void keyReleased( KeyEvent e )
  {
    // leer
  }

  @Override
  public void keyTyped( KeyEvent e )
  {
    // leer
  }


	/* --- WindowListener --- */

  @Override
  public void windowActivated( WindowEvent e )
  {
    // leer
  }

  @Override
  public void windowClosed( WindowEvent e )
  {
    // leer
  }

  @Override
  public void windowClosing( WindowEvent e )
  {
    doClose();
  }

  @Override
  public void windowDeactivated( WindowEvent e )
  {
    // leer
  }

  @Override
  public void windowDeiconified( WindowEvent e )
  {
    // leer
  }

  @Override
  public void windowIconified( WindowEvent e )
  {
    // leer
  }

  @Override
  public void windowOpened( WindowEvent e )
  {
    // leer
  }


	/* --- zu ueberschreibende Methoden --- */

  protected boolean doAction( EventObject e )
  {
    return false;
  }


  protected boolean showPopup( MouseEvent e )
  {
    return false;
  }


	/* --- private Methoden --- */

  private boolean doActionInternal( EventObject e )
  {
    boolean rv = false;
    setWaitCursor( true );
    try {
      rv = doAction( e );
    }
    catch( Exception ex ) {
      EmuUtil.exitSysError( this, null, ex );
    }
    setWaitCursor( false );
    return rv;
  }


  private boolean showPopupInternal( MouseEvent e )
  {
    boolean rv = false;
    if( e != null ) {
      if( e.isPopupTrigger() ) {
	rv = showPopup( e );
      }
    }
    return rv;
  }
}

