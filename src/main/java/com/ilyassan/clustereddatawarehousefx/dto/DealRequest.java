package com.ilyassan.clustereddatawarehousefx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for submitting FX deal details")
public class DealRequest {

    @NotBlank(message = "Deal unique ID is required")
    @Size(max = 100, message = "Deal unique ID must not exceed 100 characters")
    @Schema(description = "Unique identifier for the deal", example = "DEAL-2024-001", required = true)
    private String dealUniqueId;

    @NotBlank(message = "From currency code is required")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
    @Schema(description = "ISO 4217 currency code for the ordering currency", example = "USD", required = true)
    private String fromCurrencyCode;

    @NotBlank(message = "To currency code is required")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
    @Schema(description = "ISO 4217 currency code for the target currency", example = "EUR", required = true)
    private String toCurrencyCode;

    @NotNull(message = "Deal timestamp is required")
    @PastOrPresent(message = "Deal timestamp cannot be in the future")
    @Schema(description = "Timestamp when the deal occurred", example = "2024-01-15T10:30:00", required = true)
    private LocalDateTime dealTimestamp;

    @NotNull(message = "Deal amount is required")
    @DecimalMin(value = "0.0001", inclusive = true, message = "Deal amount must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Deal amount must have at most 15 integer digits and 4 decimal places")
    @Schema(description = "Deal amount in the ordering currency", example = "1000.50", required = true)
    private BigDecimal dealAmount;
}
