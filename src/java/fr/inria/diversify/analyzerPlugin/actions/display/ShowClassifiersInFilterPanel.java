package fr.inria.diversify.analyzerPlugin.actions.display;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.searching.SwitchClasifierAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.FilterPanel;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClassifier;

import javax.swing.*;

/**
 * Created by marodrig on 18/08/2015.
 */
@Deprecated
public class ShowClassifiersInFilterPanel extends TestEyeAction {

    private final DefaultListModel resultList;

    public ShowClassifiersInFilterPanel(DefaultListModel resultList) {
        this.resultList = resultList;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        TestEyeProjectComponent component = getComponent(anActionEvent);
        for (TransformClassifier c : component.getClassifiers() ) {
            SwitchClasifierAction a = new SwitchClasifierAction(c);
//            resultList.addElement(new FilterPanel.ActionCheckBox(a, c, true));
        }
    }
}
