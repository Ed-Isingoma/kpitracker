package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.ActivityService;
import org.pahappa.systems.kpiTracker.models.Activity;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "activitiesView")
@ViewScoped
@Getter
@Setter
public class ActivitiesView extends PaginatedTableView<Activity, ActivitiesView, ActivitiesView> {

    private transient ActivityService activityService;
    private String searchTerm;

    @PostConstruct
    public void init() {
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        super.setMaximumresultsPerpage(10);
    }

    @Override
    public void reloadFromDB(int offset, int limit, java.util.Map<String, Object> filters) throws Exception {
        Search search = new Search().addSortDesc("dateCreated");
        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterOr(
                    com.googlecode.genericdao.search.Filter.like("title", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.like("assignedUser.fullName", "%" + searchTerm + "%"),
                    com.googlecode.genericdao.search.Filter.like("department.name", "%" + searchTerm + "%")
            );
        }
        super.setDataModels(this.activityService.getInstances(search, offset, limit));
        super.setTotalRecords(this.activityService.countInstances(search));
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return "";
    }

    public void deleteActivity(Activity activity) {
        try {
            this.activityService.deleteInstance(activity);
            UiUtils.showMessageBox("Success", "Activity deleted successfully.");
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getMessage());
        }
    }

    @Override
    public List<Activity> load(int i, int i1, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
        return Collections.emptyList();
    }
}