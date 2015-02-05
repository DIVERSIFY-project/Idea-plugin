package fr.inria.diversify.analyzerPlugin.actions;

import fr.inria.diversify.analyzerPlugin.MainToolWinv0;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;

/**
 * Created by marodrig on 03/11/2014.
 */
@Deprecated
public abstract class WinAction {

    private MainToolWinv0 mainToolWin;


    public WinAction(MainToolWinv0 mainToolWin) {
        this.mainToolWin = mainToolWin;
    }

    /**
     * Window to control.
     *
     * @return
     */
    protected MainToolWinv0 getMainToolWin() {
        return mainToolWin;
    }

    protected void setMainToolWin(MainToolWinv0 mainToolWin) {
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
        //ComplainAction c = new ComplainAction(getMainToolWin(), "Cannot apply!! Something went wrong + " + e.getMessage(), e, false);
        //c.execute();
    }

    public abstract void execute();

    /**
     * Diminutive for a very used property getWinMain()
     * @return The MainToolWin
     */
    protected MainToolWinv0 win() {
        return mainToolWin;
    }
}
