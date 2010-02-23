package org.openshapa.views.discrete.datavalues;

import com.usermetrix.jclient.UserMetrix;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.db.TimeStamp;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.DataValue;
import org.openshapa.models.db.TimeStampDataValue;
import org.openshapa.views.discrete.EditorComponent;

/**
 * This class is the character editor of a TimeStampDataValues.
 */
public final class TimeStampDataValueEditor extends EditorComponent {

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix
            .getInstance(TimeStampDataValueEditor.class);

    /** The TimeStampDataValue that this view represents. **/
    private TimeStampDataValue model;

    /** The parent datacell for the TimeStamp that this view represents. */
    private long parentCell;

    /** The source of the TimeStampDataValue being edited. */
    private TimeStampSource dataSourceType;

    /**
     *
     */
    public enum TimeStampSource {
        /** Timestamp is the Onset of the datacell associated. */
        Onset,
        /** Timestamp is the Offset of the datacell associated. */
        Offset,
        /** Timestamp is an argument of a datacell's matrix. */
        MatrixArg,
        /** Timestamp is an argument of a predicate within a datacell. */
        PredicateArg
    }

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param sourceType What timestamp are we displaying.
     */
    public TimeStampDataValueEditor(final JTextComponent ta,
                                    final DataCell cell,
                                    final TimeStampSource sourceType) {
        super(ta);
        setEditable(true);
        parentCell = cell.getID();
        dataSourceType = sourceType;
        this.addPreservedChars(":");
        this.setDeleteChar('0');
        resetValue();
    }

    /**
     * @return The model that this data value view represents.
     */
    public DataValue getModel() {
        return this.model;
    }

    /**
     * Reset the values by retrieving from the database.
     */
    public void resetValue() {
        try {
            // reget the parentCell in case other data items have changed
            DataCell c = (DataCell) OpenSHAPA.getProject().getDB()
                                             .getCell(parentCell);

            switch (dataSourceType) {
                case Onset:
                    model = new TimeStampDataValue(c.getDB());
                    model.setItsValue(c.getOnset());
                    break;
                case Offset:
                    model = new TimeStampDataValue(c.getDB());
                    model.setItsValue(c.getOffset());
                    break;
                default:
                    break;
            }

            setText(this.getModel().toString());
        } catch (SystemErrorException e) {
            logger.error("Unable to resetValue.", e);
        }
    }

    /**
     * Update the database with the model value.
     */
    public void updateDatabase() {
        try {
            // Reget the parentCell in case other data items have changed
            DataCell c = (DataCell) OpenSHAPA.getProject().getDB()
                                             .getCell(parentCell);

            TimeStampDataValue tsdv = (TimeStampDataValue) this.getModel();
            switch (dataSourceType) {
                case Onset:
                    c.setOnset(tsdv.getItsValue());
                    break;
                case Offset:
                    c.setOffset(tsdv.getItsValue());
                    break;
                default:
                    break;
            }
            c.getDB().replaceCell(c);
        } catch (SystemErrorException se) {
            logger.error("Unable to update Database: ", se);
        }
    }

