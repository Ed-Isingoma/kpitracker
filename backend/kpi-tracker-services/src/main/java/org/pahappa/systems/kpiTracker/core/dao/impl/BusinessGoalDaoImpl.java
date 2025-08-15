package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.BusinessGoalDao;
import org.pahappa.systems.kpiTracker.models.BusinessGoal;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("businessGoalDao")
public class BusinessGoalDaoImpl extends BaseDAOImpl<BusinessGoal> implements BusinessGoalDao {
}