package com.ilyassan.clustereddatawarehousefx.service;

import com.ilyassan.clustereddatawarehousefx.dto.DealImportResult;
import com.ilyassan.clustereddatawarehousefx.dto.DealRequest;
import com.ilyassan.clustereddatawarehousefx.dto.DealResponse;
import com.ilyassan.clustereddatawarehousefx.entity.Deal;
import com.ilyassan.clustereddatawarehousefx.mapper.DealMapper;
import com.ilyassan.clustereddatawarehousefx.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final DealMapper dealMapper;

    @Override
    @Transactional
    public DealResponse importDeal(DealRequest dealRequest) {
        if (dealRepository.existsByDealUniqueId(dealRequest.getDealUniqueId())) {
            throw new IllegalArgumentException("Deal with ID " + dealRequest.getDealUniqueId() + " already exists");
        }

        Deal deal = dealMapper.toEntity(dealRequest);
        Deal savedDeal = dealRepository.save(deal);
        log.info("Successfully imported deal with ID: {}", savedDeal.getDealUniqueId());

        return dealMapper.toResponse(savedDeal);
    }

    @Override
    @Transactional
    public DealImportResult importDeals(List<DealRequest> dealRequests) {
        List<DealResponse> successfulDeals = new ArrayList<>();
        List<DealImportResult.DealFailure> failures = new ArrayList<>();

        for (DealRequest dealRequest : dealRequests) {
            try {
                if (dealRepository.existsByDealUniqueId(dealRequest.getDealUniqueId())) {
                    failures.add(DealImportResult.DealFailure.builder()
                            .dealUniqueId(dealRequest.getDealUniqueId())
                            .reason("Duplicate deal ID")
                            .field("dealUniqueId")
                            .build());
                    log.warn("Skipped duplicate deal with ID: {}", dealRequest.getDealUniqueId());
                    continue;
                }

                Deal deal = dealMapper.toEntity(dealRequest);
                Deal savedDeal = dealRepository.save(deal);
                successfulDeals.add(dealMapper.toResponse(savedDeal));
                log.info("Successfully imported deal with ID: {}", savedDeal.getDealUniqueId());

            } catch (DataIntegrityViolationException e) {
                failures.add(DealImportResult.DealFailure.builder()
                        .dealUniqueId(dealRequest.getDealUniqueId())
                        .reason("Data integrity violation: " + e.getMessage())
                        .field("unknown")
                        .build());
                log.error("Failed to import deal with ID: {}", dealRequest.getDealUniqueId(), e);

            } catch (Exception e) {
                failures.add(DealImportResult.DealFailure.builder()
                        .dealUniqueId(dealRequest.getDealUniqueId())
                        .reason("Unexpected error: " + e.getMessage())
                        .field("unknown")
                        .build());
                log.error("Unexpected error importing deal with ID: {}", dealRequest.getDealUniqueId(), e);
            }
        }

        return DealImportResult.builder()
                .totalRecords(dealRequests.size())
                .successfulImports(successfulDeals.size())
                .failedImports(failures.size())
                .successfulDeals(successfulDeals)
                .failures(failures)
                .build();
    }

    @Override
    public DealResponse getDealById(String dealUniqueId) {
        Deal deal = dealRepository.findByDealUniqueId(dealUniqueId)
                .orElseThrow(() -> new IllegalArgumentException("Deal with ID " + dealUniqueId + " not found"));

        return dealMapper.toResponse(deal);
    }

    @Override
    public List<DealResponse> getAllDeals() {
        return dealRepository.findAll()
                .stream()
                .map(dealMapper::toResponse)
                .toList();
    }
}
