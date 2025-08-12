package org.pahappa.systems.kpiTracker.constants;

public enum GoalType {
    BUSINESS_GOAL("Business Goal"),
    PROFESSIONAL_ATTRIBUTES("Professional Attributes");

    private final String name;

    GoalType(String name) {
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