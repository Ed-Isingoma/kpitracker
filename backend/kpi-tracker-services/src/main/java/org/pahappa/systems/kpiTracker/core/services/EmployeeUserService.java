package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.sers.webutils.server.core.service.UserService;

import java.util.List;

public interface EmployeeUserService extends UserService, GenericService<EmployeeUser> {
    /**
     * Retrieves a list of all employees assigned to a specific department.
     * @param department The department to search for.
     * @return A list of EmployeeUser objects.
     */
    List<EmployeeUser> getEmployeesInDepartment(Department department);

    /**
     * Retrieves a list of all employees assigned to a specific team.
     * @param team The team to search for.
     * @return A list of EmployeeUser objects.
     */
    List<EmployeeUser> getEmployeesInTeam(Team team);

    EmployeeUser getCurrentUser();
}