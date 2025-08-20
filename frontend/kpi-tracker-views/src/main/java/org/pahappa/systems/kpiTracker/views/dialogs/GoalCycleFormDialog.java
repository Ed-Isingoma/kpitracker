package org.pahappa.systems.kpiTracker.views.dialogs;

import org.pahappa.systems.kpiTracker.core.services.GoalCycleService;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.utils.SystemCrashHandler;
import org.sers.webutils.server.shared.SharedAppData;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "goalCycleFormDialog")
@SessionScoped
public class GoalCycleFormDialog extends DialogForm<GoalCycle> {

    private transient GoalCycleService goalCycleService;

    public GoalCycleFormDialog() {
        super("New Goal Cycle", 520, 350);
    }

    @Override
    public void persist() throws Exception {
        getGoalCycleService().saveInstance(super.getModel());
    }

    @Override
    public void save() {
        try {
            this.persist();
            UiUtils.showMessageBox("Success", "Goal Cycle saved successfully.");
        } catch (Exception e) {
            UiUtils.ComposeFailure("Save Failed", e.getMessage());
            UiUtils.keepDialogOpenOnValidationError();
            SystemCrashHandler.reportSystemCrash(e, SharedAppData.getLoggedInUser());
        }
    }

    @Override
    public GoalCycle getModel() {
        return super.getModel();
    }

    @Override
    public void setModel(GoalCycle model) {
        super.setModel(model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.setModel(new GoalCycle());
    }

    private GoalCycleService getGoalCycleService() {
        if (this.goalCycleService == null) {
            this.goalCycleService = ApplicationContextProvider.getBean(GoalCycleService.class);
        }
        return this.goalCycleService;
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
    }

    @Override
    public void beanInit() {
    }

    @Override
    public void pageLoadInit() {
    }
}