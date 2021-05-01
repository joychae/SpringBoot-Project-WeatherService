package com.weather.weatherdataapi.repository;

import com.weather.weatherdataapi.model.entity.info.AirPollutionInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirPollutionRepository extends JpaRepository<AirPollutionInfo, Long> {
}
