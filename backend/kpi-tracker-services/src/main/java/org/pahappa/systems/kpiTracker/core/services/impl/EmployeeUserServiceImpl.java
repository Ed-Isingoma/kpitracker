package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.apache.commons.lang.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.security.util.CustomSecurityUtil;
import org.sers.webutils.server.core.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("employeeUserService")
@Transactional
@Primary
public class EmployeeUserServiceImpl extends UserServiceImpl implements EmployeeUserService {

    // Define a constant for the default password
    private static final String DEFAULT_PASSWORD = "default@123";

    @Override
    @Transactional
    public User saveUser(User user) throws ValidationFailedException {
        if (user instanceof EmployeeUser) {
            EmployeeUser employee = (EmployeeUser) user;

            // Set username to be the email address for login
            employee.setUsername(employee.getEmailAddress());

            // --- CHANGE START: Set default password for new users ---
            if (employee.isNew()) {
                employee.setClearTextPassword(DEFAULT_PASSWORD);
            }
            // --- CHANGE END ---

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
        Search userSearch = new Search(User.class);
        if (search != null) {
            userSearch.setFilters(search.getFilters());
            userSearch.setSorts(search.getSorts());
            userSearch.setDisjunction(search.isDisjunction());
            userSearch.setResultMode(search.getResultMode());
            userSearch.setFetches(search.getFetches());
            userSearch.setPage(search.getPage());
        }

        List<User> users = super.getUsers(userSearch, offset, limit);
        List<EmployeeUser> employeeUsers = new ArrayList<>();
        for (User user : users) {
            if (user instanceof EmployeeUser) {
                employeeUsers.add((EmployeeUser) user);
            }
        }
        return employeeUsers;
    }

    @Override
    public EmployeeUser getInstanceByID(String id) {
        User user = super.getUserById(id);
        if (user instanceof EmployeeUser) {
            return (EmployeeUser) user;
        }
        return null;
    }

    @Override
    public int countInstances(Search search) {
        Search userSearch = new Search(User.class);
        if (search != null) {
            userSearch.setFilters(search.getFilters());
        }
        return super.countUsers(userSearch);
    }

    @Override
    public void deleteInstance(EmployeeUser instance) throws OperationFailedException {
        super.deleteUser(instance);
    }

    @Override
    public List<EmployeeUser> getAllInstances() {
        Search search = new Search(User.class);
        List<User> users = super.getUsers(search, 0, Integer.MAX_VALUE);

        List<EmployeeUser> employeeUsers = new ArrayList<>();
        for (User user : users) {
            if (user instanceof EmployeeUser) {
                employeeUsers.add((EmployeeUser) user);
            }
        }
        return employeeUsers;
    }
}