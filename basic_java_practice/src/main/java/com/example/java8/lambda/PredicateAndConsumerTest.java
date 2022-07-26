package com.example.java8.lambda;/**
 * @author
 * @version
 * @create
 */

import com.example.java8.Stream.Person;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 利用函数式接口包 简化代码，省去自己创建接口类
 *
 * @author zhanbing
 * @Ddate 2018年11月26日
 **/
public class PredicateAndConsumerTest {
    public static void CheckAndExecute(List<Person> personList, Predicate<Person> predicate, Consumer<Person> consume) {
        personList.forEach(p -> {
            if (predicate.test(p)) {
                consume.accept(p);
            }
        });
    }
}
