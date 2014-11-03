package fr.inria.diversify.analyzerPlugin;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.ui.JBCheckboxMenuItem;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.clasifiers.ClassifierFactory;
import fr.inria.diversify.analyzerPlugin.clasifiers.TransformClasifier;
import fr.inria.diversify.analyzerPlugin.io.PluginDataExport;
import fr.inria.diversify.analyzerPlugin.io.PluginDataLoader;
import fr.inria.diversify.analyzerPlugin.model.*;
import fr.inria.diversify.buildSystem.maven.MavenDependencyResolver;
import fr.inria.diversify.codeFragment.CodeFragment;
import fr.inria.diversify.diversification.InputConfiguration;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.factories.SpoonMetaFactory;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import fr.inria.diversify.transformation.ast.ASTReplace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

/**
 * Created by marodrig on 26/08/2014.
 */
public class MainToolWin implements ToolWindowFactory {

    private static String TEMP_MOD = "_mod";

    private static String UNCLASSIFIED_TEXT = "Unclassified";

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

    private HashMap<String, Boolean> filterVisible;

    private List<TransformClasifier> classifiers;

    private String transfJSONPath;

    private boolean showClassifIntersection = false;

    protected List<TransformClasifier> getClassifiers() {
        if (classifiers == null) {
            classifiers = buildClasifiers();
        }
        return classifiers;
    }

    protected HashMap<String, Boolean> getFilterVisible() {
        if (filterVisible == null) {
            filterVisible = new HashMap<String, Boolean>();
            filterVisible.put(UNCLASSIFIED_TEXT, true);
        }
        return filterVisible;
    }

    private JPanel pnlContent;
    private JTable tblTransf;
    private JButton btnLoadTransf;
    private JButton btnLoadInstu;
    private Tree treeTransformations;
    private Tree treeTests;
    //private JButton btnSave;
    private JLabel lblTPCount;
    private JLabel lblTestCount;
    private JButton btnSave;
    private JButton btnMedium;
    private JButton btnStrong;
    private JButton btnWeak;
    private Project project;
    private PluginDataLoader formatter;

    private InputProgram inputProgram;

    //Current code position shown in the Test tree
    private CodePosition currentCodePosition = null;

    PopUpTransformations popUpTransformations;

    class PopUpTransformations extends JPopupMenu {

        private final JBCheckboxMenuItem showIfAtLeast;

        private JMenuItem applyItem;

        private JMenuItem gotoThisPosition;

        private Collection<JMenuItem> sortItems;

        //Avoid some events to fire up momentary
        private boolean batchChange = false;

        private class ChangeAllVisibilityListener implements ActionListener {

            public boolean changeTo;

            public JMenu menu;

            public ChangeAllVisibilityListener(JMenu menu, boolean changeTo) {
                super();
                this.menu = menu;
                this.changeTo = changeTo;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                batchChange = true;
                for (int i = 0; i < menu.getItemCount(); i++) {
                    JMenuItem item = menu.getItem(i);
                    if (item != null) {
                        item.setSelected(changeTo);
                        if (item instanceof JBCheckboxMenuItem) filterVisible.put(item.getText(), changeTo);
                    }
                }
                showIfAtLeast.setSelected(!changeTo);
                showClassifIntersection = !changeTo;
                batchChange = false;
                filter();
            }
        }

        public void updateItemsEnableStatus() {
            CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
            gotoThisPosition.setEnabled(data != null);
            TransformationRepresentation tr = getTPOfItem();
            applyItem.setEnabled(tr != null && data != null && data instanceof Transplant);
            applyItem.setText(applyItem.isEnabled() && tr.isTransplantApplied((Transplant) data) ? "Remove transplant" : "Apply transplant");
            for (JMenuItem item : sortItems) {
                item.setEnabled(formatter.getPotsTotalHitCount() > 0);
            }
        }

