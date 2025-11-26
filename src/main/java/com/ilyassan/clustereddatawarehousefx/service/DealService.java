package com.ilyassan.clustereddatawarehousefx.service;

import com.ilyassan.clustereddatawarehousefx.dto.DealImportResult;
import com.ilyassan.clustereddatawarehousefx.dto.DealRequest;
import com.ilyassan.clustereddatawarehousefx.dto.DealResponse;

import java.util.List;

public interface DealService {

    DealResponse importDeal(DealRequest dealRequest);

    DealImportResult importDeals(List<DealRequest> dealRequests);

    DealResponse getDealById(String dealUniqueId);

    List<DealResponse> getAllDeals();
}
