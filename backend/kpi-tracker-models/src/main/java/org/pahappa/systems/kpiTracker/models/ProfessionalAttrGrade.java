package org.pahappa.systems.kpiTracker.models;

import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "professional_attr_grade")
public class ProfessionalAttrGrade extends BaseEntity {
    private User evaluatorUser;
    private User evaluatedUser;
    private ProfessionalAttrSubcategory professionalAttrSubcategory;
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @OneToOne
    @JoinColumn(name="professional_attr_subcategory", nullable=false)
    public ProfessionalAttrSubcategory getProfessionalAttrSubcategory() {
        return professionalAttrSubcategory;
    }

    public void setProfessionalAttrSubcategory(ProfessionalAttrSubcategory professionalAttrSubcategory) {
        this.professionalAttrSubcategory = professionalAttrSubcategory;
    }

    @OneToOne
    @JoinColumn(name = "evaluator_user", nullable = false)
    public User getEvaluatorUser() {
        return evaluatorUser;
    }

    public void setEvaluatorUser(User evaluatorUser) {
        this.evaluatorUser = evaluatorUser;
    }

    @OneToOne
    @JoinColumn(name = "evaluated_user", nullable = false)
    public User getEvaluatedUser() {
        return evaluatedUser;
    }

    public void setEvaluatedUser(User evaluatedUser) {
        this.evaluatedUser = evaluatedUser;
    }

}
