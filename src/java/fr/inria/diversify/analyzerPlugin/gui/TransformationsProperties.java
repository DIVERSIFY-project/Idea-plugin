package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.openapi.actionSystem.DataKey;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * Created by marodrig on 29/01/2015.
 */
public class TransformationsProperties extends JTable implements com.intellij.openapi.actionSystem.DataProvider {

    public static final DataKey<TransformationsProperties>
            TEST_EYE_PROPERTY_TABLE = DataKey.create("test.eye.property.table");


    public TransformationsProperties() {
        super();
    }

    public void showTransformations(final CodePosition data) {
        //Show the properties in the table
        if (data == null) return;

        Object[] s = new Object[]{"Property", "Value"};
        DefaultTableModel dtm = new DefaultTableModel(s, 0);
        if (data instanceof TransformationInfo) {
            TransformationInfo rep = (TransformationInfo) data;
            dtm.addRow(new Object[]{"Hits", rep.getHits()});
            dtm.addRow(new Object[]{"Test count", rep.getTests().size()});
            dtm.addRow(new Object[]{"Assert count", rep.getAsserts().size()});
            dtm.addRow(new Object[]{"Assert Hit total", rep.getTotalAssertionHits()});
            dtm.addRow(new Object[]{"Spoon type", rep.getSpoonType()});
            dtm.addRow(new Object[]{"Type", rep.getType()});
            dtm.addRow(new Object[]{"Total transplants", rep.getTransplants().size()});
            dtm.addRow(new Object[]{"Nb of Var Diff", rep.getVarDiff()});
            dtm.addRow(new Object[]{"Nb of Call Diff", rep.getCallDiff()});
        }
        dtm.addRow(new Object[]{"Source", data.getSource()});
        if (data instanceof TransplantInfo) {
            TransplantInfo t = (TransplantInfo) data;
            dtm.addRow(new Object[]{"Spoon type", t.getSpoonType()});
            dtm.addRow(new Object[]{"Type", t.getType()});
            dtm.addRow(new Object[]{"Variable Map", t.getVariableMap()});
            dtm.addRow(new Object[]{"Tags", t.getTags()});
            dtm.addRow(new Object[]{"Index", t.getIndex()});
        }

        setModel(dtm);

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int column = 0; column < getColumnCount(); column++) {
            TableColumn tableColumn = getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();
            for (int row = 0; row < getRowCount(); row++) {
                TableCellRenderer cellRenderer = getCellRenderer(row, column);
                Component c = prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + getIntercellSpacing().width;
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
                CodePosition p = data;
                if (p instanceof TransplantInfo) {
                    TransplantInfo t = (TransplantInfo) p;
                    JTable table = (JTable)e.getSource();
                    t.setTags((String) table.getValueAt(4, 1));
                }
            }
        });
    }

    @Nullable
    @Override
    public Object getData(String s) {
        return s.equals(TEST_EYE_PROPERTY_TABLE.getName()) ? this : null;
    }
}
