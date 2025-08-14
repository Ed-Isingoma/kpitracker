package org.pahappa.systems.kpiTracker.models.security;

public final class RoleConstants {
	private RoleConstants() {
	}

	@SystemRole(name = "Api user", description = "Has role for api users")
	public static final String ROLE_API_USER = "Api User";

	@SystemRole(name = "CEO/Admin", description = "System Administrator with all privileges")
	public static final String CEO_ADMIN_ROLE = "CEO/Admin";

	@SystemRole(name = "Human Resource", description = "Human Resource role for managing users and organization structure.")
	public static final String HR_ROLE = "Human Resource";

	@SystemRole(name = "Department Lead", description = "Lead for a department.")
	public static final String DEPARTMENT_LEAD_ROLE = "Department Lead";

	@SystemRole(name = "Team Lead", description = "Lead for a team.")
	public static final String TEAM_LEAD_ROLE = "Team Lead";

	@SystemRole(name = "Individual Contributor", description = "An individual contributor/employee.")
	public static final String INDIVIDUAL_ROLE = "Individual Contributor";
}