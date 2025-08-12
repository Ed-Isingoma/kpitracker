package org.pahappa.systems.kpiTracker.constants;

public enum ActivityStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    ONGOING("Ongoing"),
    COMPLETED("Completed"),
    REJECTED("Rejected");

    private final String name;

    ActivityStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}