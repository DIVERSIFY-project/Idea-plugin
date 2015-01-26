package fr.inria.diversify.analyzerPlugin.actions.reporting;

import fr.inria.diversify.analyzerPlugin.MainToolWin;
import fr.inria.diversify.analyzerPlugin.actions.Complain;
import fr.inria.diversify.analyzerPlugin.actions.WinAction;
import fr.inria.diversify.analyzerPlugin.model.PertTestCoverageData;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a report with global depth data
 * <p/>
 * Created by marodrig on 20/11/2014.
 */
public class DepthsHistogramAction extends WinAction {

    private HashMap<Integer, Integer> histogram;

    private HashMap<Integer, Integer> distribution;

    public DepthsHistogramAction(MainToolWin mainToolWin) {
        super(mainToolWin);
        histogram = new HashMap<Integer, Integer>();
        distribution = new HashMap<Integer, Integer>();
    }

    private void incKey(HashMap<Integer, Integer> h, Integer key, int value, boolean increment) {
        if (h.containsKey(key)) {
            if (increment) {
                int k = h.get(key);
                h.put(key, k + 1);
            }
        } else { h.put(key, 1); }
    }

    @Override
    public void execute() {
        histogram = new HashMap<Integer, Integer>();

        Collection<TransformationInfo> reps = getMainToolWin().getVisibleRepresentations();
        for (TransformationInfo r : reps) {
            if ( r.hasVisibleTransplants() ) {
                HashMap<Integer, Integer> localHistogram = new HashMap<Integer, Integer>();
                for (PertTestCoverageData p : r.getTests().values()) {
                    incKey(localHistogram, p.getMaxDepth(), 1, false);

                    if (p.getMaxDepth() != p.getMeanDepth()) {
                        incKey(localHistogram, p.getMeanDepth(), 1, false);
                    }

                    if (p.getMaxDepth() != p.getMinDepth()) {
                        incKey(localHistogram, p.getMinDepth(), 1, false);
                    }
                }
                incKey(distribution, localHistogram.size(), 1, true);
                for (Map.Entry<Integer, Integer> entry : localHistogram.entrySet()) {
                    incKey(histogram, entry.getKey(), entry.getValue(), true);
                }
            }
        }

        StringBuilder b = new StringBuilder();

        b.append(System.lineSeparator());
        b.append("========== HISTOGRAM ==========");
        b.append(System.lineSeparator());
        for (Map.Entry<Integer, Integer> entry : histogram.entrySet()) {
            //write values per test
            b.append(entry.getKey()).append(",").append(entry.getValue());
            b.append(System.lineSeparator());
        }

        b.append(System.lineSeparator());
        b.append("========== DISTRIBUTION ==========");
        b.append(System.lineSeparator());

        for (Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
            //write values per test
            b.append(entry.getKey()).append(",").append(entry.getValue());
            b.append(System.lineSeparator());
        }

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:/reportHistogram.txt"), "utf-8"));
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
