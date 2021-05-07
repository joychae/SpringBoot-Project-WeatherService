package com.weather.weatherdataapi.model.vo.redis;

import com.weather.weatherdataapi.model.entity.SmallRegion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("small_region")
@NoArgsConstructor
public class SmallRegionRedisVO {
    @Id
    private String admCode;

    private long id;

    private Long big_region_id;

    private String smallRegionName;

    private String longitude;

    private String latitude;

    public SmallRegionRedisVO(SmallRegion smallRegion) {
        this.admCode = smallRegion.getAdmCode();

        this.id = smallRegion.getId();
        this.big_region_id = smallRegion.getBigRegion() == null ? null : smallRegion.getBigRegion().getId();
        this.smallRegionName = smallRegion.getSmallRegionName();
        this.longitude = smallRegion.getLongitude();
        this.latitude = smallRegion.getLatitude();
    }
}