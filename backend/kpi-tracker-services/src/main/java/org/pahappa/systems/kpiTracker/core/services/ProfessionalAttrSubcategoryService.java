package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.core.services.base.GenericService;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrCategory;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrSubcategory;

import java.util.List;

public interface ProfessionalAttrSubcategoryService extends GenericService<ProfessionalAttrSubcategory> {

    List<ProfessionalAttrSubcategory> getSubcategoriesForAttribute(ProfessionalAttrCategory attribute);
}
