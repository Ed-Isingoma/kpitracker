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
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "activityFormDialogEmployee")
@ViewScoped
@Getter
@Setter
public class ActivityFormDialogEmployee implements Serializable {

    private static final long serialVersionUID = 1L;
    private Activity model;
    private String updateTarget;

    private transient ActivityService activityService;
    private transient EmployeeUserService employeeUserService;

    private List<EmployeeUser> availableUsers;

    @PostConstruct
    public void init() {
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        this.employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        this.model = new Activity();
        this.availableUsers = new ArrayList<>();
    }

    /**
     * Resets the form for creating a new Activity.
     */
    public void newActivity() {
        this.model = new Activity();
        this.availableUsers = new ArrayList<>();
    }

    /**
     * Populates the list of available users based on the team/department of the parent goal.
     * This should be called by the prepareNewActivity method in the main view bean.
     */
    public void loadUsersForGoalContext() {
        if (model.getGoal() != null) {
            Team team = model.getGoal().getTeam();
            Department dept = model.getGoal().getDepartment();

            Search search = new Search(EmployeeUser.class).addFilterEqual("recordStatus", RecordStatus.ACTIVE);

            if (team != null) {
                search.addFilterEqual("team", team);
            } else if (dept != null) {
                search.addFilterEqual("department", dept);
            }
            this.availableUsers = employeeUserService.getInstances(search, 0, 0);
        } else {
            this.availableUsers = new ArrayList<>();
        }
    }

    /**
     * Saves the current Activity.
     */
    public void save() {
        try {
            // Pre-populate department and team from the parent Goal if they are not set
            if(model.getGoal() != null){
                model.setDepartment(model.getGoal().getDepartment());
                model.setTeam(model.getGoal().getTeam());
            }

            activityService.saveInstance(model);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Activity saved successfully.");
            FacesContext.getCurrentInstance().addMessage(null, message);

        } catch (Exception e) {
            FacesContext.getCurrentInstance().validationFailed();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to save activity: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
}
