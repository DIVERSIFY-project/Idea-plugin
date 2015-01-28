package fr.inria.diversify.analyzerPlugin;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.ProjectType;
import com.intellij.openapi.util.ActionCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.HashMap;

/**
* Created by marodrig on 28/01/2015.
*/
public class FakeActionManager extends ActionManager {

    HashMap<String, AnAction> actions = new HashMap<>();

    public FakeActionManager() {

    }

    @Override
    public ActionPopupMenu createActionPopupMenu(String s, ActionGroup actionGroup) {
        return null;
    }

    @Override
    public ActionToolbar createActionToolbar(String s, ActionGroup actionGroup, boolean b) {
        return null;
    }

    @Override
    public AnAction getAction(String s) {
        return actions.get(s);
    }

    @Override
    public AnAction getAction(String s, ProjectType projectType) {
        return null;
    }

    @Override
    public String getId(AnAction anAction) {
        return null;
    }

    @Override
    public void registerAction(String s, AnAction anAction) {
        actions.put(s, anAction);
    }

    @Override
    public void registerAction(String s, AnAction anAction, PluginId pluginId) {

    }

    @Override
    public void unregisterAction(String s) {

    }

    @Override
    public String[] getActionIds(String s) {
        return new String[0];
    }

    @Override
    public boolean isGroup(String s) {
        return false;
    }

    @Override
    public JComponent createButtonToolbar(String s, ActionGroup actionGroup) {
        return null;
    }

    @Override
    public AnAction getActionOrStub(String s) {
        return null;
    }

    @Override
    public void addTimerListener(int i, TimerListener timerListener) {

    }

    @Override
    public void removeTimerListener(TimerListener timerListener) {

    }

    @Override
    public void addTransparentTimerListener(int i, TimerListener timerListener) {

    }

    @Override
    public void removeTransparentTimerListener(TimerListener timerListener) {

    }

    @Override
    public ActionCallback tryToExecute(AnAction anAction, InputEvent inputEvent, Component component, String s, boolean b) {
        return null;
    }

    @Override
    public void addAnActionListener(AnActionListener anActionListener) {

    }

    @Override
    public void addAnActionListener(AnActionListener anActionListener, Disposable disposable) {

    }

    @Override
    public void removeAnActionListener(AnActionListener anActionListener) {

    }

    @Nullable
    @Override
    public KeyboardShortcut getKeyboardShortcut(String s) {
        return null;
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return null;
    }
}
