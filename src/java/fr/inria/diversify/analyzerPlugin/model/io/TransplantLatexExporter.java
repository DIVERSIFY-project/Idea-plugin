package fr.inria.diversify.analyzerPlugin.model.io;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import fr.inria.diversify.transformation.ast.ASTReplace;
import fr.inria.diversify.transformation.ast.ASTTransformation;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import spoon.compiler.Environment;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marodrig on 25/08/2015.
 */
public class TransplantLatexExporter {

    private static class StringBuilderDashDash {
        StringBuilder sb;

        public StringBuilderDashDash() {
            sb = new StringBuilder();
        }

        public StringBuilderDashDash _(Object s) {
            return _(s.toString());
        }

        public StringBuilderDashDash _(String s) {
            sb.append(s);
            return this;
        }

        public String toString() {
            return sb.toString();
        }
    }

    private static class LatexPrintVisitor extends DefaultJavaPrettyPrinter {

        public CtElement transplantationPo;

        public CtElement transplant;

        public Map<String, String> map;

        public String type;

        private boolean intercept = false;
        private StringBuilder trabsplantStrBffr;

        public LatexPrintVisitor(Environment env) {
            super(env);
        }

        @Override
        public DefaultJavaPrettyPrinter scan(CtElement e) {
            if (e == null) return this;
            if (e.equals(transplantationPo) && e.getPosition().equals(transplantationPo.getPosition())) {

                String preTp = "";
                String preT = "";

                if (type.toLowerCase().contains("delete")) {
                    preTp = " - ";
                } else if (type.toLowerCase().contains("add")) {
                    preT = " + ";
                } else if (type.toLowerCase().contains("replace")) {
                    preTp = "-";
                    preT = " + ";
                }

                this.write("_ " + preTp + " ");
                super.scan(e);
                this.write(" _ \r\n");
                if (transplant != null) {
                    CtElement oldParent = transplant.getParent();
                    transplant.setParent(transplantationPo.getParent());
                    this.write("(* " + preT + " ");
                    trabsplantStrBffr = new StringBuilder();
                    intercept = true;
                    super.scan(transplant);
                    intercept = false;

                    String tStr = trabsplantStrBffr.toString();
                    for (Map.Entry<String, String> entry : map.entrySet() ) {
                        tStr.replace(entry.getKey(), entry.getValue());
                    }

                    write(trabsplantStrBffr.toString());
                    this.write("*) \r\n");
                    transplant.setParent(oldParent);
                }
            } else {
                super.scan(e);
            }
            return this;
        }

        @Override
        public DefaultJavaPrettyPrinter write(String s) {
            if (intercept) {
                trabsplantStrBffr.append(s);
            } else super.write(s);
            return this;
        }


        @Override
        public void visitCtTypeReferenceWithoutGenerics(CtTypeReference<?> ref) {
            write(ref.getSimpleName());
        }

        public <T> void visitCtTypeReference(CtTypeReference<T> ref) {
            write(ref.getSimpleName());
        }
    }

    private String projectName = "";

    public void export(String file, TransplantInfo info, String comments, String lstId) throws IOException {
        export(new BufferedWriter(new FileWriter(file), 2048), info, comments, lstId);
    }

