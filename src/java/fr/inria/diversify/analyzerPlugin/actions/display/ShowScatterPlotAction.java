package fr.inria.diversify.analyzerPlugin.actions.display;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.EventLogList;
import fr.inria.diversify.analyzerPlugin.gui.Scatter;
import icons.TestEyeIcons;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.*;

/**
 *
 * Created by marodrig on 10/02/2015.
 */
public class ShowScatterPlotAction extends TestEyeAction {

    //private final EventLogList list;

    public ShowScatterPlotAction() {
        super("Show scatter plot", "Show scatter plot", TestEyeIcons.AddTransformation);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        try {
            //1. Create the frame.
            JFrame frame = new JFrame("FrameDemo");

//2. Optional: What happens when the frame closes?
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//3. Create components and put them in the frame.
//...create emptyLabel...
            frame.getContentPane().add(new JLabel(), BorderLayout.CENTER);

//4. Size the frame.
            frame.pack();

//5. Show it.
            frame.setVisible(true);
        } catch (Exception e) {
            hardComplain("Cannot show. Errors", e);
        }

    }

}
