package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.BusinessGoalDepartmentAssignmentDao;
import org.pahappa.systems.kpiTracker.models.BusinessGoalDepartmentAssignment;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("businessGoalDepartmentAssignmentDao")
public class BusinessGoalDepartmentAssignmentDaoImpl extends BaseDAOImpl<BusinessGoalDepartmentAssignment> implements BusinessGoalDepartmentAssignmentDao {
}