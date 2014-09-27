package fr.inria.diversify.analyzerPlugin;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by marodrig on 03/09/2014.
 */
public class PluginDataFormatterTest {

    private String getResourcePath(String name) throws Exception {
        return getClass().getResource("/" + name).toURI().getPath();
    }

    private void assertProperLoadingOfTransformations(PluginDataLoader formatter) {
        //Test all where loaded
        ArrayList<TransformationRepresentation> ar = new ArrayList<TransformationRepresentation>(formatter.getRepresentations());
        assertEquals(2, ar.size());
        assertEquals(2, formatter.getTotalPots());

        //Test some properties where loaded properly
        ArrayList<String> strings = new ArrayList<String>(Arrays.asList(new String[]{
                "org.easymock.internal.RecordState:318", "org.easymock.internal.MethodSerializationWrapper:40"}));
        for (TransformationRepresentation t : ar) {
            assertTrue(strings.contains(t.getPosition()));
            strings.remove(t.getPosition());
        }
    }

    private void assertProperLoadingOfLog(PluginDataLoader formatter) {
        //Assert the proper loading of log (Data obtained from the logMain_123456 file)
        ArrayList<TransformationRepresentation> ar = new ArrayList<TransformationRepresentation>(formatter.getRepresentations());

        //Tests
        assertEquals(2, ar.get(0).getTests().size());
        assertEquals(1, ar.get(1).getTests().size());

        //Assertions
        assertEquals(3, ar.get(0).getAsserts().size());
        assertEquals(1, ar.get(1).getAsserts().size());

        //Hits
        assertEquals(6, ar.get(0).getHits());
        assertEquals(1, ar.get(1).getHits());

        //Test the counting of the ids founds
        //assertEquals(4, formatter.getAssertionsDeclared());
        assertEquals(3, formatter.getTestDeclaredCount());


        /*
        //TP, Asserts and Test executed at least once
        assertEquals(7, formatter.getPotsTotalHitCount());
        assertEquals(4, formatter.getAssertsDeclaredCoveringATP());
        assertEquals(3, formatter.getTestExecutedCount());

        //Count test and assertion exercising at least one TP
        assertEquals(3, formatter.getAssertionsExecutedCoveringCount());
        assertEquals(2, formatter.getTestDeclaredCoveringATPCount());
        */
    }

    private void testConvertFromTransformationsJSON(String path) throws Exception {
        PluginDataLoader formatter = new PluginDataLoader();
        Collection<TransformationRepresentation> representations =
                formatter.fromJSON(getResourcePath(path));

        assertProperLoadingOfTransformations(formatter);
    }

    /**
     * Test the conversion from a pool of multisosies
     */
    @Test
    public void testConvertFromMultiSosie() throws Exception {
        testConvertFromTransformationsJSON("multisosiefile_size2.json");
    }

    /**
     * Test the conversion of a pool of sosies
     */
    @Test
    public void testConvertFromSosiePool() throws Exception {
        testConvertFromTransformationsJSON("sosiepool_size2.json");
    }

    /**
     * Test the convertion from JSON transformation file (multisosie or pool of sosies) and a log dir of verbose logger
     */
    @Test
    public void testScattered() throws Exception {
        //Load from scattered resources
        PluginDataLoader formatter = new PluginDataLoader();
        Collection<TransformationRepresentation> representations =
                formatter.fromScattered(getResourcePath("sosiepool_size2.json"), getResourcePath("."));

        assertEquals(0, formatter.getErrors().size());
        //Test the proper loading of transformations
        assertProperLoadingOfTransformations(formatter);
        assertProperLoadingOfLog(formatter);


    }

    /**
     * Test the convertion from JSON transformation file (multisosie or pool of sosies) and a log dir of verbose logger
     */
    @Test
    public void testFromJSONAndThenScattered() throws Exception {
        //Load from scattered resources
        PluginDataLoader formatter = new PluginDataLoader();
        formatter.fromJSON(getResourcePath("sosiepool_size2.json"));
        Collection<TransformationRepresentation> representations = formatter.fromLogDir(getResourcePath("."));

        assertEquals(0, formatter.getErrors().size());
        //Test the proper loading of transformations
        assertProperLoadingOfTransformations(formatter);
        assertProperLoadingOfLog(formatter);
    }


    @Test
    public void reportProjects() throws Exception {
        PluginDataLoader formatter1 = reportBenoitNumbers("COMMON COLLECTION",
                "C:\\MarcelStuff\\projects\\DIVERSE\\programs\\single-sosies-pools\\NonRep-Indexed\\commons-collections-index-non-rep.json",
                "C:\\MarcelStuff\\projects\\DIVERSE\\programs\\instrumented\\commons-collections-trunk\\log"
        );

        PluginDataLoader formatter2 = reportBenoitNumbers("COMMON MATH",
                "C:\\MarcelStuff\\projects\\DIVERSE\\programs\\single-sosies-pools\\NonRep-Indexed\\commons-math-index-non-rep.json",
                "C:\\MarcelStuff\\projects\\DIVERSE\\programs\\instrumented\\commons-math-trunk\\log"
        );

        PluginDataLoader formatter3 = reportBenoitNumbers("EASY MOCK",
                "C:\\MarcelStuff\\projects\\DIVERSE\\programs\\single-sosies-pools\\easymock3.2-non-rep-index.json",
                "C:\\MarcelStuff\\projects\\DIVERSE\\programs\\instrumented\\easymock-light-3.2\\log"
        );

        reportAll(
                new String[]{"COLL", "MATH", "EASY"},
                new PluginDataLoader[]{formatter1, formatter2, formatter3});

    }

