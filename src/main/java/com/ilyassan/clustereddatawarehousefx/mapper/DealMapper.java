package com.ilyassan.clustereddatawarehousefx.mapper;

import com.ilyassan.clustereddatawarehousefx.dto.DealRequest;
import com.ilyassan.clustereddatawarehousefx.dto.DealResponse;
import com.ilyassan.clustereddatawarehousefx.entity.Deal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DealMapper {

    @Mapping(target = "createdAt", ignore = true)
    Deal toEntity(DealRequest dealRequest);

    DealResponse toResponse(Deal deal);
}
