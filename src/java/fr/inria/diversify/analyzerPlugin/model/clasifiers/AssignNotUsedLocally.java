package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.transformation.ast.ASTTransformation;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.QueryVisitor;
import spoon.reflect.visitor.ReferenceFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Gives a high weight to unused local assignment
 *
 * Created by marodrig on 13/10/2014.
 */
public class AssignNotUsedLocally extends TransformClassifier {

    //Take into consideration control flow and also not only the deletion but also the replacements!!!!!!

    @Override
    public boolean isUserFilter() {
        return false;
    }

    protected int WEIGHT = 10;

    static ReferenceFilter<CtReference> refFilter = new ReferenceFilter<CtReference>() {
        @Override
        public boolean matches(CtReference reference) {
            return true;
        }

        @Override
        public Class<CtReference> getType() {
            return CtReference.class;
        }
    };

    class VarAccesFilter implements Filter<CtVariableAccess> {

        CtVariableReference reference;

        public VarAccesFilter(CtVariableReference reference) {
            this.reference = reference;
        }

        @Override
        public boolean matches(CtVariableAccess element) {
            return element.getVariable().equals(reference);
        }

        @Override
        public Class<?> getType() {
            return CtVariableAccess.class;
        }
    }

    protected CtElement getParentMethod(CtElement son) {
        CtElement parent = son.getParent();

        while (parent != null && !(parent instanceof CtExecutable)) {
            parent = parent.getParent();
        }
        if (parent == null)
            return son.getParent();
        else
            return parent;
    }

    @Override
    protected boolean canClassify(TransplantInfo transform) {
        if (transform.getTransformation() instanceof ASTTransformation) {
            ASTTransformation t = (ASTTransformation) transform.getTransformation();
            CtElement e = t.getTransplantationPoint().getCtCodeFragment();
            return e instanceof CtAssignment ||
                    e instanceof CtNewClass ||
                    e instanceof CtLocalVariable;
        }
        return false;
    }


    @Override
    protected int calculateValue(TransplantInfo transform) {
        ASTTransformation t = (ASTTransformation) transform.getTransformation();
        CtElement e = t.getTransplantationPoint().getCtCodeFragment();
        CtElement m = getParentMethod(e);
        //Don't search in elements which are not within a method
        if (m != null) {
            //Get all the assignments in "e"
            TypeFilter assignFilter = new TypeFilter(CtAssignment.class);
            QueryVisitor<CtAssignment> assignQuery = new QueryVisitor<CtAssignment>(assignFilter);
            assignQuery.scan(e);
            List<CtAssignment> assignments = assignQuery.getResult();

            //Is needed more than one assignment to make an assignment useless
            if (assignments.size() > 0) {
                TypeFilter varAccessFilter = new TypeFilter(CtVariableAccess.class);

                //Get all the access in the method
                QueryVisitor<CtVariableAccess> visitor = new QueryVisitor<CtVariableAccess>(varAccessFilter);
                visitor.scan(m);
                List<CtVariableAccess> varAccess = visitor.getResult();

                //Flatten the list of assignments and access
                ArrayList<CtElement> elements = new ArrayList<CtElement>();
                elements.addAll(assignments);
                elements.addAll(varAccess);
                Collections.sort(elements, new Comparator<CtElement>() {
                    @Override
                    public int compare(CtElement o1, CtElement o2) {
                        return (o1.getPosition().getSourceStart() - o2.getPosition().getSourceStart());
                    }
                });

                //Search for unused assignments
                for (int i = 0; i < elements.size() - 1; i++) {
                    if (elements.get(i) instanceof CtAssignment ) {
                        CtVariableReference varRef = getVariableReference(elements.get(i));
                        if ( varRef != null ) {
                            boolean accessMissing = true;
                            for (int j = i + 1; j < elements.size() && accessMissing; j++) {
                                if (elements.get(j) instanceof CtAssignment) {
                                    //If there is a var Ref with the same name... voilà!!
                                    CtVariableReference vr = getVariableReference(elements.get(j));
                                    if (varRef.toString().equals(vr.toString())) return WEIGHT;
                                } else if ( elements.get(j) instanceof CtVariableAccess ) {
                                    //We found and access of the previous assignment, stop searching
                                    CtVariableReference vr = getVariableReference(elements.get(j));
                                    if (varRef.toString().equals(vr.toString()) || varRef.equals(vr)) {
                                        accessMissing = false;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        return 0;
    }

    private CtVariableReference getVariableReference(CtElement element) {
        if (element instanceof CtAssignment) {
            CtAssignment a = (CtAssignment) element;
            if (a.getAssigned() instanceof CtVariableAccess) {
                return ((CtVariableAccess) a.getAssigned()).getVariable();
            }
        } else if (element instanceof CtVariableAccess) {
            return ((CtVariableAccess) element).getVariable();
        }
        return null;
    }

    @Override
    public String getDescription() {
        return "Variable/field assign not used locally";
    }

    @Override
    public int getWeight() {
        return STRONG;
    }
}
