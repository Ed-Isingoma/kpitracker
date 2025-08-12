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
    private double targetValue;
    private double actualValue;
    private BusinessGoal businessGoal;
    private int weight;
    private User owner; // Simplified to User for now. Can be extended.
    private GoalStatus status = GoalStatus.PENDING;
    private String dataSource;
    private String reportingFrequency;
    private Date dueDate;

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "measure")
    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    @Column(name = "target_value")
    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    @Column(name = "actual_value")
    public double getActualValue() {
        return actualValue;
    }

    public void setActualValue(double actualValue) {
        this.actualValue = actualValue;
    }

    @ManyToOne
    @JoinColumn(name = "business_goal_id", nullable = false)
    public BusinessGoal getBusinessGoal() {
        return businessGoal;
    }

    public void setBusinessGoal(BusinessGoal businessGoal) {
        this.businessGoal = businessGoal;
    }

    @Column(name = "weight")
    public int getWeight() {
        return weight;
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

    @Column(name = "data_source")
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @Column(name = "reporting_frequency")
    public String getReportingFrequency() {
        return reportingFrequency;
    }

    public void setReportingFrequency(String reportingFrequency) {
        this.reportingFrequency = reportingFrequency;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "due_date")
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return this.title;
    }
}