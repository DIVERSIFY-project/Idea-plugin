package fr.inria.diversify.analyzerPlugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import fr.inria.diversify.analyzerPlugin.IDEObjects;
import fr.inria.diversify.analyzerPlugin.IDEObjectsImpl;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.TestEyeExplorer;

import javax.swing.*;

/**
 * Base action for all TestEye actions
 * <p/>
 * Created by marodrig on 27/01/2015.
 */
public abstract class TestEyeAction extends AnAction {

    private static final Logger logger = Logger.getInstance("#" + TestEyeAction.class.getName());

    private static final String TEST_EYE_NOT_SET = "Test eye component not set";


    private IDEObjects ideObjects;

    public TestEyeAction() {
        super();
    }

    public TestEyeAction(String caption, String description, Icon icon) {
        super(caption, description, icon);
    }

    /**
     * Get the project component from the event
     *
     * @param event Event containing the component
     * @return
     */
    public TestEyeProjectComponent getComponent(AnActionEvent event) {
        if (!event.getProject().hasComponent(TestEyeProjectComponent.class))
            throw new IllegalStateException(TEST_EYE_NOT_SET);
        return event.getProject().getComponent(TestEyeProjectComponent.class);
    }



    /**
     * A method that shows a complain text
     *
     * @param message   Message to show in the complain
     * @param component Center component
     * @param e         Exception that caused the complain
     */
    protected void softComplain(JComponent component, String message, Exception e) {
        if (e != null) message += e.getMessage();
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("Warning: " + message, MessageType.WARNING, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(component), Balloon.Position.atRight);
    }

    /**
     * A method that shows a complain text
     *
     * @param message Message to show in the complain
     * @param e       Exception that caused the complain
     */
    protected void hardComplain(String message, Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), e.getClass().getSimpleName() +  ". Unable to load transformations", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Tries to execute an action
     *
     * @param actionClass class of the action to execute
     * @param event       Event to pass on to the action
     */
    protected void tryExecute(Class<?> actionClass, AnActionEvent event) {
        getIdeObjects().getApplicationComponent().tryExecute(actionClass, event);
    }

    /**
     * Set the proxy object to access IDE singletons
     * @param ideObjects
     */
    public void setIdeObjects(IDEObjects ideObjects) {
        this.ideObjects = ideObjects;
    }

    public IDEObjects getIdeObjects() {
        //Convention over configuration
        if ( ideObjects == null ) setIdeObjects(new IDEObjectsImpl());
        return ideObjects;
    }
}
