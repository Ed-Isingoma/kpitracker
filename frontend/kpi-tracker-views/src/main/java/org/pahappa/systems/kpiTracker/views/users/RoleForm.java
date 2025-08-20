package org.pahappa.systems.kpiTracker.views.users;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.client.views.presenters.WebFormView;
import org.sers.webutils.model.security.Permission;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.server.core.service.PermissionService;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "roleForm")
@ViewScoped
@Getter @Setter
@ViewPath(path = HyperLinks.ROLE_FORM)
public class RoleForm extends WebFormView<Role, RoleForm, RolesView> {

    private static final long serialVersionUID = 1L;
    private transient RoleService roleService;
    private transient PermissionService permissionService;

    private List<Permission> selectedPermissions = new ArrayList<>();
    private String modelId;

    // This map will hold our grouped permissions for the UI
    private Map<String, List<Permission>> groupedPermissions = new LinkedHashMap<>();

    @Override
    @PostConstruct
    public void beanInit() {
        this.roleService = ApplicationContextProvider.getBean(RoleService.class);
        this.permissionService = ApplicationContextProvider.getBean(PermissionService.class);
        this.resetModal();
        categorizePermissions();
    }

    private void categorizePermissions() {
        // Group permissions based on their names
        groupedPermissions.put("User Management", filterPermissions("user"));
        groupedPermissions.put("Department Management", filterPermissions("department"));
        groupedPermissions.put("Team Management", filterPermissions("team"));
        groupedPermissions.put("Goal Management", filterPermissions("goal"));
        groupedPermissions.put("KPI Management", filterPermissions("kpi"));
        groupedPermissions.put("Activity Management", filterPermissions("activity")); // 'activit' to catch 'activity' and 'activities'
        groupedPermissions.put("Peer Rating", filterPermissions("peer"));

        // Add any remaining permissions to a "Miscellaneous" group
        List<Permission> allOtherPermissions = permissionService.getPermissions();
        allOtherPermissions.removeAll(groupedPermissions.values().stream().flatMap(List::stream).collect(Collectors.toList()));
        if(!allOtherPermissions.isEmpty()){
            groupedPermissions.put("Miscellaneous", allOtherPermissions);
        }
    }

    private List<Permission> filterPermissions(String keyword) {
        return permissionService.getPermissions().stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword))
                .sorted(Comparator.comparing(Permission::getName))
                .collect(Collectors.toList());
    }

    @Override
    public void pageLoadInit() {
        if (modelId != null) {
            super.model = (Role) roleService.getObjectById(modelId);
            if(super.model != null){
                setFormProperties();
            }
        }
    }

    @Override
    public void persist() throws Exception {
        this.model.setPermissions(new HashSet<>(this.selectedPermissions));
        this.roleService.saveRole(this.model);
    }

    @Override
    public void save() {
        try {
            this.persist();
            super.loadParentView();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action Failed", e.getMessage()));
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Role();
        this.selectedPermissions = new ArrayList<>();
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if(super.model.getPermissions() != null){
            this.selectedPermissions = new ArrayList<>(super.model.getPermissions());
        }
    }

    @Override
    public String getViewUrl() {
        return HyperLinks.ROLES_VIEW;
    }
}