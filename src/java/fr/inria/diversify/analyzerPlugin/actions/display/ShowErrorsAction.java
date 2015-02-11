package fr.inria.diversify.analyzerPlugin.actions.display;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.EventLogList;

import javax.swing.*;

/**
 *
 * Created by marodrig on 10/02/2015.
 */
public class ShowErrorsAction extends TestEyeAction {

    private final EventLogList list;

    public ShowErrorsAction(EventLogList list) {
        this.list = list;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        try {
            TestEyeProjectComponent c = getComponent(event);
            DefaultListModel<String> model = (DefaultListModel<String>)list.getModel();
            model.clear();
            for  ( String s : c.getLogMessages() ) model.addElement(s);
        } catch (Exception e) {
            hardComplain("Cannot show errors", e);
        }
    }

}
