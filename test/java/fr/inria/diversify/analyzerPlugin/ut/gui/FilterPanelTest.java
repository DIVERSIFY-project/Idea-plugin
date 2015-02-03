package fr.inria.diversify.analyzerPlugin.ut.gui;

import fr.inria.diversify.analyzerPlugin.gui.FilterPanel;
import fr.inria.diversify.analyzerPlugin.ut.component.TestEyeProjectComponentTest;
import org.junit.Test;

import javax.swing.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 02/02/2015.
 */
public class FilterPanelTest {

    @Test
    public void testFillWithCheckBoxes() {
        FilterPanel p = new FilterPanel(new TestEyeProjectComponentTest.MyClassiferFactory());
        //2 chckboxes, "show all" and the separator
        assertEquals(4, p.getModel().getSize());
        assertTrue(p.getModel().getElementAt(1) instanceof JLabel);
        assertTrue(p.getModel().getElementAt(2) instanceof FilterPanel.ActionCheckBox);
        assertTrue(p.getModel().getElementAt(3) instanceof FilterPanel.ActionCheckBox);
    }

    @Test
    public void testUncheckAll() {
        FilterPanel p = new FilterPanel(new TestEyeProjectComponentTest.MyClassiferFactory());
        p.uncheckAllNoTriggerEvent();
        assertFalse(((FilterPanel.ActionCheckBox) p.getModel().getElementAt(2)).isSelected());
        assertFalse(((FilterPanel.ActionCheckBox) p.getModel().getElementAt(3)).isSelected());
    }

    @Test
    public void testCheckAll() {
        FilterPanel p = new FilterPanel(new TestEyeProjectComponentTest.MyClassiferFactory());
        p.uncheckAllNoTriggerEvent();
        p.checkAllNoTriggerEvent();
        assertTrue(((FilterPanel.ActionCheckBox) p.getModel().getElementAt(2)).isSelected());
        assertTrue(((FilterPanel.ActionCheckBox) p.getModel().getElementAt(3)).isSelected());
    }

}
