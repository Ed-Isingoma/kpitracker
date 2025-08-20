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
        groupedPermissions.put("Activity Management", filterPermissions("activity"));
        groupedPermissions.put("Peer Rating", filterPermissions("peer"));

        // Add any remaining permissions to a "Miscellaneous" group
        List<Permission> allOtherPermissions = new ArrayList<>(permissionService.getPermissions());
        List<Permission> categorizedPermissions = groupedPermissions.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        allOtherPermissions.removeAll(categorizedPermissions);

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
        // Initialize services if they're null (due to transient fields)
        if (roleService == null) {
            this.roleService = ApplicationContextProvider.getBean(RoleService.class);
        }
        if (permissionService == null) {
            this.permissionService = ApplicationContextProvider.getBean(PermissionService.class);
        }

        if (modelId != null && !modelId.trim().isEmpty()) {
            try {
                super.model = (Role) roleService.getObjectById(modelId);
                if(super.model != null){
                    setFormProperties();
                }
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Loading Role", e.getMessage()));
                super.model = new Role();
                this.selectedPermissions = new ArrayList<>();
            }
        } else {
            // New role
            super.model = new Role();
            this.selectedPermissions = new ArrayList<>();
        }

        // Ensure permissions are categorized
        if (groupedPermissions.isEmpty()) {
            categorizePermissions();
        }
    }

    @Override
    public void persist() throws Exception {
        if (super.model == null) {
            throw new Exception("Role model is null");
        }

        // Initialize service if null
        if (roleService == null) {
            this.roleService = ApplicationContextProvider.getBean(RoleService.class);
        }

        // Set permissions - ensure we have a HashSet
        if (this.selectedPermissions != null) {
            super.model.setPermissions(new HashSet<>(this.selectedPermissions));
        } else {
            super.model.setPermissions(new HashSet<>());
        }

        // Save the role
        this.roleService.saveRole(super.model);
    }

    @Override
    public void save() {
        try {
            // Validate required fields
            if (super.model == null || super.model.getName() == null || super.model.getName().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Role name is required"));
                return;
            }

            this.persist();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Role saved successfully"));

            // Navigate back to roles view
            super.loadParentView();

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Save Failed", e.getMessage()));
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
        if(super.model != null && super.model.getPermissions() != null){
            this.selectedPermissions = new ArrayList<>(super.model.getPermissions());
        } else {
            this.selectedPermissions = new ArrayList<>();
        }
    }

    @Override
    public String getViewUrl() {
        return HyperLinks.ROLES_VIEW;
    }

    // Helper method to check if we're editing
    public boolean isEditing() {
        return super.model != null && super.model.getId() != null;
    }

    // Get all permissions for simple display
    public List<Permission> getAllPermissions() {
        if (permissionService == null) {
            permissionService = ApplicationContextProvider.getBean(PermissionService.class);
        }
        return permissionService.getPermissions().stream()
                .sorted(Comparator.comparing(Permission::getName))
                .collect(Collectors.toList());
    }

    // Method to check if a permission is selected
    public boolean isPermissionSelected(Permission permission) {
        return this.selectedPermissions != null && this.selectedPermissions.contains(permission);
    }

    // Method to toggle permission selection
    public void togglePermissionSelection(Permission permission) {
        if (this.selectedPermissions == null) {
            this.selectedPermissions = new ArrayList<>();
        }

        if (this.selectedPermissions.contains(permission)) {
            this.selectedPermissions.remove(permission);
        } else {
            this.selectedPermissions.add(permission);
        }
    }

    // Legacy method for backward compatibility
    public void togglePermission(Permission permission) {
        togglePermissionSelection(permission);
    }
}