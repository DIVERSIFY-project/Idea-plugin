package fr.inria.diversify.analyzerPlugin.actions.searching.classificationProperty;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import fr.inria.diversify.analyzerPlugin.actions.searching.FilterAndSortAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.classificationProperty.QueryExtCoverageForm;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.ClassificationProperties;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.QueryExtendedCoverage;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.QueryExtendedCoverage.CoverageProperties;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClassifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by marodrig on 18/08/2015.
 */
public class QueryExtendedCoveragePropertyAction extends ClassificationPropertyAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        TestEyeProjectComponent component = event.getProject().getComponent(TestEyeProjectComponent.class);
        QueryExtendedCoverage classifier = component.getClassiferByClass(QueryExtendedCoverage.class);
        QueryExtCoverageForm f = new QueryExtCoverageForm();
        installListener(f, (CoverageProperties)classifier.getProperties(), component, event);
        showPropertyWindow(f.pnlQuery);
    }

    private void installListener(final QueryExtCoverageForm form, final CoverageProperties properties,
                                 TestEyeProjectComponent component, final AnActionEvent event) {

        form.btnRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                form.btnRefresh.setEnabled(false);
                form.btnRefresh.setText("Please wait");

                String dbFileName = properties.getDBFileName();
                if (dbFileName == null || dbFileName.isEmpty()) {
                    //Shows a window to load the file
                    FileChooserDescriptor f = new FileChooserDescriptor(true, false, false, false, false, false);
                    //Get the file path
                    VirtualFile fv = FileChooser.chooseFile(f, null, null);
                    dbFileName = fv == null ? "" : fv.getCanonicalPath();
                }

                if (dbFileName == null || dbFileName.isEmpty()) return;


                Connection c = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
                    c.setAutoCommit(false);
                    System.out.println("Opened database successfully");

                    Statement stmt = c.createStatement();
                    try {
                        ResultSet rs = stmt.executeQuery(
                                "SELECT tindex FROM SOSIES" +
                                        //"INNER JOIN EXCOVERAGE ON SOSIES.tp_position = EXCOVERAGE.position " +
                                        " WHERE " + form.txtSql.getText() + ";");
                        Set<String> transplantationPoints = new HashSet<>();
                        while (rs.next()) {
                            transplantationPoints.add(rs.getString("tindex"));
                        }
                        properties.setTransplantationPoints(transplantationPoints);
                        properties.setDBFileName(dbFileName);

                        tryExecute(FilterAndSortAction.class, event);

                    } catch (Exception ex1) {
                        //custom title, warning icon
                        JOptionPane.showMessageDialog(form.btnRefresh,
                                ex1.getMessage(),
                                "Query error" ,
                                JOptionPane.WARNING_MESSAGE);
                    }

                    stmt.close();
                    c.close();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                } finally {
                    if (c != null) try {
                        c.close();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    form.btnRefresh.setText("Refresh");
                    form.btnRefresh.setEnabled(true);
                }
                super.mouseClicked(e);
            }
        });
    }

    private void showPropertyWindow(JComponent component) {
        if (component == null) return;
        //1. Create the frame.
        JFrame frame = new JFrame("Classification properties");
        frame.setAlwaysOnTop (true);
        //2. Optional: What happens when the frame closes?
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //3. Create components and put them in the frame.
        //...create emptyLabel...
        frame.getContentPane().add(component, BorderLayout.CENTER);
        //4. Size the frame.
        frame.pack();
        //5. Show it.
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2, dim.height / 2);
        frame.setPreferredSize(new Dimension(178, 272));
        frame.setVisible(true);
    }
}
