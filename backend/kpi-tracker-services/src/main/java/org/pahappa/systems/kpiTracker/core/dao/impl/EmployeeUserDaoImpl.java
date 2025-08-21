package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.EmployeeUserDao;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.springframework.stereotype.Repository;

@Repository("employeeUserDao")
public class EmployeeUserDaoImpl extends BaseDAOImpl<EmployeeUser> implements EmployeeUserDao {
}