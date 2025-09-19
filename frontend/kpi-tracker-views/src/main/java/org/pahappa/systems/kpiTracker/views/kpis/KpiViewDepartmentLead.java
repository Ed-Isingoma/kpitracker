package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalCycleService;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.KPI;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "kpiViewDepartmentLead")
@ViewScoped
@Getter
@Setter
public class KpiViewDepartmentLead extends PaginatedTableView<KPI, KpiViewDepartmentLead, KpiViewDepartmentLead> {

    private KpiService kpiService;
    private GoalCycleService goalCycleService;
    private TeamService teamService;

    private EmployeeUser currentUser;

    // Filter properties
    private List<GoalCycle> allGoalCycles;
    private List<Team> teamsInDepartment;
    private GoalCycle selectedGoalCycle;
    private Team selectedTeam;
    private String searchTerm;
    private double departmentOverallAchievement;

    @PostConstruct
    public void init() {
        this.currentUser = (EmployeeUser) SharedAppData.getLoggedInUser();
        // Stop initialization if the user is not a department lead
        if (this.currentUser == null || this.currentUser.getDepartment() == null) {
            return;
        }

        this.kpiService = ApplicationContextProvider.getBean(KpiService.class);
        this.goalCycleService = ApplicationContextProvider.getBean(GoalCycleService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);

        this.allGoalCycles = this.goalCycleService.getAllInstances();
        this.selectedGoalCycle = this.goalCycleService.findCurrentCycle();

        // Populate teams dropdown with only teams from the lead's department
        Search teamSearch = new Search(Team.class);
        teamSearch.addFilterEqual("department", this.currentUser.getDepartment());
        this.teamsInDepartment = this.teamService.getInstances(teamSearch, 0, 0);

        try {
            this.reloadFilterReset();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        if (this.currentUser == null || this.currentUser.getDepartment() == null) {
            super.setDataModels(Collections.emptyList());
            super.setTotalRecords(0);
            return;
        }

        Search search = createFullSearch();

        super.setDataModels(kpiService.getInstances(search, offset, limit));
        super.setTotalRecords(kpiService.countInstances(search));

        // Calculate achievement based on ALL filtered results, not just the current page
        List<KPI> allFilteredKpis = kpiService.getInstances(search, 0, 0);
        this.departmentOverallAchievement = this.kpiService.calculateOverallWeightedAchievement(allFilteredKpis);
    }

    private Search createFullSearch() {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        search.addFilterEqual("owner.department", this.currentUser.getDepartment());

        if (selectedGoalCycle != null) {
            search.addFilterEqual("goal.goalCycle", selectedGoalCycle);
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

    /**
     * Action method called by filter components (e.g., dropdowns) to refresh the table.
     */
    public void onFilterChange() throws Exception {
        this.reloadFilterReset();
    }

    /**
     * Action method to update a KPI, typically for marking it as 'achieved'.
     * @param kpiToUpdate The KPI to be saved.
     */
    public void updateKpi(KPI kpiToUpdate) {
        try {
            // Security Check: Ensure the KPI belongs to someone in the lead's department
            if (!kpiToUpdate.getOwner().getDepartment().equals(this.currentUser.getDepartment())) {
                // Log and show error
                return;
            }
            this.kpiService.saveInstance(kpiToUpdate);
            // Recalculate achievement after the change
            List<KPI> allFilteredKpis = kpiService.getInstances(createFullSearch(), 0, 0);
            this.departmentOverallAchievement = this.kpiService.calculateOverallWeightedAchievement(allFilteredKpis);
        } catch (Exception e) {
            // Add proper error handling/logging
            e.printStackTrace();
        }
    }

    @Override
    public List<KPI> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return super.load(first, pageSize, null, null, null);
    }

    @Override
    public List<ExcelReport> getExcelReportModels() { return Collections.emptyList(); }

    @Override
    public String getFileName() { return "KPI_Report_Department"; }
}