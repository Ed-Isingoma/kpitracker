package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.GoalDao;
import org.pahappa.systems.kpiTracker.models.Goal;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("goalDao")
public class GoalDaoImpl extends BaseDAOImpl<Goal> implements GoalDao {
}