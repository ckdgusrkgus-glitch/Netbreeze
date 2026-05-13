package com.netbreeze.flathome.domain.repository;

import com.netbreeze.flathome.domain.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HouseholdRepository extends JpaRepository<Household, Long> {

    Optional<Household> findByComplexIdAndBuildingAndUnit(
            String complexId, String building, String unit);
}
