package fr.inria.diversify.analyzerPlugin.gui.classificationProperty;


import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import fr.inria.diversify.analyzerPlugin.actions.searching.FilterAndSortAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.ClassificationProperties;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClassifier;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by marodrig on 18/08/2015.
 */
public class QueryExtCoverageForm {

    public JPanel pnlQuery;
    public JButton btnRefresh;
    public JTextField txtSql;

    private String dbFileName = "";

    public QueryExtCoverageForm() {
    }
}
