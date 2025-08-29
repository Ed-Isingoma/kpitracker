package org.pahappa.systems.kpiTracker.models;

import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "kpis")
public class KPI extends BaseEntity {

    private String title;
    private String measure;
    private boolean achieved;
    private int weight;
    private User owner;
    private GoalStatus status = GoalStatus.PENDING;
    private Goal goal;

    @ManyToOne
    @JoinColumn(name="goal", nullable = false)
    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "weight")
    public int getWeight() {
        return weight;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @ManyToOne
    @JoinColumn(name = "owner_id")
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.title;
    }
}