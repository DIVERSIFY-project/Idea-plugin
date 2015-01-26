package fr.inria.diversify.analyzerPlugin;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.ui.JBCheckboxMenuItem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.actions.*;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationProperties;
import fr.inria.diversify.analyzerPlugin.actions.replay.PerformCurrentTransformation;
import fr.inria.diversify.analyzerPlugin.actions.reporting.DepthsDistributionAction;
import fr.inria.diversify.analyzerPlugin.actions.reporting.DepthsHistogramAction;
import fr.inria.diversify.analyzerPlugin.actions.reporting.DepthsReportsAction;
import fr.inria.diversify.analyzerPlugin.actions.searching.SearchPosition;
import fr.inria.diversify.analyzerPlugin.actions.searching.SeekCodeTransformation;
import fr.inria.diversify.analyzerPlugin.clasifiers.ClassifierFactory;
import fr.inria.diversify.analyzerPlugin.clasifiers.TransformClasifier;
import fr.inria.diversify.analyzerPlugin.io.PluginDataExport;
import fr.inria.diversify.analyzerPlugin.io.PluginDataLoader;
import fr.inria.diversify.analyzerPlugin.model.*;
import fr.inria.diversify.buildSystem.maven.MavenDependencyResolver;
import fr.inria.diversify.diversification.InputConfiguration;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.factories.SpoonMetaFactory;
import fr.inria.diversify.transformation.Transformation;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.kevoree.log.Log;

