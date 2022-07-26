package com.example.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;


@Data
@Builder
public class JsonAnnotation {

    /**
     * 用于属性，把属性的名称序列化时转换为另外一个名称。示例：
     */
    @JsonProperty("myName")
    private String my__name;

    @JsonProperty("annotationTest")
    private String my_age2;

    /**
     * 用于属性或者方法，把属性的格式序列化时转换成指定的格式。示例：
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy年MM月dd号 HH:mm")
    private Date myDate;


}
