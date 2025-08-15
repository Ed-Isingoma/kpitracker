package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.KPIDao;
import org.pahappa.systems.kpiTracker.models.KPI;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("kpiDao")
public class KPIDaoImpl extends BaseDAOImpl<KPI> implements KPIDao {
}