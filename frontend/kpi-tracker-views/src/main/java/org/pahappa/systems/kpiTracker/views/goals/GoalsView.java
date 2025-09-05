package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalCycleService;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.views.dialogs.GoalCycleFormDialog;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@ManagedBean(name = "goalsView")
@ViewScoped
public class GoalsView extends PaginatedTableView<GoalCycle, GoalsView, GoalsView> {

    private static final long serialVersionUID = 1L;
    private GoalCycleService goalCycleService;
    @Setter
    @Getter
    private String searchTerm;

    @Setter
    @Getter
    @ManagedProperty(value = "#{goalCycleFormDialog}")
    private GoalCycleFormDialog goalCycleFormDialog;

    @PostConstruct
    public void init() {
        this.goalCycleService = ApplicationContextProvider.getBean(GoalCycleService.class);
        super.setMaximumresultsPerpage(12); // Show 12 cards per page
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reloadFilterReset() throws Exception {
        this.searchTerm = null;
        super.reloadFilterReset();
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) throws Exception {
        Search search = new Search().addSortDesc("dateCreated");
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterILike("title", "%" + searchTerm + "%");
        }
        super.setDataModels(this.goalCycleService.getInstances(search, offset, limit));
        super.setTotalRecords(this.goalCycleService.countInstances(search));
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null; // Not implemented for this view
    }

    @Override
    public String getFileName() {
        return null; // Not implemented for this view
    }

    @Override
    public List<GoalCycle> load(int i, int i1, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
        return Collections.emptyList();
    }
}