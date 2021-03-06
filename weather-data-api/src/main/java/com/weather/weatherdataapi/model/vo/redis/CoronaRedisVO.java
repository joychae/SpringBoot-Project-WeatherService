package com.weather.weatherdataapi.model.vo.redis;

import com.weather.weatherdataapi.model.entity.info.CoronaInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;

@Getter
@RedisHash("corona")
@NoArgsConstructor
public class CoronaRedisVO {
    @Id
    private String bigRegionAdmCode;

    private long id;

    private long bigRegionId;

    private LocalDate date;

    private int newLocalCaseCount;

    private int newForeignCaseCount;

    public CoronaRedisVO(CoronaInfo coronaInfo) {
        this.bigRegionAdmCode = coronaInfo.getBigRegion().getAdmCode();

        this.id = coronaInfo.getId();
        this.bigRegionId = coronaInfo.getBigRegion().getId();
        this.date = coronaInfo.getDate();
        this.newLocalCaseCount = coronaInfo.getNewLocalCaseCount();
        this.newForeignCaseCount = coronaInfo.getNewForeignCaseCount();
    }

    @Override
    public String toString() {
        return "CoronaRedisVO{" +
                "bigRegionAdmCode='" + bigRegionAdmCode + '\'' +
                ", id=" + id +
                ", bigRegionId=" + bigRegionId +
                ", date=" + date +
                ", newLocalCaseCount=" + newLocalCaseCount +
                ", newForeignCaseCount=" + newForeignCaseCount +
                '}';
    }

}
