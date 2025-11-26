package com.ilyassan.clustereddatawarehousefx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Result of deal import operation")
public class DealImportResult {

    @Schema(description = "Total number of deals submitted", example = "100")
    private int totalRecords;

    @Schema(description = "Number of successfully imported deals", example = "95")
    private int successfulImports;

    @Schema(description = "Number of failed imports", example = "5")
    private int failedImports;

    @Schema(description = "List of successfully imported deals")
    @Builder.Default
    private List<DealResponse> successfulDeals = new ArrayList<>();

    @Schema(description = "List of failures with details")
    @Builder.Default
    private List<DealFailure> failures = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Details of a failed deal import")
    public static class DealFailure {

        @Schema(description = "Deal unique ID that failed", example = "DEAL-2024-001")
        private String dealUniqueId;

        @Schema(description = "Reason for failure", example = "Duplicate deal ID")
        private String reason;

        @Schema(description = "Field that caused the error", example = "dealUniqueId")
        private String field;
    }
}
