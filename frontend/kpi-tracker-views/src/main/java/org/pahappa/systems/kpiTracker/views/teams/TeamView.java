package org.pahappa.systems.kpiTracker.views.teams;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.TeamFormDialog;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.SessionExpiredException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

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
@ViewPath(path = HyperLinks.TEAMS_VIEW)
@Getter @Setter
public class TeamView extends PaginatedTableView<Team, TeamView, TeamView> {

    private static final long serialVersionUID = 1L;
    private TeamService teamService;
    private String searchTerm;

    @ManagedProperty("#{teamFormDialog}")
    private TeamFormDialog teamFormDialog;

    @PostConstruct
    public void init() {
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        super.setMaximumresultsPerpage(12);
        try {
            super.enforceSecurity(null, true, new String[]{PermissionConstants.PERM_VIEW_TEAMS});
        } catch (SessionExpiredException | IOException e) {
            e.printStackTrace();
        }
        reloadFilterReset();
    }

    @Override
    public void reloadFilterReset() {
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getLocalizedMessage()));
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.setFirstResult(offset);
        search.setMaxResults(limit);
        search.addSortDesc("dateCreated");

        if (StringUtils.isNotBlank(this.searchTerm)) {
            search.addFilter(Filter.or(
                    Filter.ilike("name", "%" + this.searchTerm + "%"),
                    Filter.ilike("description", "%" + this.searchTerm + "%"),
                    Filter.ilike("department.name", "%" + this.searchTerm + "%")
            ));
        }

        super.setTotalRecords(this.teamService.countInstances(search));
        super.setDataModels(this.teamService.getInstances(search, offset, limit));
    }

    @Override
    public List<ExcelReport> getExcelReportModels() { return null; }

    @Override
    public String getFileName() { return null; }

    // THIS IS THE CORRECTED METHOD
    @Override
    public List<Team> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        try {
            reloadFromDB(first, pageSize, null);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Error", "Failed to load data."));
            e.printStackTrace();
        }
        this.setRowCount(super.getTotalRecords());
        return super.getDataModels();
    }

    public void deleteTeam(Team team) {
        try {
            this.teamService.deleteInstance(team);
            reloadFilterReset();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Team deleted successfully."));
        } catch (OperationFailedException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", e.getLocalizedMessage()));
        }
    }
}