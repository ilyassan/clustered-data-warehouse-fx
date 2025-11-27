package com.ilyassan.clustereddatawarehousefx.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class WelcomeController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("message", "Welcome to Bloomberg FX Deals Data Warehouse API");
        response.put("version", "1.0.0");
        response.put("description", "A production-ready Spring Boot application for importing, validating, and managing foreign exchange (FX) deal transactions");

        Map<String, Object> documentation = new LinkedHashMap<>();
        documentation.put("swagger_ui", "/swagger-ui.html");
        documentation.put("api_docs", "/api-docs");
        response.put("documentation", documentation);

        Map<String, Map<String, String>> endpoints = new LinkedHashMap<>();

        Map<String, String> importDeal = new LinkedHashMap<>();
        importDeal.put("method", "POST");
        importDeal.put("path", "/api/deals");
        importDeal.put("description", "Import a single FX deal");
        endpoints.put("import_deal", importDeal);

        Map<String, String> batchImport = new LinkedHashMap<>();
        batchImport.put("method", "POST");
        batchImport.put("path", "/api/deals/batch");
        batchImport.put("description", "Import multiple FX deals in batch (no rollback policy)");
        endpoints.put("batch_import", batchImport);

        Map<String, String> getAllDeals = new LinkedHashMap<>();
        getAllDeals.put("method", "GET");
        getAllDeals.put("path", "/api/deals");
        getAllDeals.put("description", "Retrieve all FX deals");
        endpoints.put("get_all_deals", getAllDeals);

        Map<String, String> getDealById = new LinkedHashMap<>();
        getDealById.put("method", "GET");
        getDealById.put("path", "/api/deals/{dealUniqueId}");
        getDealById.put("description", "Retrieve a specific deal by unique ID");
        endpoints.put("get_deal_by_id", getDealById);

        Map<String, String> health = new LinkedHashMap<>();
        health.put("method", "GET");
        health.put("path", "/actuator/health");
        health.put("description", "Application health check endpoint");
        endpoints.put("health", health);

        response.put("available_endpoints", endpoints);

        Map<String, String> notes = new LinkedHashMap<>();
        notes.put("validation", "All currency codes must be valid ISO 4217 codes");
        notes.put("duplicate_prevention", "Deals with duplicate unique IDs will be rejected");
        notes.put("batch_processing", "Batch imports process each deal independently - successful deals are saved even if others fail");
        response.put("important_notes", notes);

        return ResponseEntity.ok(response);
    }
}
