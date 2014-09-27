package fr.inria.diversify.analyzerPlugin;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.LocalFsFinder;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.buildSystem.maven.MavenDependencyResolver;
import fr.inria.diversify.codeFragment.CodeFragment;
import fr.inria.diversify.diversification.InputConfiguration;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.factories.SpoonMetaFactory;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import fr.inria.diversify.transformation.ast.ASTReplace;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by marodrig on 26/08/2014.
 */
public class MainToolWin implements ToolWindowFactory {

    private static String TEMP_MOD = "_mod";

    private boolean throwErrors;
    private InputConfiguration inputConfiguration;

    public Tree getTreeTests() {
        return treeTests;
    }

    public InputProgram getInputProgram() {
        return inputProgram;
    }

    public boolean isThrowErrors() {
        return throwErrors;
    }

    public void setThrowErrors(boolean throwErrors) {
        this.throwErrors = throwErrors;
    }

    private class NodeData {

        String description;
        String position;
        JSONObject jsonObject;

        public String toString() {
            return description;
        }
    }

    private ToolWindow parentToolWindow;

    private JPanel pnlContent;
    private JTable tblTransf;
    private JButton btnLoadTransf;
    private JButton btnLoadInstu;
    private Tree treeTransformations;
    private JBScrollPane tblTransfScroll;
    private Tree treeTests;
    //private JButton button1;
    private JLabel lblTPCount;
    private JLabel lblTestCount;
    private Project project;
    private PluginDataLoader formatter;

    private String srcDir;

    private InputProgram inputProgram;
    private Collection<Transformation> transformations;

    //Transformation JSONs ordered by transplantation point position
    private HashMap<String, ArrayList<JSONObject>> transfPerPosition;

    //Current code position shown in the Test tree
    private CodePosition currentCodePosition = null;

    class PopUpTransformations extends JPopupMenu {

