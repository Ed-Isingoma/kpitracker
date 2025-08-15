package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.dao.DepartmentDao;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}