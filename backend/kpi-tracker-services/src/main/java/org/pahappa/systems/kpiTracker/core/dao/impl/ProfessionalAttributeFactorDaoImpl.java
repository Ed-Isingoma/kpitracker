package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.ProfessionalAttributeFactorDao;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttributeFactor;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.springframework.stereotype.Repository;

@Repository("professionalAttributeFactorDao")
public class ProfessionalAttributeFactorDaoImpl extends BaseDAOImpl<ProfessionalAttributeFactor> implements ProfessionalAttributeFactorDao {
}