    /**
     * Exports the transplant to latex
     *
     * @param writer   Writer to write the Transfplant to
     * @param info     Transplant
     * @param comments Some comments to write to the latex
     * @param lstId    Id of the listing in the latex
     * @throws IOException
     */
    public void export(Writer writer, TransplantInfo info, String comments, String lstId) throws IOException {
        StringBuilderDashDash sb = new StringBuilderDashDash();
        sb._("%")._(getProjectName())._("\n");
        sb._("%TP Pos: ")._(info.getTransplantationPoint().getPosition())._("\n");
        sb._("%T  Pos: ");
        if (!info.getType().toLowerCase().contains("delete")) sb._(info.getPosition())._("\n");
        else sb._("DELETE TRANSFORMATION");
        sb._("%ID: ")._(info.getIndex())._("\n");

        sb._(comments)._("\n\n");

        CtElement e = getTPElement(info);
        if (e != null) {
            CtMethod method = e.getParent(CtMethod.class);

            if (method != null) {
                sb._("\\begin{minipage}{\\columnwidth}")._("\n");
                sb._("\\begin{lstlisting}[caption={")._(method.getParent(CtClass.class).getSimpleName()).
                        _("-")._(method.getSimpleName())._("},label={lst:");
                sb._(lstId)._("}, numbers=left]")._("\n");

                sb._(getMethodString(method, info));

            } else {
                sb._("\\begin{minipage}{\\columnwidth}")._("\n");
                sb._("\\begin{lstlisting}[caption={")._(info.getIndex())._("},label={lst:");
                sb._(lstId)._("}, numbers=left]")._("\n");

                sb._("_")._(info.getTransplantationPoint().getSource())._(" _ \n");
                sb._("(* ");
                if (info.getType().toLowerCase().contains("delete")) sb._("DELETE SOSIE")._("\n");
                else sb._(info.getSource())._(" *) \n");
            }
        }

        sb._("// Var Map:")._(info.getVariableMap())._("\r\n");

        sb._("\\end{lstlisting}")._("\r\n");
        sb._("\\tabcolsep=0.11cm")._("\r\n");
        sb._("\\begin{tabular}{>{\\small}c>{\\small}c>{\\small}c>{\\small}c>{\\small}c>{\\small}c>{\\small}c>{\\small}c}")._("\r\n");
        sb._("\\hline")._("\r\n");
        sb._("\\rowcolor{lightgray} \\#tc & \\#assert & transfo & node & min & max & median & mean   \\\\")._("\r\n");
        sb._("\\rowcolor{lightgray}  & & type & type & depth  & depth & depth & depth  \\\\")._("\r\n");
        sb._("\\hline ")._("\r\n");
        sb._("  & rep &  &  &  &  & \\\\")._("\r\n");
        sb._("\\hline")._("\r\n");
        sb._("\\end{tabular}")._("\r\n");
        sb._("\\end{minipage}")._("\r\n");
        writer.write(sb.toString());
        writer.flush();
        writer.close();
    }

    private String getMethodString(CtMethod method, TransplantInfo info) {

        LatexPrintVisitor v = new LatexPrintVisitor(method.getFactory().getEnvironment());

        //Get sosie and transplant
        if (info.getType().toLowerCase().contains("delete")) {
            ASTDelete astTransformation = (ASTDelete) info.getTransformation();
            v.transplantationPo = astTransformation.getTransplantationPoint().getCtCodeFragment();
            v.type = astTransformation.getName();
        } else if (info.getType().toLowerCase().contains("replace")) {
            ASTReplace astTransformation = (ASTReplace) info.getTransformation();
            v.transplantationPo = astTransformation.getTransplantationPoint().getCtCodeFragment();
            v.transplant = astTransformation.getTransplant().getCtCodeFragment();
            v.type = astTransformation.getName();
        } else {
            ASTAdd astTransformation = (ASTAdd) info.getTransformation();
            v.transplantationPo = astTransformation.getTransplantationPoint().getCtCodeFragment();
            v.transplant = astTransformation.getTransplant().getCtCodeFragment();
            v.type = astTransformation.getName();
        }
        HashMap<String, String> map = new HashMap<>();
        if ( info.getVariableMap() != null && !info.getVariableMap().isEmpty() ) {
            String[] m = info.getVariableMap().substring(2, info.getVariableMap().length() - 1).split("; ");
            for (String s : m) {
                String[] sp = s.split("->");
                if (sp.length == 2 && !sp[0].trim().isEmpty() && !sp[1].trim().isEmpty())
                    map.put(sp[0].trim(), sp[1].trim());
            }
        }
        v.map = map;
        method.accept(v);
        return v.getResult();
    }


    private CtElement getTPElement(TransplantInfo info) {
        if (info.getTransformation() == null ||
                !(info.getTransformation() instanceof ASTTransformation)) return null;

        ASTTransformation ast = (ASTTransformation) info.getTransformation();
        return ast.getTransplantationPoint().getCtCodeFragment();
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }
}