import javax.swing.*;
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

    private Collection<TransformationInfo> visibleRepresentations;

    Comparator<TransformationInfo> currentComparator;

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
    private JLabel lblTPCount;
    private JLabel lblTestCount;
    private JButton btnSave;
    private JButton btnMedium;
    private JButton btnStrong;
    private JButton btnWeak;
    private JButton btnReports;
    private JTextPane txtDiff;
    private JButton btnSearchNext;
    private JButton btnSearchPrev;
    private JTextField txtSearchPosition;
    private Project project;
    private PluginDataLoader formatter;
    private InputProgram inputProgram;

    //Current code position shown in the Test tree
    private CodePosition currentCodePosition = null;

    PopUpTransformations popUpTransformations;

    public JTextField getTextSearch() {
        return txtSearchPosition;
    }

    public JTable getPropertyTable() {
        return tblTransf;
    }

    public Project getProject() {
        return project;
    }

    public Component getPanelContent() {
        return pnlContent;
    }

    public Collection<TransformationInfo> getVisibleRepresentations() {
        if (visibleRepresentations == null) visibleRepresentations = new ArrayList<TransformationInfo>();
        return visibleRepresentations;
    }

    public void setVisibleRepresentations(Collection<TransformationInfo> visibleRepresentations) {
        this.visibleRepresentations = visibleRepresentations;
    }

    public JTextPane getTxtDiff() {
        return txtDiff;
    }


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
            TransformationInfo tr = getTPOfItem();
            applyItem.setEnabled(tr != null && data != null && data instanceof TransplantInfo);
            applyItem.setText(applyItem.isEnabled() && tr.isTransplantApplied((TransplantInfo) data) ? "Remove transplant" : "Apply transplant");
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
                    sortAndShowTransformations(new Comparator<TransformationInfo>() {
                        @Override
                        public int compare(TransformationInfo o1, TransformationInfo o2) {
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
                    sortAndShowTransformations(new Comparator<TransformationInfo>() {
                        @Override
                        public int compare(TransformationInfo o1, TransformationInfo o2) {
                            return (int) Math.signum(o1.getHits() - o2.getHits()) * -1;
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
                    sortAndShowTransformations(new Comparator<TransformationInfo>() {
                        @Override
                        public int compare(TransformationInfo o1, TransformationInfo o2) {
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
                    sortAndShowTransformations(new Comparator<TransformationInfo>() {
                        @Override
                        public int compare(TransformationInfo o1, TransformationInfo o2) {
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
                    sortAndShowTransformations(new Comparator<TransformationInfo>() {
                        @Override
                        public int compare(TransformationInfo o1, TransformationInfo o2) {
                            return (int) Math.signum((double) (o1.getTotalAssertionHits() - o2.getTotalAssertionHits())) * -1;
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
                    sortAndShowTransformations(new Comparator<TransformationInfo>() {
                        @Override
                        public int compare(TransformationInfo o1, TransformationInfo o2) {
                            return (o1.getTransplants().size() - o2.getTransplants().size()) * -1;
                        }
                    });
                }
            });

            anItem = new JMenuItem("Sort by Var diff");
            //sortItems.add(anItem);
            add(anItem);
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sortAndShowTransformations(new Comparator<TransformationInfo>() {
                        @Override
                        public int compare(TransformationInfo o1, TransformationInfo o2) {
                            return (o2.getVarDiff() - o1.getVarDiff());
                        }
                    });
                }
            });


            anItem = new JMenuItem("Sort by Call diff");
            //sortItems.add(anItem);
            add(anItem);
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sortAndShowTransformations(new Comparator<TransformationInfo>() {
                        @Override
                        public int compare(TransformationInfo o1, TransformationInfo o2) {
                            return (o2.getCallDiff() - o1.getCallDiff());
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
                    seekCodePosition(false);
                }
            });
            add(anItem);
            gotoThisPosition = anItem;

            //Apply transplant
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

            anItem = new JMenuItem("Toggle breakpoints in 0 hits");
            anItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setBreakpoints();
                }
            });
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
     * Sets breakpoints in all zero hits TP
     */
    private void setBreakpoints() {
        new SetBreakPointsActions(this).execute();
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
     * Returns the representations obtained so far
     *
     * @return A Collection of transformation representations
     */
    public Collection<TransformationInfo> getRepresentations() {
        return formatter.getRepresentations();
    }

    /**
     * Filter the code positions by classifications
     */
    private void filter() {

        final Collection<TransformationInfo> reps = formatter.getRepresentations();

        ProgressManager.getInstance().run(new Task.Backgroundable(project,
                "Sorting and filtering (This will be done only once)...") {

            public void onSuccess() {
                super.onSuccess();
                showTransformations(reps);
            }

            public void run(@NotNull ProgressIndicator progressIndicator) {
                int i = 0;

                try {
                    String pomPath = project.getBasePath() + File.separator + "pom.xml";
                    String srcDir = getSrcCodePath();

                    if (getVisibleRepresentations() == null)
                        setVisibleRepresentations(new ArrayList<TransformationInfo>());
                    else getVisibleRepresentations().clear();

                    int progress = 1;
                    for (TransformationInfo p : reps) {

                        if ( progressIndicator.isCanceled() ) return;
                        progressIndicator.setFraction((double) progress / (double) reps.size());
                        progress++;

                        for (TransplantInfo transplant : p.getTransplants()) {
                            try {
                                i++;
                                getTransplantTransformation(transplant, pomPath, srcDir);
                            } catch (RuntimeException rex) {
                                //Skip this transplant
                                Log.warn(i + ". There was a problem with " + transplant.toString() + ". Because " + rex.getMessage());
                                softComplain(rex.getMessage());
                                p.getTransplants().remove(transplant);
                                break;
                            }
                            transplant.setVisibility(TransplantInfo.Visibility.unclassified);
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
                                        transplant.setVisibility(TransplantInfo.Visibility.show);
                                        if (showClassifIntersection) break;
                                    } else {
                                        transplant.setVisibility(TransplantInfo.Visibility.hide);
                                        if (!showClassifIntersection) break;
                                    }
                                }
                            }
                            //If no classification functions and was able to classify the transplant
                            //then the transplant become  unclassified and its visibility is assignment depending
                            //on a special case of classification function
                            if (transplant.getVisibility() == TransplantInfo.Visibility.unclassified) {
                                TransplantInfo.Visibility vis = getFilterVisible().get(UNCLASSIFIED_TEXT) ?
                                        TransplantInfo.Visibility.show : TransplantInfo.Visibility.hide;
                                transplant.setVisibility(vis);
                            }
                        }
                    }

                } catch (IOException e) {
                    complain("Cannot perform weighting", e);
                }
            }
        });

    }

    /**
     * Complain softly by creating a balloon text instead of a message box
     *
     * @param message
     */
    private void softComplain(String message) {
        new Complain(this, message, null, true).execute();
    }

    /**
     * Gets the transplantation point (TP) of an item. The item could be a TransformationRepresentation or a Transplant
     *
     * @return The transformation representation belonging to that item
     */
    private TransformationInfo getTPOfItem() {
        CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
        if (data != null) {
            if (data instanceof TransformationInfo) return (TransformationInfo) data;
            if (data instanceof TransplantInfo) {
                return ((TransplantInfo) data).getTransplantationPoint();
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

        Log.info("Hey, I'm loggin");

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
        final MainToolWin me = this;
        btnReports.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DepthsReportsAction(me).execute();
                new DepthsHistogramAction(me).execute();
                new DepthsDistributionAction(me, 20).execute();
                new HitsAndAssertsReportAction(me, 20).execute();
            }
        });

        btnSearchNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SearchPosition(me, 1).execute();
            }
        });
        btnSearchPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SearchPosition(me, -1).execute();
            }
        });

        getTreeTests().setToggleClickCount(0);
        getTreeTests().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    //CodePosition data = getDataOfSelectedTransformationItem(getTreeTests());
                    seekCodePosition(true);
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
                    //CodePosition data = getDataOfSelectedTransformationItem(getTreeTests());
                    seekCodePosition(true);
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
                    //CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
                    seekCodePosition(false);
                }
            }
        });

        getTreeTransformations().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CodePosition data = getDataOfSelectedTransformationItem(getTreeTransformations());
                if (data == null) return; //No selected node
                if (data != currentCodePosition) {
                    showProperties();
                    showTests(data);
                    new ShowDifferencesAction(me, data).execute();
                    currentCodePosition = data;
                }
                if (e.getClickCount() == 2) {
                    seekCodePosition(false);
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
        if (p instanceof TransplantInfo) {
            TransplantInfo t = (TransplantInfo) p;
            t.setTags(strong);
        }
        showProperties();
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
        inputConfiguration.setInputProgram(inputProgram);
        try {
            MavenDependencyResolver dr = new MavenDependencyResolver();
            dr.DependencyResolver(pomPath);
            inputProgram.setFactory(new SpoonMetaFactory().buildNewFactory(srcDir, 7));
        } catch (Exception e) {
            inputProgram = null;
            complain("Unexpected error when applying: " + e.getMessage(), e);
        }

        //TODO: Add a progress bar
        try {
            //inputProgram.processCodeFragments(formatter.getSourceJSONArray());
            inputProgram.processCodeFragments();
            if (inputProgram.getCodeFragments() == null && inputProgram.getCodeFragments().size() == 0)
                complain("Unable to apply transformations", null);
        } catch (Exception e) {
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
    /*
    private CodeFragment getCodeFragmentFromInputProgram(String position, String source) {
        CodeFragment cf = inputProgram.getCodeFragment(position, source);
        if (cf == null) {
            throw new RuntimeException("Unable to find code fragment with position '" + position + "' and source '" + source + "'");
        }
        return cf;
    }*/

    /**
     * Obtains the Transformation object from the transformation representation.
     * This is delayed since obtaining the Transformation object itself is expensive. So a TransformationRepresentation
     * is used until the actual Transformation is needed
     *
     * @param t Possible transplant (Deletes don't have) in the Transformation
     * @return A transformation
     */
    private Transformation getTransformation(TransplantInfo t) {
        t.initTransformation(inputConfiguration);
        Transformation result = t.getTransformation();
        if (result == null) {
            complain("Unknown type of transformation", null);
        }
        return result;
    }

    /**
     * Obtains the Transformation of a given Transplant
     *
     * @param transplant Transplant for which we want to obtain the Transformation
     * @return The Transformation file of the given Transplant
     */
    public Transformation getTransplantTransformation(TransplantInfo transplant,
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
        PerformCurrentTransformation perform = new PerformCurrentTransformation(this, pomPath, srcDir);
        perform.execute();
    }


    /**
     * Sorts the transformations by a given comparator
     *
     * @param comparator Comparator to sort
     */
    private void sortAndShowTransformations(Comparator<TransformationInfo> comparator) {
        currentComparator = comparator;
        filter();
    }

    private Collection<TransformationInfo> sortTransformations(Comparator<TransformationInfo> comparator, Collection<TransformationInfo> representations) {
        ArrayList<TransformationInfo> ta = new ArrayList<TransformationInfo>(representations);
        Collections.sort(ta, comparator);
        return ta;
    }

    /**
     * Gets the user object (CodePosition) of the selected component of a tree
     *
     * @param tree Tree for which we want to extract the CodePosition of the selected node
     * @return A CodePosition object contained in the node
     */
    public CodePosition getDataOfSelectedTransformationItem(Tree tree) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
        return node == null ? null : (CodePosition) node.getUserObject();
    }

    /**
     * Returns the selected code position in the interface
     *
     * @return
     */
    public CodePosition getSelectedCodePosition() {
        if (treeTests.hasFocus()) {
            return getDataOfSelectedTransformationItem(treeTests);
        } else if (treeTransformations.hasFocus()) {
            return getDataOfSelectedTransformationItem(treeTransformations);
        }
        return null;
    }

    /**
     * Show properties of the transformation represented by the CodePosition passed as parameter
     */
    private void showProperties() {
        ShowTransformationProperties showProperty = new ShowTransformationProperties(this);
        showProperty.execute();
    }

    private void seekCodePosition(Boolean includeMethodName) {
        seekCodePosition(getSelectedCodePosition(), includeMethodName);
    }

    /**
     * Seek the code position
     *
     * @param includeMethodName Tells if the methodName is included
     * @param cp                code position to navigate to
     */
    private void seekCodePosition(CodePosition cp, Boolean includeMethodName) {
        SeekCodeTransformation seek = new SeekCodeTransformation(this, cp, includeMethodName);
        seek.execute();
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
        } else new Complain(this, error, e, false).execute();
    }

    /**
     * Shows the test representations belonging to a code position in  the Test Representation Tree
     *
     * @param cp Code position to which the test wants to be shown
     */
    public void showTests(CodePosition cp) {
        treeTests.setModel(null);
        if (cp instanceof TransformationInfo) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Test");
            DefaultTreeModel model = new DefaultTreeModel(root);
            TransformationInfo tp = (TransformationInfo) cp;
            for (PertTestCoverageData t : tp.getTests().values()) {
                DefaultMutableTreeNode rep = new DefaultMutableTreeNode(t);
                model.insertNodeInto(rep, root, root.getChildCount());
                for (AssertInfo a : t.getTest().getAsserts()) {
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
    private void showTransformations(Collection<TransformationInfo> representations) {
        int tpCount = 0;
        int tCount = 0;

        if (currentComparator != null) {
            setVisibleRepresentations(sortTransformations(currentComparator, representations));
        } else {
            getVisibleRepresentations().addAll(representations);
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Transformations");
        DefaultTreeModel model = new DefaultTreeModel(root);
        for (TransformationInfo tp : getVisibleRepresentations()) {

            DefaultMutableTreeNode rep = new DefaultMutableTreeNode(tp);
            for (TransplantInfo t : tp.getTransplants()) {
                if (t.getVisibility() == TransplantInfo.Visibility.show) {
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
    public void loadTransformations(final String resourcePath) {
        Task.Modal m = new Task.Modal(project, "Loading Transformations, patience...", false) {
            public void onSuccess() {
                super.onSuccess();
                try {
                    showTransformations(formatter.fromJSON(resourcePath));
                } catch (IOException e) {
                    complain("I was unable to open or read from the file. Perhaps is opened already by another application?", e);
                } catch (JSONException e) {
                    complain("I was unable to load any transplantation points :(... A wrong JSON file format perhaps?", e);
                }
            }
            public void run(@NotNull ProgressIndicator progressIndicator) {
                transfJSONPath = resourcePath;
                formatter = new PluginDataLoader();
                try {
                    //Disable load instru button
                    btnLoadInstu.setEnabled(false);
                    initInputProgram(project.getBasePath() + File.separator + "pom.xml", getSrcCodePath());
                    //Disable load instru button
                    btnLoadInstu.setEnabled(true);
                } catch (IOException e) {
                    complain("I was unable to open or read from the file. Perhaps is opened already by another application?", e);
                }
            }
        };

        ProgressManager.getInstance().run(m);
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
    public void loadInstrumentation(final String resourcePath) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project,
                "Loading Instrumentation") {
            public void run(@NotNull ProgressIndicator progressIndicator) {
                int i = 0;
                try {
                    formatter.fromLogDir(resourcePath);
                    //Update the table with data from the representation
                    CodePosition cp = getDataOfSelectedTransformationItem(getTreeTransformations());
                    showProperties();
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
        });
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