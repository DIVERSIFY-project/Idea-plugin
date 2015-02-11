package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * Created by marodrig on 06/02/2015.
 */
public class EventLogList extends JList {

    private Collection<String> messages;

    public EventLogList() {
        super();
        DefaultListModel<String> s = new DefaultListModel<>();
        s.addElement("Error: ");
        s.addElement("Warning: ");
        s.addElement("Info: ");
        s.addElement("Debug: ");
        setModel(s);
        setCellRenderer(new EventListRenderer());
    }

    public void setMessages(Collection<String> messages) {
        this.messages = messages;
        DefaultListModel<String> model = new DefaultListModel<>();
        for ( String msg : messages ) {
            model.addElement(msg);
        }
        setModel(model);
    }

    public Collection<String> getMessages() {
        return messages;
    }
}
