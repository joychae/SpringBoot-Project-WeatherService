package com.weather.weatherdataapi.service.info;

import com.weather.weatherdataapi.exception.AlreadyExistsLatestDataException;
import com.weather.weatherdataapi.exception.FailedFetchException;
import com.weather.weatherdataapi.exception.repository.info.InvalidLivingHealthInfoException;
import com.weather.weatherdataapi.model.dto.ScoreResultDto;
import com.weather.weatherdataapi.model.dto.responsedto.TotalDataResponseDto;
import com.weather.weatherdataapi.model.dto.responsedto.info.LivingHealthResponseDto;
import com.weather.weatherdataapi.model.entity.BigRegion;
import com.weather.weatherdataapi.model.entity.info.*;
import com.weather.weatherdataapi.model.vo.redis.LivingHealthRedisVO;
import com.weather.weatherdataapi.repository.info.LivingHealthInfoRepository;
import com.weather.weatherdataapi.repository.redis.LivingHealthRedisRepository;
import com.weather.weatherdataapi.service.RegionService;
import com.weather.weatherdataapi.util.ExceptionUtil;
import com.weather.weatherdataapi.util.HealthScoreUtil;
import com.weather.weatherdataapi.util.LivingScoreUtil;
import com.weather.weatherdataapi.util.openapi.health.HealthApi;
import com.weather.weatherdataapi.util.openapi.health.asthma.AsthmaItem;
import com.weather.weatherdataapi.util.openapi.health.food_poison.FoodPoisonItem;
import com.weather.weatherdataapi.util.openapi.health.pollen_risk.PollenRiskItem;
import com.weather.weatherdataapi.util.openapi.living.LivingApi;
import com.weather.weatherdataapi.util.openapi.living.uv.UvItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LivingHealthServiceV2 {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");

    private final LivingHealthInfoRepository livingHealthInfoRepository;
    private final LivingHealthRedisRepository livingHealthRedisRepository;
    private final RegionService regionService;

    private final HealthAsthmaService asthmaService;
    private final HealthFoodPoisonService foodPoisonService;
    private final HealthPollenRiskService pollenRiskService;
    private final LivingUvService livingUvService;

    private final LivingApi livingApi;
    private final HealthApi healthApi;

    public void setInfoAndScore(BigRegion currentBigRegion, ScoreResultDto scoreResultDto, TotalDataResponseDto weatherDataResponseDto) {
        HealthAsthmaInfo asthmaInfo = asthmaService.getInfoByBigRegion(currentBigRegion);
        HealthFoodPoisonInfo foodPoisonInfo = foodPoisonService.getInfoByBigRegion(currentBigRegion);
        HealthPollenRiskInfo pollenRiskInfo = pollenRiskService.getInfoByBigRegion(currentBigRegion);
        LivingUvInfo uvInfo = livingUvService.getInfoByBigRegion(currentBigRegion);

        LivingHealthInfo info = new LivingHealthInfo();
        info.setAsthmaToday(asthmaInfo.getToday());
        info.setAsthmaTomorrow(asthmaInfo.getTomorrow());
        info.setAsthmaTheDayAfterTomorrow(asthmaInfo.getTheDayAfterTomorrow());

        info.setFoodPoisonToday(foodPoisonInfo.getToday());
        info.setFoodPoisonTomorrow(foodPoisonInfo.getTomorrow());
        info.setFoodPoisonTheDayAfterTomorrow(foodPoisonInfo.getTheDayAfterTomorrow());

        info.setOakPollenRiskToday(pollenRiskInfo.getToday());
        info.setOakPollenRiskTomorrow(pollenRiskInfo.getTomorrow());
        info.setOakPollenRiskTheDayAfterTomorrow(pollenRiskInfo.getTheDayAfterTomorrow());

        info.setUvToday(uvInfo.getToday());
        info.setUvTomorrow(uvInfo.getTomorrow());
        info.setUvTheDayAfterTomorrow(uvInfo.getTheDayAfterTomorrow());

        LivingHealthResponseDto responseDto = new LivingHealthResponseDto(info);
        weatherDataResponseDto.setLivingHealthWeather(responseDto);

        convertInfoToScore(scoreResultDto, info);
        scoreResultDto.setLivingHealthValid(true);

    }

    public void convertInfoToScore(ScoreResultDto scoreResultDto, LivingHealthInfo livingHealthInfo) {
        List<Integer> uvInfoList = new ArrayList<>(7);
        List<Integer> asthmaInfoList = new ArrayList<>(7);
        List<Integer> pollenRiskInfoList = new ArrayList<>(7);
        List<Integer> foodPoisonInfoList = new ArrayList<>(7);

        uvInfoList.add(LivingScoreUtil.convertUvInfoToScore(livingHealthInfo.getUvToday()));
        uvInfoList.add(LivingScoreUtil.convertUvInfoToScore(livingHealthInfo.getUvTomorrow()));

        asthmaInfoList.add(HealthScoreUtil.convertHealthInfoToScore(livingHealthInfo.getAsthmaToday()));
        asthmaInfoList.add(HealthScoreUtil.convertHealthInfoToScore(livingHealthInfo.getAsthmaTomorrow()));

        pollenRiskInfoList.add(HealthScoreUtil.convertHealthInfoToScore(livingHealthInfo.getOakPollenRiskToday()));
        pollenRiskInfoList.add(HealthScoreUtil.convertHealthInfoToScore(livingHealthInfo.getOakPollenRiskTomorrow()));

        foodPoisonInfoList.add(HealthScoreUtil.convertFoodPoisonInfoToScore(livingHealthInfo.getFoodPoisonToday()));
        foodPoisonInfoList.add(HealthScoreUtil.convertFoodPoisonInfoToScore(livingHealthInfo.getFoodPoisonTomorrow()));

        // 3~7??? ?????? ?????? ????????? ?????? 3??? ?????? ?????? ????????? ???????????????.
        Integer uvTheDayAfterTomorrow = LivingScoreUtil.convertUvInfoToScore(livingHealthInfo.getUvTheDayAfterTomorrow());
        Integer asthmaTheDayAfterTomorrow = HealthScoreUtil.convertHealthInfoToScore(livingHealthInfo.getAsthmaTheDayAfterTomorrow());
        Integer pollenRiskTheDayAfterTomorrow = HealthScoreUtil.convertHealthInfoToScore(livingHealthInfo.getOakPollenRiskTheDayAfterTomorrow());
        Integer foodPoisonTheDayAfterTomorrow = HealthScoreUtil.convertFoodPoisonInfoToScore(livingHealthInfo.getFoodPoisonTheDayAfterTomorrow());

        for (int i = 0; i < 5; i++) {
            uvInfoList.add(uvTheDayAfterTomorrow);
            asthmaInfoList.add(asthmaTheDayAfterTomorrow);
            pollenRiskInfoList.add(pollenRiskTheDayAfterTomorrow);
            foodPoisonInfoList.add(foodPoisonTheDayAfterTomorrow);
        }

        scoreResultDto.setAsthmaResult(asthmaInfoList);
        scoreResultDto.setPollenRiskResult(pollenRiskInfoList);
        scoreResultDto.setFoodPoisonResult(foodPoisonInfoList);
        scoreResultDto.setUvResult(uvInfoList);

    }

    public LivingHealthInfo getInfoByBigRegion(BigRegion bigRegion) {
        LivingHealthInfo livingHealthInfo;

        // ?????? ???????????? ????????? ????????? ???????????????.
        if (livingHealthRedisRepository.existsById(bigRegion.getAdmCode()) == false) {
            refreshCache();
        }

        try {
            LivingHealthRedisVO livingHealthRedisVO = livingHealthRedisRepository.findById(bigRegion.getAdmCode()).orElseThrow(() -> new InvalidLivingHealthInfoException());
            livingHealthInfo = new LivingHealthInfo(livingHealthRedisVO, bigRegion);

            return livingHealthInfo;
        } catch (InvalidLivingHealthInfoException e) {
            log.error(e.getMessage());
            log.error("??????/?????????????????? ????????? ????????? ??? ????????????. ???????????? ?????? ??????/?????????????????? ????????? ????????????.");

            return new LivingHealthInfo();
        }

    }

    public void tryFetchAndStoreInfoUsingOpenApi() {
        try {
            log.info("?????? ???????????? ???????????? ?????? ??????/?????????????????? ????????? DB??? ??????????????????.");
            long startTime = System.currentTimeMillis();

            try {
                fetchAndStoreInfoUsingOpenApi(LocalDate.now());
            }
            // ?????? ????????? ???????????? ????????? ??? ???????????????.
            // ???????????? ????????? ????????? ????????????.
            //   -> ?????? ?????? ???????????? ?????? ????????? ?????? ???????????? ??????????????? ?????? ???????????????.
            // ????????? ?????? ???????????? ?????? ??? ??? ???????????????.
            catch (FailedFetchException e) {
                log.error("?????? ???????????? ?????? ??????/?????????????????? ????????? ???????????? ??? ?????????????????????.");
                log.error("?????? ???????????? ?????? ??? ??? ????????? ???????????????.");
                fetchAndStoreInfoUsingOpenApi(LocalDate.now().minusDays(1));
            }

            long endTime = System.currentTimeMillis();
            float diffTimeSec = (endTime - startTime) / 1000f;
            log.info("???????????? ??????????????? ???????????????. ({}sec)", diffTimeSec);
        } catch (AlreadyExistsLatestDataException e) {
            log.warn(e.getMessage());
            log.warn("?????? ???????????? ???????????? ??????/?????????????????? ????????? DB??? ?????? ???????????? ????????????.");
        } catch (FailedFetchException e) {
            log.error(e.getMessage());
            log.error(ExceptionUtil.getStackTraceString(e));
            log.error("?????? ???????????? ??????/?????????????????? ????????? ???????????? ??? ?????????????????????.");
        }

        refreshCache();
    }

    @Transactional
    public void fetchAndStoreInfoUsingOpenApi(LocalDate date) throws AlreadyExistsLatestDataException, FailedFetchException {

        List<BigRegion> allValidBigRegionList = regionService.getAllValidBigRegionList();

        for (BigRegion bigRegion : allValidBigRegionList) {
            String admCode = bigRegion.getAdmCode();

            UvItem uvItem = livingApi.getUvResponse(admCode, date);
            AsthmaItem asthmaItem = healthApi.getAsthmaResponse(admCode, date);
            FoodPoisonItem foodPoisonItem = healthApi.getFoodPoisonResponse(admCode, date);
            PollenRiskItem pollenRiskItem = healthApi.getPollenRiskResponse(admCode, date);

            // FIXME: uv ????????? ???????????? ???????????? ??????. ?????? ????????? ????????? ??????????????? ??????????????? ?????????.
            String fetchedItemTimeText = uvItem.getDate();
            LocalDate fetchedItemTime = LocalDate.parse(fetchedItemTimeText, DATETIME_FORMATTER);

            if (checkAlreadyHasLatestData(bigRegion, fetchedItemTime))
                throw new AlreadyExistsLatestDataException();

            LivingHealthInfo livingHealthInfo = new LivingHealthInfo(bigRegion, fetchedItemTime, uvItem, asthmaItem, foodPoisonItem, pollenRiskItem);
            livingHealthInfoRepository.save(livingHealthInfo);
        }

        log.info("??????/?????????????????? ???????????? ??????????????? ?????????????????????.");
    }

    @Transactional
    public void refreshCache() {
        livingHealthRedisRepository.deleteAll();

        List<BigRegion> allValidBigRegionList = regionService.getAllValidBigRegionList();

        for (BigRegion bigRegion : allValidBigRegionList) {
            LivingHealthInfo livingHealthInfo = livingHealthInfoRepository.findFirstByBigRegionOrderByCreatedAtDesc(bigRegion).orElseThrow(() -> new InvalidLivingHealthInfoException());

            LivingHealthRedisVO redisVO = new LivingHealthRedisVO(livingHealthInfo);
            livingHealthRedisRepository.save(redisVO);
        }

    }

    private boolean checkAlreadyHasLatestData(BigRegion bigRegion, LocalDate fetchedDate) {

        Optional<LivingHealthInfo> queriedLatestInfo = livingHealthInfoRepository.findFirstByBigRegionOrderByCreatedAtDesc(bigRegion);
        if (queriedLatestInfo.isPresent() == false)
            return false;

        LocalDate latestDate = queriedLatestInfo.get().getDate();

        if (latestDate.isBefore(fetchedDate))
            return false;

        return true;
    }

}
