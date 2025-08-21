package org.pahappa.systems.kpiTracker.security;

import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.shared.SharedAppData;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * A request-scoped bean that provides methods to check user permissions.
 * This is used in the JSF pages to conditionally render components based on the
 * logged-in user's rights.
 */
@ManagedBean(name = "componentRenderer")
@RequestScoped
public class ComponentRenderer {

    private final User loggedInUser;

    public ComponentRenderer() {
        this.loggedInUser = SharedAppData.getLoggedInUser();
    }

    /**
     * Checks if the logged-in user has the 'Manage Users' permission.
     * @return true if the user has the permission, false otherwise.
     */
    public boolean isCanManageUsers() {
        return loggedInUser != null && loggedInUser.hasPermission(PermissionConstants.PERM_MANAGE_USERS);
    }

    /**
     * Checks if the logged-in user has the 'Manage Departments' permission.
     * @return true if the user has the permission, false otherwise.
     */
    public boolean isCanManageDepartments() {
        return loggedInUser != null && loggedInUser.hasPermission(PermissionConstants.PERM_MANAGE_DEPARTMENTS);
    }

    /**
     * Checks if the logged-in user has the 'Manage Teams' permission.
     * @return true if the user has the permission, false otherwise.
     */
    public boolean isCanManageTeams() {
        return loggedInUser != null && loggedInUser.hasPermission(PermissionConstants.PERM_MANAGE_TEAMS);
    }

    /**
     * Checks if the logged-in user has the 'Manage Goals' permission.
     * @return true if the user has the permission, false otherwise.
     */
    public boolean isCanManageGoals() {
        return loggedInUser != null && loggedInUser.hasPermission(PermissionConstants.PERM_MANAGE_GOALS);
    }

    /**
     * A general check for any administrative privilege. Useful for top-level menus.
     * @return true if the user has any of the core management permissions.
     */
    public boolean isAdministrator() {
        if (loggedInUser == null) {
            return false;
        }
        // An admin is someone who can manage users, departments, or teams.
        return isCanManageUsers() || isCanManageDepartments() || isCanManageTeams();
    }

    // Add more methods here for other permissions from PermissionConstants.java as you need them.
    // For example:
    // public boolean isCanViewUsers() { ... }
    // public boolean isCanViewGoals() { ... }
}