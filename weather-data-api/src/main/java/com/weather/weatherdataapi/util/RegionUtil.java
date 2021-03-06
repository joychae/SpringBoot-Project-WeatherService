package com.weather.weatherdataapi.util;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class RegionUtil {

    private static Dictionary<String, String> aliasAndFullNameDict;
    private static List<String> allValidBigRegionNameList;

    static {
        initializeAliasAndFullNameDict();
        initializeAllBigRegionNameList();
    }

    private static void initializeAliasAndFullNameDict() {
        aliasAndFullNameDict = new Hashtable<>();

        aliasAndFullNameDict.put("강원", "강원도");
        aliasAndFullNameDict.put("경기", "경기도");
        aliasAndFullNameDict.put("경남", "경상남도");
        aliasAndFullNameDict.put("경북", "경상북도");
        aliasAndFullNameDict.put("광주", "광주광역시");
        aliasAndFullNameDict.put("대구", "대구광역시");
        aliasAndFullNameDict.put("대전", "대전광역시");
        aliasAndFullNameDict.put("부산", "부산광역시");
        aliasAndFullNameDict.put("서울", "서울특별시");
        aliasAndFullNameDict.put("세종", "세종특별자치시");
        aliasAndFullNameDict.put("울산", "울산광역시");
        aliasAndFullNameDict.put("인천", "인천광역시");
        aliasAndFullNameDict.put("전남", "전라남도");
        aliasAndFullNameDict.put("전북", "전라북도");
        aliasAndFullNameDict.put("제주", "제주특별자치도");
        aliasAndFullNameDict.put("충남", "충청남도");
        aliasAndFullNameDict.put("충북", "충청북도");
    }

    private static void initializeAllBigRegionNameList() {
        allValidBigRegionNameList = new ArrayList<>();

        allValidBigRegionNameList.add("서울특별시");
        allValidBigRegionNameList.add("부산광역시");
        allValidBigRegionNameList.add("대구광역시");
        allValidBigRegionNameList.add("인천광역시");
        allValidBigRegionNameList.add("광주광역시");
        allValidBigRegionNameList.add("대전광역시");
        allValidBigRegionNameList.add("울산광역시");
        allValidBigRegionNameList.add("세종특별자치시");
        allValidBigRegionNameList.add("경기도");
        allValidBigRegionNameList.add("강원도");
        allValidBigRegionNameList.add("충청북도");
        allValidBigRegionNameList.add("충청남도");
        allValidBigRegionNameList.add("전라북도");
        allValidBigRegionNameList.add("전라남도");
        allValidBigRegionNameList.add("경상북도");
        allValidBigRegionNameList.add("경상남도");
        allValidBigRegionNameList.add("제주특별자치도");
    }

    public static String convertAliasToFullName(String alias) {
        String convertedFullName = aliasAndFullNameDict.get(alias);
        return convertedFullName == null ? alias : convertedFullName;
    }

    public static List<String> getAllValidBigRegionNameList() {
        return allValidBigRegionNameList;
    }

}
