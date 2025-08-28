package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.GoalCycle;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrCategory;

import java.util.List;

public interface ProfessionalAttrCategoryService extends GenericService<ProfessionalAttrCategory> {
    List<ProfessionalAttrCategory> getAttributesForCycle(GoalCycle goalCycle);
}
