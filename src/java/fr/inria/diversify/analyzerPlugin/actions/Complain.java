package fr.inria.diversify.analyzerPlugin.actions;

import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import fr.inria.diversify.analyzerPlugin.MainToolWin;

import javax.swing.*;

/**
 * Created by marodrig on 03/11/2014.
 */
public class Complain extends WinAction {

    private final String error;
    private final Exception exception;
    private final boolean soft;

    public Complain(MainToolWin mainToolWin, String error, Exception e, boolean soft) {
        super(mainToolWin);
        this.error = error;
        this.exception = e;
        this.soft = soft;
    }


    @Override
    public void execute() {
        if (soft) {
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder("Warning: " + error, MessageType.WARNING, null)
                    .setFadeoutTime(7500)
                    .createBalloon()
                    .show(RelativePoint.getCenterOf(getMainToolWin().getTreeTransformations()),
                            Balloon.Position.atRight);
        } else {
            JOptionPane.showMessageDialog(null,
                    error,
                    "Ups...",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
