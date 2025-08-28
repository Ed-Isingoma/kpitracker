package org.pahappa.systems.kpiTracker.views.goals;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.core.services.GoalCycleService;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.core.services.ProfessionalAttrCategoryService; // 1. ADD THIS IMPORT
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrCategory; // 2. ADD THIS IMPORT
import org.pahappa.systems.kpiTracker.views.dialogs.GoalFormDialog;
import org.pahappa.systems.kpiTracker.views.dialogs.ProfessionalAttrFormDialog; // 3. ADD THIS IMPORT
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "goalCycleView")
@ViewScoped
@Getter
@Setter
@ViewPath(path = "/pages/admin/goals/goalCycleView.xhtml")
public class GoalCycleView implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient GoalCycleService goalCycleService;
    private transient GoalService goalService;
    private transient ProfessionalAttrCategoryService professionalAttrCategoryService;

    private GoalCycle selectedCycle;
    private List<Goal> organisationalGoals;
    private List<ProfessionalAttrCategory> professionalAttributes;
    private String cycleId;
    private int overallPerformance = 0; // Placeholder for now

    @ManagedProperty(value = "#{goalFormDialog}")
    private GoalFormDialog goalFormDialog;

    @ManagedProperty(value = "#{professionalAttrFormDialog}")
    private ProfessionalAttrFormDialog professionalAttrFormDialog;

    @PostConstruct
    public void init() {
        this.goalCycleService = ApplicationContextProvider.getBean(GoalCycleService.class);
        this.goalService = ApplicationContextProvider.getBean(GoalService.class);
        this.professionalAttrCategoryService = ApplicationContextProvider.getBean(ProfessionalAttrCategoryService.class);
    }

    public void loadCycle() {
        if (cycleId != null) {
            this.selectedCycle = goalCycleService.getInstanceByID(cycleId);
            if (this.selectedCycle != null) {
                this.organisationalGoals = goalService.getOrganisationalGoalsForCycle(this.selectedCycle);
                this.professionalAttributes = professionalAttrCategoryService.getAttributesForCycle(this.selectedCycle);
                this.overallPerformance = calculateOverallPerformance();
            }
        }
    }

    private int calculateOverallPerformance() {
        if (this.organisationalGoals == null || this.organisationalGoals.isEmpty()) {
            return 0;
        }
        // Dummy calculation for now
        return (int) this.organisationalGoals.stream().mapToInt(Goal::getWeight).average().orElse(0.0);
    }

    /**
     * Prepares the GoalFormDialog to create a new Organisational goal
     * for the current cycle.
     */
    public void prepareNewOrganisationalGoal() {
        if (selectedCycle != null) {
            goalFormDialog.prepareNewGoal(GoalLevel.ORGANISATIONAL.name(), null);
            goalFormDialog.getModel().setGoalCycle(this.selectedCycle);
            goalFormDialog.setUpdateTarget(":cycleDashboardForm:organisationalGoalsTable");
        }
    }

    public void prepareEditOrganisationalGoal(Goal goalToEdit) {
        goalFormDialog.setModel(goalToEdit);
        goalFormDialog.setUpdateTarget(":cycleDashboardForm:organisationalGoalsTable");
    }

    /**
     * Prepares the ProfessionalAttrFormDialog to create a new attribute
     * for the current cycle.
     */
    public void prepareNewProfessionalAttribute() {
        if (selectedCycle != null) {
            professionalAttrFormDialog.prepareNewProfessionalAttr(this.selectedCycle);
            professionalAttrFormDialog.setUpdateTarget(":cycleDashboardForm:professionalAttrsTable");
        }
    }

    /**
     * Prepares the ProfessionalAttrFormDialog to edit an existing attribute.
     * @param attributeToEdit The attribute to be edited.
     */
    public void prepareEditProfessionalAttribute(ProfessionalAttrCategory attributeToEdit) {
        professionalAttrFormDialog.setModel(attributeToEdit);
        professionalAttrFormDialog.setUpdateTarget(":cycleDashboardForm:professionalAttrsTable");
    }
}