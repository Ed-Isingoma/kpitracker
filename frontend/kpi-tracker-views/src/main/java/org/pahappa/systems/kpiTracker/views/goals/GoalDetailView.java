package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.core.services.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.Activity;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.models.security.RoleConstants;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.ActivityFormDialog;
import org.pahappa.systems.kpiTracker.views.dialogs.GoalFormDialog;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "goalDetailView")
@ViewScoped
@Getter
@Setter
@ViewPath(path = "/pages/admin/goals/goalDetailView.xhtml")
public class GoalDetailView extends PaginatedTableView<BaseEntity, GoalDetailView, GoalDetailView> {

    private static final long serialVersionUID = 1L;
    private transient GoalService goalService;
    private transient ActivityService activityService;

    private String goalId;
    private Goal selectedGoal;

    @ManagedProperty(value = "#{goalFormDialog}")
    private GoalFormDialog goalFormDialog;

    @ManagedProperty(value = "#{activityFormDialog}")
    private ActivityFormDialog activityFormDialog;

    @PostConstruct
    @Override
    public void init() {
        this.goalService = ApplicationContextProvider.getBean(GoalService.class);
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        super.setMaximumresultsPerpage(10);
        reloadFilterReset();
    }

    public void loadGoal() {
        if (goalId != null) {
            this.selectedGoal = goalService.getInstanceByID(goalId);
            if (this.selectedGoal != null) {
                try {
                    super.reloadFilterReset();
                } catch (Exception e) {
                    // Handle exception appropriately
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        if (selectedGoal == null) {
            super.setDataModels(Collections.emptyList());
            super.setTotalRecords(0);
            return;
        }

        if (selectedGoal.getGoalLevel() == GoalLevel.TEAM) {
            // Load activities for a Team Goal
            Search search = new Search(Activity.class)
                    .addFilterEqual("goal", selectedGoal)
                    .addFilterEqual("recordStatus", RecordStatus.ACTIVE)
                    .addSort("dateCreated", true);

            super.setTotalRecords(activityService.countInstances(search));
            super.setDataModels((List<BaseEntity>) (List<?>) activityService.getInstances(search, offset, limit));
        } else {
            // Load sub-goals for Organisational or Departmental Goals
            Search search = new Search(Goal.class)
                    .addFilterEqual("parentGoal", selectedGoal)
                    .addFilterEqual("recordStatus", RecordStatus.ACTIVE)
                    .addSort("dateCreated", true);

            super.setTotalRecords(goalService.countInstances(search));
            super.setDataModels((List<BaseEntity>) (List<?>) goalService.getInstances(search, offset, limit));
        }
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null; // Not needed for now
    }

    @Override
    public String getFileName() {
        return null; // Not needed for now
    }

    public void prepareNewSubItem() {
        if (selectedGoal == null) return;

        switch (selectedGoal.getGoalLevel()) {
            case ORGANISATIONAL:
            case DEPARTMENT:
                prepareNewSubGoal();
                break;
            case TEAM:
                prepareNewActivity();
                break;
        }
    }

    public void prepareNewSubGoal() {
        GoalLevel newGoalLevel = (selectedGoal.getGoalLevel() == GoalLevel.ORGANISATIONAL) ? GoalLevel.DEPARTMENT : GoalLevel.TEAM;
        goalFormDialog.prepareNewGoal(newGoalLevel.name(), this.selectedGoal);
        goalFormDialog.setUpdateTarget(":goalDetailForm:childGoalsTable");

        User user = SharedAppData.getLoggedInUser();
        if (user instanceof EmployeeUser) {
            EmployeeUser employee = (EmployeeUser) user;
            if (newGoalLevel == GoalLevel.DEPARTMENT && employee.getDepartment() != null) {
                goalFormDialog.getModel().setDepartment(employee.getDepartment());
            } else if (newGoalLevel == GoalLevel.TEAM && employee.getTeam() != null) {
                goalFormDialog.getModel().setTeam(employee.getTeam());
            }
        }
    }

    public void prepareEditSubGoal(Goal goalToEdit) {
        goalFormDialog.setModel(goalToEdit);
        goalFormDialog.setUpdateTarget(":goalDetailForm:childGoalsTable");
    }

    public void prepareNewActivity() {
        if (selectedGoal != null) {
            activityFormDialog.prepareNewActivity(this.selectedGoal);
            activityFormDialog.setUpdateTarget(":goalDetailForm:activitiesTable");
        }
    }

    public void prepareEditActivity(Activity activityToEdit) {
        activityFormDialog.setModel(activityToEdit);
        activityFormDialog.setUpdateTarget(":goalDetailForm:activitiesTable");
    }

    public boolean canAddSubItem() {
        if (this.selectedGoal == null) return false;
        User user = SharedAppData.getLoggedInUser();
        if (user == null) return false;

        switch (this.selectedGoal.getGoalLevel()) {
            case ORGANISATIONAL:
                return user.hasRole(RoleConstants.DEPARTMENT_LEAD_ROLE);
            case DEPARTMENT:
                return user.hasRole(RoleConstants.TEAM_LEAD_ROLE);
            case TEAM:
                return user.hasRole(RoleConstants.INDIVIDUAL_ROLE);
            default:
                return false;
        }
    }

    public String getSubItemTypeName() {
        if (this.selectedGoal != null) {
            switch (this.selectedGoal.getGoalLevel()) {
                case ORGANISATIONAL:
                    return "Departmental Goal";
                case DEPARTMENT:
                    return "Team Goal";
                case TEAM:
                    return "Activity";
            }
        }
        return "Sub-Item";
    }

    @Override
    public List<BaseEntity> load(int i, int i1, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
        return List.of();
    }

    @Override
    public void reloadFilterReset(){
        super.setTotalRecords(goalService.countInstances(new Search()));
        try{
            super.reloadFilterReset();
        }catch(Exception e){
            UiUtils.ComposeFailure("Error",e.getLocalizedMessage());
        }

    }
}