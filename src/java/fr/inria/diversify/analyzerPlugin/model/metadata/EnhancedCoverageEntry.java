package fr.inria.diversify.analyzerPlugin.model.metadata;

import fr.inria.diversify.analyzerPlugin.LoadingException;

import java.util.HashMap;

/**
 * Created by marodrig on 03/02/2015.
 */
public class EnhancedCoverageEntry extends EntryLog {

    public static final String TP = "T";
    public static final String NEW_TEST = "TB";
    public static final String END_TEST = "TE";
    public static final String ASSERT = "AS";
    public static final String ASSERT_COUNT = "ASC";
    public static final String TP_COUNT = "TC";

    //executions of the loggin element
    private int executions = 1;
    //Min depth of the entry (Transplant points only)
    private int minDepth;

    private int maxDepth;

    private int meanDepth;

    public EnhancedCoverageEntry(String file, int line, HashMap<Integer, String> idMap) {
        super(file, line, idMap);
    }

    /**
     * Extract the data from the spliced login string
     */
    protected void fromLineData(String[] lineData) throws LoadingException {
        super.fromLineData(lineData);

        if (getType().equals(END_TEST)) return;

        //Get Position
        String p = getIdMap().get(Integer.parseInt(lineData[2]));
        if ( p == null ) throw new LoadingException(-1, "Unable to find position for id: " + lineData[2], "Error");
        position = p.substring(p.indexOf(">") + 1);

        if (getType().equals(TP_COUNT) || getType().equals(ASSERT_COUNT)) {
            executions = Integer.parseInt(lineData[3]);
            if (getType().equals(TP_COUNT)) {
                minDepth = Integer.parseInt(lineData[4]);
                meanDepth = Integer.parseInt(lineData[5]);
                maxDepth = Integer.parseInt(lineData[6]);
            }
        } else if (getType().equals(TP)) {
            maxDepth = meanDepth = minDepth = Integer.parseInt(lineData[3]);
        }
    }

    public int getExecutions() {
        return executions;
    }

    public int getMinDepth() {
        return minDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMeanDepth() {
        return meanDepth;
    }
}
