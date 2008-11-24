/*
 * (c) 2008 Jens Mueller
 *
 * Kleincomputer-Emulator
 *
 * Dialog fuer Eingabe von Bytes
 */

package jkcemu.tools.hexeditor;

import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.text.ParseException;
import java.util.EventObject;
import javax.swing.*;
import jkcemu.base.BasicDlg;


public class ReplyBytesDlg extends BasicDlg
{
  public enum InputFormat { HEX8, DEC8, DEC16, DEC32, STRING };

  private byte[]       approvedBytes;
  private String       approvedText;
  private InputFormat  approvedInputFmt;
  private boolean      approvedBigEndian;
  private JRadioButton btnHex8;
  private JRadioButton btnDec8;
  private JRadioButton btnDec16;
  private JRadioButton btnDec32;
  private JRadioButton btnString;
  private JRadioButton btnLittleEndian;
  private JRadioButton btnBigEndian;
  private JLabel       labelByteOrder;
  private JTextField   fldInput;
  private JButton      btnOK;
  private JButton      btnCancel;


  public ReplyBytesDlg(
		Window      owner,
		String      title,
		InputFormat inputFmt,
		boolean     bigEndian,
		String      text )
  {
    super( owner, title != null ? title : "Eingabe" );
    this.approvedBytes     = null;
    this.approvedText      = null;
    this.approvedInputFmt  = null;
    this.approvedBigEndian = false;


    // Fensterinhalt
    setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints(
						0, 0,
						1, 1,
						0.0, 0.0,
						GridBagConstraints.WEST,
						GridBagConstraints.NONE,
						new Insets( 5, 5, 0, 5 ),
						0, 0 );


    // Eingabebereich
    add( new JLabel( "Bytes eingeben als:" ), gbc );

    this.labelByteOrder = new JLabel( "Byte-Anordnung:" );
    gbc.gridx++;
    add( this.labelByteOrder, gbc );

    ButtonGroup grpType  = new ButtonGroup();
    ButtonGroup grpOrder = new ButtonGroup();

    this.btnHex8 = new JRadioButton( "8-Bit hexadezimale Zahlen", true );
    this.btnHex8.setMnemonic( KeyEvent.VK_H );
    this.btnHex8.addActionListener( this );
    grpType.add( this.btnHex8 );
    gbc.insets.top = 0;
    gbc.gridx      = 0;
    gbc.gridy++;
    add( this.btnHex8, gbc );

    this.btnLittleEndian = new JRadioButton( "Little Endian", !bigEndian );
    this.btnLittleEndian.setMnemonic( KeyEvent.VK_L );
    this.btnLittleEndian.addActionListener( this );
    grpOrder.add( this.btnLittleEndian );
    gbc.gridx++;
    add( this.btnLittleEndian, gbc );
  
    this.btnDec8 = new JRadioButton( "8-Bit Dezimalzahlen", false );
    this.btnDec8.setMnemonic( KeyEvent.VK_8 );
    this.btnDec8.addActionListener( this );
    grpType.add( this.btnDec8 );
    gbc.gridx = 0;
    gbc.gridy++;
    add( this.btnDec8, gbc );

    this.btnBigEndian = new JRadioButton( "Big Endian", bigEndian );
    this.btnBigEndian.setMnemonic( KeyEvent.VK_B );
    this.btnBigEndian.addActionListener( this );
    grpOrder.add( this.btnBigEndian );
    gbc.gridx++;
    add( this.btnBigEndian, gbc );
  
    this.btnDec16 = new JRadioButton( "16-Bit Dezimalzahlen", false );
    this.btnDec16.setMnemonic( KeyEvent.VK_6 );
    this.btnDec16.addActionListener( this );
    grpType.add( this.btnDec16 );
    gbc.gridx = 0;
    gbc.gridy++;
    add( this.btnDec16, gbc );

    this.btnDec32 = new JRadioButton( "32-Bit Dezimalzahlen", false );
    this.btnDec32.setMnemonic( KeyEvent.VK_3 );
    this.btnDec32.addActionListener( this );
    grpType.add( this.btnDec32 );
    gbc.gridy++;
    add( this.btnDec32, gbc );

    this.btnString = new JRadioButton( "Zeichenkette (ISO-8859-1)", false );
    this.btnString.setMnemonic( KeyEvent.VK_Z );
    this.btnString.addActionListener( this );
    grpType.add( this.btnString );
    gbc.insets.bottom = 5;
    gbc.gridy++;
    add( this.btnString, gbc );

    gbc.insets.top    = 5;
    gbc.insets.bottom = 0;
    gbc.gridwidth     = 2;
    gbc.gridy++;
    add( new JLabel( "Eingabe:" ), gbc );

    if( inputFmt != null ) {
      switch( inputFmt ) {
	case HEX8:
	  this.btnHex8.setSelected( true );
	  break;

	case DEC8:
	  this.btnDec8.setSelected( true );
	  break;

	case DEC16:
	  this.btnDec16.setSelected( true );
	  break;

	case DEC32:
	  this.btnHex8.setSelected( true );
	  break;

	case STRING:
	  this.btnString.setSelected( true );
	  break;
      }
    }

    this.fldInput = new JTextField();
    if( text != null ) {
      this.fldInput.setText( text );
    }
    this.fldInput.addActionListener( this );
    gbc.fill          = GridBagConstraints.HORIZONTAL;
    gbc.weightx       = 1.0;
    gbc.insets.top    = 0;
    gbc.insets.bottom = 5;
    gbc.gridy++;
    add( this.fldInput, gbc );


    // Knoepfe
    JPanel panelBtn = new JPanel( new GridLayout( 1, 2, 5, 5 ) );
    gbc.anchor      = GridBagConstraints.CENTER;
    gbc.fill        = GridBagConstraints.NONE;
    gbc.weightx     = 0.0;
    gbc.insets.top  = 5;
    gbc.gridy++;
    add( panelBtn, gbc );

    this.btnOK = new JButton( "OK" );
    this.btnOK.addActionListener( this );
    panelBtn.add( this.btnOK );

    this.btnCancel = new JButton( "Abbrechen" );
    this.btnCancel.addActionListener( this );
    panelBtn.add( this.btnCancel );


    // Fenstergroesse und -position
    pack();
    setParentCentered();
    setResizable( true );


    // sonstiges
    updByteOrderFields();
  }


