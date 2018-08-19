package de.sciss.virtualkeyboard;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple virtual keyboard in the Brazilian ABNT2 layout.
 *
 * In order to use this class you must:
 *
 * 1. Create a new instance providing the size of the virtual keyboard; <br>
 * 2. Provide a text component that will be used to store the the keys typed
 * (this is performed with a separate call to setCurrentTextComponent; <br>
 * 3. Call the show method in order to show the virtual keyboard in a given
 * JFrame.
 *
 * Note: This class has been edited for more narrow key labels,
 * more US like layout, and with capital alpha keys showing during 'shift'.
 * The selected state of shift has been fixed to work with other look-and-feels
 * (WebLaF). Keys are no longer focusable. The keyboard is itself a panel.
 *
 * @author Wilson de Carvalho
 */
public class VirtualKeyboard extends JPanel {

    /**
     * Private class for storing key specification.
     */
    private class Key {

        public final int        keyCode;
        public final String     value;
        public final String     shiftValue;
        public final boolean    isModifier;
        public final int        width;

        public Key(int keyCode, String value, String shiftValue, boolean isModifier, int width) {
            this.keyCode    = keyCode;
            this.value      = value;
            this.shiftValue = shiftValue;
            this.isModifier = isModifier;
            this.width      = width;
        }

        public Key(int keyCode, String value, String shiftValue, boolean isModifier) {
            this(keyCode, value, shiftValue, isModifier, 1);
        }

        public Key(int keyCode, String value, String shiftValue) {
            this(keyCode, value, shiftValue, false);
        }

        public Key(int keyCode, String value, boolean isModifier) {
            this(keyCode, value, value, isModifier);
        }

        public Key(int keyCode, String value) {
            this(keyCode, value, value);
        }

        public boolean hasShiftValue() {
            return !this.value.equals(this.shiftValue);
        }

        public boolean isLetter() {
            return value.length() == 1
                    && Character.isLetter(value.toCharArray()[0]);
        }

        public boolean isDead() {
            return keyCode >= KeyEvent.VK_DEAD_GRAVE && keyCode <= KeyEvent.VK_DEAD_SEMIVOICED_SOUND;
        }
    }

    // Special keys definition
    private final Key CAPS_LOCK_KEY     = new Key(KeyEvent.VK_CAPS_LOCK , "\u21ea", true);
    private final Key SHIFT_KEY         = new Key(KeyEvent.VK_SHIFT     , "\u21E7", true);

    private final Key ACUTE_KEY         = new Key(KeyEvent.VK_DEAD_ACUTE        , "´" , "´" , false, 0);
    private final Key GRAVE_KEY         = new Key(KeyEvent.VK_DEAD_GRAVE        , "`" , "`" , false, 0);
    private final Key TILDE_KEY         = new Key(KeyEvent.VK_DEAD_TILDE        , "~" , "~" , false, 0);
    private final Key CIRCUMFLEX_KEY    = new Key(KeyEvent.VK_DEAD_CIRCUMFLEX   , "^" , "^" , false, 0);
    private final Key DIAERESIS_KEY     = new Key(KeyEvent.VK_DEAD_DIAERESIS    , "\"", "\"", false, 0);

    // First key row
    private Key[] row1 = new Key[] {
        new Key(KeyEvent.VK_BACK_QUOTE, "`", "~"),
        new Key(KeyEvent.VK_1, "1", "!"), new Key(KeyEvent.VK_2, "2", "@"),
        new Key(KeyEvent.VK_3, "3", "#"), new Key(KeyEvent.VK_4, "4", "$"),
        new Key(KeyEvent.VK_5, "5", "%"), new Key(KeyEvent.VK_6, "6", "^"),
        new Key(KeyEvent.VK_7, "7", "&"), new Key(KeyEvent.VK_8, "8", "*"),
        new Key(KeyEvent.VK_9, "9", "("), new Key(KeyEvent.VK_0, "0", ")"),
        new Key(KeyEvent.VK_MINUS, "-", "_")
    };

    // Second key row
    private Key[] row2 = new Key[] {
        new Key(KeyEvent.VK_Q, "q", "Q"), new Key(KeyEvent.VK_W, "w", "W"),
        new Key(KeyEvent.VK_E, "e", "E"), new Key(KeyEvent.VK_R, "r", "R"),
        new Key(KeyEvent.VK_T, "t", "T"), new Key(KeyEvent.VK_Y, "y", "Y"),
        new Key(KeyEvent.VK_U, "u", "U"), new Key(KeyEvent.VK_I, "i", "I"),
        new Key(KeyEvent.VK_O, "o", "O"), new Key(KeyEvent.VK_P, "p", "P"),
        new Key(KeyEvent.VK_BRACELEFT, "[", "{"), new Key(KeyEvent.VK_BRACERIGHT, "]", "}")
    };

    // Third key row
    private Key[] row3 = new Key[] {
        new Key(KeyEvent.VK_A, "a", "A"), new Key(KeyEvent.VK_S, "s", "S"),
        new Key(KeyEvent.VK_D, "d", "D"), new Key(KeyEvent.VK_F, "f", "F"),
        new Key(KeyEvent.VK_G, "g", "G"), new Key(KeyEvent.VK_H, "h", "H"),
        new Key(KeyEvent.VK_J, "j", "J"), new Key(KeyEvent.VK_K, "k", "K"),
        new Key(KeyEvent.VK_L, "l", "L"), new Key(KeyEvent.VK_SEMICOLON, ";", ":"),
        new Key(KeyEvent.VK_QUOTE, "'", "\""), new Key(KeyEvent.VK_EQUALS, "=", "+")
    };

    // Fourth key row
    private Key[] row4 = new Key[] {
        new Key(KeyEvent.VK_Z, "z", "Z"), new Key(KeyEvent.VK_X, "x", "X"),
        new Key(KeyEvent.VK_C, "c", "C"), new Key(KeyEvent.VK_V, "v", "V"),
        new Key(KeyEvent.VK_B, "b", "B"), new Key(KeyEvent.VK_N, "n", "N"),
        new Key(KeyEvent.VK_M, "m", "M"), new Key(KeyEvent.VK_COMMA, ",", "<"),
        new Key(KeyEvent.VK_PERIOD, ".", ">"), new Key(KeyEvent.VK_SEMICOLON, ";", ":"),
        new Key(KeyEvent.VK_SLASH, "/", "?"), new Key(KeyEvent.VK_BACK_SLASH, "\\", "|")
    };

    // Fifth key row
    private Key[] row5 = new Key[] {
        SHIFT_KEY, CAPS_LOCK_KEY,
        new Key(KeyEvent.VK_SPACE, " ", " ", false, 4),
        new Key(KeyEvent.VK_BACK_SPACE, "\u232b" /* "\u0232b" */),
        ACUTE_KEY, GRAVE_KEY, TILDE_KEY, CIRCUMFLEX_KEY, DIAERESIS_KEY
    };

    private final Map<Key, AbstractButton> buttons;

    private Component currentComponent;

    private boolean isCapsLockPressed   = false;
    private boolean isShiftPressed      = false;

    private Key accentuationBuffer;

    public VirtualKeyboard() {
        super(new GridLayout(5, 1));
        this.buttons = new HashMap<>();

        add(initRow(row1));
        add(initRow(row2));
        add(initRow(row3));
        add(initRow(row4));
        add(initRow(row5));

        final KeyboardFocusManager focusManager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addPropertyChangeListener(
                e -> {
                    String prop = e.getPropertyName();
                    if (("focusOwner".equals(prop))) {
                        currentComponent = (Component) e.getNewValue();
                    }
                });

        currentComponent = focusManager.getFocusOwner();
    }

    private JPanel initRow(Key[] keys) {
        boolean sizeVaries = false;

        for (Key key : keys) {
            if (key.width > 1) {
                sizeVaries = true;
                break;
            }
        }

        final GridBagConstraints cons = new GridBagConstraints();
        final GridBagLayout      gbl  = new GridBagLayout();

        final LayoutManager lay;
        if (sizeVaries) {
            lay = gbl;
        } else {
            lay = new GridLayout(1, keys.length);
        }
        cons.ipadx          = 0;
        cons.ipady          = 0;
        cons.insets.top     = 0;
        cons.insets.left    = 0;
        cons.insets.bottom  = 0;
        cons.insets.right   = 0;
        cons.weighty        = 1.0;
        cons.fill           = GridBagConstraints.BOTH;
        final JPanel p = new JPanel(lay);

        int widthSum = 0;
        for (Key key : keys) {
            widthSum += key.width;
        }

        for (Key key : keys) {
            final AbstractButton b;
            if (buttons.containsKey(key)) {
                b = buttons.get(key);
            } else {
                b = key.isModifier ? new JToggleButton(key.value) : new JButton(key.value);
                b.setFocusable(false);
                b.setFocusPainted(false);
                b.setBorderPainted(false);
                buttons.put(key, b);
                b.addActionListener(e -> actionListener(key));
            }
            cons.gridwidth  = Math.max(1, key.width);
            cons.weightx    = (double) key.width / widthSum;
            gbl.setConstraints(b, cons);
            p.add(b);
        }
        return p;
    }

    private void actionListener(Key key) {
        if (!(currentComponent instanceof JComponent)) {
            return;
        }
        currentComponent.requestFocus();
        final JTextComponent currentTextComponent = getCurrentTextComponent();
        switch (key.keyCode) {
            case KeyEvent.VK_CAPS_LOCK:
                capsLockPressed();
                break;
            case KeyEvent.VK_SHIFT:
                shiftPressed();
                break;
            case KeyEvent.VK_BACK_SPACE:
                if (currentTextComponent == null) {
                    return;
                }
                backspacePressed(currentTextComponent);
                break;
            case KeyEvent.VK_TAB:
                tabPressed();
                break;
            default:
                if (currentTextComponent == null) {
                    return;
                }
                otherKeyPressed(key, currentTextComponent);
                break;
        }
    }

    private void capsLockPressed() {
        isCapsLockPressed = !isCapsLockPressed;
        buttons.forEach((k, b) -> {
            if (k.isLetter() && k.hasShiftValue()) {
                if (isCapsLockPressed) {
                    b.setText(k.shiftValue);
                } else {
                    b.setText(k.value);
                }
            }
        });
        final AbstractButton b = buttons.get(CAPS_LOCK_KEY);
        b.setSelected(isCapsLockPressed);
    }

    private void shiftPressed() {
        isShiftPressed = !isShiftPressed;
        buttons.forEach((k, b) -> {
            if (k.hasShiftValue()) {
                if (isShiftPressed) {
                    b.setText(k.shiftValue);
                } else {
                    b.setText(k.value);
                }
            }
        });
        final AbstractButton b = buttons.get(SHIFT_KEY);
        b.setSelected(isShiftPressed);
    }

    private void backspacePressed(JTextComponent component) {
        if (currentComponent instanceof JTextComponent) {
            final int caretPosition = component.getCaretPosition();
            if (!component.getText().isEmpty() && caretPosition > 0) {
                try {
                    component.setText(component.getText(0, caretPosition - 1)
                            + component.getText(caretPosition,
                                    component.getText().length() - caretPosition));
                } catch (BadLocationException ignored) {
                }
                component.setCaretPosition(caretPosition - 1);
            }
        }
    }

    private void tabPressed() {
        if (currentComponent instanceof JComponent) {
            final Component nextComponent = ((JComponent) currentComponent).getNextFocusableComponent();
            if (nextComponent != null) {
                nextComponent.requestFocus();
                this.currentComponent = nextComponent;
            }
        }
    }

    private void otherKeyPressed(Key key, JTextComponent currentTextComponent) {
        if (key.isLetter()) {
            String keyString;
            if (accentuationBuffer == null) {
                keyString = key.value;
            } else {
                switch (key.keyCode) {
                    case KeyEvent.VK_A:
                        keyString = accentuationBuffer == ACUTE_KEY         ? "á"
                                  : accentuationBuffer == GRAVE_KEY         ? "à"
                                  : accentuationBuffer == TILDE_KEY         ? "ã"
                                  : accentuationBuffer == CIRCUMFLEX_KEY    ? "â"
                                  : accentuationBuffer == DIAERESIS_KEY     ? "ä"
                                  : key.value;
                        break;
                    case KeyEvent.VK_E:
                        keyString = accentuationBuffer == ACUTE_KEY         ? "é"
                                  : accentuationBuffer == GRAVE_KEY         ? "è"
                                  : accentuationBuffer == TILDE_KEY         ? "ẽ"
                                  : accentuationBuffer == CIRCUMFLEX_KEY    ? "ê"
                                  : accentuationBuffer == DIAERESIS_KEY     ? "ë"
                                  : key.value;
                        break;
                    case KeyEvent.VK_I:
                        keyString = accentuationBuffer == ACUTE_KEY         ? "í"
                                  : accentuationBuffer == GRAVE_KEY         ? "ì"
                                  : accentuationBuffer == TILDE_KEY         ? "ĩ"
                                  : accentuationBuffer == CIRCUMFLEX_KEY    ? "î"
                                  : accentuationBuffer == DIAERESIS_KEY     ? "ï"
                                  : key.value;
                        break;
                    case KeyEvent.VK_O:
                        keyString = accentuationBuffer == ACUTE_KEY         ? "ó"
                                  : accentuationBuffer == GRAVE_KEY         ? "ò"
                                  : accentuationBuffer == TILDE_KEY         ? "õ"
                                  : accentuationBuffer == CIRCUMFLEX_KEY    ? "ô"
                                  : accentuationBuffer == DIAERESIS_KEY     ? "ö"
                                  : key.value;
                        break;
                    case KeyEvent.VK_U:
                        keyString = accentuationBuffer == ACUTE_KEY         ? "ú"
                                  : accentuationBuffer == GRAVE_KEY         ? "ù"
                                  : accentuationBuffer == TILDE_KEY         ? "ũ"
                                  : accentuationBuffer == CIRCUMFLEX_KEY    ? "û"
                                  : accentuationBuffer == DIAERESIS_KEY     ? "ü"
                                  : key.value;
                        break;
                    default:
                        keyString = key.value;
                        break;
                }
                accentuationBuffer = null;
            }
            if (isCapsLockPressed) {
                keyString = keyString.toUpperCase();
                if (isShiftPressed) {
                    shiftPressed();
                }
            } else if (isShiftPressed) {
                keyString = keyString.toUpperCase();
                shiftPressed();
            }
            addText(currentTextComponent, keyString);
        } else if (key.isDead()) {
            accentuationBuffer = key;
        } else {
            String keyString;
            if (isCapsLockPressed) {
                keyString = key.value.toUpperCase();
                if (isShiftPressed) {
                    shiftPressed();
                }
            } else if (isShiftPressed) {
                keyString = key.shiftValue;
                shiftPressed();
            } else {
                keyString = key.value;
            }
            addText(currentTextComponent, keyString);
        }
    }

    private JTextComponent getCurrentTextComponent() {
        if (currentComponent instanceof JTextComponent) {
            return (JTextComponent) currentComponent;
        } else {
            return null;
        }
    }

    /**
     * Adds text considering the caret position.
     *
     * @param component Text component.
     * @param text Text that will be added.
     */
    private void addText(JTextComponent component, String text) {
        int caretPosition = component.getCaretPosition();
        try {
            component.setText(component.getText(0, caretPosition)
                    + text + component.getText(caretPosition,
                            component.getText().length() - caretPosition));
            component.setCaretPosition(caretPosition + 1);
        } catch (BadLocationException ignored) {
        }
    }
}
