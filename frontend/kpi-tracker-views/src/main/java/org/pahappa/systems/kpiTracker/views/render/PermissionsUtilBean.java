package org.pahappa.systems.kpiTracker.views.render;

import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 * This bean makes the permission constants available to the JSF Expression Language (EL).
 * It is ApplicationScoped so it's created only once for the entire application.
 */
// RENAMED THE BEAN TO AVOID NAME COLLISIONS
@ManagedBean(name = "permissions")
@ApplicationScoped
public class PermissionsUtilBean {

    public String getPERM_VIEW_USERS() {
        return PermissionConstants.PERM_VIEW_USERS;
    }

    public String getPERM_MANAGE_USERS() {
        return PermissionConstants.PERM_MANAGE_USERS;
    }

    public String getPERM_VIEW_DEPARTMENTS() {
        return PermissionConstants.PERM_VIEW_DEPARTMENTS;
    }

    public String getPERM_MANAGE_DEPARTMENTS() {
        return PermissionConstants.PERM_MANAGE_DEPARTMENTS;
    }

    public String getPERM_VIEW_TEAMS() {
        return PermissionConstants.PERM_VIEW_TEAMS;
    }

    public String getPERM_MANAGE_TEAMS() {
        return PermissionConstants.PERM_MANAGE_TEAMS;
    }

    public String getPERM_VIEW_GOALS() {
        return PermissionConstants.PERM_VIEW_GOALS;
    }

    public String getPERM_MANAGE_GOALS() {
        return PermissionConstants.PERM_MANAGE_GOALS;
    }

    public String getPERM_VIEW_KPIS() {
        return PermissionConstants.PERM_VIEW_KPIS;
    }

    public String getPERM_MANAGE_KPIS() {
        return PermissionConstants.PERM_MANAGE_KPIS;
    }

    public String getPERM_VIEW_ACTIVITIES() {
        return PermissionConstants.PERM_VIEW_ACTIVITIES;
    }

    public String getPERM_MANAGE_ACTIVITIES() {
        return PermissionConstants.PERM_MANAGE_ACTIVITIES;
    }

    public String getPERM_PERFORM_PEER_RATING() {
        return PermissionConstants.PERM_PERFORM_PEER_RATING;
    }
}