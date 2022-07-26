package com.example.java8.lambda;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Alexis
 * @data 2021-10-21
 * @description :
 */
public class LambdaDemo {


    @Test
    public  void test() {
        //封装多个相同返回值的方法  避免多个 if
        List<Function<String, String>> functionList =new ArrayList<>();
        functionList.add(this::A);
        functionList.add(this::B);
        functionList.add(this::C);

        for (int i = 0; i < functionList.size(); i++) {
            final String apply = functionList.get(i).apply(i +"");
        }
    }

    public String A(String s){
        System.out.println(s);
        return null;
    }
    public String B(String s){
        System.out.println(s);
        return null;
    }
    public String C(String s){
        System.out.println(s);
        return null;
    }
}
