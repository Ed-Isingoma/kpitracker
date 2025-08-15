package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;
import org.pahappa.systems.kpiTracker.models.security.PermissionInterpreter;
import org.pahappa.systems.kpiTracker.models.security.RoleConstants;
import org.pahappa.systems.kpiTracker.models.security.RolesInterpreter;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.migrations.Migration;
import org.sers.webutils.model.security.Permission;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.server.core.dao.PermissionDao;
import org.sers.webutils.server.core.dao.RoleDao;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.shared.CustomLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CustomPermissionRoleMigrations {

	@Autowired
	PermissionDao permissionDao;

	@Autowired
	RoleDao roleDao;

	@Autowired
	RoleService roleService;

	@Migration(orderNumber = 1)
	public void setupPermissionsAndRoles() {
		// Step 1: Save all permissions from the interpreter
		for (Permission permission : PermissionInterpreter.reflectivelyGetPermissions()) {
			if (permissionDao.searchUniqueByPropertyEqual("name", permission.getName()) == null) {
				try {
					permission.setRecordStatus(RecordStatus.ACTIVE);
					permission.setDateCreated(new Date());
					permission.setDateChanged(new Date());
					permissionDao.mergeBG(permission);
					CustomLogger.log(getClass(), "Saved Permission: " + permission.getName());
				} catch (Exception exe) {
					CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR, "Failed to save permission '" + permission.getName() + "': " + exe.getMessage());
				}
			}
		}

		// Step 2: Save all roles from the interpreter
		for (Role role : RolesInterpreter.reflectivelyGetRoles()) {
			if (roleDao.searchUniqueByPropertyEqual("name", role.getName()) == null) {
				try {
					role.setRecordStatus(RecordStatus.ACTIVE);
					role.setDateCreated(new Date());
					role.setDateChanged(new Date());
					roleDao.mergeBG(role);
					CustomLogger.log(getClass(), "Saved Role: " + role.getName());
				} catch (Exception exe) {
					CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR, "Failed to save role '" + role.getName() + "': " + exe.getMessage());
				}
			}
		}

		// Step 3: Assign permissions to roles
		assignPermissionsToRoles();
	}

	private void assignPermissionsToRoles() {
		try {
			// CEO/Admin Role gets ALL permissions
			Role ceoAdminRole = roleService.getRoleByName(RoleConstants.CEO_ADMIN_ROLE);
			if (ceoAdminRole != null) {
				ceoAdminRole.setPermissions(new HashSet<>(permissionDao.findAll()));
				roleService.saveRole(ceoAdminRole);
			}

			// HR Role
			Role hrRole = roleService.getRoleByName(RoleConstants.HR_ROLE);
			if (hrRole != null) {
				hrRole.setPermissions(getPermissions(
						PermissionConstants.PERM_VIEW_USERS, PermissionConstants.PERM_MANAGE_USERS,
						PermissionConstants.PERM_VIEW_DEPARTMENTS, PermissionConstants.PERM_MANAGE_DEPARTMENTS,
						PermissionConstants.PERM_VIEW_TEAMS, PermissionConstants.PERM_MANAGE_TEAMS
				));
				roleService.saveRole(hrRole);
			}

			// Department Lead Role
			Role deptLeadRole = roleService.getRoleByName(RoleConstants.DEPARTMENT_LEAD_ROLE);
			if (deptLeadRole != null) {
				deptLeadRole.setPermissions(getPermissions(
						PermissionConstants.PERM_VIEW_TEAMS, PermissionConstants.PERM_MANAGE_TEAMS,
						PermissionConstants.PERM_VIEW_GOALS, PermissionConstants.PERM_VIEW_KPIS,
						PermissionConstants.PERM_VIEW_ACTIVITIES, PermissionConstants.PERM_PERFORM_PEER_RATING
				));
				roleService.saveRole(deptLeadRole);
			}

			// Team Lead Role
			Role teamLeadRole = roleService.getRoleByName(RoleConstants.TEAM_LEAD_ROLE);
			if (teamLeadRole != null) {
				teamLeadRole.setPermissions(getPermissions(
						PermissionConstants.PERM_VIEW_ACTIVITIES, PermissionConstants.PERM_MANAGE_ACTIVITIES,
						PermissionConstants.PERM_PERFORM_PEER_RATING
				));
				roleService.saveRole(teamLeadRole);
			}

			// Individual Role
			Role individualRole = roleService.getRoleByName(RoleConstants.INDIVIDUAL_ROLE);
			if (individualRole != null) {
				individualRole.setPermissions(getPermissions(
						PermissionConstants.PERM_VIEW_ACTIVITIES, PermissionConstants.PERM_PERFORM_PEER_RATING
				));
				roleService.saveRole(individualRole);
			}

			CustomLogger.log(getClass(), "Successfully assigned permissions to roles.");
		} catch (Exception e) {
			CustomLogger.log(getClass(), CustomLogger.LogSeverity.LEVEL_ERROR, "Error assigning permissions to roles: " + e.getMessage());
		}
	}

	private Set<Permission> getPermissions(String... names) {
		Set<Permission> permissions = new HashSet<>();
		for (String name : names) {
			Permission perm = permissionDao.searchUniqueByPropertyEqual("name", name);
			if (perm != null) {
				permissions.add(perm);
			}
		}
		return permissions;
	}
}