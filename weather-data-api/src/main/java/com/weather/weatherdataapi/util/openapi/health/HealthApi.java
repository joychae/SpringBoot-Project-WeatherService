package com.weather.weatherdataapi.util.openapi.health;

import com.weather.weatherdataapi.exception.FailedFetchException;
import com.weather.weatherdataapi.util.ExceptionUtil;
import com.weather.weatherdataapi.util.openapi.OpenApiUtil;
import com.weather.weatherdataapi.util.openapi.health.asthma.AsthmaItem;
import com.weather.weatherdataapi.util.openapi.health.asthma.AsthmaResponse;
import com.weather.weatherdataapi.util.openapi.health.food_poison.FoodPoisonItem;
import com.weather.weatherdataapi.util.openapi.health.food_poison.FoodPoisonResponse;
import com.weather.weatherdataapi.util.openapi.health.pollen_risk.PollenRiskItem;
import com.weather.weatherdataapi.util.openapi.health.pollen_risk.PollenRiskResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class HealthApi {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final HealthService service;
    private final OpenApiUtil openApiUtil;


    public HealthApi(OpenApiUtil openApiUtil) {
        this.openApiUtil = openApiUtil;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://apis.data.go.kr/1360000/HealthWthrIdxService/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(HealthService.class);
    }

    public AsthmaItem getAsthmaResponse(String areaNo, LocalDate date) throws FailedFetchException {
        try {
            String requestTimeText = date.format(DATE_TIME_FORMATTER) + "06";

            Call<AsthmaResponse> call = service.generateAsthmaResponseCall(openApiUtil.getDataGoKrApiKey(), areaNo, requestTimeText);
            Response<AsthmaResponse> execute;
            AsthmaResponse response;

            try {
                execute = call.execute();
                response = execute.body();
            }
            // retrofit에서 exception이 발생할 때 실행됩니다.
            catch (Exception e) {
                throw new FailedFetchException();
            }

            if (response.getHeader().getResultCode().equals("00") == false) {
                throw new FailedFetchException("값을 정상적으로 조회하지 못했습니다.");
            } else if (response.getBody().getItemList().size() == 0) {
                throw new FailedFetchException("조회한 결과가 없습니다.");
            }

            return response.getBody().getItemList().get(0);
        }
        // 값을 정상적으로 조회하지 못했을 때 실행됩니다.
        catch (FailedFetchException e) {
            log.error(e.getMessage());
            log.error(ExceptionUtil.getStackTraceString(e));

            throw e;
        }

    }

    public FoodPoisonItem getFoodPoisonResponse(String areaNo, LocalDate date) throws FailedFetchException {
        try {
            String requestTimeText = date.format(DATE_TIME_FORMATTER) + "06";

            Call<FoodPoisonResponse> call = service.generateFoodPoisonResponseCall(openApiUtil.getDataGoKrApiKey(), areaNo, requestTimeText);
            Response<FoodPoisonResponse> execute = call.execute();
            FoodPoisonResponse response = execute.body();

            if (response.getHeader().getResultCode().equals("00") == false) {
                throw new FailedFetchException("값을 정상적으로 조회하지 못했습니다.");
            } else if (response.getBody().getItemList().size() == 0) {
                throw new FailedFetchException("조회한 결과가 없습니다.");
            }

            return response.getBody().getItemList().get(0);
        }
        // 값을 정상적으로 조회하지 못했을 때 실행됩니다.
        catch (FailedFetchException e) {
            log.error(e.getMessage());
            log.error(ExceptionUtil.getStackTraceString(e));

            throw e;
        }
        // retrofit에서 exception이 발생할 때 실행됩니다.
        catch (IOException e) {
            log.error(e.getMessage());
            log.error(ExceptionUtil.getStackTraceString(e));

            throw new FailedFetchException("원격 서버에서 응답받은 xml데이터가 FoodPoisonResponse 객체에 매핑될 수 없습니다.");
        }

    }

    public PollenRiskItem getPollenRiskResponse(String areaNo, LocalDate date) throws FailedFetchException {
        try {
            String requestTimeText = date.format(DATE_TIME_FORMATTER) + "06";

            Call<PollenRiskResponse> call = service.generateOakPollenRiskResponseCall(openApiUtil.getDataGoKrApiKey(), areaNo, requestTimeText);
            Response<PollenRiskResponse> execute = call.execute();
            PollenRiskResponse response = execute.body();

            if (response.getHeader().getResultCode().equals("00") == false) {
                throw new FailedFetchException("값을 정상적으로 조회하지 못했습니다.");
            } else if (response.getBody().getItemList().size() == 0) {
                throw new FailedFetchException("조회한 결과가 없습니다.");
            }

            return response.getBody().getItemList().get(0);
        }
        // 값을 정상적으로 조회하지 못했을 때 실행됩니다.
        catch (FailedFetchException e) {
            log.error(e.getMessage());
            log.error(ExceptionUtil.getStackTraceString(e));

            throw e;
        }
        // retrofit에서 exception이 발생할 때 실행됩니다.
        catch (IOException e) {
            log.error(e.getMessage());
            log.error(ExceptionUtil.getStackTraceString(e));

            throw new FailedFetchException("원격 서버에서 응답받은 xml데이터가 PollenRiskResponse 객체에 매핑될 수 없습니다.");
        }

    }

}
