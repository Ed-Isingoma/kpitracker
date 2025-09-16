package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalCycleService;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.KPI;
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

@ManagedBean(name = "kpiViewTeamLead")
@ViewScoped
@Getter
@Setter
public class KpiViewTeamLead extends PaginatedTableView<KPI, KpiViewTeamLead, KpiViewTeamLead> {
    private KpiService kpiService;
    private GoalCycleService goalCycleService;

    private EmployeeUser currentUser;
    private List<GoalCycle> allGoalCycles;
    private GoalCycle selectedGoalCycle;
    private String searchTerm;
    private double teamOverallAchievement;

    @PostConstruct
    public void init() {
        super.init();
        this.currentUser = (EmployeeUser) SharedAppData.getLoggedInUser();
        // Stop initialization if the user is not a team lead
        if (this.currentUser == null || this.currentUser.getTeam() == null) {
            return;
        }

        this.kpiService = ApplicationContextProvider.getBean(KpiService.class);
        this.goalCycleService = ApplicationContextProvider.getBean(GoalCycleService.class);

        this.allGoalCycles = this.goalCycleService.getAllInstances();
        this.selectedGoalCycle = this.goalCycleService.findCurrentCycle();

        try {
            this.reloadFilterReset();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        if (this.currentUser == null || this.currentUser.getTeam() == null) {
            super.setDataModels(Collections.emptyList());
            super.setTotalRecords(0);
            return;
        }

        Search search = createFullSearch();

        super.setDataModels(kpiService.getInstances(search, offset, limit));
        super.setTotalRecords(kpiService.countInstances(search));

        // Calculate achievement based on ALL filtered results, not just the current page
        List<KPI> allFilteredKpis = kpiService.getInstances(search, 0, 0);
        this.teamOverallAchievement = this.kpiService.calculateOverallWeightedAchievement(allFilteredKpis);
    }

    private Search createFullSearch() {
        Search search = new Search(KPI.class);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        // **IMPORTANT**: Non-negotiable filter to scope data to the lead's team
        search.addFilterEqual("owner.team", this.currentUser.getTeam());

        if (selectedGoalCycle != null) {
            search.addFilterEqual("goal.goalCycle", selectedGoalCycle);
        }
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            search.addFilterOr(
                    com.googlecode.genericdao.search.Filter.ilike("title", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.ilike("goal.title", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.ilike("owner.firstName", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.ilike("owner.lastName", "%" + searchTerm + "%")
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
            // Security Check: Ensure the KPI belongs to someone in the lead's team
            if (!kpiToUpdate.getOwner().getTeam().equals(this.currentUser.getTeam())) {
                // Log and show error
                return;
            }
            this.kpiService.saveInstance(kpiToUpdate);

            List<KPI> allFilteredKpis = kpiService.getInstances(createFullSearch(), 0, 0);
            this.teamOverallAchievement = this.kpiService.calculateOverallWeightedAchievement(allFilteredKpis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<KPI> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return super.load(first, pageSize, null, null, null);
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return "KPI_Report_Team";
    }
}