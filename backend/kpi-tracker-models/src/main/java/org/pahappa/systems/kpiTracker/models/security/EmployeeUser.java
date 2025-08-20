package org.pahappa.systems.kpiTracker.models.security;

import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Team;
import org.sers.webutils.model.security.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "employee_users")
public class EmployeeUser extends User {

    private Department department;
    private Team team;
    private String fullName; // New persistent field
    private String designation; // New persistent field

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = true)
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Column(name = "full_name")
    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "designation")
    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * Override the transient getter from the parent to ensure it returns the correct full name.
     * And also ensure the parent's logic doesn't break.
     */
    @Override
    @Transient
    public String toString() {
        if (this.fullName != null && !this.fullName.isEmpty()) {
            return this.fullName;
        }
        // Fallback to the parent's implementation if fullName is not set
        return super.toString();
    }
}