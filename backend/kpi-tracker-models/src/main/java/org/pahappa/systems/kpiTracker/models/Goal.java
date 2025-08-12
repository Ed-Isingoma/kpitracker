package org.pahappa.systems.kpiTracker.models;

import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.pahappa.systems.kpiTracker.constants.Timeframe;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "goals")
public class Goal extends BaseEntity {

    private String title;
    private String description;
    private Date startDate;
    private Timeframe timeframe;
    private GoalStatus status = GoalStatus.PENDING;
    private int businessGoalWeight;
    private int professionalAttributesWeight;

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "description", length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "timeframe")
    public Timeframe getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(Timeframe timeframe) {
        this.timeframe = timeframe;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    @Column(name = "business_goal_weight")
    public int getBusinessGoalWeight() {
        return businessGoalWeight;
    }

    public void setBusinessGoalWeight(int businessGoalWeight) {
        this.businessGoalWeight = businessGoalWeight;
    }

    @Column(name = "professional_attributes_weight")
    public int getProfessionalAttributesWeight() {
        return professionalAttributesWeight;
    }

    public void setProfessionalAttributesWeight(int professionalAttributesWeight) {
        this.professionalAttributesWeight = professionalAttributesWeight;
    }

    @Override
    public String toString() {
        return this.title;
    }
}