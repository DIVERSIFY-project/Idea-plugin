package fr.inria.diversify.analyzerPlugin.ut.actions;

import fr.inria.diversify.analyzerPlugin.FakeAnActionEvent;
import fr.inria.diversify.analyzerPlugin.FakeCodePositionTree;
import fr.inria.diversify.analyzerPlugin.FakeProject;
import fr.inria.diversify.analyzerPlugin.actions.reporting.SaveLatexAction;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.io.TransplantLatexExporter;
import fr.inria.diversify.analyzerPlugin.ut.FakeSavePathProvider;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class SaveLatexActionTest {

    /**
     * Test that the Save latex action performs as expected
     * @throws Exception
     */
    @Test
    public void testActionPerformed(@Mocked final TransplantLatexExporter anyExporter) throws Exception {
        FakeCodePositionTree tree = new FakeCodePositionTree();
        tree.setFakeCodePosition(new TransplantInfo());
        SaveLatexAction action = new SaveLatexAction(tree, new FakeSavePathProvider());
        action.actionPerformed(new FakeAnActionEvent(new FakeProject()));
        new Verifications() {{
            anyExporter.export(FakeSavePathProvider.FILE_NAME, (TransplantInfo) any, anyString, anyString);
        }};
    }

    /**
     * Verify that no export is done in absence of a proper TransplantInfo
     * @throws Exception
     */
    /*
    @Test
    public void testSoftComplain(
            @Mocked final TransplantLatexExporter anyExporter,
            @Mocked final JOptionPane anyPane) throws Exception {

        FakeCodePositionTree tree = new FakeCodePositionTree();
        tree.setFakeCodePosition(null); //This will raise the soft complain
        SaveLatexAction action = new SaveLatexAction(tree, new FakeLoadPathProvider());

        expectHardComplain();
        action.actionPerformed(new FakeAnActionEvent(new FakeProject()));

        new Verifications() {{
            anyExporter.export(FakeLoadPathProvider.FILE_NAME, (TransplantInfo) any, anyString);
            times = 0;
        }};
    }*/
}