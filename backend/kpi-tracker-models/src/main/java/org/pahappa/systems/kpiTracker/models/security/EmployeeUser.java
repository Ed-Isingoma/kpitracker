package org.pahappa.systems.kpiTracker.models.security;

import org.pahappa.systems.kpiTracker.models.Department;
import org.pahappa.systems.kpiTracker.models.Team;
import org.sers.webutils.model.security.User;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "employee_users")
public class EmployeeUser extends User {

    private Department department;
    private Team team;

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
}