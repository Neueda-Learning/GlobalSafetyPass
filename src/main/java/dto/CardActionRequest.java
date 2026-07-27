package com.hsbc.travel.dto;

public class CardActionRequest {
    private boolean enableOverseas;
    private java.math.BigDecimal newDailyLimit;

    public boolean isEnableOverseas() { return enableOverseas; }
    public void setEnableOverseas(boolean enableOverseas) { this.enableOverseas = enableOverseas; }
    public java.math.BigDecimal getNewDailyLimit() { return newDailyLimit; }
    public void setNewDailyLimit(java.math.BigDecimal newDailyLimit) { this.newDailyLimit = newDailyLimit; }
}
