package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.dao.DepartmentDao;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.core.services.impl.base.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service("departmentService")
@Transactional
public class DepartmentServiceImpl extends GenericServiceImpl<Department> implements DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;


//    @Override
    public boolean isDeletable(Department instance) throws OperationFailedException {
        // To be implemented: Check if department has users or teams.
        // For now, we allow deletion.
        // Example check:
        // Search userSearch = new Search(User.class).addFilterEqual("department", instance);
        // if(userService.count(userSearch) > 0) {
        //     throw new OperationFailedException("Cannot delete department with assigned users.");
        // }
        return true;
    }

    public void saveAndHandleLeadRoles(Department department) throws ValidationFailedException, OperationFailedException {
        RoleService roleService = ApplicationContextProvider.getBean(RoleService.class);
        EmployeeUserService employeeUserService = ApplicationContextProvider.getBean(EmployeeUserService.class);

        final String LEAD_ROLE_NAME = "Department Lead";
        Role departmentLeadRole = roleService.getRoleByName(LEAD_ROLE_NAME);

        if (departmentLeadRole == null) {
            throw new OperationFailedException("'" + LEAD_ROLE_NAME + "' role was not found in the database. Please create it.");
        }

        EmployeeUser newLead = department.getDepartmentLead();
        EmployeeUser oldLead = null;

        if (department.isSaved()) {
            Department existingDepartment = this.departmentDao.find(department.getId());
            if (existingDepartment != null) {
                oldLead = existingDepartment.getDepartmentLead();
            }
        }

        if (!Objects.equals(oldLead, newLead)) {

            if (oldLead != null) {
                oldLead.getRoles().remove(departmentLeadRole);
                employeeUserService.saveInstance(oldLead);
            }
            if (newLead != null) {
                newLead.getRoles().add(departmentLeadRole);
                employeeUserService.saveInstance(newLead);
            }
        }

        this.saveInstance(department);

    }

//    @Override
    public Department saveInstance(Department department) throws ValidationFailedException, OperationFailedException {

        Validate.notNull(department, "Department details are required");
        Validate.hasText(department.getName(), "Department name is required");

        // Ensure name is unique
        Search search = new Search().addFilterEqual("name", department.getName());
        if (department.isSaved()) {
            search.addFilterNotEqual("id", department.getId());
        }
        if (departmentDao.count(search) > 0) {
            throw new ValidationFailedException("A department with this name already exists.");
        }

        return super.save(department);
    }

    @Override
    public void deleteDepartment(Department department) throws OperationFailedException, ValidationFailedException {
        Validate.notNull(department, "Department to delete cannot be null");

        if (!isDeletable(department)) {
            throw new OperationFailedException("Cannot delete a department that has active teams assigned to it.");
        }

        department.setRecordStatus(RecordStatus.DELETED);
        super.save(department);
    }
}