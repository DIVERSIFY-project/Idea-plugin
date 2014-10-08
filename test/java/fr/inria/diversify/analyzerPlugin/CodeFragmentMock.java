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
    public boolean isReplace(CodeFragment other, boolean varNameMatch) {
        return false;
    }

    public void set_codeFragmentString(String _codeFragmentString) {
        this._codeFragmentString = _codeFragmentString;
    }
}
