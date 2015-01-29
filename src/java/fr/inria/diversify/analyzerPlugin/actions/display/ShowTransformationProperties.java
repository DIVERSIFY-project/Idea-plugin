package fr.inria.diversify.analyzerPlugin.actions.display;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.MainToolWinv0;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.WinAction;
import fr.inria.diversify.analyzerPlugin.gui.TransformationsProperties;
import fr.inria.diversify.analyzerPlugin.gui.TreeTransformations;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * Created by marodrig on 03/11/2014.
 */
public class ShowTransformationProperties extends TestEyeAction {

    public static final String ID = "TestEye." + ShowTransformationProperties.class.getSimpleName();

    private final TransformationsProperties propertyTable;

    public ShowTransformationProperties(TransformationsProperties p) {
        this.propertyTable = p;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        //Get the Tranformation's tree thanks tho the data context magic in IntelliJ IDEA framework
        TreeTransformations tree = event.getData(TreeTransformations.TEST_EYE_TREE_TRANSFORMATIONS);

        CodePosition data = tree.getSelectedCodePosition();
        if ( data != null ) {
            TransformationsProperties p = event.getData(TransformationsProperties.TEST_EYE_PROPERTY_TABLE);
            if (p == null) p = propertyTable;
            p.showTransformations(data);
        }
    }
}
