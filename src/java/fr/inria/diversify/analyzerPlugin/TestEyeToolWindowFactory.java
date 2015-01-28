package fr.inria.diversify.analyzerPlugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import fr.inria.diversify.analyzerPlugin.gui.TestEyeExplorer;

/**
 * Created by marodrig on 27/01/2015.
 */
public class TestEyeToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        TestEyeExplorer explorer = new TestEyeExplorer(project);
        final ContentManager contentManager = toolWindow.getContentManager();
        final Content content = contentManager.getFactory().createContent(explorer, null, false);
        contentManager.addContent(content);
    }

}
