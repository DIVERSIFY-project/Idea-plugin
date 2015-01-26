package fr.inria.diversify.analyzerPlugin;

import fr.inria.diversify.codeFragment.CodeFragment;

/**
 * Created by marodrig on 06/10/2014.
 */
public class CodeFragmentMock extends CodeFragment {


    private String _codeFragmentString;

    @Override
    public String codeFragmentString() {
        return _codeFragmentString;
    }

    @Override
    public boolean isReplaceableBy(CodeFragment codeFragment, boolean b, boolean b2) {
        return false;
    }

    @Override
    public CodeFragment clone() {
        return null;
    }

    public void set_codeFragmentString(String _codeFragmentString) {
        this._codeFragmentString = _codeFragmentString;
    }
}
