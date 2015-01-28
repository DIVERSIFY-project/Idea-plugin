package fr.inria.diversify.analyzerPlugin.deprecated;

import fr.inria.diversify.analyzerPlugin.MainToolWinv0;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;
import org.junit.Ignore;
import org.junit.Test;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import static org.junit.Assert.*;

/**
 * Created by marodrig on 05/09/2014.
 */
public class MainToolWinTest {

    private String getResourcePath(String name) throws Exception {
        return getClass().getResource("/" + name).toURI().getPath();
    }


    @Test
    @Ignore
    public void testCreation() throws Exception {
        //Test that no weird things occur during creation
        MainToolWinv0 m = new MainToolWinv0();
        assertNotNull(m);
    }

    /**
     * Test the proper showing of the representation data
     */
    @Test
    @Deprecated
    @Ignore
    public void testRepresentationToJTree() throws Exception {
        MainToolWinv0 m = new MainToolWinv0();
        m.setThrowErrors(true);
        m.loadTransformations(getResourcePath("sosiepool_size2.json"));
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)m.getTreeTransformations().getModel().getRoot();

        assertEquals(2, root.getChildCount());
        assertEquals(1, root.getChildAt(0).getChildCount());
        assertTrue(m.getBtnLoadInstu().isEnabled());
    }

    /*
    @Test
    public void testGetCodeFragments() throws Exception {

        MavenDependencyResolver dr = new MavenDependencyResolver();
        dr.DependencyResolver(getResourcePath("easymock-light-3.2/pom.xml"));
        InputProgram inputProgram = new InputProgram();
        inputProgram.setFactory(new SpoonMetaFactory().buildNewFactory(getResourcePath("easymock-light-3.2/src/main"), 6));
        inputProgram.setSourceCodeDir(getResourcePath("easymock-light-3.2/src/main"));

        Assert.assertTrue(inputProgram.getCodeFragments().size() > 0);
    }*/


    /**
     * Test the proper showing of the representation data
     */
    @Test
    @Deprecated
    @Ignore
    public void testInstruLogToJTree() throws Exception {
        MainToolWinv0 m = new MainToolWinv0();
        m.setThrowErrors(true);
        m.loadTransformations(getResourcePath("sosiepool_size2.json"));
        m.loadInstrumentation(getResourcePath("."));


        DefaultMutableTreeNode root = (DefaultMutableTreeNode)m.getTreeTransformations().getModel().getRoot();
        CodePosition p = (CodePosition) ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject();
        m.showTests(p);

        root = (DefaultMutableTreeNode)m.getTreeTests().getModel().getRoot();
        assertEquals(2, root.getChildCount());
        assertTrue(0 < root.getChildAt(0).getChildCount());
        assertTrue(0 < root.getChildAt(1).getChildCount());
        assertEquals("org.easymock.tests.ArgumentToStringTest.setUp", root.getChildAt(0).toString());
        assertEquals("org.easymock.tests.ArgumentToStringTest.testThaTest", root.getChildAt(1).toString());
    }

    @Test
    @Deprecated
    @Ignore
    public void testPerformTransformation() throws Exception {
        MainToolWinv0 m = new MainToolWinv0();
        m.setThrowErrors(true);
        m.loadTransformations(getResourcePath("sosiepool_size2.json"));

        DefaultMutableTreeNode root = (DefaultMutableTreeNode)m.getTreeTransformations().getModel().getRoot();
        DefaultMutableTreeNode t1 = (DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0);
        m.getTreeTransformations().setSelectionPath(new TreePath(t1.getPath()));

        String projectPath = getResourcePath("easymock-light-3.2");
        m.performCurrentSelectedTransformation(projectPath + "/pom.xml", projectPath + "/src/main/java");

        assertNotNull(m.getInputProgram());
        assertTrue(m.getInputProgram().getCodeFragments().size() > 0);
    }
}
