package org.pahappa.systems.kpiTracker.views.users;

import com.google.common.collect.Sets;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.base.CustomService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.CustomServiceImpl;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Permission;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.utils.SearchField;
import org.sers.webutils.model.utils.SortField;
import org.sers.webutils.server.core.service.PermissionService;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.CustomLogger;
import org.sers.webutils.server.shared.CustomLogger.LogSeverity;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.*;

@ManagedBean(name = "rolesView")
@Getter
@Setter
@ViewScoped // Changed to ViewScoped for better state management on this page
@ViewPath(path = HyperLinks.ROLES_VIEW)
public class RolesView extends PaginatedTableView<Role, RolesView, RolesView> {

	private static final long serialVersionUID = 1L;
	private transient RoleService roleService;
	private transient CustomService customService;

	private String searchTerm;
	private Search search;
	private List<SearchField> searchFields;
	private SortField selectedSortField = new SortField("dateCreated", "dateCreated", true);


	@PostConstruct
	public void init() {
		this.roleService = ApplicationContextProvider.getBean(RoleService.class);
		this.customService = ApplicationContextProvider.getBean(CustomService.class);
		this.searchFields = Arrays.asList(
				new SearchField("Name", "name"), new SearchField("Description", "description"));
		reloadFilterReset();
	}

	// This method is now primarily for the LazyDataTable, but we'll use it to load all data.
	@Override
	public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
		this.search = CustomServiceImpl.composeRoleSearch(searchFields, searchTerm, null, null, selectedSortField);
		super.setTotalRecords(customService.countRoles(search));
		// Load ALL matching records for our ui:repeat grid
		super.setDataModels(this.customService.getRoles(search, 0, super.getTotalRecords()));
	}

	@Override
	public void reloadFilterReset() {
		try {
			// This will call the reloadFromDB above, populating our dataModels list
			super.reloadFilterReset();
		} catch (Exception e) {
			CustomLogger.log(LogSeverity.LEVEL_ERROR, e.getMessage());
		}
	}

	// This is required by LazyDataModel, but our ui:repeat won't use it directly.
	@Override
	public List<Role> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
		// We already loaded the data in reloadFilterReset, so just return it.
		return super.getDataModels();
	}

	@Override
	public List<ExcelReport> getExcelReportModels() {
		return Collections.emptyList();
	}

	@Override
	public String getFileName() {
		return null;
	}

	public void deleteSelectedRole(Role role) {
		try {
			customService.deleteRole(role);
			reloadFilterReset(); // Refresh the list
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Role deleted successfully."));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action Failed", e.getMessage()));
		}
	}


}