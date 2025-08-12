package org.pahappa.systems.kpiTracker.models;

import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;

@Entity
@Table(name = "departments")
public class Department extends BaseEntity {

    private String name;
    private String description;
    private User departmentLead;

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
    @JoinColumn(name = "department_lead_id")
    public User getDepartmentLead() {
        return departmentLead;
    }

    public void setDepartmentLead(User departmentLead) {
        this.departmentLead = departmentLead;
    }

    @Override
    public String toString() {
        return this.name;
    }
}