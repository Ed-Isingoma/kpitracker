package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.ActivityReportDao;
import org.pahappa.systems.kpiTracker.models.ActivityReport;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("activityReportDao")
public class ActivityReportDaoImpl extends BaseDAOImpl<ActivityReport> implements ActivityReportDao {
}