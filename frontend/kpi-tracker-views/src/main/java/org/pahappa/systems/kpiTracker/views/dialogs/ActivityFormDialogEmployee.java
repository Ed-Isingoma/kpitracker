package org.pahappa.systems.kpiTracker.views.dialogs;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.models.Activity;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "activityFormDialogEmployee")
@ViewScoped
@Getter
@Setter
public class ActivityFormDialogEmployee extends DialogForm<Activity> {

    private static final long serialVersionUID = 1L;
    private String updateTarget;

    private transient ActivityService activityService;
    private transient EmployeeUserService employeeUserService;

    private List<EmployeeUser> availableUsers;

    /**
     * Constructor to set up the dialog properties.
     * The name "activityDialog" is a widgetVar used to control the dialog.
     * Width and height are for the dialog's dimensions.
     */
    public ActivityFormDialogEmployee() {
        super(HyperLinks.ACTIVITY_FORM_DIALOG_EMPLOYEE_VIEW, 400, 600);
    }

    @PostConstruct
    public void init() {
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        this.employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        this.availableUsers = new ArrayList<>();
        // Set the initial model to a new instance
        super.setModel(null);
    }

    /**
     * This method is called when an existing entity is passed to the form (editing mode).
     * We call super to set the isEditing flag and then load any necessary related data.
     */
    @Override
    public void setFormProperties() {
        super.setFormProperties(); // Sets isEditing = true
        loadUsersForGoalContext(); // Load users relevant to the goal being edited
    }

    /**
     * Resets the form to its initial state for creating a new Activity.
     * This is called by setModel(null).
     */
    @Override
    public void resetModal() {
        super.resetModal();
        this.model = new Activity();
        this.availableUsers = new ArrayList<>();
    }

    /**
     * The core persistence logic. This is called by the save() method in DialogForm.
     * It should not handle UI feedback like FacesMessages; the parent save() does that.
     * @throws Exception if saving fails.
     */
    @Override
    public void persist() throws Exception {

        if (model.getGoal() != null) {
            model.setDepartment(model.getGoal().getDepartment());
            model.setTeam(model.getGoal().getTeam());
        }
        activityService.saveInstance(model);
    }

    public void loadUsersForGoalContext() {

        if (model == null || model.getAssignedUser() == null) {
            this.availableUsers = new ArrayList<>();
            return;
        }

        if (model.getGoal() != null) {
            Team team = model.getGoal().getTeam();
            Department dept = model.getGoal().getDepartment();
            Search search = new Search(EmployeeUser.class).addFilterEqual("recordStatus", RecordStatus.ACTIVE);

            if (team != null) {
                search.addFilterEqual("team", team);
            } else if (dept != null) {
                search.addFilterEqual("department", dept);
            } else {
                System.out.println("--- LOG: No Team or Department on the Goal. Searching all active users.");
            }

            this.availableUsers = employeeUserService.getInstances(search, 0, 0);
            EmployeeUser assignedUser = model.getAssignedUser();

            if (!this.availableUsers.contains(assignedUser)) {
                this.availableUsers.add(assignedUser);
            }
            System.out.println("========== 3 (Dialog Logic): User is: " + (model != null ? model.getAssignedUser().getFullName() : "Model is NULL"));
        } else {
            this.availableUsers = new ArrayList<>();
            this.availableUsers.add(model.getAssignedUser());
        }
    }
}