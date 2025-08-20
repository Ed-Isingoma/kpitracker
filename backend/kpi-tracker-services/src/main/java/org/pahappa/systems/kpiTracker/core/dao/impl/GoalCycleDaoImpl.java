package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.GoalCycleDao;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.springframework.stereotype.Repository;

@Repository("goalCycleDao")
public class GoalCycleDaoImpl extends BaseDAOImpl<GoalCycle> implements GoalCycleDao {
}