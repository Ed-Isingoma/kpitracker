package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
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

    @Override
    @Transactional
    public User saveUser(User user) throws ValidationFailedException {
        super.validateUser(user);
        CustomSecurityUtil.prepUserCredentials(user);
        return super.getUserDAO().save(user);
    }

    @Override
    public EmployeeUser saveInstance(EmployeeUser entityInstance) throws ValidationFailedException, OperationFailedException {
        return (EmployeeUser) this.saveUser(entityInstance);
    }

    @Override
    public List<EmployeeUser> getInstances(Search search, int offset, int limit) {
        // Use the base User class for the search to match the DAO's expectation.
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
        // Use the base User class for the search to match the DAO's expectation.
        Search userSearch = new Search(User.class);
        if (search != null) {
            userSearch.setFilters(search.getFilters());
        }
        // Note: This might over-count if there are other User types and filters are not specific enough.
        // This is a limitation of the current service structure.
        return super.countUsers(userSearch);
    }

    @Override
    public void deleteInstance(EmployeeUser instance) throws OperationFailedException {
        super.deleteUser(instance);
    }

    @Override
    public List<EmployeeUser> getAllInstances() {
        // Create a search for the base User class, as expected by the inherited DAO.
        Search search = new Search(User.class);
        List<User> users = super.getUsers(search, 0, Integer.MAX_VALUE);

        // Filter the results in memory to return only EmployeeUser instances.
        List<EmployeeUser> employeeUsers = new ArrayList<>();
        for (User user : users) {
            if (user instanceof EmployeeUser) {
                employeeUsers.add((EmployeeUser) user);
            }
        }
        return employeeUsers;
    }
}