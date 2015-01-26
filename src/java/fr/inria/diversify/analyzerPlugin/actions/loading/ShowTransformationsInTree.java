package fr.inria.diversify.analyzerPlugin.actions.loading;

import fr.inria.diversify.analyzerPlugin.MainToolWin;
import fr.inria.diversify.analyzerPlugin.actions.WinAction;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

import javax.swing.*;
import java.util.Collection;

/**
 * Action to show a set of transformationinfo in a JTree
 *
 * Created by marodrig on 26/01/2015.
 */
public class ShowTransformationsInTree extends WinAction {

    /**
     * Infos that we want to show
     */
    private final Collection<TransformationInfo> infos;

    /**
     * Tree that we want to put infos in
     */
    private final JTree tree;

    public ShowTransformationsInTree(MainToolWin mainToolWin, Collection<TransformationInfo> infos, JTree tree) {
        super(mainToolWin);
        this.infos = infos;
        this.tree = tree;
    }

    @Override
    public void execute() {

    }
}
