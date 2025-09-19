package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.SearchResult;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalCycleService;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.models.Activity;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.KPI;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.views.dialogs.ActivityFormDialogEmployee;
import org.pahappa.systems.kpiTracker.views.dialogs.KpiFormDialog;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "kpiViewNormalUser")
@ViewScoped
@Getter
@Setter
public class KpiViewNormalUser extends PaginatedTableView<Goal, GoalService, KpiViewNormalUser> {

    private GoalCycle selectedGoalCycle;
    private List<GoalCycle> allGoalCycles;
    private EmployeeUser loggedInUser;
    private double userOverallAchievement;

    private GoalService goalService;
    private GoalCycleService goalCycleService;
    private KpiService kpiService;

    @ManagedProperty("#{kpiFormDialog}")
    private KpiFormDialog kpiFormDialog;

    @ManagedProperty("#{activityFormDialogEmployee}")
    private ActivityFormDialogEmployee activityFormDialogEmployee;

    @PostConstruct
    public void init() {
        this.goalService = ApplicationContextProvider.getBean(GoalService.class);
        this.goalCycleService = ApplicationContextProvider.getBean(GoalCycleService.class);
        this.kpiService = ApplicationContextProvider.getBean(KpiService.class);
        this.loggedInUser = (EmployeeUser) SharedAppData.getLoggedInUser();
        this.allGoalCycles = goalCycleService.getAllInstances();

        if (this.allGoalCycles != null && !this.allGoalCycles.isEmpty()) {
            this.selectedGoalCycle = this.allGoalCycles.get(0);
        }
    }

    @Override
    public void reloadFromDB(int first, int pageSize, Map<String, Object> filters) throws Exception {
        if (this.selectedGoalCycle != null && this.loggedInUser != null) {
            SearchResult<Goal> result = goalService
                    .searchAndCountRelevantGoals(this.loggedInUser, this.selectedGoalCycle, first, pageSize);

            super.setDataModels(result.getResult());
            super.setTotalRecords(result.getTotalCount());

            List<KPI> allUserKpisInCycle = this.kpiService.getKpisForCycle(this.selectedGoalCycle);
            this.userOverallAchievement = this.kpiService.calculateOverallWeightedAchievement(allUserKpisInCycle);

        } else {
            super.setTotalRecords(0);
            super.setDataModels(new ArrayList<>());
            this.userOverallAchievement = 0.0; // Also reset here
        }
    }

    public void onCycleChange() {
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepareNewKpi(Goal parentGoal) {
        kpiFormDialog.newKPI();
        kpiFormDialog.getModel().setGoal(parentGoal);
        kpiFormDialog.setUpdateTarget(":userGoalsForm:goalsDataScroller");
    }

    public void prepareAndShowNewActivity(Goal goal) {
        activityFormDialogEmployee.setModel(new Activity());

        activityFormDialogEmployee.getModel().setGoal(goal);
        activityFormDialogEmployee.getModel().setAssignedUser(this.loggedInUser);

        activityFormDialogEmployee.loadUsersForGoalContext();
        activityFormDialogEmployee.setUpdateTarget(":userGoalsForm:goalsDataScroller");
        activityFormDialogEmployee.show(null);
    }

    public List<KPI> getKpisForGoal(Goal goal) {
        List<KPI> allKpisForGoal = kpiService.getKpisForCycleAndGoal(this.selectedGoalCycle, goal);
        List<KPI> userKpisForGoal = new ArrayList<>();
        for (KPI kpi : allKpisForGoal) {
            if (kpi.getOwner() != null && kpi.getOwner().equals(this.loggedInUser)) {
                userKpisForGoal.add(kpi);
            }
        }
        return userKpisForGoal;
    }

    public void updateUserKpi(KPI kpiToUpdate) {
        try {
            if (!kpiToUpdate.getOwner().equals(this.loggedInUser)) {
                System.err.println("SECURITY ALERT: User " + loggedInUser.getId() + " attempted to update KPI " + kpiToUpdate.getId());
                return;
            }

            this.kpiService.saveInstance(kpiToUpdate);

            // After saving, recalculate the overall achievement to reflect the change.
            List<KPI> allUserKpisInCycle = this.kpiService.getKpisForCycle(this.selectedGoalCycle);
            this.userOverallAchievement = this.kpiService.calculateOverallWeightedAchievement(allUserKpisInCycle);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Goal> load(int first, int pageSize, Map<String, SortMeta> multiSortMeta, Map<String, FilterMeta> filterBy) {
        Map<String, Object> simpleFilters = new java.util.HashMap<>();
        if (filterBy != null) {
            for (Map.Entry<String, FilterMeta> entry : filterBy.entrySet()) {
                simpleFilters.put(entry.getKey(), entry.getValue().getFilterValue());
            }
        }
        String sortField = null;
        org.primefaces.model.SortOrder sortOrder = org.primefaces.model.SortOrder.UNSORTED;
        return super.load(first, pageSize, sortField, sortOrder, simpleFilters);
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }
}