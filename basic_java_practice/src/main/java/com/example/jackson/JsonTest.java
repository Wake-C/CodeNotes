package com.example.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.internal.org.objectweb.asm.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JsonTest {
    public static void main(String[] args) throws Exception {
        ObjectMapper  mapper  = new ObjectMapper();
        final MyValue myValue = mapper.readValue("{\"name\":\"Bob\", \"age\":13}", MyValue.class);
        System.out.println(myValue.toString());

        final String json = mapper.writeValueAsString(myValue);
        System.out.println(json);


        final Map map1 = mapper.readValue("{\n" +
                "  \"id\": 22,\n" +
                "  \"name\": \"饺子\"\n" +
                "}", Map.class);
        System.out.println(map1);


        JsonTest jsonTest = new JsonTest();
        jsonTest.writeJSon(myValue);
        final MyValue myValue1 = jsonTest.readJson();
        System.out.println(myValue1);


        //Jackson 数据绑定   从java类型到json
        final HashMap<String, Integer> map = new HashMap<>();
        map.put("饺子", 1);
        map.put("汤圆", 2);
        mapper.writeValue(new File("student.json"), map);

        final Map map2 = mapper.readValue(new File("student.json"), Map.class);
        System.out.println(map2);
        System.out.println(map2.get("饺子"));

        //    数据绑定 从json到java类型
        MyValueData myValueData = new MyValueData();
        myValueData.setMyValue(myValue);
        myValueData.setName("中元节");
        myValueData.setVerified(true);
        myValueData.setMarks(new int[]{1, 3, 5});
        Map valueDataMap = new HashMap();
        valueDataMap.put("myValueData", myValueData);

        mapper.writeValue(new File("myValue.json"), myValueData);

        final Map map3 = mapper.readValue(new File("myValue.json"), Map.class);
        System.out.println(map3);


        String         jsonString = "{\"name\":\"Mahesh Kumar\", \"age\":21,\"verified\":false,\"marks\": [100,90,85]}";
        final JsonNode jsonNode   = mapper.readTree(jsonString);
        final JsonNode name       = jsonNode.path("name");
        System.out.println("Name: " + name.asText());

        log.info("注解测试");
        final JsonAnnotation build = JsonAnnotation.builder().my__name("my__name").my_age2("my__age__18").myDate(new Date()).build();
        final String         jsonStr     = mapper.writeValueAsString(build);
        System.out.println(jsonStr);


    }

    public void writeJSon(MyValue object) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("student.json"), object);
    }

    public MyValue readJson() throws Exception {
        ObjectMapper  objectMapper = new ObjectMapper();
        final MyValue myValue      = objectMapper.readValue(new File("student.json"), MyValue.class);
        return myValue;
    }
}
