package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Filter;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.*;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.KPI;
import org.pahappa.systems.kpiTracker.models.Team;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "kpiViewAdmin")
@ViewScoped
@Getter
@Setter
public class KpiViewAdmin extends PaginatedTableView<KPI, KpiViewAdmin, KpiViewAdmin> {

    private KpiService kpiService;
    private GoalCycleService goalCycleService;
    private DepartmentService departmentService;
    private TeamService teamService;

    // Filter properties
    private List<GoalCycle> allGoalCycles;
    private List<Department> allDepartments;
    private List<Team> allTeams;
    private GoalCycle selectedGoalCycle;
    private Department selectedDepartment;
    private Team selectedTeam;
    private String searchTerm;
    private double overallAchievement;

    @PostConstruct
    public void init() {
        super.init();
        this.kpiService = ApplicationContextProvider.getBean(KpiService.class);
        this.goalCycleService = ApplicationContextProvider.getBean(GoalCycleService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);

        this.allGoalCycles = this.goalCycleService.getAllInstances();
        this.allDepartments = this.departmentService.getAllInstances();
        this.allTeams = this.teamService.getAllInstances();
        this.selectedGoalCycle = this.goalCycleService.findCurrentCycle();

        try {
            this.reloadFilterReset();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        Search search = createFullSearch();

        super.setDataModels(kpiService.getInstances(search, offset, limit));
        super.setTotalRecords(kpiService.countInstances(search));

        // To get all filtered results for calculation, use offset/limit of 0
        List<KPI> allFilteredKpis = kpiService.getInstances(search, 0, 0);
        this.overallAchievement = this.kpiService.calculateOverallWeightedAchievement(allFilteredKpis);
    }

    private Search createFullSearch() {
        Search search = new Search(KPI.class);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        if (selectedGoalCycle != null) {
            search.addFilterEqual("goal.goalCycle", selectedGoalCycle);
        }
        if (selectedDepartment != null) {
            search.addFilterEqual("owner.department", selectedDepartment);
        }
        if (selectedTeam != null) {
            search.addFilterEqual("owner.team", selectedTeam);
        }
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            search.addFilterOr(
                    Filter.ilike("title", "%" + searchTerm + "%"),
                    Filter.ilike("goal.title", "%" + searchTerm + "%"),
                    Filter.ilike("owner.firstName", "%" + searchTerm + "%"),
                    Filter.ilike("owner.lastName", "%" + searchTerm + "%")
            );
        }
        return search;
    }

    public void onFilterChange() throws Exception {
        this.reloadFilterReset();
    }

    @Override
    public List<KPI> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        // This correctly calls the PaginatedTableView's load method, which in turn calls our reloadFromDB
        return super.load(first, pageSize, null, null, null);
    }

    @Override
    public List<ExcelReport> getExcelReportModels() { return Collections.emptyList(); }

    @Override
    public String getFileName() { return "KPI_Report_Admin"; }
}