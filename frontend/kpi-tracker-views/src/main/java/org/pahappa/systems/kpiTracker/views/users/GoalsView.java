package org.pahappa.systems.kpiTracker.views.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.utils.SearchField;
import com.googlecode.genericdao.search.Search;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

@Setter
@Getter
@ManagedBean(name = "goalsView")
@ViewScoped // CORRECT SCOPE: Data lasts for the life of the view
@NoArgsConstructor
@ViewPath(path = HyperLinks.GOALS_VIEW)
public class GoalsView extends PaginatedTableView<Goal, GoalsView, GoalsView> implements Serializable {

    private static final long serialVersionUID = 1L;
    private GoalService goalService;

    private String searchTerm;
    private List<SearchField> searchFields;
    private Search search;

    private List<Goal> organisationalGoals;
    private List<Goal> departmentGoals;
    private List<Goal> teamGoals;

    @PostConstruct
    public void init() {
        // STEP 1: REVERT TO THE SERVICE LOCATOR PATTERN. This is the correct way for your project.
        this.goalService = ApplicationContextProvider.getBean(GoalService.class);

        this.searchFields = Arrays.asList(
                new SearchField("Title", "title"), // Ensure this matches your Goal entity field name
                new SearchField("Description", "description")
        );
        super.setMaximumresultsPerpage(10);
        this.reloadFilterReset();
    }

    @Override
    public void reloadFilterReset() {
        this.search = new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        // STEP 2: IMPLEMENT A SIMPLE, WORKING SEARCH LOGIC.
        // This replaces the non-existent GeneralSearchUtils.
        if (StringUtils.isNotBlank(this.searchTerm)) {
            // This creates an OR filter group like: (title LIKE '%...%' OR description LIKE '%...%')
            Search orSearch = new Search();
            for (SearchField field : this.searchFields) {
                orSearch.addFilterILike(field.getPath(), "%" + this.searchTerm + "%");
            }
            // Add the group of OR filters to the main search
            this.search.addFilterOr(orSearch.getFilters().toArray(new com.googlecode.genericdao.search.Filter[0]));
        }

        this.search.addSortDesc("dateCreated");

        // Now that goalService is initialized correctly, this will work.
        super.setTotalRecords(this.goalService.countGoals(this.search));

        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        // Fetch all goals matching the search criteria
        List<Goal> allGoals = goalService.getGoals(search, 0, 0);

        // Filter the single list into the three lists for the accordion
        this.organisationalGoals = allGoals.stream()
                .filter(g -> g.getGoalLevel() == GoalLevel.ORGANISATIONAL)
                .collect(Collectors.toList());

        this.departmentGoals = allGoals.stream()
                .filter(g -> g.getGoalLevel() == GoalLevel.DEPARTMENT)
                .collect(Collectors.toList());

        this.teamGoals = allGoals.stream()
                .filter(g -> g.getGoalLevel() == GoalLevel.TEAM)
                .collect(Collectors.toList());

        super.setDataModels(allGoals);
    }

    // Unchanged methods...

    public void deleteGoal(Goal goal) {
        try {
            goalService.deleteInstance(goal);
            reloadFilterReset();
            UiUtils.showMessageBox("Action Success!", "Goal has been deleted.");
        } catch (Exception e) {
            UiUtils.showMessageBox("Action Failed!", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Goal> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return super.getDataModels();
    }

    @Override
    public List<org.sers.webutils.server.core.service.excel.reports.ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return "Goals_Report";
    }
}