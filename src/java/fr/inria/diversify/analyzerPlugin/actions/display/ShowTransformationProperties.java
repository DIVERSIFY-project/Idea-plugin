package fr.inria.diversify.analyzerPlugin.actions.display;

import fr.inria.diversify.analyzerPlugin.MainToolWinv0;
import fr.inria.diversify.analyzerPlugin.actions.WinAction;
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
public class ShowTransformationProperties extends WinAction {

    public ShowTransformationProperties(MainToolWinv0 toolWin) {
        super(toolWin);
    }

    @Override
    public void execute() {

        JTable tblTransf = getMainToolWin().getPropertyTable();
        CodePosition data = getDataOfTransformationTree();
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
                CodePosition p = getDataOfTransformationTree();
                if (p instanceof TransplantInfo) {
                    TransplantInfo t = (TransplantInfo) p;
                    JTable table = (JTable)e.getSource();
                    t.setTags((String) table.getValueAt(4, 1));
                }
            }
        });
    }
}
