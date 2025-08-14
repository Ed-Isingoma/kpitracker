package org.pahappa.systems.kpiTracker.models.security;

import org.sers.webutils.model.security.PermissionAnnotation;
public final class PermissionConstants {
    private PermissionConstants() {
    }

    @SystemPermission(name = "Api user", description = "Has role for api users")
    public static final String PERM_API_USER = "Api User";

    // KTS_UC_01.0: Manage Users (Added for our current task)
    @PermissionAnnotation(id = "A1B2C3D4-E5F6-7890-1234-567890ABCDEF", name = "View Users", description = "Can view the list of system users")
    public static final String PERM_VIEW_USERS = "perm_view_users";
    @PermissionAnnotation(id = "B2C3D4E5-F6A7-8901-2345-67890ABCDEF0", name = "Manage Users", description = "Can create, edit, and delete system users")
    public static final String PERM_MANAGE_USERS = "perm_manage_users";

    // KTS_UC_02.0: Manage Departments
    @PermissionAnnotation(id = "4E62A933-C2A2-4328-8B4A-3A9A4B7D2B0F", name = "View Departments", description = "Can view the list of departments")
    public static final String PERM_VIEW_DEPARTMENTS = "perm_view_departments";
    @PermissionAnnotation(id = "B8E8E8E8-C2A2-4328-8B4A-3A9A4B7D2B0F", name = "Manage Departments", description = "Can create, edit, and delete departments")
    public static final String PERM_MANAGE_DEPARTMENTS = "perm_manage_departments";

    // KTS_UC_03.0: Manage Teams
    @PermissionAnnotation(id = "C1F3B4A5-D6E7-4890-9123-F4G5H6I7J8K9", name = "View Teams", description = "Can view the list of teams")
    public static final String PERM_VIEW_TEAMS = "perm_view_teams";
    @PermissionAnnotation(id = "D2G4C5B6-E7F8-4901-A234-G5H6I7J8K9L0", name = "Manage Teams", description = "Can create, edit, and delete teams")
    public static final String PERM_MANAGE_TEAMS = "perm_manage_teams";

    // KTS_UC_05.0: Manage Goals
    @PermissionAnnotation(id = "E3H5D6C7-F8G9-4A12-B345-H6I7J8K9L0M1", name = "View Goals", description = "Can view the list of goals")
    public static final String PERM_VIEW_GOALS = "perm_view_goals";
    @PermissionAnnotation(id = "F4I6E7D8-G9H0-4B23-C456-I7J8K9L0M1N2", name = "Manage Goals", description = "Can create, edit, and delete goals")
    public static final String PERM_MANAGE_GOALS = "perm_manage_goals";

    // KTS_UC_09.0: Manage KPIs
    @PermissionAnnotation(id = "G5J7F8E9-H0I1-4C34-D567-J8K9L0M1N2O3", name = "View KPIs", description = "Can view the list of KPIs")
    public static final String PERM_VIEW_KPIS = "perm_view_kpis";
    @PermissionAnnotation(id = "H6K8G9F0-I1J2-4D45-E678-K9L0M1N2O3P4", name = "Manage KPIs", description = "Can create, edit, and delete KPIs")
    public static final String PERM_MANAGE_KPIS = "perm_manage_kpis";

    // KTS_UC_08.0: Manage Activities
    @PermissionAnnotation(id = "I7L9H0G1-J2K3-4E56-F789-L0M1N2O3P4Q5", name = "View Activities", description = "Can view the list of activities")
    public static final String PERM_VIEW_ACTIVITIES = "perm_view_activities";
    @PermissionAnnotation(id = "J8M0I1H2-K3L4-4F67-G890-M1N2O3P4Q5R6", name = "Manage Activities", description = "Can create, edit, and delete activities")
    public static final String PERM_MANAGE_ACTIVITIES = "perm_manage_activities";

    // KTS_UC_13.0: Peer Rating
    @PermissionAnnotation(id = "K9N1J2I3-L4M5-4G78-H901-N2O3P4Q5R6S7", name = "Perform Peer Rating", description = "Can rate peers on professional attributes")
    public static final String PERM_PERFORM_PEER_RATING = "perm_perform_peer_rating";
}