package org.pahappa.systems.kpiTracker.views.dialogs;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.*;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.utils.SystemCrashHandler;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ManagedBean(name = "goalFormDialog")
@ViewScoped
@Getter
@Setter
public class GoalFormDialog extends DialogForm<Goal> {

    private static final long serialVersionUID = 1L;
    private transient GoalService goalService;
    private transient DepartmentService departmentService;

    private List<Department> allDepartments;
    private Set<BusinessGoalDepartmentAssignment> departmentAssignments = new HashSet<>();
    private String updateTarget;

    public GoalFormDialog() {
        super("Goal Form", 800, 550);
    }

    @PostConstruct
    @Override
    public void beanInit() {
        this.goalService = ApplicationContextProvider.getBean(GoalService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.allDepartments = departmentService.getAllInstances();
    }

    public void prepareNewGoal(String goalLevel, Object parent) {
        resetModal(); // Start with a clean slate
        super.model = new Goal();
        super.model.setGoalLevel(GoalLevel.valueOf(goalLevel));

        if (parent instanceof GoalCycle) {
            super.model.setGoalCycle((GoalCycle) parent);
        } else if (parent instanceof Goal) {
            super.model.setParentGoal((Goal) parent);
            super.model.setGoalCycle(((Goal) parent).getGoalCycle());
        }
    }

    @Override
    public Goal getModel(){
        return super.getModel();
    }

    @Override
    public void setModel(Goal model) {
        resetModal(); // Also reset when opening for an edit
        super.setModel(model);
        if (model != null && model.getGoalLevel() == GoalLevel.ORGANISATIONAL && model.isSaved()) {
            // Eagerly fetch and copy the set to avoid lazy loading issues and to allow modification
            this.departmentAssignments = new HashSet<>(goalService.getInstanceByID(model.getId()).getDepartmentAssignments());
        }
    }

    @Override
    public void save() {
        try {
            if (super.model.getGoalLevel() == GoalLevel.ORGANISATIONAL) {
                super.model.setDepartmentAssignments(this.departmentAssignments);
            }
            this.goalService.saveInstance(super.model);
            UiUtils.showMessageBox("Success", "Goal saved successfully.");
        } catch (Exception e) {
            UiUtils.ComposeFailure("Save Failed", e.getMessage());
            UiUtils.keepDialogOpenOnValidationError();
            SystemCrashHandler.reportSystemCrash(e, SharedAppData.getLoggedInUser());
        }
    }

    @Override
    public void persist() throws Exception {
        // The save() method is overridden and has more logic, so this is not directly used.
    }

    /**
     * This is the key fix. It resets all context-specific state of the dialog.
     */
    @Override
    public void resetModal() {
        super.resetModal(); // Resets isEditing flag
        super.model = new Goal();
        this.departmentAssignments = new HashSet<>();
        this.updateTarget = null; // Reset the update target
    }

    public void addDepartmentAssignment() {
        this.departmentAssignments.add(new BusinessGoalDepartmentAssignment());
    }

    public void removeDepartmentAssignment(BusinessGoalDepartmentAssignment assignment) {
        this.departmentAssignments.remove(assignment);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
    }

    @Override
    public void pageLoadInit() {
        // Not used
    }
}