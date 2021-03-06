package com.weather.weatherdataapi.service;

import com.weather.weatherdataapi.Global;
import com.weather.weatherdataapi.model.dto.CoordinateDto;
import com.weather.weatherdataapi.model.dto.RegionDto;
import com.weather.weatherdataapi.model.dto.ScoreWeightDto;
import com.weather.weatherdataapi.model.dto.requestdto.RegionRequestDto;
import com.weather.weatherdataapi.model.dto.responsedto.UserRegionResponseDto;
import com.weather.weatherdataapi.model.entity.SmallRegion;
import com.weather.weatherdataapi.model.entity.User;
import com.weather.weatherdataapi.model.entity.UserOftenSeenRegion;
import com.weather.weatherdataapi.repository.UserOftenSeenRegionRepository;
import com.weather.weatherdataapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserOftenSeenRegionRepository userOftenSeenRegionRepository;
    private final RegionService regionService;

    /* Create New User */

    public User createNewUser() {
        ScoreWeightDto defaultScoreWeightDto = ScoreWeightDto.getDefaultDto();

        return createNewUser(defaultScoreWeightDto);
    }

    public User createNewUser(ScoreWeightDto scoreWeightDto) {
        return createNewUser(scoreWeightDto, RegionRequestDto.getDefaultDto());
    }

    public User createNewUser(RegionRequestDto regionRequestDto) {
        return createNewUser(ScoreWeightDto.getDefaultDto(), regionRequestDto);
    }

    public User createNewUser(ScoreWeightDto scoreWeightDto, RegionRequestDto regionRequestDto) {
        String newIdentification = "wl" + ZonedDateTime.now().toString() + UUID.randomUUID();

        User newUser = new User(newIdentification, scoreWeightDto);

        userRepository.save(newUser);

        updateOftenSeenRegionRefs(newUser, regionRequestDto);

        return newUser;
    }

    public User getOrCreateUserByIdentification(String identification) {
        if (StringUtils.hasText(identification) == false)
            return createNewUser();

        Optional<User> queriedUser = getUserByIdentification(identification);

        if (queriedUser.isPresent() == false)
            return createNewUser();

        return queriedUser.get();
    }

    /**
     * ?????? User??? ????????? ?????? ????????? ???????????????.
     *
     * @param user          ?????? ??????????????????.
     * @param coordinateDto ?????? ???????????? ?????? ???????????????.
     */
    public UserRegionResponseDto getMyRegion(User user, CoordinateDto coordinateDto) {
        RegionDto currentRegion = regionService.getRegionByCoordinate(coordinateDto);

        SmallRegion latestRequestRegion = user.getLatestRequestRegion();
        RegionDto latestRequestRegionDto = latestRequestRegion == null ? null : new RegionDto(latestRequestRegion.getBigRegion().getBigRegionName(), latestRequestRegion.getSmallRegionName());

        List<UserOftenSeenRegion> oftenSeenRegions = userOftenSeenRegionRepository.findAllByUser(user);

        UserRegionResponseDto userRegionResponseDto = new UserRegionResponseDto(user.getIdentification(), currentRegion, latestRequestRegionDto, oftenSeenRegions);

        return userRegionResponseDto;
    }

    // TODO: ?????? ????????? ???????????? ???????????? ????????? ?????????. preference??? scoreWeight. ??? ?????? ????????? ???????????? ?????? ??????????????? ??????????
    @Transactional
    public void updatePreference(User user, ScoreWeightDto scoreWeightDto) {
        user.updateUserPreference(scoreWeightDto);
    }

    @Transactional
    public void updateCurrentRegion(User user, SmallRegion currentRegion) {
        user.setLatestRequestRegion(currentRegion);
    }

    @Transactional
    public void updateDevice(User user, String device) {
        user.setDevice(device);
    }

    @Transactional
    public void updateOftenSeenRegionRefs(User user, RegionRequestDto regionRequestDto) {

        userOftenSeenRegionRepository.deleteAllByUser(user);

        for (RegionDto regionDto : regionRequestDto.getOftenSeenRegions()) {
            String bigRegionName = regionDto.getBigRegionName();
            String smallRegionName = regionDto.getSmallRegionName();

            SmallRegion smallRegion = regionService.getSmallRegionByName(bigRegionName, smallRegionName);

            UserOftenSeenRegion userOftenSeenRegion = new UserOftenSeenRegion(user, smallRegion);

            userOftenSeenRegionRepository.save(userOftenSeenRegion);
        }

    }

    public HttpHeaders createHeadersWithUserIdentification(User user) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(Global.IDENTIFICATION_TEXT, user.getIdentification());
        return responseHeaders;
    }

    private Optional<User> getUserByIdentification(String identification) {
        return userRepository.findByIdentification(identification);
    }

}
