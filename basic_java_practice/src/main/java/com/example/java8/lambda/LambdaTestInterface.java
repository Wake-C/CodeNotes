package com.example.java8.lambda;

/*
为了避免后来的人在这个接口中增加接口函数导致其有多个接口函数需要被实现，变成"非函数接口”，我们可以在这个上面加上一个声明
 @FunctionalInterface, 这样别人就无法在里面添加新的接口函数了
*/
@FunctionalInterface
interface LambdaTestInterface {
    public void run(String x, String y);
}
