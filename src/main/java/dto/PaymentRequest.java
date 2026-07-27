package com.hsbc.travel.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class PaymentRequest {

    @NotNull(message = "Card ID is required")
    private Long cardId;

    @NotBlank(message = "Merchant is required")
    private String merchant;

    private String merchantLocation;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3)
    private String currency;

    private BigDecimal exchangeRate;

    @NotBlank(message = "Category is required")
    private String category;

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getMerchant() { return merchant; }
    public void setMerchant(String merchant) { this.merchant = merchant; }
    public String getMerchantLocation() { return merchantLocation; }
    public void setMerchantLocation(String merchantLocation) { this.merchantLocation = merchantLocation; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
