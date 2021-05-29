package com.weather.weatherdataapi.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.weather.weatherdataapi.model.dto.ScoreWeightDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Timestamped {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @JsonIgnore
    @Column
    private String identification;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "latest_request_region_id")
    private SmallRegion latestRequestRegion;

    @Column
    private String device;

    @Column
    private int temp;

    @Column
    private int rainPer;

    @Column
    private int weather;

    @Column
    private int humidity;

    @Column
    private int wind;

    @Column
    private int pm10;

    @Column
    private int pm25;

    @Column
    private int corona;

    @Column
    private int uv;

    @Column
    private int pollenRisk;

    @Column
    private int asthma;

    @Column
    private int foodPoison;

    public User(String identification, ScoreWeightDto scoreWeightDto) {
        this.identification = identification;
        this.temp = scoreWeightDto.getTempWeight();
        this.rainPer = scoreWeightDto.getRainPerWeight();
        this.weather = scoreWeightDto.getWeatherWeight();
        this.humidity = scoreWeightDto.getHumidityWeight();
        this.wind = scoreWeightDto.getWindWeight();
        this.pm10 = scoreWeightDto.getPm10Weight();
        this.pm25 = scoreWeightDto.getPm25Weight();
        this.corona = scoreWeightDto.getCoronaWeight();
        this.uv = scoreWeightDto.getUvWeight();
        this.pollenRisk = scoreWeightDto.getPollenRiskWeight();
        this.asthma = scoreWeightDto.getAsthmaWeight();
        this.foodPoison = scoreWeightDto.getFoodPoisonWeight();
    }

    public void updateUserPreference(ScoreWeightDto scoreWeightDto) {
        this.temp = scoreWeightDto.getTempWeight();
        this.rainPer = scoreWeightDto.getRainPerWeight();
        this.weather = scoreWeightDto.getWeatherWeight();
        this.humidity = scoreWeightDto.getHumidityWeight();
        this.wind = scoreWeightDto.getWindWeight();
        this.pm10 = scoreWeightDto.getPm10Weight();
        this.pm25 = scoreWeightDto.getPm25Weight();
        this.corona = scoreWeightDto.getCoronaWeight();
        this.uv = scoreWeightDto.getUvWeight();
        this.pollenRisk = scoreWeightDto.getPollenRiskWeight();
        this.asthma = scoreWeightDto.getAsthmaWeight();
        this.foodPoison = scoreWeightDto.getFoodPoisonWeight();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", identification='" + identification + '\'' +
                ", latestRequestRegion='" + latestRequestRegion + '\'' +
                ", temp=" + temp +
                ", rainPer=" + rainPer +
                ", weather=" + weather +
                ", humidity=" + humidity +
                ", wind=" + wind +
                ", pm10=" + pm10 +
                ", pm25=" + pm25 +
                ", corona=" + corona +
                ", uv=" + uv +
                ", pollenRisk=" + pollenRisk +
                ", asthma=" + asthma +
                ", foodPoison=" + foodPoison +
                '}';
    }

}
