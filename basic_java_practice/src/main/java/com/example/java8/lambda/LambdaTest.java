package com.example.java8.lambda;

import com.example.java8.Stream.Person;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lambda practice
 */
@SuppressWarnings("ALL")
public class LambdaTest {


    @Test
    public void test1() {
        //函数式接口  把只有一个方法的接口实例化
        Runnable sleeper = () -> {
            System.out.println("The test  of Df ");
        };
        sleeper.run();
    }

    @Test
    public void test2() {
        LambdaTestInterface test = (x, y) -> {
            System.out.println(x);
            System.out.println(y);
        };
        test.run("father", "Mama");
    }

    @Test
    public void test3() {
        //方法调用
        Thread thread = new Thread(this::provide);
        thread.run();
    }

    public void provide() {
        System.out.println("测试lomdba方法引用");
    }

    @Test
    public void test4() {
        catchVariable(1000, 4);
    }

    public void catchVariable(int x, int y) {
        Runnable run = () -> {
            int b = 0;
            for (int i = 0; i < x; i++) {
                b += (x + y);
                // lambda expression 无法修改外部变量  线程不安全 使用数组可以修改 a[0]++;
                //x++;
                System.out.println(b);
            }
            System.out.println(b);
        };
        run.run();
    }

    //将一个函数应用到集合每一个元素上
    @Test
    public void test5() {
        List list = new ArrayList();
        list.add("测试");
        list.add("测试2");
        list.add("测试3");
        list.add("测试4");
        list.add("测试5");
        list.forEach(System.out::println);
        List list2 = new ArrayList();
        list.forEach(list2::add);
        list2.forEach(System.out::println);
    }

    @Test
    public void test6() {
        //更好的实践 静态方法  and lambda
        String strings[] = {"ttttttttt", "mamamama", "aaaa", "aaaaaaaaaaaaaaa"};
        for (String string : strings) {
            System.out.println(string);
        }
        //静态方法实现更好
        //	Arrays.sort(strings,(x,y)->Integer.compare( x.length(),y.length() )  );
        Arrays.sort(strings, Comparator.comparing(String::length));
        for (String string : strings) {
            System.out.println(string);
        }
    }

    @Test
    public void test7() {
        //引用 predicate和consume来省略自己创建函数式接口
        List<Person> personList = Lists.newArrayList(
                new Person(1, "测试名"),
                new Person(2, "测试名2"),
                new Person(3, "测试名3"),
                new Person(4, "3333"));
        PredicateAndConsumerTest.CheckAndExecute(personList, person -> person.getTestName().contains("3"), person -> person.setTestName("corrected three"));
        personList.forEach(person -> System.out.println(person.getTestName()));

        personList.stream().filter(person -> person.getTestName().contains("2")).forEach(person -> System.out.println(person.getTestName()));
    }


    @Test
    public void test8() {
        repeat(5, () -> System.out.println("my shot "));
    }

    public void repeat(int n, Runnable run) {
        for (int i = 0; i < n; i++) {
            run.run();
        }
    }

    /**
     * List 生成方式
     */
    @Test
    public void test9() {
        //不可变
        final List<String> test = Arrays.asList("test", "222", "333");
        //封装为可变
        final ArrayList<Integer> integers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 9));
        //复制三遍
        final ArrayList copiList = new ArrayList<>(Collections.nCopies(3, "哈哈"));
        final ArrayList arrayList = new ArrayList<Object>() {{
            add("tom");
            add("tom2");
            add("tom3");
        }};
        final List<String> collect = Stream.of("all wokr don't play make jack dull", "try to find a better way ").collect(Collectors.toList());
    }

    /**
     * 组合  返回函数
     */
    @Test
    public void test10() {
        String test = "Ae";
        final UnaryOperator<String> compose = compose(String::toUpperCase, o -> {
            return o + "haha";
        });
        System.out.println(compose.apply(test));
    }

    public static UnaryOperator<String> compose(UnaryOperator<String> op1, UnaryOperator<String> op2) {
        return t -> op1.apply(op2.apply(t));
    }
}