        public PopUpTransformations() {
            JMenuItem anItem = new JMenuItem("Sort alphabetically");
            add(new JMenuItem("Sort alphabetically"));
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sortAndShowTransformations(new Comparator<TransformationRepresentation>() {
                        @Override
                        public int compare(TransformationRepresentation o1, TransformationRepresentation o2) {
                            return (o1.getSource().compareTo(o2.getSource())) * -1;
                        }
                    });

                }
            });

            anItem = new JMenuItem("Sort by hits");
            anItem.setEnabled(formatter.getPotsTotalHitCount() > 0);
            add(anItem);
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sortAndShowTransformations(new Comparator<TransformationRepresentation>() {
                        @Override
                        public int compare(TransformationRepresentation o1, TransformationRepresentation o2) {
                            return (o1.getHits() - o2.getHits()) * -1;
                        }
                    });

                }
            });

            anItem = new JMenuItem("Sort by tests");
            anItem.setEnabled(formatter.getPotsTotalHitCount() > 0);
            add(anItem);
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sortAndShowTransformations(new Comparator<TransformationRepresentation>() {
                        @Override
                        public int compare(TransformationRepresentation o1, TransformationRepresentation o2) {
                            return (o1.getTests().size() - o2.getTests().size()) * -1;
                        }
                    });

                }
            });

            anItem = new JMenuItem("Sort by asserts");
            anItem.setEnabled(formatter.getPotsTotalHitCount() > 0);
            add(anItem);
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sortAndShowTransformations(new Comparator<TransformationRepresentation>() {
                        @Override
                        public int compare(TransformationRepresentation o1, TransformationRepresentation o2) {
                            return (o1.getAsserts().size() - o2.getAsserts().size()) * -1;
                        }
                    });

                }
            });

            anItem = new JMenuItem("Sort by asserts hits");
            anItem.setEnabled(formatter.getPotsTotalHitCount() > 0);
            add(anItem);
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sortAndShowTransformations(new Comparator<TransformationRepresentation>() {
                        @Override
                        public int compare(TransformationRepresentation o1, TransformationRepresentation o2) {
                            return (o1.getTotalAssertionHits() - o2.getTotalAssertionHits()) * -1;
                        }
                    });

                }
            });

            anItem = new JMenuItem("Sort by total transplants");
            anItem.setEnabled(formatter.getPotsTotalHitCount() > 0);
            add(anItem);
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sortAndShowTransformations(new Comparator<TransformationRepresentation>() {
                        @Override
                        public int compare(TransformationRepresentation o1, TransformationRepresentation o2) {
                            return (o1.getTransplants().size() - o2.getTransplants().size()) * -1;
                        }
                    });

                }
            });

            addSeparator();
            anItem = new JMenuItem("Go to this position");
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
                    seekCodePosition(data, false);
                }
            });
            add(anItem);

            CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
            TransformationRepresentation tr = getTPOfItem(getTreeTransformations());
            anItem = new JMenuItem();
            anItem.setEnabled(data != null && data instanceof Transplant);
            anItem.setText(anItem.isEnabled() && tr.isTransplantApplied((Transplant) data) ? "Remove transplant" : "Apply transplant");
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        performCurrentSelectedTransformation(
                                project.getBasePath() + File.separator + "pom.xml", getSrcCodePath());
                    } catch (IOException ex) {
                        complain("Cannot perform transformation because ", ex);
                    }
                }
            });
            add(anItem);
        }
    }

    private TransformationRepresentation getTPOfItem(Tree treeTransformations) {
        CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
        if (data instanceof TransformationRepresentation) return (TransformationRepresentation) data;
        if (data instanceof Transplant) {
            return ((Transplant) data).getTransplantationPoint();
        }
        complain("Unable to found parent transformation", null);
        return null;
    }


    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        this.project = project;
        parentToolWindow = toolWindow;

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        String resPath = null;
        try {
            resPath = getClass().getResource("/images").toURI().getPath();
            TransplantNodeRenderer renderer = new TransplantNodeRenderer(
                    new ImageIcon(resPath + "/add.png"), new ImageIcon(resPath + "/replace.png"),
                    new ImageIcon(resPath + "/delete.png"), new ImageIcon(resPath + "/tp.png")
            );
            getTreeTransformations().setCellRenderer(renderer);

        } catch (URISyntaxException e) {
            complain("Cannot load images :(", null);
        }

        Content content = contentFactory.createContent(pnlContent, "", false);
        toolWindow.getContentManager().addContent(content);


    }

    public MainToolWin() {
        getTreeTransformations().setRootVisible(false);
        getTreeTransformations().setModel(null);
        getTreeTests().setRootVisible(false);
        getTreeTests().setModel(null);

        getBtnLoadTransf().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doBtnLoadTransfClick(e);
            }
        });
        getBtnLoadInstu().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doBtnLoadInstrumentationResultClick(e);
            }
        });

        getTreeTests().setToggleClickCount(0);
        getTreeTests().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    CodePosition data = getDataOfSelectedTransformationItem(getTreeTests());
                    seekCodePosition(data, true);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        getTreeTests().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    CodePosition data = getDataOfSelectedTransformationItem(getTreeTests());
                    seekCodePosition(data, true);
                }
            }
        });


        getTreeTransformations().setToggleClickCount(0);
        getTreeTransformations().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
                    seekCodePosition(data, false);
                }
            }
        });
        getTreeTransformations().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
                if (data == null) return; //No selected node
                if (data != currentCodePosition) {
                    showProperties(data);
                    showTests(data);
                    currentCodePosition = data;
                }
                if (e.getClickCount() == 2) {
                    seekCodePosition(data, false);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    PopUpTransformations menu = new PopUpTransformations();
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    /**
     * Initializes the sosie generator input program
     *
     * @param pomPath Path containing the pom file
     * @param srcDir  Path containing the source
     */
    private void initInputProgram(String pomPath, String srcDir) throws IOException {
        inputConfiguration = new InputConfiguration();
        inputConfiguration.getProperty("processor", "fr.inria.diversify.codeFragmentProcessor.StatementProcessor");
        inputConfiguration.getProperty("CodeFragmentClass", "fr.inria.diversify.codeFragment.Statement");
        inputProgram = new InputProgram();
        inputProgram.setSourceCodeDir(srcDir);
        try {
            MavenDependencyResolver dr = new MavenDependencyResolver();
            dr.DependencyResolver(pomPath);
            inputProgram.setFactory(new SpoonMetaFactory().buildNewFactory(srcDir, 5));
        } catch (Exception e) {
            inputProgram = null;
            complain("Unexpected error when applying: " + e.getMessage(), e);
        }

        //TODO: Add a progress bar
        try {
            inputProgram.processCodeFragments(formatter.getSourceJSONArray());
            if (inputProgram.getCodeFragments() == null && inputProgram.getCodeFragments().size() == 0) {
                complain("Unable to apply transformations", null);
                return;
            }
        } catch (JSONException e) {
            complain("Cannot process the array", e);
        }
    }

    /**
     * Obtains a code fragment from the input program given its source and source position
     *
     * @param position Source position of the fragment
     * @param source   Source of the fragment
     * @return The code fragment found. Raises an exception or show a error message box if the fragment was not found
     */
    private CodeFragment getCodeFragmentFromInputProgram(String position, String source) {

        CodeFragment cf = inputProgram.getCodeFragment(position, source);
        if (cf == null) {
            complain("Unable to find code fragment with position '" + position + "' and source '" + source + "'", null);
        }
        return cf;
    }

    /**
     * Obtains the Transformation object from the transformation representation.
     * This is delayed since obtaining the Transformation object itself is expensive. So a TransformationRepresentation
     * is used until the actual Transformation is needed
     *
     * @param parentTP TransformationRepresentation to create the Trasnformation from
     * @param t        Possible transplant (Deletes don't have) in the Transformation
     * @return A transformation
     */
    private Transformation getTransformation(TransformationRepresentation parentTP,
                                             Transplant t) {
        //All transformations has a TP so get it!
        CodeFragment pot = getCodeFragmentFromInputProgram(parentTP.getPosition(), parentTP.getSource());
        if (pot == null) return null;

        if (t.getType().equals("delete")) {
            ASTDelete trans = new ASTDelete();
            trans.setTransplantationPoint(pot);
            t.setTransformation(trans);
            trans.setInputConfiguration(inputConfiguration);
            return trans;
        } else if (t.getType().contains("replace")) {
            CodeFragment transplantCF = getCodeFragmentFromInputProgram(t.getPosition(), t.getSource());
            if (transplantCF == null) return null;

            ASTReplace trans = new ASTReplace();
            trans.setTransplantationPoint(pot);
            trans.setTransplant(transplantCF);
            trans.setInputConfiguration(inputConfiguration);
            return trans;
        } else if (t.getType().contains("add")) {
            CodeFragment transplantCF = getCodeFragmentFromInputProgram(t.getPosition(), t.getSource());
            if (transplantCF == null) return null;

            ASTAdd trans = new ASTAdd();
            trans.setTransplantationPoint(pot);
            trans.setCodeFragmentToAdd(transplantCF);
            trans.setInputConfiguration(inputConfiguration);
            return trans;
        }

        complain("Unknown type of transformation", null);
        return null;
    }

    /**
     * Performs the transplant on the code in the selected transformations
     */
    public void performCurrentSelectedTransformation(String pomPath, String srcDir) throws IOException {

        CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
        if (data != null && data instanceof Transplant) {
            //The intelligible code of the casting cast of the "castation".
            //Anyway,  we get here the parent node...
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode)
                    getTreeTransformations().getLastSelectedPathComponent()).getParent();

            //And here the transplantation point of the current transplant
            TransformationRepresentation tp = (TransformationRepresentation) parentNode.getUserObject();

            //obtain transplant we want to apply
            Transplant transplant = (Transplant) data;

            Transformation transf = transplant.getTransformation();
            if (transf == null) { //don't search twice
                if (inputProgram == null) initInputProgram(pomPath, srcDir); //Init input program if still null
                //Build a transformation object from the Tree
                transf = getTransformation(tp, transplant);
                transplant.setTransformation(transf);
                //Return if none find. At this point someone has already complain if something went wrong
                if (transf == null) return;
            }

            try {
                //Applies or restores the transformation
                tp.switchTransformation(transplant, srcDir, srcDir + TEMP_MOD);
                seekCodePosition(tp, false);
            } catch (Exception e) {
                complain("Cannot apply!! Something went wrong + " + e.getMessage(), e);
            }
        }
    }


    private void sortAndShowTransformations(Comparator<TransformationRepresentation> comparator) {
        ArrayList<TransformationRepresentation> ta = new ArrayList<TransformationRepresentation>(formatter.getRepresentations());
        Collections.sort(ta, comparator);
        showTransformations(ta);
    }

    private CodePosition getDataOfSelectedTransformationItem(Tree tree) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
        return node == null ? null : (CodePosition) node.getUserObject();
    }


    /**
     * Show properties of the transformation represented by that node
     *
     * @param data User data of the node
     */
    private void showProperties(CodePosition data) {
        //Show the properties in the table
        if (data == null) return;

        Object[] s = new Object[]{"Property", "Value"};
        DefaultTableModel dtm = new DefaultTableModel(s, 0);
        if (data instanceof TransformationRepresentation) {
            TransformationRepresentation rep = (TransformationRepresentation) data;
            dtm.addRow(new Object[]{"Hits", rep.getHits()});
            dtm.addRow(new Object[]{"Test count", rep.getTests().size()});
            dtm.addRow(new Object[]{"Assert count", rep.getAsserts().size()});
            dtm.addRow(new Object[]{"Assert Hit total", rep.getTotalAssertionHits()});
            dtm.addRow(new Object[]{"Spoon type", rep.getSpoonTransformationType()});
            dtm.addRow(new Object[]{"Type", rep.getType()});
            dtm.addRow(new Object[]{"Total transplants", rep.getTransplants().size()});
        }
        dtm.addRow(new Object[]{"Source", data.getSource()});
        if (data instanceof Transplant) {
            Transplant t = (Transplant) data;
            dtm.addRow(new Object[]{"Spoon type", t.getSpoonType()});
            dtm.addRow(new Object[]{"Type", t.getType()});
            dtm.addRow(new Object[]{"Variable Map", t.getVariableMap()});
        }

        tblTransf.setModel(dtm);

        tblTransf.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int column = 0; column < tblTransf.getColumnCount(); column++) {
            TableColumn tableColumn = tblTransf.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();
            for (int row = 0; row < tblTransf.getRowCount(); row++) {
                TableCellRenderer cellRenderer = tblTransf.getCellRenderer(row, column);
                Component c = tblTransf.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + tblTransf.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);
                //  We've exceeded the maximum width, no need to check other rows
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }
            tableColumn.setPreferredWidth(preferredWidth);
        }
    }

    /**
     * Seek the code position
     *
     * @param data: Code position to seek
     * @param includeMethodName Tells if the methodName is included
     */
    private void seekCodePosition(CodePosition data, Boolean includeMethodName) {

        if (data == null) return;

        String[] p = data.getPosition().split(":");
        String className = p[0];
        if ( includeMethodName ) {
            className = className.substring(0, className.lastIndexOf('.'));
        }

        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(className, scope);

        if (psiClass != null) {
            //FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            //FileEditor[] fe = fileEditorManager.openFile(vf, true, true);

            //Open the file containing the transformation
            VirtualFile vf = psiClass.getContainingFile().getVirtualFile();
            vf.refresh(false, false);
            //Jump there
            int line = Integer.parseInt(p[1]);
            line = line > 1 ? line - 1 : line;
            new OpenFileDescriptor(project, vf, line, 0).navigateInEditor(project, false);


        } else {
            JOptionPane.showMessageDialog(pnlContent,
                    "I was unable to find the class corresponding to the transformation :( ...\n" +
                            "Do the transformation file belongs to this project?",
                    "Ups...",
                    JOptionPane.ERROR_MESSAGE);
            //What else?
            return;
        }
    }

    /**
     * Loads the Virtual file
     *
     * @return A Virtual file or null in case the user cancels
     */
    private VirtualFile userSelectsFile(boolean directory) {
        //Shows a window to load the file
        FileChooserDescriptor f = new FileChooserDescriptor(!directory, directory, false, false, false, false);
        LocalFsFinder.FileChooserFilter fs = new LocalFsFinder.FileChooserFilter(f, false);
        //Get the file
        return FileChooser.chooseFile(f, project, null);
    }

    /**
     * Set the transformations to the transformation tree
     */
    private void loadTransformationsToTree() {
        VirtualFile fv = userSelectsFile(false);
        //Returns if the user cancels
        if (fv == null) return;
        loadTransformations(fv.getCanonicalPath());
        //if ( fv.getParent(). )
    }

    private void complain(String error, Exception e) {
        if (isThrowErrors()) {
            throw new RuntimeException(error, e);
        } else {
            JOptionPane.showMessageDialog(null,
                    error,
                    "Ups...",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Shows the test in the test tree of a given code position
     *
     * @param cp
     */
    public void showTests(CodePosition cp) {
        treeTests.setModel(null);
        if (cp instanceof TransformationRepresentation) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Test");
            DefaultTreeModel model = new DefaultTreeModel(root);
            TransformationRepresentation tp = (TransformationRepresentation) cp;
            for (TestRepresentation t : tp.getTests()) {
                DefaultMutableTreeNode rep = new DefaultMutableTreeNode(t);
                model.insertNodeInto(rep, root, root.getChildCount());
                for (AssertRepresentation a : t.getAsserts()) {
                    model.insertNodeInto(new DefaultMutableTreeNode(a), rep, rep.getChildCount());
                }
            }
            treeTests.setModel(model);
        }
    }

    private void showTransformations(Collection<TransformationRepresentation> representations) {
        int tpCount = 0;
        int tCount = 0;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Transformations");
        DefaultTreeModel model = new DefaultTreeModel(root);
        for (TransformationRepresentation tp : representations) {
            tpCount++;
            DefaultMutableTreeNode rep = new DefaultMutableTreeNode(tp);
            model.insertNodeInto(rep, root, root.getChildCount());
            for (Transplant t : tp.getTransplants()) {
                tCount++;
                model.insertNodeInto(new DefaultMutableTreeNode(t), rep, rep.getChildCount());
            }
        }
        treeTransformations.setModel(model);
        lblTPCount.setText("Transformations: " + tCount + " | " + "Pots: " +
                tpCount + " | Pot hits: " + formatter.getPotsTotalHitCount());
    }

    public void loadTransformations(String resourcePath) {

        formatter = new PluginDataLoader();
        try {
            //Disable load instru button
            btnLoadInstu.setEnabled(false);
            showTransformations(formatter.fromJSON(resourcePath));
            //Disable load instru button
            btnLoadInstu.setEnabled(true);
        } catch (IOException e) {
            complain("I was unable to open or read from the file. Perhaps is opened already by another application?", e);
        } catch (JSONException e) {
            complain("I was unable to load any transplantation points :(... A wrong JSON file format perhaps?", e);
        }


    }

    /**
     * Sets the test results to the interface
     */
    private void loadInstrumentationResultsToTree() {
        VirtualFile fv = userSelectsFile(true);
        //Returns if the user cancels
        if (fv == null) return;
        loadInstrumentation(fv.getCanonicalPath());
    }

    /**
     * Loads the log data into the interface
     *
     * @param resourcePath
     */
    public void loadInstrumentation(String resourcePath) {

        try {
            formatter.fromLogDir(resourcePath);
            //Update the table with data from the representation
            CodePosition cp = getDataOfSelectedTransformationItem(getTreeTransformations());
            showProperties(cp);
            showTests(cp);

            String template = "Test. Declared: %d - Executed: %d | Asserts. Declared: %d - Executed : %d";
            lblTestCount.setText(String.format(template,
                    formatter.getTestDeclaredCount(), formatter.getTestExecutedCount(),
                    formatter.getAssertionsDeclared(), formatter.getAssertsDeclaredCoveringATP()));

            lblTPCount.setText("Transformations: " + formatter.getTotalTransformations() + " | " + "Pots: " +
                    formatter.getTotalPots() + " | Pot hits: " + formatter.getPotsTotalHitCount());

        } catch (LoadingException e) {
            complain("I was unable to open or read from the file. Perhaps is opened already by another application?", e);
        }

    }


    public Tree getTreeTransformations() {
        return treeTransformations;
    }

    public JButton getBtnLoadTransf() {
        return btnLoadTransf;
    }

    public JButton getBtnLoadInstu() {
        return btnLoadInstu;
    }

    /**
     * Initializes the interface
     */
    public void initInterface() {
        btnLoadInstu.setEnabled(false);
    }

    private void doBtnLoadInstrumentationResultClick(ActionEvent e) {
        loadInstrumentationResultsToTree();
    }


    private void doBtnLoadTransfClick(ActionEvent e) {
        loadTransformationsToTree();
    }

    private String getSrcCodePath() {


        Module[] modules = ModuleManager.getInstance(project).getModules();
        final ContentEntry[] contentEntries = ModuleRootManager.getInstance(modules[0]).getContentEntries();
        for (ContentEntry ce : contentEntries) {
            for (SourceFolder sf : ce.getSourceFolders()) {
                if (!sf.isTestSource()) {
                    return sf.getFile().getPath();
                }
            }
        }
        return null;
    }

}
//StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
            /*
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder("BOOOOOOM!", MessageType.ERROR, null)
                    .setFadeoutTime(7500)
                    .createBalloon()
                    .show(RelativePoint.getCenterOf(treeTransformations),
                            Balloon.Position.atRight);*/