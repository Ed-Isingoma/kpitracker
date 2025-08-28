package org.pahappa.systems.kpiTracker.views.professionalAttrs;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.ProfessionalAttrCategoryService;
import org.pahappa.systems.kpiTracker.core.services.ProfessionalAttrSubcategoryService;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrCategory;
import org.pahappa.systems.kpiTracker.models.ProfessionalAttrSubcategory;
import org.pahappa.systems.kpiTracker.views.dialogs.ProfessionalAttrSubcategoryFormDialog;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ManagedBean(name = "professionalAttrSubcategoryView")
@ViewScoped
@Getter
@Setter
@ViewPath(path = "/pages/admin/goals/professionalAttrDetailView.xhtml")
public class ProfessionalAttrSubcategoryView implements Serializable {

    private static final long serialVersionUID = 1L;

    private ProfessionalAttrCategoryService professionalAttrCategoryService;
    private ProfessionalAttrSubcategoryService professionalAttrSubcategoryService;

    private String attrId;
    private ProfessionalAttrCategory selectedAttribute;

    private List<ProfessionalAttrSubcategory> subCategories;

    @ManagedProperty(value = "#{professionalAttrSubcategoryFormDialog}")
    private ProfessionalAttrSubcategoryFormDialog subcategoryFormDialog;

    @PostConstruct
    public void init() {
        this.professionalAttrSubcategoryService = ApplicationContextProvider.getBean(ProfessionalAttrSubcategoryService.class);
        this.professionalAttrCategoryService = ApplicationContextProvider.getBean(ProfessionalAttrCategoryService.class);
        this.subCategories = Collections.emptyList();
    }

    /**
     * This method is called by the <f:viewAction> on the page.
     * It uses the attrId passed in the URL to load the correct attribute.
     */
    public void loadAttribute() {
        if (attrId != null) {
            this.selectedAttribute = professionalAttrCategoryService.getInstanceByID(attrId);
            if (this.selectedAttribute != null) {
                // Future logic to load sub-categories will go here.
                // e.g., this.subCategories = subCategoryService.getSubCategoriesFor(this.selectedAttribute);
            }
        }
    }

    public void loadData() {
        if (attrId != null) {
            this.selectedAttribute = professionalAttrCategoryService.getInstanceByID(attrId);
            if (this.selectedAttribute != null) {
                // Now, load the sub-categories for the selected attribute
                this.subCategories = professionalAttrSubcategoryService.getSubcategoriesForAttribute(this.selectedAttribute);
            }
        }
    }

    public void prepareNewSubcategory() {
        if (this.selectedAttribute != null) {
            subcategoryFormDialog.prepareNewProfessionalAttr(this.selectedAttribute);
            subcategoryFormDialog.setUpdateTarget(":subcategoryForm:subcategoriesTable");
        }
    }
}