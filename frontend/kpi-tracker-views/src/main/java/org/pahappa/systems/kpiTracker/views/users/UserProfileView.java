package org.pahappa.systems.kpiTracker.views.users;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.Activity;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ManagedBean(name = "userProfileView")
@ViewScoped
@Getter
@Setter
public class UserProfileView implements Serializable {

    private static final long serialVersionUID = 1L;
    private String userId;
    private EmployeeUser selectedUser;
    private List<Goal> userGoals;
    private List<Activity> userActivities;

    private transient EmployeeUserService employeeUserService;
    private transient GoalService goalService;
    private transient ActivityService activityService;

    /**
     * This method is called by the <f:viewAction> in the XHTML.
     * It loads the user and their related data based on the userId from the URL.
     */
    public void loadUser() {
        if (userId == null || userId.isEmpty()) {
            // Handle case where no user ID is provided
            return;
        }

        this.employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);
        this.goalService = ApplicationContextProvider.getBean(GoalService.class);
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);

        this.selectedUser = employeeUserService.getInstanceByID(userId);

        if (this.selectedUser != null) {
            // Load activities directly assigned to the user
            Search activitySearch = new Search(Activity.class)
                    .addFilterEqual("assignedUser", this.selectedUser)
                    .addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            this.userActivities = activityService.getInstances(activitySearch, 0, 0);

            // Load goals assigned to the user's team
            if (this.selectedUser.getTeam() != null) {
                Search goalSearch = new Search(Goal.class)
                        .addFilterEqual("team", this.selectedUser.getTeam())
                        .addFilterEqual("recordStatus", RecordStatus.ACTIVE);
                this.userGoals = goalService.getInstances(goalSearch, 0, 0);
            } else {
                this.userGoals = Collections.emptyList();
            }
        }
    }
}