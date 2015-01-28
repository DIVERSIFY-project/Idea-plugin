package fr.inria.diversify.analyzerPlugin.actions;

import fr.inria.diversify.analyzerPlugin.MainToolWinv0;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Creates a report with global depth data
 * <p/>
 * Created by marodrig on 20/11/2014.
 */
public class HitsAndAssertsReportAction extends WinAction {

    private HashMap<Integer, Integer> histogram;

    private HashMap<Integer, Integer> distribution;

    int top;

    public HitsAndAssertsReportAction(MainToolWinv0 mainToolWin, int top) {
        super(mainToolWin);
        histogram = new HashMap<Integer, Integer>();
        distribution = new HashMap<Integer, Integer>();
        this.top = top;
    }

    @Override
    public void execute() {
        histogram = new HashMap<Integer, Integer>();
        Collection<TransformationInfo> reps = getMainToolWin().getVisibleRepresentations();
        Iterator<TransformationInfo> t = reps.iterator();

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < top; i++) {
            b.append(t.next().getHits()).append(",").append(t.next().getTotalAssertionHits()).append(System.lineSeparator());
        }

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:/topHitsAndAsserts.txt"), "utf-8"));
            writer.append(b);
        } catch (IOException ex) {
            new ComplainAction(getMainToolWin(), "Cannot write to file", ex, false).execute();
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
            }
        }
    }
}
