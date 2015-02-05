package fr.inria.diversify.analyzerPlugin;

import fr.inria.diversify.analyzerPlugin.gui.CodePositionTree;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;

/**
 * Created by marodrig on 04/02/2015.
 */
public class FakeCodePositionTree extends CodePositionTree {

    private CodePosition fakeCodePosition;

    @Override
    public CodePosition getSelectedCodePosition() {
        return this.fakeCodePosition;
    }

    public void setFakeCodePosition(CodePosition fakeCodePosition) {
        this.fakeCodePosition = fakeCodePosition;
    }
}
