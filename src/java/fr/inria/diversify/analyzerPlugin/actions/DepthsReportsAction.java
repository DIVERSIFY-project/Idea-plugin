package fr.inria.diversify.analyzerPlugin.actions;

import fr.inria.diversify.analyzerPlugin.MainToolWin;
import fr.inria.diversify.analyzerPlugin.model.PertTestCoverageData;
import fr.inria.diversify.analyzerPlugin.model.TransformationRepresentation;

import java.io.*;
import java.util.Collection;

/**
 * Creates a report with global depth data
 *
 * Created by marodrig on 20/11/2014.
 */
public class DepthsReportsAction extends WinAction {

    public DepthsReportsAction(MainToolWin mainToolWin) {
        super(mainToolWin);
    }

    @Override
    public void execute() {
        StringBuilder b = new StringBuilder();
        Collection<TransformationRepresentation> reps = getMainToolWin().getVisibleRepresentations();
        for ( TransformationRepresentation r : reps ) {
            if ( r.hasVisibleTransplants() ) {
                //b.append(r.getPosition()).append(System.lineSeparator());//Write position
                for (PertTestCoverageData p : r.getTests().values()) {
                    //write values per test
                    b.append(r.getPosition()).append(",");
                    b.append(p.getTest().toString()).append(",");
                    b.append(p.getMinDepth()).append(",").
                            append(p.getMeanDepth()).append(",").
                            append(p.getMaxDepth()).append(",").
                            append(p.getStackMinDepth()).append(",").
                            append(p.getStackMeanDepth()).append(",").
                            append(p.getStackMaxDepth()).append(",").
                            append(p.getHits());

                    b.append(System.lineSeparator());
                }
                //b.append(System.lineSeparator());
            }
        }
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:/report.txt"), "utf-8"));
            writer.append(b);
        } catch (IOException ex) {
            new Complain(getMainToolWin(), "Cannot write to file", ex, false).execute();
        } finally {
            try {writer.close();} catch (Exception ex) {}
        }
    }
}
