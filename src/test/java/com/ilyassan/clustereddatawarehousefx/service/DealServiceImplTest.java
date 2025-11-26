package com.ilyassan.clustereddatawarehousefx.service;

import com.ilyassan.clustereddatawarehousefx.dto.DealImportResult;
import com.ilyassan.clustereddatawarehousefx.dto.DealRequest;
import com.ilyassan.clustereddatawarehousefx.dto.DealResponse;
import com.ilyassan.clustereddatawarehousefx.entity.Deal;
import com.ilyassan.clustereddatawarehousefx.mapper.DealMapper;
import com.ilyassan.clustereddatawarehousefx.repository.DealRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {

    @Mock
    private DealRepository dealRepository;

    @Mock
    private DealMapper dealMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private DealServiceImpl dealService;

    private DealRequest dealRequest;
    private Deal deal;
    private DealResponse dealResponse;

    @BeforeEach
    void setUp() {
        dealRequest = DealRequest.builder()
                .dealUniqueId("DEAL-001")
                .fromCurrencyCode("USD")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(1000.50))
                .build();

        deal = Deal.builder()
                .dealUniqueId("DEAL-001")
                .fromCurrencyCode("USD")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(1000.50))
                .createdAt(LocalDateTime.now())
                .build();

        dealResponse = DealResponse.builder()
                .dealUniqueId("DEAL-001")
                .fromCurrencyCode("USD")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(1000.50))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void importDeal_Success() {
        when(dealRepository.existsByDealUniqueId("DEAL-001")).thenReturn(false);
        when(dealMapper.toEntity(dealRequest)).thenReturn(deal);
        when(dealRepository.save(deal)).thenReturn(deal);
        when(dealMapper.toResponse(deal)).thenReturn(dealResponse);

        DealResponse result = dealService.importDeal(dealRequest);

        assertNotNull(result);
        assertEquals("DEAL-001", result.getDealUniqueId());
        verify(dealRepository).existsByDealUniqueId("DEAL-001");
        verify(dealRepository).save(deal);
    }

    @Test
    void importDeal_ThrowsException_WhenDuplicateId() {
        when(dealRepository.existsByDealUniqueId("DEAL-001")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dealService.importDeal(dealRequest)
        );

        assertEquals("Deal with ID DEAL-001 already exists", exception.getMessage());
        verify(dealRepository, never()).save(any());
    }

    @Test
    void importDeals_AllSuccess() {
        List<DealRequest> requests = Arrays.asList(dealRequest);

        when(validator.validate(any(DealRequest.class))).thenReturn(Collections.emptySet());
        when(dealRepository.existsByDealUniqueId("DEAL-001")).thenReturn(false);
        when(dealMapper.toEntity(dealRequest)).thenReturn(deal);
        when(dealRepository.save(deal)).thenReturn(deal);
        when(dealMapper.toResponse(deal)).thenReturn(dealResponse);

        DealImportResult result = dealService.importDeals(requests);

        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getSuccessfulImports());
        assertEquals(0, result.getFailedImports());
        assertEquals(1, result.getSuccessfulDeals().size());
        assertTrue(result.getFailures().isEmpty());
    }

    @Test
    void importDeals_WithValidationFailure() {
        List<DealRequest> requests = Arrays.asList(dealRequest);

        ConstraintViolation<DealRequest> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Invalid currency code");
        when(violation.getPropertyPath()).thenReturn(mock(Path.class));

        Set<ConstraintViolation<DealRequest>> violations = new HashSet<>();
        violations.add(violation);

        when(validator.validate(any(DealRequest.class))).thenReturn(violations);

        DealImportResult result = dealService.importDeals(requests);

        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getSuccessfulImports());
        assertEquals(1, result.getFailedImports());
        assertTrue(result.getSuccessfulDeals().isEmpty());
        assertEquals(1, result.getFailures().size());
        verify(dealRepository, never()).save(any());
    }

    @Test
    void importDeals_WithDuplicate() {
        List<DealRequest> requests = Arrays.asList(dealRequest);

        when(validator.validate(any(DealRequest.class))).thenReturn(Collections.emptySet());
        when(dealRepository.existsByDealUniqueId("DEAL-001")).thenReturn(true);

        DealImportResult result = dealService.importDeals(requests);

        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getSuccessfulImports());
        assertEquals(1, result.getFailedImports());
        assertEquals(1, result.getFailures().size());
        assertEquals("Duplicate deal ID", result.getFailures().get(0).getReason());
        verify(dealRepository, never()).save(any());
    }

    @Test
    void importDeals_MixedResults() {
        DealRequest validRequest = DealRequest.builder()
                .dealUniqueId("DEAL-002")
                .fromCurrencyCode("USD")
                .toCurrencyCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(BigDecimal.valueOf(500))
                .build();

        List<DealRequest> requests = Arrays.asList(dealRequest, validRequest);

        when(validator.validate(any(DealRequest.class))).thenReturn(Collections.emptySet());
        when(dealRepository.existsByDealUniqueId("DEAL-001")).thenReturn(true);
        when(dealRepository.existsByDealUniqueId("DEAL-002")).thenReturn(false);
        when(dealMapper.toEntity(validRequest)).thenReturn(deal);
        when(dealRepository.save(any())).thenReturn(deal);
        when(dealMapper.toResponse(any())).thenReturn(dealResponse);

        DealImportResult result = dealService.importDeals(requests);

        assertEquals(2, result.getTotalRecords());
        assertEquals(1, result.getSuccessfulImports());
        assertEquals(1, result.getFailedImports());
        assertEquals(1, result.getSuccessfulDeals().size());
        assertEquals(1, result.getFailures().size());
    }

    @Test
    void getDealById_Success() {
        when(dealRepository.findByDealUniqueId("DEAL-001")).thenReturn(Optional.of(deal));
        when(dealMapper.toResponse(deal)).thenReturn(dealResponse);

        DealResponse result = dealService.getDealById("DEAL-001");

        assertNotNull(result);
        assertEquals("DEAL-001", result.getDealUniqueId());
        verify(dealRepository).findByDealUniqueId("DEAL-001");
    }

    @Test
    void getDealById_ThrowsException_WhenNotFound() {
        when(dealRepository.findByDealUniqueId("DEAL-999")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dealService.getDealById("DEAL-999")
        );

        assertEquals("Deal with ID DEAL-999 not found", exception.getMessage());
    }

    @Test
    void getAllDeals_Success() {
        List<Deal> deals = Arrays.asList(deal);
        when(dealRepository.findAll()).thenReturn(deals);
        when(dealMapper.toResponse(deal)).thenReturn(dealResponse);

        List<DealResponse> results = dealService.getAllDeals();

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(dealRepository).findAll();
    }

    @Test
    void getAllDeals_ReturnsEmptyList() {
        when(dealRepository.findAll()).thenReturn(Collections.emptyList());

        List<DealResponse> results = dealService.getAllDeals();

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(dealRepository).findAll();
    }
}
