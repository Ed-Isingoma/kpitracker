package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.SearchResult;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalCycleService;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.KPI;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.views.dialogs.ActivityFormDialog;
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

    private GoalService goalService;
    private GoalCycleService goalCycleService;
    private KpiService kpiService;

    @ManagedProperty("#{kpiFormDialog}")
    private KpiFormDialog kpiFormDialog;

    @ManagedProperty("#{activityFormDialog}")
    private ActivityFormDialog activityFormDialog;

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

            System.out.println("Data reloaded for cycle '" +
                    this.selectedGoalCycle.getTitle() + "'. Found " + result.getTotalCount() + " goals.");
        } else {
            super.setTotalRecords(0);
            super.setDataModels(new ArrayList<>());
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
        kpiFormDialog.setUpdateTarget(":myGoalsForm:goalsDataScroller");
    }

    public void prepareNewActivity(Goal parentGoal) {
        System.out.println("Preparing new activity for goal: " + parentGoal.getTitle());
    }

    public List<KPI> getKpisForGoal(Goal goal) {
        return kpiService.getKpisForCycleAndGoal(this.selectedGoalCycle, goal);
    }

    // This load method is correct. It bridges new PrimeFaces to your old framework.
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