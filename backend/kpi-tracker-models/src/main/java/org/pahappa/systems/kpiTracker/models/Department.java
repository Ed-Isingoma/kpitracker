package org.pahappa.systems.kpiTracker.models;

import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;

@Entity
@Table(name = "departments")
public class Department extends BaseEntity {

    private String name;
    private String description;
    private EmployeeUser departmentLead;

    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name = "department_lead_id", nullable = true)
    public EmployeeUser getDepartmentLead() {
        return departmentLead;
    }

    public void setDepartmentLead(EmployeeUser departmentLead) {
        this.departmentLead = departmentLead;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Department && (super.getId() != null) ? super.getId().equals(((Department) object).getId())
                : (object == this);
    }

    @Override
    public int hashCode() {
        return super.getId() != null ? this.getClass().hashCode() + super.getId().hashCode() : super.hashCode();
    }
}
