package org.pahappa.systems.kpiTracker.models;

import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;

@Entity
@Table(name = "teams")
public class Team extends BaseEntity {

    private String name;
    private String description;
    private EmployeeUser teamLead;
    private Department department;

    @Column(name = "name", nullable = false)
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
    @JoinColumn(name = "team_lead_id", nullable = true)
    public EmployeeUser getTeamLead() {
        return teamLead;
    }

    public void setTeamLead(EmployeeUser teamLead) {
        this.teamLead = teamLead;
    }

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return this.name;
    }
}