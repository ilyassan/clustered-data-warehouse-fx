package com.ilyassan.clustereddatawarehousefx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response object containing deal details")
public class DealResponse {

    @Schema(description = "Unique identifier for the deal", example = "DEAL-2024-001")
    private String dealUniqueId;

    @Schema(description = "ISO 4217 currency code for the ordering currency", example = "USD")
    private String fromCurrencyCode;

    @Schema(description = "ISO 4217 currency code for the target currency", example = "EUR")
    private String toCurrencyCode;

    @Schema(description = "Timestamp when the deal occurred", example = "2024-01-15T10:30:00")
    private LocalDateTime dealTimestamp;

    @Schema(description = "Deal amount in the ordering currency", example = "1000.50")
    private BigDecimal dealAmount;

    @Schema(description = "Timestamp when the deal was created in the system", example = "2024-01-15T10:31:00")
    private LocalDateTime createdAt;
}
