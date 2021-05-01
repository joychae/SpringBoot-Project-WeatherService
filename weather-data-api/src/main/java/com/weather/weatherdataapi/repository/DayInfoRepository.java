package com.weather.weatherdataapi.repository;

import com.weather.weatherdataapi.model.entity.info.DayInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayInfoRepository extends JpaRepository<DayInfo, Long> {
}
