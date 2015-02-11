package fr.inria.diversify.analyzerPlugin.gui;

import fr.inria.diversify.persistence.json.input.JsonSectionInput;

import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Custom renderer to display events levels in different colors
 *
 * @author marodrig
 */
public class EventListRenderer extends JLabel implements ListCellRenderer<String> {

    private final static Font LOG_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    //private final static String ERROR = JsonSectionInput.ERROR;
    private final static String ERROR = "ERROR";
    //private final static String ERROR = JsonSectionInput.WARNING;
    private final static String WARNING = "WARNING";
    //private final static String ERROR = JsonSectionInput.INFO;
    private final static String INFO = "INFO";
    //private final static String ERROR = JsonSectionInput.DEBUG;
    private final static String DEBUG = "DEBUG";

    /**
     * The color orange, darker.  In the default sRGB space.
     */
    public final static Color WARNING_COLOR = new Color(200, 150, 0);

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String logEvent, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        if ( logEvent.toUpperCase().startsWith(ERROR) ) setForeground(Color.RED);
        else if ( logEvent.toUpperCase().startsWith(WARNING) ) setForeground(WARNING_COLOR);
        else if ( logEvent.toUpperCase().startsWith(INFO) ) setForeground(Color.BLACK);
        else if ( logEvent.toUpperCase().startsWith(DEBUG) ) setForeground(Color.BLUE);
        setText(logEvent);
        setFont(LOG_FONT);
        return this;
    }
}
