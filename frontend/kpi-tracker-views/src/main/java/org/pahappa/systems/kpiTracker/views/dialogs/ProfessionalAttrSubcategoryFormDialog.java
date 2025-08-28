package org.pahappa.systems.kpiTracker.views.dialogs;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.ProfessionalAttrSubcategoryService;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrCategory;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrSubcategory;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;


@Getter
@Setter
public class ProfessionalAttrSubcategoryFormDialog implements Serializable {

    private static final long serialVersionUID = 1L;
    private ProfessionalAttrSubcategory model;
    private ProfessionalAttrSubcategoryService subcategoryService;
    private String updateTarget;

    public ProfessionalAttrSubcategoryFormDialog() {
        this.subcategoryService = ApplicationContextProvider.getBean(ProfessionalAttrSubcategoryService.class);
        this.model = new ProfessionalAttrSubcategory();
    }

    public void prepareNewProfessionalAttr(ProfessionalAttrCategory parentAttribute) {
        this.model = new ProfessionalAttrSubcategory();
        this.model.setProfessionalAttrCategory(parentAttribute);
    }

    public void save() {
        try {
            if (this.model.getDescription() == null || this.model.getDescription().trim().isEmpty()) {
                throw new RuntimeException("Description is required.");
            }
            if (this.model.getMaximumValue() <= 0) {
                throw new RuntimeException("Maximum Value must be a positive number.");
            }

            subcategoryService.saveInstance(this.model);

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Sub-Category saved successfully.");
            FacesContext.getCurrentInstance().addMessage(null, message);

        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
}