  public boolean getApprovedBigEndian()
  {
    return this.approvedBigEndian;
  }


  public byte[] getApprovedBytes()
  {
    return this.approvedBytes;
  }


  public InputFormat getApprovedInputFormat()
  {
    return this.approvedInputFmt;
  }


  public String getApprovedText()
  {
    return this.approvedText;
  }


	/* --- ueberschriebene Methoden --- */

  public void windowOpened( WindowEvent e )
  {
    this.fldInput.requestFocus();
  }


  protected boolean doAction( EventObject e )
  {
    boolean rv = false;
    if( e != null ) {
      Object src = e.getSource();
      if( (src == this.btnHex8)
	  || (src == this.btnDec8)
	  || (src == this.btnDec16)
	  || (src == this.btnDec32)
	  || (src == this.btnString) )
      {
	rv = true;
	updByteOrderFields();
	this.fldInput.requestFocus();
      }
      else if( (src == this.btnLittleEndian) || (src == this.btnBigEndian) ) {
	rv = true;
	this.fldInput.requestFocus();
      }
      else if( (src == this.fldInput) || (src == this.btnOK) ) {
	rv = true;
	doApprove();
      }
      else if( src == this.btnCancel ) {
	rv = true;
	doClose();
      }
    }
    return rv;
 }


	/* --- private Methoden --- */

  private void doApprove()
  {
    byte[] rv = null;
    try {
      InputFormat inputFmt  = null;
      boolean     bigEndian = this.btnBigEndian.isSelected();
      String      text      = this.fldInput.getText();
      if( text != null ) {
	int len = text.length();
	if( len > 0 ) {
	  if( this.btnString.isSelected() ) {
	    inputFmt = InputFormat.STRING;
	    rv       = new byte[ len ];
	    for( int i = 0; i < len; i++ ) {
	      char ch = text.charAt( i );
	      if( ch > 0xFF ) {
		throw new ParseException(
			String.format(
				"Das Zeichen \'%c\' ist"
					+ " gr\u00F6\u00DFer als 8 Bit und\n"
					+ "kann deshalb hier nicht verwendet"
					+ " werden.",
				ch ),
			i );
	      }
	      rv[ i ] = (byte) ch;
	    }
	  } else {
	    inputFmt         = InputFormat.HEX8;
	    int bytesPerItem = 1;
	    int radix        = 16;
	    if( this.btnDec8.isSelected() ) {
	      inputFmt = InputFormat.DEC8;
	      radix    = 10;
	    }
	    else if( this.btnDec16.isSelected() ) {
	      inputFmt     = InputFormat.DEC16;
	      bytesPerItem = 2;
	      radix        = 10;
	    }
	    else if( this.btnDec32.isSelected() ) {
	      inputFmt     = InputFormat.DEC32;
	      bytesPerItem = 4;
	      radix        = 10;
	    }
	    String[] items = text.toUpperCase().split( "[\u0020,:;]" );
	    if( items != null ) {
	      if( items.length > 0 ) {
		rv = new byte[ items.length * bytesPerItem ];
		for( int i = 0; i < items.length; i++ ) {
		  try {
		    int value = Integer.parseInt( items[ i ], radix );
		    int pos   = i * bytesPerItem;
		    if( this.btnBigEndian.isSelected() ) {
		      for( int k = bytesPerItem - 1; k >= 0; --k ) {
			rv[ pos + k ] = (byte) (value & 0xFF);
			value >>= 8;
		      }
		    } else {
		      for( int k = 0; k < bytesPerItem; k++ ) {
			rv[ pos + k ] = (byte) (value & 0xFF);
			value >>= 8;
		      }
		    }
		  }
		  catch( NumberFormatException ex ) {
		    throw new ParseException(
				String.format(
					"%s: ung\u00FCltiges Format",
					items[ i ] ),
				i );
		  }
		}
	      }
	    }
	  }
	}
      }
      if( rv != null ) {
	this.approvedBytes     = rv;
	this.approvedText      = text;
	this.approvedInputFmt  = inputFmt;
	this.approvedBigEndian = false;
	doClose();
      }
    }
    catch( Exception ex ) {
      showErrorDlg( this, ex.getMessage() );
    }
  }


  private void updByteOrderFields()
  {
    boolean state = (this.btnDec16.isSelected() || this.btnDec32.isSelected());
    this.labelByteOrder.setEnabled( state );
    this.btnLittleEndian.setEnabled( state );
    this.btnBigEndian.setEnabled( state );
  }
}

