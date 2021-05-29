package com.weather.weatherdataapi.model.dto.responsedto;

import com.weather.weatherdataapi.model.dto.RegionDto;
import com.weather.weatherdataapi.model.dto.responsedto.info.CoronaResponseDto;
import com.weather.weatherdataapi.model.dto.responsedto.info.LivingHealthResponseDto;
import com.weather.weatherdataapi.model.entity.BigRegion;
import com.weather.weatherdataapi.model.entity.SmallRegion;
import com.weather.weatherdataapi.model.entity.info.AirPollutionInfo;
import com.weather.weatherdataapi.model.entity.info.WeatherDayInfo;
import com.weather.weatherdataapi.model.entity.info.WeatherWeekInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotalDataResponseDto {

    private RegionDto region;

    private LivingHealthResponseDto livingHealthWeather;

    private WeatherWeekInfo weekInfo;

    private WeatherDayInfo dayInfo;

    private AirPollutionInfo airPollution;

    private CoronaResponseDto corona;

    private List<Integer> dayScoreList;

    public TotalDataResponseDto(BigRegion bigRegion, SmallRegion smallRegion) {
        this.region = new RegionDto(bigRegion.getBigRegionName(), smallRegion.getSmallRegionName());
    }

    public TotalDataResponseDto(BigRegion bigRegion, SmallRegion smallRegion, List<Integer> dayScoreList) {
        this.region = new RegionDto(bigRegion.getBigRegionName(), smallRegion.getSmallRegionName());
        this.dayScoreList = dayScoreList;
    }

    @Override
    public String toString() {
        return "TotalDataResponseDto{" +
                "region=" + region +
                ", livingHealthWeather=" + livingHealthWeather +
                ", weekInfo=" + weekInfo +
                ", dayInfo=" + dayInfo +
                ", airPollution=" + airPollution +
                ", corona=" + corona +
                ", dayScoreList=" + dayScoreList +
                '}';
    }

}
