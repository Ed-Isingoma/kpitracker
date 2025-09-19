package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.dao.EmployeeUserDao;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Team;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.security.util.CustomSecurityUtil;
import org.sers.webutils.server.core.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("employeeUserService")
@Transactional
@Primary
public class EmployeeUserServiceImpl extends UserServiceImpl implements EmployeeUserService {

    @Autowired
    private EmployeeUserDao employeeUserDao;

    // Define a constant for the default password
    private static final String DEFAULT_PASSWORD = "default@123";

    @Override
    @Transactional
    @PreAuthorize("hasPermission(null, ' " + PermissionConstants.PERM_MANAGE_USERS + " ')")
    public User saveUser(User user) throws ValidationFailedException {
        if (user instanceof EmployeeUser) {
            EmployeeUser employee = (EmployeeUser) user;

            employee.setUsername(employee.getEmailAddress());

            if (employee.isNew()) {
                employee.setClearTextPassword(DEFAULT_PASSWORD);
            }

            this.validateUser(employee);

            // This utility handles hashing the clearTextPassword if it's present
            CustomSecurityUtil.prepUserCredentials(employee);

            return super.getUserDAO().save(employee);
        } else {
            // Fallback for generic User
            super.validateUser(user);
            CustomSecurityUtil.prepUserCredentials(user);
            return super.getUserDAO().save(user);
        }
    }

    public EmployeeUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) {
            return null;
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return (EmployeeUser) super.getUserDAO()
                .searchUniqueByPropertyEqual("username", userDetails.getUsername());
    }

    @Override
    public void validateUser(User user) throws ValidationFailedException {
        if (user instanceof EmployeeUser) {
            EmployeeUser employee = (EmployeeUser) user;
            Validate.hasText(employee.getFullName(), "Full name is required.");
            Validate.hasText(employee.getEmailAddress(), "Email address is required.");

            User existingUser = super.findUserByUsername(employee.getEmailAddress());
            if (existingUser != null && !existingUser.getId().equals(employee.getId())) {
                throw new ValidationFailedException("A user with this email address already exists.");
            }

            // --- CHANGE START: Removed password validation for new users ---
            // The password is now set automatically, so this check is no longer needed.
            // --- CHANGE END ---

        } else {
            super.validateUser(user);
        }
    }

    @Override
    public EmployeeUser saveInstance(EmployeeUser entityInstance) throws ValidationFailedException, OperationFailedException {
        return (EmployeeUser) this.saveUser(entityInstance);
    }

    @Override
    public List<EmployeeUser> getInstances(Search search, int offset, int limit) {
        // Use the correct DAO for EmployeeUser
        return employeeUserDao.search(search.setFirstResult(offset).setMaxResults(limit));
    }

    @Override
    public EmployeeUser getInstanceByID(String id) {
        return employeeUserDao.find(id);
    }

    @Override
    public int countInstances(Search search) {
        // This is the crucial fix. It uses the DAO that understands EmployeeUser.
        return employeeUserDao.count(search);
    }


    @Override

    @PreAuthorize("hasPermission(null, ' " + PermissionConstants.PERM_MANAGE_USERS + " ')")
    public void deleteInstance(EmployeeUser instance) throws OperationFailedException {
        super.deleteUser(instance);
    }

    @Override
    public List<EmployeeUser> getAllInstances() {
        return employeeUserDao.findAll();
    }

    @Override
    public List<EmployeeUser> getEmployeesInDepartment(Department department) {
        if (department == null) {
            return new java.util.ArrayList<>();
        }
        return employeeUserDao.search(new Search().addFilterEqual("department", department));
    }

    @Override
    public List<EmployeeUser> getEmployeesInTeam(Team team) {
        if (team == null) {
            return new java.util.ArrayList<>();
        }
        return employeeUserDao.search(new Search().addFilterEqual("team", team));
    }
}