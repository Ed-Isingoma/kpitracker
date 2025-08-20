package org.pahappa.systems.kpiTracker.views.dialogs;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.Activity;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.DialogForm;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.utils.SystemCrashHandler;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "activityFormDialog")
@ViewScoped // Changed from SessionScoped to ViewScoped for correctness
@Getter
@Setter
public class ActivityFormDialog extends DialogForm<Activity> {

    private static final long serialVersionUID = 1L;
    private transient ActivityService activityService;
    private transient EmployeeUserService employeeUserService;
    private transient DepartmentService departmentService;
    private transient TeamService teamService;

    private List<Department> allDepartments;
    private List<Team> availableTeams;
    private List<EmployeeUser> availableUsers;

    // A generic update target for the form to refresh the activities table
    private String updateTarget = ":activitiesFormDialog";

    public ActivityFormDialog() {
        super("Activity Form", 750, 550);
    }

    @PostConstruct
    @Override
    public void beanInit() {
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        this.employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);

        this.allDepartments = departmentService.getInstances(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE), 0, 0);
        this.availableTeams = new ArrayList<>();
        this.availableUsers = new ArrayList<>();
        resetModal();
    }

    @Override
    public void persist() throws Exception {
        this.activityService.saveInstance(super.getModel());
    }

    @Override
    public void save() {
        try {
            this.persist();
            super.hide(); // Close dialog on success
            UiUtils.showMessageBox("Success", "Activity saved successfully.");
        } catch (Exception e) {
            UiUtils.ComposeFailure("Save Failed", e.getMessage());
            // This utility from your webutils library keeps the dialog open on validation error
            UiUtils.keepDialogOpenOnValidationError();
            SystemCrashHandler.reportSystemCrash(e, SharedAppData.getLoggedInUser());
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Activity();
        this.availableTeams = new ArrayList<>();
        this.availableUsers = new ArrayList<>();
    }

    @Override
    public Activity getModel() {
        return super.getModel();
    }

    @Override
    public void setModel(Activity model) {
        super.setModel(model);
        if (model != null && model.isSaved()) {
            // Pre-populate dropdowns for editing an existing activity
            if (model.getDepartment() != null) {
                // Load teams for the activity's department
                this.availableTeams = teamService.getInstances(new Search().addFilterEqual("department", model.getDepartment()), 0, 0);

                // Load users based on whether a team is also selected
                if (model.getTeam() != null) {
                    this.availableUsers = employeeUserService.getInstances(new Search().addFilterEqual("team", model.getTeam()), 0, 0);
                } else {
                    // If no team, load all users from the department
                    this.availableUsers = employeeUserService.getInstances(new Search().addFilterEqual("department", model.getDepartment()), 0, 0);
                }
            }
        }
    }

    /**
     * Handles the AJAX event when a department is selected.
     * It populates the list of teams and the list of all users for that department.
     */
    public void onDepartmentChange() {
        Department selectedDept = super.getModel().getDepartment();
        if (selectedDept != null) {
            // Populate teams for the selected department
            this.availableTeams = teamService.getInstances(new Search().addFilterEqual("department", selectedDept), 0, 0);
            // Populate users for the entire department initially
            this.availableUsers = employeeUserService.getInstances(new Search().addFilterEqual("department", selectedDept), 0, 0);
        } else {
            this.availableTeams = new ArrayList<>();
            this.availableUsers = new ArrayList<>();
        }
        // Reset team and user selection in the model
        super.getModel().setTeam(null);
        super.getModel().setAssignedUser(null);
    }

    /**
     * Handles the AJAX event when a team is selected.
     * It filters the list of users to only those in the selected team.
     * If the team is deselected, it reverts to showing all users in the department.
     */
    public void onTeamChange() {
        Team selectedTeam = super.getModel().getTeam();
        Department selectedDept = super.getModel().getDepartment();

        if (selectedTeam != null) {
            // Filter users to the selected team
            this.availableUsers = employeeUserService.getInstances(new Search().addFilterEqual("team", selectedTeam), 0, 0);
        } else if (selectedDept != null) {
            // If team is deselected, show all users from the department again
            this.availableUsers = employeeUserService.getInstances(new Search().addFilterEqual("department", selectedDept), 0, 0);
        } else {
            // Safeguard in case department is somehow null
            this.availableUsers = new ArrayList<>();
        }
        // Reset user selection in the model
        super.getModel().setAssignedUser(null);
    }


    /**
     * Prepares the dialog to create a new activity that is a child of a specific goal.
     * This is typically called from the GoalDetailView.
     *
     * @param parentGoal The goal to which this new activity will belong.
     */
    public void prepareNewActivity(Goal parentGoal) {
        resetModal(); // Resets to a new Activity object
        if (parentGoal != null && parentGoal.getTeam() != null) {
            super.getModel().setGoal(parentGoal);
            super.getModel().setDepartment(parentGoal.getTeam().getDepartment());
            super.getModel().setTeam(parentGoal.getTeam());

            // Pre-load users from the goal's team
            this.availableUsers = employeeUserService.getInstances(new Search().addFilterEqual("team", parentGoal.getTeam()), 0, 0);
        }
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
    }

    @Override
    public void pageLoadInit() {
        // Not needed for this dialog
    }
}