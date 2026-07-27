package com.hsbc.travel.dto;

import java.util.ArrayList;
import java.util.List;

public class ReadinessResult {
    private int score;
    private List<WarningItem> warnings = new ArrayList<>();
    private List<String> recommendedActions = new ArrayList<>();

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public List<WarningItem> getWarnings() { return warnings; }
    public void setWarnings(List<WarningItem> warnings) { this.warnings = warnings; }
    public List<String> getRecommendedActions() { return recommendedActions; }
    public void setRecommendedActions(List<String> recommendedActions) { this.recommendedActions = recommendedActions; }

    public static class WarningItem {
        private String type;
        private String message;
        private String severity;

        public WarningItem() {}
        public WarningItem(String type, String message, String severity) {
            this.type = type;
            this.message = message;
            this.severity = severity;
        }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
    }
}
