package org.pahappa.systems.kpiTracker.views.teams;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.TeamFormDialog;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.SessionExpiredException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.utils.SystemCrashHandler;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "teamsView")
@ViewScoped
@ViewPath(path = "/pages/admin/teams/teamsView.xhtml")
public class TeamView extends PaginatedTableView<Team, TeamView, TeamView> {

    private TeamService teamService;
    // Getters and Setters
    @Getter
    @Setter
    private String searchTerm;

    @Getter
    @Setter
    @ManagedProperty("#{teamFormDialog}")
    private TeamFormDialog teamFormDialog;

    @PostConstruct
    public void init() {
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        super.setMaximumresultsPerpage(10);
        try {
            super.enforceSecurity(null, true, new String[]{PermissionConstants.PERM_VIEW_TEAMS});
        } catch (SessionExpiredException | IOException e) {
            try {
                SystemCrashHandler.reportSystemCrash(e, User.class.newInstance());
            } catch (InstantiationException | IllegalAccessException ex ) {
                throw new RuntimeException(ex);
            }
        }
        reloadFilterReset();
    }


    @Override
    public void reloadFilterReset() {
        // Set the total records for pagination before loading data
        Search search = new Search();
        if (StringUtils.isNotBlank(this.searchTerm)) {
            search.addFilter(Filter.or(
                    Filter.ilike("name", "%" + this.searchTerm + "%"),
                    Filter.ilike("description", "%" + this.searchTerm + "%")
            ));
        }
        super.setTotalRecords(teamService.countInstances(search));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        try {
            Search search = new Search();
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            search.setFirstResult(offset);
            search.setMaxResults(limit);

            // CONSISTENCY: Added search term filter to match DepartmentsView logic
            if (StringUtils.isNotBlank(this.searchTerm)) {
                search.addFilter(Filter.or(
                        Filter.ilike("name", "%" + this.searchTerm + "%"),
                        Filter.ilike("description", "%" + this.searchTerm + "%")
                ));
            }

            super.setDataModels(this.teamService.getInstances(search, offset, limit));
            // CONSISTENCY: Update total records based on the current search
            super.setTotalRecords(this.teamService.countInstances(search));

        } catch (Exception e) {
            System.err.println("AN ERROR OCCURRED IN 'reloadFromDB' for TeamView:");
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading teams", e.getMessage()));
        }
    }

    @Override
    public List<org.sers.webutils.server.core.service.excel.reports.ExcelReport> getExcelReportModels() { return null; }
    @Override
    public String getFileName() { return null; }

    @Override
    public List<Team> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        try {
            reloadFromDB(first, pageSize, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return super.getDataModels();
    }

    public void deleteTeam(Team team) {
        try {
            this.teamService.deleteInstance(team);
            // FIX: Use UiUtils to show messages, as the base class doesn't have these methods.
        } catch (OperationFailedException e) {
            // FIX: Use UiUtils for error messages and log the exception.
            SystemCrashHandler.reportSystemCrash(e, SharedAppData.getLoggedInUser());
        }
    }
}