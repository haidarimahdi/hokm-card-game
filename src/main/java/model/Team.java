package model;

public enum Team {
    TEAM_A("Team 1"),
    TEAM_B("Team 2");

    private final String displayName;

    Team(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
