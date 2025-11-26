package com.ilyassan.clustereddatawarehousefx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyassan.clustereddatawarehousefx.dto.DealRequest;
import com.ilyassan.clustereddatawarehousefx.repository.DealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DealControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DealRepository dealRepository;

    @BeforeEach
    void setUp() {
        dealRepository.deleteAll();
    }

    @Test
    void importDeal_Success() throws Exception {
        DealRequest request = DealRequest.builder()
                .dealUniqueId("DEAL-INT-001")
                .fromCurrencyCode("USD")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(1000.50))
                .build();

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dealUniqueId").value("DEAL-INT-001"))
                .andExpect(jsonPath("$.fromCurrencyCode").value("USD"))
                .andExpect(jsonPath("$.toCurrencyCode").value("EUR"))
                .andExpect(jsonPath("$.dealAmount").value(1000.50));
    }

    @Test
    void importDeal_ValidationError_MissingFields() throws Exception {
        DealRequest request = DealRequest.builder()
                .dealUniqueId("DEAL-INT-002")
                .build();

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void importDeal_ValidationError_InvalidCurrency() throws Exception {
        DealRequest request = DealRequest.builder()
                .dealUniqueId("DEAL-INT-003")
                .fromCurrencyCode("ZZZ")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(1000))
                .build();

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.validationErrors.fromCurrencyCode").exists());
    }

    @Test
    void importDeal_DuplicateId() throws Exception {
        DealRequest request = DealRequest.builder()
                .dealUniqueId("DEAL-INT-004")
                .fromCurrencyCode("USD")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(500))
                .build();

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    void importDeals_Batch_AllSuccess() throws Exception {
        DealRequest request1 = DealRequest.builder()
                .dealUniqueId("BATCH-001")
                .fromCurrencyCode("USD")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(1000))
                .build();

        DealRequest request2 = DealRequest.builder()
                .dealUniqueId("BATCH-002")
                .fromCurrencyCode("GBP")
                .toCurrencyCode("JPY")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(2000))
                .build();

        List<DealRequest> requests = Arrays.asList(request1, request2);

        mockMvc.perform(post("/api/deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").value(2))
                .andExpect(jsonPath("$.successfulImports").value(2))
                .andExpect(jsonPath("$.failedImports").value(0))
                .andExpect(jsonPath("$.successfulDeals").isArray())
                .andExpect(jsonPath("$.successfulDeals", hasSize(2)));
    }

    @Test
    void importDeals_Batch_MixedResults() throws Exception {
        DealRequest validRequest = DealRequest.builder()
                .dealUniqueId("BATCH-VALID-001")
                .fromCurrencyCode("USD")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(1000))
                .build();

        DealRequest invalidRequest = DealRequest.builder()
                .dealUniqueId("BATCH-INVALID-001")
                .fromCurrencyCode("ZZZ")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(1000))
                .build();

        List<DealRequest> requests = Arrays.asList(validRequest, invalidRequest);

        mockMvc.perform(post("/api/deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").value(2))
                .andExpect(jsonPath("$.successfulImports").value(1))
                .andExpect(jsonPath("$.failedImports").value(1))
                .andExpect(jsonPath("$.successfulDeals", hasSize(1)))
                .andExpect(jsonPath("$.failures", hasSize(1)))
                .andExpect(jsonPath("$.failures[0].dealUniqueId").value("BATCH-INVALID-001"));
    }

    @Test
    void importDeals_Batch_WithDuplicate() throws Exception {
        DealRequest request1 = DealRequest.builder()
                .dealUniqueId("BATCH-DUP-001")
                .fromCurrencyCode("USD")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(1000))
                .build();

        DealRequest request2 = DealRequest.builder()
                .dealUniqueId("BATCH-DUP-001")
                .fromCurrencyCode("GBP")
                .toCurrencyCode("JPY")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(2000))
                .build();

        List<DealRequest> requests = Arrays.asList(request1, request2);

        mockMvc.perform(post("/api/deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").value(2))
                .andExpect(jsonPath("$.successfulImports").value(1))
                .andExpect(jsonPath("$.failedImports").value(1))
                .andExpect(jsonPath("$.failures[0].reason").value("Duplicate deal ID"));
    }

    @Test
    void getDealById_Success() throws Exception {
        DealRequest request = DealRequest.builder()
                .dealUniqueId("DEAL-GET-001")
                .fromCurrencyCode("USD")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(750))
                .build();

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/deals/DEAL-GET-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dealUniqueId").value("DEAL-GET-001"))
                .andExpect(jsonPath("$.fromCurrencyCode").value("USD"))
                .andExpect(jsonPath("$.dealAmount").value(750));
    }

    @Test
    void getDealById_NotFound() throws Exception {
        mockMvc.perform(get("/api/deals/NON-EXISTENT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void getAllDeals_Success() throws Exception {
        DealRequest request1 = DealRequest.builder()
                .dealUniqueId("ALL-001")
                .fromCurrencyCode("USD")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(100))
                .build();

        DealRequest request2 = DealRequest.builder()
                .dealUniqueId("ALL-002")
                .fromCurrencyCode("GBP")
                .toCurrencyCode("JPY")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(200))
                .build();

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/deals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].dealUniqueId", containsInAnyOrder("ALL-001", "ALL-002")));
    }

    @Test
    void getAllDeals_EmptyList() throws Exception {
        mockMvc.perform(get("/api/deals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
