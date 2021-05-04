package com.weather.weatherdataapi.service;

import com.weather.weatherdataapi.model.dto.responsedto.ScoreResultResponseDto;
import com.weather.weatherdataapi.model.dto.responsedto.WeatherDataResponseDto;
import com.weather.weatherdataapi.model.entity.BigRegion;
import com.weather.weatherdataapi.model.entity.info.LivingHealthInfo;
import com.weather.weatherdataapi.repository.BigRegionRepository;
import com.weather.weatherdataapi.repository.info.LivingHealthInfoRepository;
import com.weather.weatherdataapi.util.HealthScoreUtil;
import com.weather.weatherdataapi.util.LivingScoreUtil;
import com.weather.weatherdataapi.util.openapi.living_health.LivingHealthApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LivingHealthService {

    private final LivingHealthInfoRepository livingHealthInfoRepository;
    private final LivingHealthApi livingHealthApi;
    private final BigRegionRepository bigRegionRepository;

    /**
     * 여기서부터는 매일 아침 6시에 생활보건기상지수를 업데이트하는 스케줄러에 쓰이는 메서드입니다.
     */
    @Transactional
    public void fetchAndStoreLivingHealthInfoUsingOpenApi() throws Exception {
        if (checkAlreadyHasLatestLivingHealthInfo() == true)
            return;

        List<BigRegion> bigRegionList = bigRegionRepository.findAll();

        for (int i = 0; i < bigRegionList.size(); i++) {
            BigRegion bigRegion = bigRegionList.get(i);
            String admCode = bigRegionList.get(i).getAdmCode();
            LivingHealthInfo livingHealthInfo = livingHealthApi.livingHealthApi(bigRegion, admCode);
            livingHealthInfoRepository.save(livingHealthInfo);
        }
        log.info("fetchAndStoreLivingHealth::생활기상지수 데이터를 성공적으로 갱신하였습니다.");
    }

    private boolean checkAlreadyHasLatestLivingHealthInfo() {
        LivingHealthInfo latestData = livingHealthInfoRepository.findFirstByOrderByCreatedAt();
        LocalDate current = LocalDate.now();
        if (latestData == null)
            return false;
        return latestData.getCreatedAt().isEqual(current);
    }

    /**
     * 정보 요청이 왔을 때, TotalDataService 로 생활보건기상지수 정보와 점수를 반환해주는 메서드입니다.
     */
    public void setInfoAndScore(BigRegion currentBigRegion, ScoreResultResponseDto scoreResultResponseDto, WeatherDataResponseDto weatherDataResponseDto) {
        weatherDataResponseDto.setLivingHealthWeather(getInfoByBigRegion(currentBigRegion));
        convertInfoToScore(currentBigRegion, scoreResultResponseDto);
    }

    /**
     * 여기서부터는 해당 지역의 생활보건기상지수 정보를 DB 에서 찾아서 반환해주는 메서드입니다.
     */
    public LivingHealthInfo getInfoByBigRegion(BigRegion bigRegion) {
        return livingHealthInfoRepository.findFirstByBigRegionOrderByCreatedAt(bigRegion);
    }

    /**
     * 여기서부터는 생활보건기상지수의 점수 변환 관련 메서드입니다.
     */
    public ScoreResultResponseDto convertInfoToScore(BigRegion bigRegion, ScoreResultResponseDto scoreResultResponseDto) {

        LivingHealthInfo livingHealthInfo = livingHealthInfoRepository.findFirstByBigRegionOrderByCreatedAt(bigRegion);

        // 천식폐질환지수 점수변환
        List<Integer> asthmaInfoList = new ArrayList<>();
        asthmaInfoList.add(convertHealthInfoToScore(livingHealthInfo.getAsthmaToday()));
        asthmaInfoList.add(convertHealthInfoToScore(livingHealthInfo.getAsthmaTomorrow()));
        for (int i = 0; i < 5; i++) {
            asthmaInfoList.add(convertHealthInfoToScore(livingHealthInfo.getAsthmaTheDayAfterTomorrow()));
        }

        scoreResultResponseDto.setAsthmaResult(asthmaInfoList);

        // 꽃가루지수 점수변환
        List<Integer> pollenRiskInfoList = new ArrayList<>();
        pollenRiskInfoList.add(convertHealthInfoToScore(livingHealthInfo.getOakPollenRiskToday()));
        pollenRiskInfoList.add(convertHealthInfoToScore(livingHealthInfo.getOakPollenRiskTomorrow()));
        for (int i = 0; i < 5; i++) {
            pollenRiskInfoList.add(convertHealthInfoToScore(livingHealthInfo.getOakPollenRiskTheDayAfterTomorrow()));
        }

        scoreResultResponseDto.setPollenRiskResult(pollenRiskInfoList);

        // 식중독지수 점수변환
        List<Integer> foodPoisonInfoList = new ArrayList<>();
        foodPoisonInfoList.add(convertFoodPoisonInfoToScore(livingHealthInfo.getFoodPoisonToday()));
        foodPoisonInfoList.add(convertFoodPoisonInfoToScore(livingHealthInfo.getFoodPoisonTomorrow()));
        for (int i = 0; i < 5; i++) {
            foodPoisonInfoList.add(convertFoodPoisonInfoToScore(livingHealthInfo.getFoodPoisonTheDayAfterTomorrow()));
        }

        scoreResultResponseDto.setFoodPoisonResult(foodPoisonInfoList);

        // 자외선지수 점수변환
        List<Integer> uvInfoList = new ArrayList<>();
        uvInfoList.add(convertUvInfoToScore(livingHealthInfo.getUvToday()));
        uvInfoList.add(convertUvInfoToScore(livingHealthInfo.getUvTomorrow()));
        for (int i = 0; i < 5; i++) {
            uvInfoList.add(convertUvInfoToScore(livingHealthInfo.getUvTheDayAfterTomorrow()));
        }

        scoreResultResponseDto.setUvResult(uvInfoList);

        return scoreResultResponseDto;
    }

    // 보건기상지수 중 식중독 지수 제외한 그 외 지수의 점수 변환
    public Integer convertHealthInfoToScore(String wthIdx) {
        Integer score = HealthScoreUtil.convertHealthWthIdxToScore(wthIdx);
        return score;
    }

    // 보건기상지수 중 식중독 지수의 점수 반환
    public Integer convertFoodPoisonInfoToScore(String wthIdx) {
        if (Integer.parseInt(wthIdx) <= 55) {
            return 100;
        } else if (Integer.parseInt(wthIdx) <= 70) {
            return 70;
        } else if (Integer.parseInt(wthIdx) <= 85) {
            return 40;
        }
        return 10;
    }

    // 생활기상지수 중 자외선 지수의 점수 변환
    public Integer convertUvInfoToScore(String wthIdx) {
        Integer score = LivingScoreUtil.convertHealthWthIdxToScore(wthIdx);
        return score;
    }

}
