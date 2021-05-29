package com.weather.weatherdataapi.model.entity.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.weather.weatherdataapi.model.entity.BigRegion;
import com.weather.weatherdataapi.model.entity.Timestamped;
import com.weather.weatherdataapi.model.vo.redis.HealthAsthmaRedisVO;
import com.weather.weatherdataapi.service.info.LivingUvService;
import com.weather.weatherdataapi.util.openapi.health.asthma.AsthmaItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class HealthAsthmaInfo extends Timestamped {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "big_region_id")
    private BigRegion bigRegion;

    @JsonIgnore
    @Column
    private LocalDate date;

    @Column
    private Integer today;

    @Column
    private Integer tomorrow;

    @Column
    private Integer theDayAfterTomorrow;

    public HealthAsthmaInfo(AsthmaItem item, BigRegion bigRegion) {
        this.bigRegion = bigRegion;

        this.date = LivingUvService.convertDateFromString(item.getDate());
        this.today = item.getToday();
        this.tomorrow = item.getTomorrow();
        this.theDayAfterTomorrow = item.getTheDayAfterTomorrow();
    }


    public HealthAsthmaInfo(HealthAsthmaRedisVO redisVO, BigRegion bigRegion) {
        this.bigRegion = bigRegion;

        this.id = redisVO.getId();
        this.date = redisVO.getDate();
        this.today = redisVO.getToday();
        this.tomorrow = redisVO.getTomorrow();
        this.theDayAfterTomorrow = redisVO.getTheDayAfterTomorrow();
    }

}
