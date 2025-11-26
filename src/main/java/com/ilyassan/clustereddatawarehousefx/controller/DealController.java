package com.ilyassan.clustereddatawarehousefx.controller;

import com.ilyassan.clustereddatawarehousefx.dto.DealImportResult;
import com.ilyassan.clustereddatawarehousefx.dto.DealRequest;
import com.ilyassan.clustereddatawarehousefx.dto.DealResponse;
import com.ilyassan.clustereddatawarehousefx.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
@Tag(name = "Deals", description = "FX Deal Management API")
public class DealController {

    private final DealService dealService;

    @PostMapping
    @Operation(summary = "Import a single FX deal", description = "Imports a single FX deal into the warehouse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deal successfully imported"),
            @ApiResponse(responseCode = "400", description = "Invalid deal data or duplicate deal ID"),
            @ApiResponse(responseCode = "422", description = "Validation error")
    })
    public ResponseEntity<DealResponse> importDeal(@Valid @RequestBody DealRequest dealRequest) {
        DealResponse response = dealService.importDeal(dealRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/batch")
    @Operation(summary = "Import multiple FX deals", description = "Imports multiple FX deals in batch. Each deal is processed independently with no rollback.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch import completed with results"),
            @ApiResponse(responseCode = "400", description = "Invalid request format")
    })
    public ResponseEntity<DealImportResult> importDeals(@RequestBody List<DealRequest> dealRequests) {
        DealImportResult result = dealService.importDeals(dealRequests);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{dealUniqueId}")
    @Operation(summary = "Get deal by ID", description = "Retrieves a specific FX deal by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deal found"),
            @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    public ResponseEntity<DealResponse> getDealById(@PathVariable String dealUniqueId) {
        DealResponse response = dealService.getDealById(dealUniqueId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all deals", description = "Retrieves all FX deals from the warehouse")
    @ApiResponse(responseCode = "200", description = "List of all deals")
    public ResponseEntity<List<DealResponse>> getAllDeals() {
        List<DealResponse> deals = dealService.getAllDeals();
        return ResponseEntity.ok(deals);
    }
}
