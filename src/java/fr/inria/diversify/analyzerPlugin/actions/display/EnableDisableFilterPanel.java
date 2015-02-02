package fr.inria.diversify.analyzerPlugin.actions.display;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.gui.FilterPanel;

/**
 * Created by marodrig on 02/02/2015.
 */
public class EnableDisableFilterPanel extends TestEyeAction {

    /**
     * Filter panel to enable
     */
    private final FilterPanel filterPanel;

    public EnableDisableFilterPanel(FilterPanel filterPanel) {
        this.filterPanel = filterPanel;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        filterPanel.setEnabled(getComponent(event).getInfos().size() > 0);
    }
}
