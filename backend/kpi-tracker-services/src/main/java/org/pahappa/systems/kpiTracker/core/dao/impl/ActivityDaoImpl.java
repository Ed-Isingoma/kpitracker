package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.ActivityDao;
import org.pahappa.systems.kpiTracker.models.Activity;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("activityDao")
public class ActivityDaoImpl extends BaseDAOImpl<Activity> implements ActivityDao {
}