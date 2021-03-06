package fr.inria.diversify.analyzerPlugin.actions;

import fr.inria.diversify.analyzerPlugin.MainToolWinv0;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

/**
 * Created by marodrig on 29/11/2014.
 */
@Deprecated
public class ShowDifferencesAction extends WinAction {

    CodePosition data;
    public ShowDifferencesAction(MainToolWinv0 mainToolWin, CodePosition data) {
        super(mainToolWin);
        this.data = data;
    }

    @Override
    public void execute() {
        if ( data instanceof TransformationInfo) {
            TransformationInfo tr = (TransformationInfo)data;
            getMainToolWin().getTxtDiff().setText(tr.getDiffReport());
        }
    }
}
