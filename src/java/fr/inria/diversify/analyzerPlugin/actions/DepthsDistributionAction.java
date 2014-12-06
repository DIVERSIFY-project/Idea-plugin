package fr.inria.diversify.analyzerPlugin.actions;

import fr.inria.diversify.analyzerPlugin.MainToolWin;
import fr.inria.diversify.analyzerPlugin.model.PertTestCoverageData;
import fr.inria.diversify.analyzerPlugin.model.TestRepresentation;
import fr.inria.diversify.analyzerPlugin.model.TransformationRepresentation;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Creates a report with global depth data
 * <p/>
 * Created by marodrig on 20/11/2014.
 */
public class DepthsDistributionAction extends WinAction {

    private HashMap<Integer, Integer> histogram;

    private HashMap<Integer, Integer> distribution;

    int top;

    public DepthsDistributionAction(MainToolWin mainToolWin, int top) {
        super(mainToolWin);
        histogram = new HashMap<Integer, Integer>();
        distribution = new HashMap<Integer, Integer>();
        this.top = top;
    }

    @Override
    public void execute() {
        histogram = new HashMap<Integer, Integer>();
        Collection<TransformationRepresentation> reps = getMainToolWin().getVisibleRepresentations();
        //Collect all top transformations iterators for the test representation
        Iterator<PertTestCoverageData>[] t = new Iterator[top];
        Iterator<TransformationRepresentation> repsIt = reps.iterator();
        for (int i = 0; i < top; i++) {
            t[i] = repsIt.next().getTests().values().iterator();
        }

        StringBuilder b = new StringBuilder();
        boolean empty = false;
        while (!empty) {
            empty = true;
            for (int i = 0; i < top; i++) {
                if (t[i].hasNext()) {
                    empty = false;
                    int v = t[i].next().getMeanDepth();
                    if ( v < 0 || v == Integer.MAX_VALUE || v == Integer.MIN_VALUE ) v = 0;
                    b.append(v);
                }
                if (i < top - 1) b.append(",");
            }
            b.append(System.lineSeparator());
        }
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:/reportDepthDistribution.txt"), "utf-8"));
            writer.append(b);
        } catch (IOException ex) {
            new Complain(getMainToolWin(), "Cannot write to file", ex, false).execute();
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
            }
        }
    }
}