package com.ilyassan.clustereddatawarehousefx.repository;

import com.ilyassan.clustereddatawarehousefx.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DealRepository extends JpaRepository<Deal, String> {

    boolean existsByDealUniqueId(String dealUniqueId);

    Optional<Deal> findByDealUniqueId(String dealUniqueId);
}
