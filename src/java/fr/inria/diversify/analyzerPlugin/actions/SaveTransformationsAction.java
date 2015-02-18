package fr.inria.diversify.analyzerPlugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.persistence.json.input.JsonSosiesInput;
import icons.TestEyeIcons;

/**
 * Created by marodrig on 05/02/2015.
 */
public class SaveTransformationsAction extends TestEyeAction {

    public SaveTransformationsAction() {
        super("Save transformations with metadata", "Save transformations with metadata", TestEyeIcons.Save);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        //JsonSosiesInput

    }
}