    /**
     * Reports all requested distribution information per project:
     * <p/>
     * Asserts per Test
     * <p/>
     * Declared Tests per TP
     * Executed Test per TP
     * <p/>
     * Declared Assertions per TP
     * Executed Assertions per TP
     * <p/>
     * Hits per TP
     *
     * @param formatter
     * @param name
     */
    private void reportAll(String[] name, PluginDataLoader[] formatter) {
        Iterator<TransformationRepresentation>[] r = new Iterator[formatter.length];
        Iterator<TestRepresentation>[] t = new Iterator[formatter.length];
        for (int i = 0; i < formatter.length; i++) {
            r[i] = formatter[i].getRepresentations().iterator();
            t[i] = formatter[i].getDeclaredTest().iterator();
        }

        String[] params = {
                "ASSRT DEC-TEST ",
                "TEST DEC-TP ", "TEST EXEC-TP ",
                "DEC ASSRT-TP ", "EXEC ASSERT-TP ",
                "HITS-TP "};
        for (int j = 0; j < params.length; j++) {
            for (int i = 0; i < formatter.length; i++) {
                System.out.print("\"" + params[j] + name[i] + "\",");
            }
        }
        System.out.println();

        TransformationRepresentation[] trans = new TransformationRepresentation[formatter.length];
        TestRepresentation[] test = new TestRepresentation[formatter.length];

        boolean allEmpty = false;
        while (allEmpty == false) {
            allEmpty = true;

            //Collect transformations and tests
            for (int i = 0; i < r.length; i++) {
                if (r[i].hasNext()) {
                    trans[i] = r[i].next();
                    allEmpty = false;
                } else {
                    trans[i] = null;
                }
                if (t[i].hasNext()) {
                    test[i] = t[i].next();
                    allEmpty = false;
                } else {
                    test[i] = null;
                }
            }

            //Print the Declared Asserts per test row
            for (int i = 0; i < r.length; i++) {
                if (test[i] != null) {
                    System.out.print(test[i].getAsserts().size());
                }
                System.out.print(",");
            }

            //Test Declared per TP
            for (int i = 0; i < r.length; i++) {
                if (trans[i] != null) {
                    System.out.print(trans[i].getTests().size());
                }
                System.out.print(",");
            }

            //Test Executed per TP
            for (int i = 0; i < r.length; i++) {
                if (trans[i] != null) {
                    System.out.print(trans[i].getTotalTestHits());
                }
                System.out.print(",");
            }

            //Assert Declare per TP
            for (int i = 0; i < r.length; i++) {
                if (trans[i] != null) {
                    System.out.print(trans[i].getAsserts().size());
                }
                System.out.print(",");
            }

            //Assert executed per TP
            for (int i = 0; i < r.length; i++) {
                if (trans[i] != null) {
                    System.out.print(trans[i].getTotalAssertionHits());
                }
                System.out.print(",");
            }

            //Hits per TP
            for (int i = 0; i < r.length; i++) {
                if (trans[i] != null) {
                    System.out.print(trans[i].getHits());
                }
                if (i < r.length - 1) {
                    System.out.print(",");
                }
            }
            System.out.println();
        }
    }

    private PluginDataLoader reportBenoitNumbers(String name, String json, String log) throws LoadingException {

        System.out.println("Project: " + name);

        //Load from scattered resources
        PluginDataLoader formatter = new PluginDataLoader();
        formatter.fromScattered(json, log);
//        assertEquals(0, formatter.getErrors().size());

        System.out.println("Test declared + : " + formatter.getTestDeclaredCount());
        System.out.println("Test declared covering a TP: " + formatter.getTestDeclaredCoveringATPCount());
        System.out.println("Test executed: " + formatter.getTestExecutedCount());
        System.out.println("Test executed covering a TP: " + formatter.getTestExecutedCoveringATPCount());


        System.out.println("\nAssertions declared: " + formatter.getAssertionsDeclared());
        System.out.println("Assertions declared covering a TP: " + formatter.getAssertsDeclaredCoveringATP());
        System.out.println("Assertions executed: " + formatter.getAssertionsExecutedCount());
        System.out.println("Assertions executed covering a TP: " + formatter.getAssertionsExecutedCoveringCount());

        System.out.println("\nTotal hits over a TP: " + formatter.getPotsTotalHitCount());

        System.out.println("\n ------------- \n");

        return formatter;
    }
}
