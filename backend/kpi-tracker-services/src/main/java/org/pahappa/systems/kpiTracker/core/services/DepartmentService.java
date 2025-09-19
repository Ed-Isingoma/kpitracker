package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;

/**
 * Service for managing Department entities.
 */
public interface DepartmentService extends GenericService<Department> {
    /**
     * Performs a soft delete on the given department after ensuring it has no active teams.
     * @param department The department to delete.
     * @throws OperationFailedException if the department still has teams.
     */
    void saveAndHandleLeadRoles(Department department) throws ValidationFailedException, OperationFailedException;

    void deleteDepartment(Department department) throws OperationFailedException, ValidationFailedException;
}