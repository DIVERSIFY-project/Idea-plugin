package fr.inria.diversify.analyzerPlugin.actions;

import fr.inria.diversify.analyzerPlugin.MainToolWin;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;

/**
 * Created by marodrig on 03/11/2014.
 */
public abstract class WinAction {

    private MainToolWin mainToolWin;

    /**
     * Window to control.
     *
     * @return
     */
    protected MainToolWin getMainToolWin() {
        return mainToolWin;
    }

    protected void setMainToolWin(MainToolWin mainToolWin) {
        this.mainToolWin = mainToolWin;
    }

    /**
     *
     * @return
     */
    protected CodePosition getDataOfTransformationTree() {
        return getMainToolWin().getDataOfSelectedTransformationItem(getMainToolWin().getTreeTransformations());
    }

    protected void complain(String s, Exception e) {
        Complain c = new Complain(getMainToolWin(), "Cannot apply!! Something went wrong + " + e.getMessage(), e, false);
        c.execute();
    }

    public abstract void execute();
}
