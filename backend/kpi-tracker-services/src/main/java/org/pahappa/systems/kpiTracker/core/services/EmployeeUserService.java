package org.pahappa.systems.kpiTracker.core.services;
import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.sers.webutils.server.core.service.UserService;

public interface EmployeeUserService extends UserService, GenericService<EmployeeUser> {
}
