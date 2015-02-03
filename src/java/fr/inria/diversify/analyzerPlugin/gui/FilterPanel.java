package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import fr.inria.diversify.analyzerPlugin.IDEObjects;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.searching.*;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.ClassifierFactory;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClasifier;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.html.ObjectView;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;

public class FilterPanel extends JBList {

    private IDEObjects ideObjects;

    private ClassifierFactory classifierFactory;

    private static final int SHOW_INTERSECTION_INDEX = 0;

    //private Collection<ActionCheckBox> actionsChecks;

    /**
     * A custom checkbox containin an action
     */
    public class ActionCheckBox extends JBCheckBox {
        public TestEyeAction action;

        public ActionCheckBox(TestEyeAction action, boolean checked) {
            super();
            this.action = action;
            setText(action.toString());
            setSelected(checked);
        }
    }

    /**
     * Shows/hide the intersection without triggering the event
     */
    public void setShowIntersectionNoTriggerEvent(boolean value) {
        DefaultListModel model = (DefaultListModel) getModel();
        ActionCheckBox c = (ActionCheckBox)model.elementAt(SHOW_INTERSECTION_INDEX);
        c.setSelected(value);
        repaint();
    }

    /**
     * A No border for the checkboxes
     */
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public FilterPanel(ClassifierFactory factory) {
        classifierFactory = factory;
        setCellRenderer(new CellRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        installCheckBoxMouseListener();
        installPopup();
        populateListWithClassifiers();
    }

    public FilterPanel() {
        this(new ClassifierFactory());
    }

    public ClassifierFactory getClassifierFactory() {
        if ( classifierFactory == null ) classifierFactory = new ClassifierFactory();
        return classifierFactory;
    }

    /**
     * Un checks all without triggering their events
     */
    public void uncheckAllNoTriggerEvent() {
        setAllSelected(false);
        repaint();
    }

    /**
     * Checks all without triggering their events
     */
    public void checkAllNoTriggerEvent() {
        setAllSelected(true);
        repaint();
    }

    private void setAllSelected(boolean b) {
        DefaultListModel model = (DefaultListModel) getModel();
        for ( int i = 0; i < model.size(); i++ ) {
            if ( model.elementAt(i) instanceof ActionCheckBox ) {
                ActionCheckBox c = (ActionCheckBox)model.elementAt(i);
                if ( c.action != null ) {
                    c.setSelected(b);
                }
            }
        }
    }

    /**
     * Populates the list with the classifiers
     */
    private void populateListWithClassifiers() {
        DefaultListModel resultList = new DefaultListModel();
        setModel(resultList);
        resultList.addElement(new ActionCheckBox(new HideShowIntersectionAction(this), false));
        resultList.addElement(new JLabel("Filters:"));
        final DefaultActionGroup filter = new DefaultActionGroup();

        for (TransformClasifier c : getClassifierFactory().buildClassifiers()) {
            resultList.addElement(new ActionCheckBox(new SwitchClasifierAction(c.getClass(), c.getDescription()), true));
        }
    }

    /**
     * Install the listener to listen to on-ckeckbox events
     */
    private void installCheckBoxMouseListener() {
        MouseAdapter listener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || !e.getComponent().isEnabled() ) return;
                int index = locationToIndex(e.getPoint());
                if (index != -1 && getModel().getElementAt(index) instanceof ActionCheckBox) {
                    ActionCheckBox checkbox = (ActionCheckBox) getModel().getElementAt(index);
                    checkbox.setSelected(!checkbox.isSelected());
                    ideObjects.getActionManager().tryToExecute(checkbox.action, e, e.getComponent(), null, true);

                }
                repaint();
            }
        };
        addMouseListener(listener);
    }


    /**
     * Installs the popup
     */
    private void installPopup() {

        FilterPanel me = this;

        final PopupHandler popupHandler = new PopupHandler() {
            public void invokePopup(Component comp, int x, int y) {
                final DefaultActionGroup popupGroup = new DefaultActionGroup();
                popupGroup.add(new HideAllClasifierAction((FilterPanel)comp));
                popupGroup.add(new ShowAllClasifierAction((FilterPanel)comp));
                ActionPopupMenu popupMenu = ideObjects.getActionManager().createActionPopupMenu(
                        FilterPanel.class.getName(), popupGroup);
                if (popupMenu != null) {
                    popupMenu.getComponent().show(comp, x, y);
                }
            }
        };
        addMouseListener(popupHandler);
    }

    /**
     * Cell renderer to render as check box
     */
    protected class CellRenderer implements ListCellRenderer {
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            if (value instanceof JBCheckBox) {
                JCheckBox checkbox = (JCheckBox) value;
                checkbox.setBackground(isSelected ?
                        getSelectionBackground() : getBackground());
                checkbox.setForeground(isSelected ?
                        getSelectionForeground() : getForeground());
                checkbox.setEnabled(isEnabled());
                checkbox.setFont(getFont());
                checkbox.setFocusPainted(false);
                checkbox.setBorderPainted(true);
                checkbox.setBorder(isSelected ?
                        UIManager.getBorder(
                                "List.focusCellHighlightBorder") : noFocusBorder);
                return checkbox;
            } else return (Component) value;
        }
    }

    public void setIdeObject(IDEObjects ideObject) {
        this.ideObjects = ideObject;
    }

    public IDEObjects getIdeObject() {
        return ideObjects;
    }
}
