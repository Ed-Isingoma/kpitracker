package org.pahappa.systems.kpiTracker.core.services;
import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.sers.webutils.server.core.service.UserService;

import java.util.List;

public interface EmployeeUserService extends UserService, GenericService<EmployeeUser> {
    List<EmployeeUser> getEmployeesInDepartment(Department department);
}
