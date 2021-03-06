package fr.inria.diversify.analyzerPlugin.actions;

import com.intellij.debugger.ui.breakpoints.JavaLineBreakpointType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.*;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointManagerImpl;
import fr.inria.diversify.analyzerPlugin.MainToolWinv0;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Created by marodrig on 23/12/2014.
 */
@Deprecated
public class SetBreakPointsActions extends WinAction {

    private final CodePosition codePosition;

    private class MyXBreakPointProperties extends XBreakpointProperties {

        @Nullable
        @Override
        public Object getState() {
            return "-";
        }

        @Override
        public void loadState(Object o) {

        }
    }

    public SetBreakPointsActions(MainToolWinv0 mainToolWin) {
        super(mainToolWin);
        codePosition = getMainToolWin().getDataOfSelectedTransformationItem(getMainToolWin().getTreeTransformations());
    }

    @Override
    public void execute() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                XBreakpointManagerImpl breakpointManager = (XBreakpointManagerImpl) XDebuggerManager.getInstance(
                        getMainToolWin().getProject()).getBreakpointManager();
                Collection<TransformationInfo> rs = getMainToolWin().getRepresentations();
                Project project = getMainToolWin().getProject();
                GlobalSearchScope scope = GlobalSearchScope.allScope(project);
                for (TransformationInfo data : rs) {
                    if (data.getHits() == 0) {
                        if (data == null) return;
                        final String[] p = data.getPosition().split(":");
                        String className = p[0];
                        final PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(className, scope);
                        if (psiClass != null) {
                            String url = psiClass.getContainingFile().getVirtualFile().getUrl();
                            breakpointManager.addLineBreakpoint(
                                    new JavaLineBreakpointType(), url, Integer.parseInt(p[1]) - 1, null);
                        }
                    }
                }
            }
        });
    }
}

