package fr.inria.diversify.analyzerPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.*;

/**
 * Class to create reports about the classification of sosies.
 * <p/>
 * Will take as an input several files containing the transformations and their classifications.
 * As an output it will create an HTML file with the report
 * <p/>
 * Created by marodrig on 23/10/2014.
 */
public class ClassificationReport {

    private class ClassificationFunctionStats {
        int weight;
        String id;
        String description;

        ClassificationFunctionStats(String id, String description, int weight) {
            this.description = description;
            this.weight = weight;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            ClassificationFunctionStats cfs = (ClassificationFunctionStats) o;
            return cfs.id.equals(id) && cfs.description.equals(description) && weight == weight;
        }
    }

    //Reads an object from a file
    private JSONObject read(String fileName) throws IOException, JSONException {
        BufferedReader r = new BufferedReader(new FileReader(fileName));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }
        return new JSONObject(sb.toString());
    }

    //gets t the slot in the table corresponding to the transformation object depending on what kind of transformation is
    //whether it is  add, replaced, or delete
    private int getSlot(String type) throws JSONException {
        if (type.contains("add")) return 0;
        if (type.contains("replace")) return 1;
        if (type.contains("delete")) return 2;
        throw new RuntimeException("Invalid type");
    }

    /*
     * Initialize the string builder that is going to print HTML page
     */
    private void initPage(StringBuilder out, List<String> headers) {
        out.append("<!DOCTYPE html>");
        out.append("<html>");
        out.append("<head>");
        out.append("<title>Execution Queue</title>");
        out.append("<style> table,th,td { border:1px solid black; border-collapse:collapse } </style>");
        out.append("</head>");
        out.append("<body>");

        out.append("<table style=\"width:300px\">").
                append("<tr>");

        out.append("<th> functions </th>");
        for (int i = 0; i < headers.size(); i++) {
            out.append("<th colspan=3>").append(headers.get(i)).append("</th>");
        }

        out.append("</tr>");
    }

    private int getClassIndex(List<ClassificationFunctionStats> list, String key) {
        int i = 0;
        for (ClassificationFunctionStats c : list) {
            if (c.id.equals(key)) return i;
            i++;
        }
        throw new RuntimeException("Not found!");
    }

    public void createReport(String directory) throws JSONException, IOException {

        File folder = new File(directory);

        //get all json files in directory
        if (folder.isDirectory()) {

            int[][] classTable;

            File[] listOfFiles = folder.listFiles();

            ArrayList<JSONObject> contents = new ArrayList<JSONObject>(listOfFiles.length);

            ArrayList<String> fileNames = new ArrayList<String>();

            ArrayList<ClassificationFunctionStats> listClass = new ArrayList<ClassificationFunctionStats>();

            for (int i = 0; i < listOfFiles.length; i++) {
                //get all Json files and obtain the dimensions of the classification table
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().toLowerCase().endsWith(".json")) {
                    //Object containing all elements in the file
                    JSONObject readed;
                    try {
                        readed = read(listOfFiles[i].getCanonicalPath());
                        fileNames.add(listOfFiles[i].getName());
                    } catch (JSONException ex) {
                        if (ex.getMessage().contains("JSONObject text must begin with")) {
                            continue; //its okay. This may happen depending on the format of the file
                        } else throw ex;
                    }
                    if (readed.has("classifications")) {
                        //obtain  all classification functions from this file
                        JSONObject classObject = readed.getJSONObject("classifications");
                        contents.add(readed);
                        Iterator itr = classObject.keys();
                        while (itr.hasNext()) {
                            String k = (String) itr.next();
                            JSONObject o = classObject.getJSONObject(k);
                            String index = o.getString("index");
                            ClassificationFunctionStats cfs =
                                    new ClassificationFunctionStats(index, k, o.getInt("weight"));
                            if (!listClass.contains(cfs)) listClass.add(cfs);
                        }
                    }
                }

            }

            StringBuilder out = new StringBuilder();
            initPage(out, fileNames);

            //sort the list of classification functions by their weight
            Collections.sort(listClass, new Comparator<ClassificationFunctionStats>() {
                @Override
                public int compare(ClassificationFunctionStats o1, ClassificationFunctionStats o2) {
                    return o2.weight - o1.weight;
                }
            });

            //Create the table
            classTable = new int[listClass.size()][contents.size() * 3];

            for (int j = 0; j < contents.size(); j++) {
                int k = j * 3;
                JSONArray repClass = contents.get(j).getJSONArray("transformationClass");
                int i = 0;
                while (i < repClass.length()) {
                    i++;//int id = repClass.getInt(i++);
                    int index = getClassIndex(listClass, repClass.getString(i++)); //Gets the id of the class function
                    int slot = getSlot(repClass.getString(i++));
                    classTable[index][k + slot]++; //Gets the type of the transformation
                }
            }


            //Print the table in html format
            for (int i = 0; i < classTable.length; i++) {
                if (listClass.get(i).weight < 5) out.append("<tr bgcolor=#FFDDDD>");
                else if (listClass.get(i).weight < 10) out.append("<tr bgcolor=#DDFFDD>");
                else out.append("<tr bgcolor=#DDDDFF>");
                out.append("<td>").append(listClass.get(i).description).append("</td>");
                for (int j = 0; j < classTable[i].length; j++) {
                    out.append("<td>").append(classTable[i][j]).append("</td>");
                }
                out.append("</tr>");
            }

            out.append("</table>");
            out.append("</body>");
            out.append("</html>");

            FileWriter fw = new FileWriter(new File(directory + "\\report.html"));
            fw.write(out.toString());
            fw.close();
        }
    }
}
