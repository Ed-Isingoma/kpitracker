package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.DepartmentDao;
import org.pahappa.systems.kpiTracker.models.Department;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("departmentDao")
public class DepartmentDaoImpl extends BaseDAOImpl<Department> implements DepartmentDao {
}