        public PopUpTransformations() {

            sortItems = new ArrayList<JMenuItem>();

            JMenuItem anItem = new JMenuItem("Sort alphabetically");
            add(anItem);
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sortAndShowTransformations(new Comparator<TransformationRepresentation>() {
                        @Override
                        public int compare(TransformationRepresentation o1, TransformationRepresentation o2) {
                            return (o1.toString().compareTo(o2.toString())) * -1;
                        }
                    });

                }
            });

            anItem = new JMenuItem("Sort by hits");
            sortItems.add(anItem);
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
            sortItems.add(anItem);
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
            sortItems.add(anItem);
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
            sortItems.add(anItem);
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
            sortItems.add(anItem);
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
            anItem.setEnabled(false);
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
                    seekCodePosition(data, false);
                }
            });
            add(anItem);
            gotoThisPosition = anItem;

            anItem = new JMenuItem();
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
            applyItem = anItem;
            add(anItem);


            addSeparator();
            JMenu subMenu = new JMenu("Show");
            add(subMenu);
            ItemListener il = new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (batchChange) return;
                    JBCheckboxMenuItem ei = (JBCheckboxMenuItem) e.getSource();
                    if (getFilterVisible().get(ei.getText()) != ei.isSelected()) {
                        getFilterVisible().put(ei.getText(), ei.isSelected());
                        filter();
                    }
                }
            };

            for (TransformClasifier tc : getClassifiers()) {
                final JBCheckboxMenuItem checkItem = new JBCheckboxMenuItem(tc.getDescription());
                getFilterVisible().put(tc.getDescription(), true);
                subMenu.add(checkItem);
                checkItem.setState(true);
                checkItem.addItemListener(il);
            }
            JBCheckboxMenuItem checkItem = new JBCheckboxMenuItem(UNCLASSIFIED_TEXT);
            subMenu.add(checkItem);
            checkItem.setSelected(true);
            checkItem.addItemListener(il);

            subMenu.addSeparator();
            showIfAtLeast = new JBCheckboxMenuItem("Show intersection");
            showIfAtLeast.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (batchChange) return;
                    showClassifIntersection = showIfAtLeast.isSelected();
                    filter();
                }
            });
            subMenu.add(showIfAtLeast);

            subMenu.addSeparator();
            JMenuItem item = new JMenuItem("Show All");
            item.addActionListener(new ChangeAllVisibilityListener(subMenu, true));
            subMenu.add(item);

            item = new JMenuItem("Hide All");
            item.addActionListener(new ChangeAllVisibilityListener(subMenu, false));
            subMenu.add(item);
        }
    }

    /**
     * Returns all the classifiers we know. It's hard coded. No fancy auto-detection methods
     *
     * @return A list containing the classifiers
     */
    private List<TransformClasifier> buildClasifiers() {
        return new ClassifierFactory().buildClassifiers();
    }

    /**
     * Filter the code positions by classifications
     */
    private void filter() {

        try {
            String pomPath = project.getBasePath() + File.separator + "pom.xml";
            String srcDir = getSrcCodePath();

            Collection<TransformationRepresentation> reps = formatter.getRepresentations();
            for (TransformationRepresentation p : reps) {
                for (Transplant transplant : p.getTransplants()) {
                    try {
                        getTransplantTransformation(transplant, pomPath, srcDir);
                    } catch (RuntimeException rex) {
                        //Skip this transplant
                        softComplain(rex.getMessage());
                        p.getTransplants().remove(transplant);
                        break;
                    }
                    transplant.setVisibility(Transplant.Visibility.unclassified);
                    for (TransformClasifier c : getClassifiers()) {
                        float v;
                        //the only way classification functions modify the score assigned
                        //is by user input, therefore only user filters must be reclassified each time
                        if (!c.isUserFilter() && transplant.isAlreadyClassified(c.getDescription())) {
                            //retrieve classification already assignment to the transformation
                            v = transplant.getClassification(c.getDescription());
                        } else {
                            // evaluates the transformation
                            v = c.value(transplant);
                            transplant.setClassification(c.getDescription(), v);
                        }

                        //sets the visibility on/off depending on the show intersection option
                        if (v != 0) {
                            if (getFilterVisible().get(c.getDescription())) {
                                transplant.setVisibility(Transplant.Visibility.show);
                                if (showClassifIntersection) break;
                            } else {
                                transplant.setVisibility(Transplant.Visibility.hide);
                                if (!showClassifIntersection) break;
                            }
                        }
                    }
                    //If no classification functions and was able to classify the transplant
                    //then the transplant become  unclassified and its visibility is assignment depending
                    //on a special case of classification function
                    if (transplant.getVisibility() == Transplant.Visibility.unclassified) {
                        Transplant.Visibility vis = getFilterVisible().get(UNCLASSIFIED_TEXT) ?
                                Transplant.Visibility.show : Transplant.Visibility.hide;
                        transplant.setVisibility(vis);
                    }
                }
            }
            showTransformations(reps);
        } catch (IOException e) {
            complain("Cannot perform weighting", e);
        }
    }

    /**
     * Complain softly by creating a balloon text instead of a message box
     * @param message
     */
    private void softComplain(String message) {
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("Warning: " + message, MessageType.WARNING, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(treeTransformations),
                        Balloon.Position.atRight);
    }

    /**
     * Gets the transplantation point (TP) of an item. The item could be a TransformationRepresentation or a Transplant
     *
     * @return The transformation representation belonging to that item
     */
    private TransformationRepresentation getTPOfItem() {
        CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
        if (data != null) {
            if (data instanceof TransformationRepresentation) return (TransformationRepresentation) data;
            if (data instanceof Transplant) {
                return ((Transplant) data).getTransplantationPoint();
            }
            complain("Unable to found parent transformation", null);
        }
        return null;
    }

    /**
     * Creates the tool window content
     *
     * @param project    Current Intellij IdeaProject
     * @param toolWindow Tool window over which the UI is represented
     */
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        this.project = project;

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        String resPath;
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


        btnStrong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tagTransplant("strong");
            }
        });
        btnMedium.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tagTransplant("medium");
            }
        });
        btnWeak.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tagTransplant("weak");
            }
        });

        getBtnLoadTransf().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doBtnLoadTransfClick();
            }
        });
        getBtnLoadInstu().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doBtnLoadInstrumentationResultClick();
            }
        });
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doBtnSave();
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
                    if (popUpTransformations == null) {
                        popUpTransformations = new PopUpTransformations();
                    }
                    popUpTransformations.updateItemsEnableStatus();
                    popUpTransformations.show(e.getComponent(), e.getX(), e.getY());
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

    private void tagTransplant(String strong) {
        CodePosition p = getDataOfSelectedTransformationItem(getTreeTransformations());
        if (p instanceof Transplant) {
            Transplant t = (Transplant) p;
            t.setTags(strong);
        }
        showProperties(p);
    }

    private void doBtnSave() {
        try {
            PluginDataExport exports = new PluginDataExport();
            exports.setOriginalJSONFIle(transfJSONPath);
            exports.setClasifiers(classifiers);
            exports.setRepresentations(formatter.getRepresentations());
            exports.save(transfJSONPath);
        } catch (JSONException e1) {
            complain("Cannot save. Error when creating the ", e1);
        } catch (IOException e2) {
            complain("Cannot save", e2);
        }
        //{}
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
            if (inputProgram.getCodeFragments() == null && inputProgram.getCodeFragments().size() == 0)
                complain("Unable to apply transformations", null);
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
            throw new RuntimeException("Unable to find code fragment with position '" + position + "' and source '" + source + "'");
        }
        return cf;
    }

    /**
     * Obtains the Transformation object from the transformation representation.
     * This is delayed since obtaining the Transformation object itself is expensive. So a TransformationRepresentation
     * is used until the actual Transformation is needed
     *
     * @param t Possible transplant (Deletes don't have) in the Transformation
     * @return A transformation
     */
    private Transformation getTransformation(Transplant t) {
        TransformationRepresentation parentTP = t.getTransplantationPoint();
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
     * Obtains the Transformation of a given Transplant
     *
     * @param transplant Transplant for which we want to obtain the Transformation
     * @return The Transformation file of the given Transplant
     */
    private Transformation getTransplantTransformation(Transplant transplant,
                                                       String pomPath, String srcDir) throws IOException {
        Transformation transf = transplant.getTransformation();
        if (transf == null) { //don't search twice
            if (inputProgram == null) initInputProgram(pomPath, srcDir); //Init input program if still null
            //Build a transformation object from the Tree
            transf = getTransformation(transplant);
            transplant.setTransformation(transf);
            //Return if none find. At this point someone has already complain if something went wrong
            if (transf == null) return null;
        }
        return transplant.getTransformation();
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

            getTransplantTransformation(transplant, pomPath, srcDir);

            try {
                //Applies or restores the transformation
                tp.switchTransformation(transplant, srcDir, srcDir + TEMP_MOD);
                seekCodePosition(tp, false);
            } catch (Exception e) {
                complain("Cannot apply!! Something went wrong + " + e.getMessage(), e);
            }
        }
    }


    /**
     * Sorts the transformations by a given comparator
     *
     * @param comparator Comparator to sort
     */
    private void sortAndShowTransformations(Comparator<TransformationRepresentation> comparator) {
        ArrayList<TransformationRepresentation> ta = new ArrayList<TransformationRepresentation>(formatter.getRepresentations());
        Collections.sort(ta, comparator);
        showTransformations(ta);
    }

    /**
     * Gets the user object (CodePosition) of the selected component of a tree
     *
     * @param tree Tree for which we want to extract the CodePosition of the selected node
     * @return A CodePosition object contained in the node
     */
    private CodePosition getDataOfSelectedTransformationItem(Tree tree) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
        return node == null ? null : (CodePosition) node.getUserObject();
    }


    /**
     * Show properties of the transformation represented by the CodePosition passed as parameter
     *
     * @param data CodePosition for which we want to know the properties
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
            dtm.addRow(new Object[]{"Tags", t.getTags()});
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

        dtm.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                CodePosition p = getDataOfSelectedTransformationItem(getTreeTransformations());
                if (p instanceof Transplant) {
                    Transplant t = (Transplant) p;
                    t.setTags((String) tblTransf.getValueAt(4, 1));
                }
            }
        });
    }

    /**
     * Seek the code position
     *
     * @param data:             Code position to seek
     * @param includeMethodName Tells if the methodName is included
     */
    private void seekCodePosition(CodePosition data, Boolean includeMethodName) {

        if (data == null) return;

        String[] p = data.getPosition().split(":");
        String className = p[0];
        if (includeMethodName) {
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
        }
    }

    /**
     * Loads a Virtual file
     *
     * @return A Virtual file or null in case the user cancels
     */
    private VirtualFile userSelectsFile(boolean directory) {
        //Shows a window to load the file
        FileChooserDescriptor f = new FileChooserDescriptor(!directory, directory, false, false, false, false);
        //LocalFsFinder.FileChooserFilter fs = new LocalFsFinder.FileChooserFilter(f, false);
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
    }

    /**
     * Shows an error box
     *
     * @param error Message of the error
     * @param e     Exception that caused the error
     */
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
     * Shows the test representations belonging to a code position in  the Test Representation Tree
     *
     * @param cp Code position to which the test wants to be shown
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

    /**
     * Actually showns the transformation in the transformation Tree
     *
     * @param representations All transformation representation found so far
     */
    private void showTransformations(Collection<TransformationRepresentation> representations) {
        int tpCount = 0;
        int tCount = 0;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Transformations");
        DefaultTreeModel model = new DefaultTreeModel(root);
        for (TransformationRepresentation tp : representations) {

            DefaultMutableTreeNode rep = new DefaultMutableTreeNode(tp);
            for (Transplant t : tp.getTransplants()) {
                if (t.getVisibility() == Transplant.Visibility.show) {
                    tCount++;
                    rep.insert(new DefaultMutableTreeNode(t), rep.getChildCount());
                }
            }
            if (rep.getChildCount() > 0) {
                tpCount++;
                model.insertNodeInto(rep, root, root.getChildCount());
            }
        }
        treeTransformations.setModel(model);
        lblTPCount.setText("Transformations: " + tCount + " | " + "Pots: " +
                tpCount + " | Pot hits: " + formatter.getPotsTotalHitCount());
    }

    /**
     * Loads the transformatios from file and shows it in the Transformation Tree
     *
     * @param resourcePath Path where the transformation file lies
     */
    public void loadTransformations(String resourcePath) {
        transfJSONPath = resourcePath;
        formatter = new PluginDataLoader();
        try {
            //Disable load instru button
            btnLoadInstu.setEnabled(false);
            showTransformations(formatter.fromJSON(resourcePath));
            initInputProgram(project.getBasePath() + File.separator + "pom.xml", getSrcCodePath());
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
     * @param resourcePath Source where the instrumentation lies
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

    /**
     * Handler for the btnLoadInstrumentationResult
     */
    private void doBtnLoadInstrumentationResultClick() {
        loadInstrumentationResultsToTree();
    }


    /**
     * Handler for the btnLoadTransf
     */
    private void doBtnLoadTransfClick() {
        loadTransformationsToTree();
    }

    /**
     * Gets the sorce code path of the current module
     *
     * @return Source code path for the current module
     */
    private String getSrcCodePath() {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        final ContentEntry[] contentEntries = ModuleRootManager.getInstance(modules[0]).getContentEntries();
        for (ContentEntry ce : contentEntries) {
            for (SourceFolder sf : ce.getSourceFolders()) {
                if (!sf.isTestSource()) {
                    if (sf.getFile() == null) complain("Cannot find file", null);
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