    /**
     * Action to take by this editor when a key is pressed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        TimeStampDataValue tdv = (TimeStampDataValue) getModel();
        switch (e.getKeyCode()) {
            // BugzID:708 - Force the Home key to behave correctly on OSX 10.4
            case KeyEvent.VK_HOME:
                this.setCaretPosition(0);
                e.consume();
                break;

            // BugzID:708 - Force the End key to behave correctly on OSX 10.4
            case KeyEvent.VK_END:
                this.setCaretPosition(this.getText().length());
                e.consume();
                break;

            case KeyEvent.VK_BACK_SPACE:
                // Can't delete empty time stamp data value.
                if (!tdv.isEmpty()) {
            try {
                this.removeBehindCaret();
                tdv.setItsValue(new TimeStamp(getText()));
                e.consume();
            } catch (SystemErrorException ex) {
                java.util.logging.Logger.getLogger(TimeStampDataValueEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
                }
                break;
            case KeyEvent.VK_DELETE:
                // Can't delete empty time stamp data value.
                if (!tdv.isEmpty()) {
            try {
                int caret = getSelectionEnd();
                this.removeAheadOfCaret();
                setCaretPosition(caret);
                if (caret < getText().length() && this.isPreserved(getText().charAt(caret))) {
                    setCaretPosition(getCaretPosition() + 1);
                }
                this.setCaretPosition(this.getCaretPosition() + 1);
                tdv.setItsValue(new TimeStamp(getText()));
                e.consume();
            } catch (SystemErrorException ex) {
                java.util.logging.Logger.getLogger(TimeStampDataValueEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
                }
                break;

            case KeyEvent.VK_LEFT:
                int selectStart = this.getSelectionStart();
                int selectEnd = this.getSelectionEnd();

                // Move caret to the left.
                int c = Math.max(0, this.getCaretPosition() - 1);
                this.setCaretPosition(c);

                // If after the move, we have a character to the left is
                // preserved character we need to skip one before passing
                // the key event down to skip again (effectively skipping
                // the preserved character).
                int b = Math.max(0, getCaretPosition());
                c = Math.max(0, this.getCaretPosition() - 1);
                if (this.isPreserved(getText().charAt(b))
                    || this.isPreserved(getText().charAt(c))) {
                    setCaretPosition(Math.max(0, getCaretPosition() - 1));
                }
                e.consume();

                // If the user is holding down shift - alter the selection as
                // well as the caret position.
                if (e.getModifiers() == KeyEvent.SHIFT_MASK) {
                    // Shrink selection left - removed entire selection.
                    if (getCaretPosition() == selectStart) {
                        select(selectStart, selectStart);
                    // Grow selection left.
                    } else if (getCaretPosition() < selectStart) {
                        select(selectEnd, getCaretPosition());
                    // Shrink selection left.
                    } else {
                        select(selectStart, getCaretPosition());
                    }
                }

                break;

            case KeyEvent.VK_RIGHT:
                selectStart = this.getSelectionStart();
                selectEnd = this.getSelectionEnd();

                // Move caret to the right.
                c = Math.min(this.getText().length(),
                             this.getCaretPosition() + 1);
                this.setCaretPosition(c);

                // If after the move, we have a character to the right that
                // is a preserved character, we need to skip one before
                // passing the key event down to skip again (effectively
                // skipping the preserved character)
                b = Math.min(getText().length() - 1, getCaretPosition());
                c = Math.min(getText().length() - 1, getCaretPosition() + 1);
                if (c < this.getText().length()
                    && (this.isPreserved(getText().charAt(c))
                        || this.isPreserved(getText().charAt(b)))) {
                    setCaretPosition(Math.min(getText().length() - 1,
                                              getCaretPosition() + 1));
                }
                e.consume();

                // If the user is holding down shift - alter the selection as
                // well as the caret position.
                if (e.getModifiers() == KeyEvent.SHIFT_MASK) {
                    // Shrink selection right - removed entire selection.
                    if (getCaretPosition() == selectEnd) {
                        select(selectEnd, selectEnd);
                    // Grow selection right.
                    } else if (getCaretPosition() > selectEnd) {
                        select(selectStart, getCaretPosition());
                    // Shrink select right.
                    } else {
                        select(getCaretPosition(), selectEnd);
                    }
                }
                break;

            default:
                break;
        }
    }

    /**
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        try {
            TimeStampDataValue tdv = (TimeStampDataValue) getModel();

//            // The backspace key removes digits from behind the caret.
//            if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
//                && e.getKeyChar() == '\u0008') {
//
//                // Can't delete empty time stamp data value.
//                if (!tdv.isEmpty()) {
//                    this.removeBehindCaret();
//                    tdv.setItsValue(new TimeStamp(getText()));
//                    e.consume();
//                }
//
//            // The delete key removes digits ahead of the caret.
//            } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
//                       && e.getKeyChar() == '\u007F') {
//
//                // Can't delete empty time stamp data value.
//                if (!tdv.isEmpty()) {
//                    int caret = getSelectionEnd();
//                    this.removeAheadOfCaret();
//                    setCaretPosition(caret);
//
//                    if (caret < getText().length()
//                        && this.isPreserved(getText().charAt(caret))) {
//                        setCaretPosition(getCaretPosition() + 1);
//                    }
//
//                    this.setCaretPosition(this.getCaretPosition() + 1);
//                    tdv.setItsValue(new TimeStamp(getText()));
//                    e.consume();
//                }

            // Key stoke is number - insert stroke at current caret position
            // but only if their is room in the editor for the new digit.
            if (Character.isDigit(e.getKeyChar())
                       && this.getCaretPosition() <= getText().length()) {
                this.removeAheadOfCaret();
                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.deleteCharAt(getCaretPosition());
                currentValue.insert(getCaretPosition(), e.getKeyChar());
                this.setCaretPosition(this.getCaretPosition() + 1);
                tdv.setItsValue(new TimeStamp(currentValue.toString()));
                e.consume();

            // Every other key stroke is ignored by the float editor.
            } else {
                e.consume();

                // Nothing has changed - skip updating the database.
                return;
            }

            // Update the strings just in case we don't change the value.
            setText(this.getModel().toString());

            // Push the value back into the database.
            updateDatabase();
        } catch (SystemErrorException se) {
            logger.error("Unable to update TimeStampDataValue", se);
        }
    }

    /**
     * focusSet is the signal that this editor has become "current".
     * @param fe Focus Event
     */
    @Override
    public void focusGained(final FocusEvent fe) {
    }

    /**
     * Action to take by this editor when a key is released.
     * @param e KeyEvent
     */
    @Override
    public void keyReleased(final KeyEvent e) {
    }
}
