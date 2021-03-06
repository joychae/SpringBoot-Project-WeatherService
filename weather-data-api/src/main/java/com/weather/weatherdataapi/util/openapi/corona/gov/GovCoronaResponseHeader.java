package com.weather.weatherdataapi.util.openapi.corona.gov;

import lombok.Getter;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Getter
@Root(name = "header")
public class GovCoronaResponseHeader {
    @Element
    private String resultCode;

    @Element
    private String resultMsg;
}
