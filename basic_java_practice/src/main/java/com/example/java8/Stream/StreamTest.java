package com.example.java8.Stream;

import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

public class StreamTest {


    //生成流
    @Test
    public void test() {
        String         array = "啊哈,asd,QQ,哦哦";
        Stream<String> split = Stream.of(array.split(","));
        System.out.println(split.collect(Collectors.toList()).toArray()[0]);

        //list map 自带流生成
        List<Integer> list = new ArrayList();
        list.add(88);
        list.add(99);
        list.forEach(System.out::println);
        list.stream().map(x -> ++x).forEach(System.out::println);
        //流不会改变源对象，只会返回一个新的流，流不存储对象，元素存储在底层集合中，需要时才会产生
        list.forEach(System.out::println);
    }

    //无状态转换
    @Test
    public void test2() {
        String array = "QQ,WW,WWW,EE,RR,qqqqqqqq,EEEEEEEEEEEEE";

        //长度为5
        System.out.println("过滤长度小于5的");
        Stream<String> words = Stream.of(array.split(",")).filter(x -> x.length() > 4);
        words.collect(Collectors.toList()).forEach(System.out::println);

        //map函数设置一个对每一个元素都执行的方法
        // 返回一个新stream  转换小写
        System.out.println("返回一个新stream  转换小写");
        Stream<String> lowerWords = Stream.of(array.split(",")).map(String::toLowerCase);
        lowerWords.collect(Collectors.toList()).forEach(System.out::println);

        //包含每个单词第一个字符
        System.out.println("返回每个单词第一个字符");
        Stream<Character> firstChar = Stream.of(array.split(",")).map(s -> {
            return s.charAt(0);
        });
        firstChar.collect(Collectors.toList()).forEach(System.out::println);

        //flatMap和map区别
        System.out.println("Steam中flatMap和map区别");
        //map返回的是 [{a,b,c}{c,d,f}]
        System.out.println("map返回的是会自动嵌套");
        final Stream<Stream<Character>> streamStream = Stream.of(array.split(",")).map(w -> characterStream(w));
        streamStream.forEach(m -> m.forEach(System.out::print));
        System.out.println("flatMap返回的是组合");
        final Stream<Character> characterStream = Stream.of(array.split(",")).flatMap(w -> characterStream(w));
        characterStream.forEach(System.out::print);

        //generate创建无限流 接受无参数函数
        //limit返回包含n个流
        System.out.println("limit返回包含n个流");
        final Stream<Long> limit = Stream.generate(Math::random).map(x -> x * 100).map(Math::round).limit(5);
        limit.collect(Collectors.toList()).forEach(System.out::println);

        //skip 抛弃前面的N个元素 前面的为空
        System.out.println("抛弃前面的N个元素 前面的为空");
        final Stream<String> skip = Stream.of(array.split(",")).skip(5);
        skip.collect(Collectors.toList()).forEach(System.out::println);

        //concat 拼接流
        System.out.println("concat 拼接流");
        final Stream<String> concat = Stream.concat(Stream.of("hello"), Stream.of("test"));
        concat.collect(Collectors.toList()).forEach(System.out::println);

        System.out.println("生成List");
        Stream.iterate(1.0, p -> ++p).limit(5).collect(Collectors.toList()).forEach(System.out::println);
    }

    public static Stream<Character> characterStream(String s) {
        List<Character> result = new ArrayList<>();
        for (char c : s.toCharArray()) {
            result.add(c);
        }
        return result.stream();
    }

    //有状态转换  获取结果依赖之前的元素
    @Test
    public void test3() {
        String               array = "qqqqqqqq,QQ,WW,WWW,EE,RR,EEEEEEEEEEEEE";
        final Stream<String> words = Stream.of(array.split(","));

        //distinct  去重
        System.out.println("distinct  去重");
        final Stream<String> distinct = Stream.of("hello", "hello", "world", "test", "test").distinct();
        distinct.collect(Collectors.toList()).forEach(System.out::println);

        //sorted
        System.out.println("sorted 排序");
        final List<String> sorted = words.sorted(Comparator.comparing(String::length).reversed()).collect(Collectors.toList());
        sorted.forEach(System.out::println);
    }

    //聚合方法  Optional的使用
    @Test
    public void test4() {
        String               array = "qqqqqqqq,QQ,WW,WWW,EE,RR,EEEEEEEEEEEEE";
        final Stream<String> words = Stream.of(array.split(","));


        List<String> list = Arrays.asList("a", "b");
        list.forEach(String::toUpperCase);
        //                                                                       Optional.of(list).flatMap(List::stream).map()
        list.forEach(System.out::println);
        //流不会改变源对象，只会返回一个新的流，流不存储对象，元素存储在底层集合中，需要时才会产生
        list.forEach(System.out::println);


        //最大值
        System.out.println("最大值");
        final Optional<String> optional = words.max(String::compareToIgnoreCase);
        if (optional.isPresent()) {
            System.out.println(optional.get());
        }
        final OptionalInt max = IntStream.of(22, 111, 13, 12, 3).max();
        if (max.isPresent()) {
            System.out.println(max.getAsInt());
        }

        //findFrist查找第一个值
        System.out.println("findFrist查找第一个值");
        String digits = "one,two,three,four,five,six,seven,eight,nine,ten";
        //第一个s开头的
        final Optional<String> findFrist = Stream.of(digits.split(",")).filter(s -> s.startsWith("s")).findFirst();
        if (findFrist.isPresent()) {
            System.out.println(findFrist.get());
        }
        //并行查找 findAny 任何地方发现第一个匹配都结束
        final Optional<String> findAny = Stream.of(digits.split(",")).parallel().filter(s -> s.startsWith("t")).findAny();
        if (findAny.isPresent()) {
            System.out.println("s2------" + findAny.get());
        }

        //只想知道流中是否有匹配元素  anyMatch
        final boolean anyMatch = Stream.of(digits.split(",")).parallel().anyMatch(s -> s.startsWith("f"));
        if (anyMatch) {
            System.out.println("match");
        }

        //allMatch noneMatch 不匹配return true
        final boolean noneMatch = Stream.of(digits.split(",")).parallel().noneMatch(s -> s.equals("ffff"));
        if (noneMatch) {
            System.out.println("不匹配");
        }

        final boolean allmatch = Stream.of(digits.split(",")).parallel().allMatch(s -> s.equals("one"));
        if (allmatch) {
            System.out.println(allmatch);
        }

        //跳过第一个s个开头的
        final Stream<String> s5 = Stream.of(digits.split(",")).filter(s -> s.startsWith("s")).skip(1);
        s5.collect(Collectors.toList()).forEach(System.out::println);

        //使用Optional值
        List result = new ArrayList();
        //如果存在值结果添加到list
        findAny.ifPresent(result::add);
        result.forEach((x) -> System.out.println("ifPressent的使用------------" + x));
        //对结果进行处理 map
        final Optional<Boolean> aBoolean = findAny.map(result::add);
        if (aBoolean.isPresent()) {
            System.out.println("添加成功-------" + result.toString());
        }

        //使用Optional值
        //如果存在值结果添加到list
        Person2 Person2 = new Person2();
        Optional.ofNullable(result).ifPresent((x) -> {
            Person2.setId(1).setNumber(88);
        });
        System.out.println("ifPressent的TestEntity使用------------" + Person2.toString());


        //orElse 如果没有自定义返回值
        final Optional<String> orelse = Stream.of(digits.split(",")).filter(s -> s.equals("one")).findAny();
        if (orelse.isPresent()) {
            System.out.println("值存在----" + orelse.get());
        }
        final String testOrelse = orelse.orElse("查找的值不存在");
        if (testOrelse != null) {
            System.out.println(testOrelse);
        }
        //flatMap  组合可选值函数 只有上一个成功才会执行下一个。 利用optional可以创建一个调用流水线
        // 可以替代if if if 嵌套   orelse 等于else处理 ，elseThrow抛出异常
        final Optional<Double> aDouble  = inverse(2.2).flatMap(StreamTest::squareRoot);
        final Double           aDouble2 = Optional.of(0.0).flatMap(StreamTest::inverse).flatMap(StreamTest::squareRoot).orElse(2.22222);
        System.out.println("flatMap测试--------" + aDouble.get());
        System.out.println("flatMap测试2--------" + aDouble2);  //no value present


        //嵌套示例
        //获取name转为大写
        System.out.println("optional 嵌套使用示例");
        final Person           build         = Person.builder().id(22).testName("test").build();
        final Optional<String> optionalTest1 = Optional.of(build).map(Person::getTestName).filter((x) -> x.equals("test")).map(s -> s.concat("原来可以这么晚")).map(String::toUpperCase);
        optionalTest1.ifPresent(System.out::println);
        System.out.println(build.toString());

        //获取不到testName没有值从orElseGet获取
        System.out.println("optional 嵌套使用示例--获取不到testName没有值从orElseGet获取");
        final Person build2        = Person.builder().id(23).build();
        final String optionalTest2 = Optional.of(build2).map(Person::getTestName).map(String::toUpperCase).orElseGet(() -> new String("获取失败"));
        System.out.println(optionalTest2);


        //修改实例
        /*   List<WxMateralsDTO> list = new ArrayList<>();
        WxResImages wxResImages =new WxResImages();
        wxResImages.setId(33).setImageUrl("httpwww.baidu.com/test");
        WxMateralsDTO wxMateralsDTO =new WxMateralsDTO();
        wxMateralsDTO.setId(1).setContent("测试");
        wxMateralsDTO.setImages(wxResImages);

        WxMateralsDTO wxMateralsDTO2 =new WxMateralsDTO();
        wxMateralsDTO2.setId(1).setContent("测试");
        list.add(wxMateralsDTO);
        list.add(wxMateralsDTO2);

        list.stream().map(x->x.getImages()).filter(image->!image.getImageUrl().contains("http")).forEach(imagef->{
                imagef.setImageUrl("http//"+imagef.getImageUrl());
            }
        );
        list.forEach(System.out::println);*/


    }

    //聚合操作
    @Test
    public void test5() {
        //求和
        Integer[]               numbers  = {1, 3, 4, 5, 6, 7, 8};
        Integer[]               numbers2 = new Integer[0];
        final Optional<Integer> sum      = Stream.of(numbers).reduce(Integer::sum);
        System.out.println(sum.get());
        final Optional<Integer> max = Stream.of(numbers).reduce(Integer::max);
        System.out.println(max.get());
        //减法非联合操作 避免   6-3-2   !=  6-(3-2)
        // 使用0做标识 等于 0+1+2+3  如果流为空 返回标识
        final Integer reduce = Stream.of(numbers2).reduce(0, (x, y) -> x + y);
        System.out.println(reduce);

        //累加器 对流的某个属性求和
        String        poetry[] = {"Gibran", "Tagore"};
        final Integer reduce1  = Stream.of(poetry).reduce(0, (total, word) -> total + word.length(), (total1, total2) -> total1 + total2);
        System.out.println(reduce1);


    }

    //收集结果
    @Test
    public void test6() {
        String[]  poetry  = {"Gibran", "Tagore", "liBai"};
        Integer[] numbers = {1, 3, 4, 5, 6, 7, 8};
        // toArray  直接用回返回object[]
        final String[] strings = Stream.of(poetry).toArray(String[]::new);

        //Collection返回集合
        final List<String> collect = Stream.of(poetry).collect(Collectors.toList());
        //set
        final Set<String> collect1 = Stream.of(poetry).collect(toSet());
        //控制得到的set类型
        final TreeSet<String> collect2 = Stream.of(poetry).collect(Collectors.toCollection(TreeSet::new));

        //将字符串收集
        final String collect3 = Stream.of(poetry).collect(Collectors.joining());
        System.out.println(collect3);

        //加分隔符
        final String collect4 = Stream.of(poetry).collect(Collectors.joining("--"));
        System.out.println(collect4);

        //字符串以外对象先转换为string
        final String collect5 = Stream.of(numbers).map(Object::toString).collect(Collectors.joining());
        System.out.println(collect5);

        //流结果聚合为 平均值 最小值 最大值  summarizing(int / double /long)
        final IntSummaryStatistics summary       = Stream.of(poetry).collect(Collectors.summarizingInt(String::length));
        double                     average       = summary.getAverage();
        double                     maxWordLength = summary.getMax();
        System.out.println("average=" + average + "max=" + maxWordLength);


        //收集到map
        //将entity中的id放入map
        List<Person> list = new ArrayList<>();
        list.add(Person.builder().id(1).testName("ceshi1").build());
        list.add(Person.builder().id(2).testName("ceshi2").build());
        list.add(Person.builder().id(3).testName("ceshi3").build());
        final Map<Integer, Person> collect6 = list.stream().collect(Collectors.toMap(Person::getId, Function.identity()));
        collect6.forEach((x, y) -> System.out.println("key=" + x + "value=" + y));

        //key重复的处理 以及返回指定map  第四个参数为指定返回类型
        list.stream().collect(Collectors.toMap(Person::getId, Function.identity(),
                (existingValue, newValue) -> {
                    throw new IllegalStateException();
                },
                TreeMap::new));

        final Map<Integer, Person> ceshi1 = Stream.of(Person.builder().id(1).testName("ceshi1").build())
                .collect(Collectors.toMap(Person::getId, Function.identity()));
        System.out.println("mapTest" + ceshi1.get(1));

        final Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
        //去除重复key
        final Map<String, String> collect7 = locales.collect(Collectors.toMap(l -> l.getDisplayLanguage(), l -> l.getDisplayLanguage(l), (existingValue, newValue) -> existingValue));
        collect7.forEach((k, v) -> System.out.println(k + v));

    }

    //分组和分片
    @Test
    public void test7() {
        //分组 相同key  groupingBy
        final Stream<Locale> locales2 = Stream.of(Locale.getAvailableLocales());
        // final Map<String, List<Locale>> collect8 = locales2.collect(Collectors.groupingBy(Locale::getCountry));

        //分组返回 set or list
        final Map<String, Set<Locale>> collect8 = locales2.collect(groupingBy(Locale::getCountry, toSet()));
        collect8.forEach((k, v) -> System.out.println("分组key=" + k + "value=" + v));

        //partitioningBy 切割为符合要求true 和false
        final Map<Boolean, List<Locale>> en = Stream.of(Locale.getAvailableLocales()).collect(Collectors.partitioningBy(l -> l.getLanguage().equals("en")));
        en.get(true).forEach(System.out::println);
        System.out.println("不是英文地区");
        en.get(false).forEach(System.out::println);

        //分组后元素处理  couting返回总个数
        final Map<String, Long> countrys = Stream.of(Locale.getAvailableLocales()).collect(groupingBy(Locale::getCountry, counting()));
        countrys.forEach((k, v) -> System.out.println("每个国家语言数目key=" + k + "value=" + v));

        //summing  求和
        // Stream.of(Locale.getAvailableLocales()).collect(groupingBy(Locale::getCountry,summingInt(StreamTest::sum)));

        //maxBy最大  根据州分组 每个洲人口最多人数
        // cities.collection(groupingBy(City::getState,maxBy(Comparator.comparing(City::getPopulation))));

        //mapping 应用到downStream结果上
        //每个洲按名称最大长度聚合
       /* cities.collect(groupingBy(City::getState,
                mapping(City::getName,
                        maxBy(Comparator.comparing(String::length)))))*/

        //获取指定国家语言集合
        final Map<String, Set<String>> collect = Stream.of(Locale.getAvailableLocales()).collect(
                groupingBy(l -> l.getDisplayCountry(),
                        mapping(l -> l.getDisplayLanguage(),
                                toSet())));
        System.out.println("america people language" + collect.get("美国"));


        List<Person2> list = new ArrayList<>();
        list.add(Person2.builder().id(1).number(2).build());
        list.add(Person2.builder().id(2).number(4).build());
        list.add(Person2.builder().id(2).number(4).build());
        //groupingby mapping返回是int or long double 用summaryStatistics处理
        // Stream.of(list).collect(groupingBy(Person2::getId,summarizingInt(Person2::getNumber)))

        //  ,拼接number
        //list.stream().collect(groupingBy(Person2::getId,mapping(Person2::getNumber,joining(",")))).var;
    }

    //原始类型流
    @Test
    public void test8() {
        //intStream  doubleStream longStream 相应对象生成流比较高效
        final IntStream intStream = IntStream.of(1, 2, 3, 4);

        //生成0到100不包含100
        final int[] ints = IntStream.range(97, 100).toArray();
        //生成150到200不包含200
        final int[] ints1 = IntStream.rangeClosed(195, 200).toArray();
        for (int anInt : ints) {
            System.out.println(anInt);
        }
        for (int anInt : ints1) {
            System.out.println(anInt);
        }
    }


    @Test
    public void main222() {
        List<WxMateralsDTO> materalsList  = new ArrayList<>();
        WxMateralsDTO       wxMateralsDTO = new WxMateralsDTO();
        materalsList.add(wxMateralsDTO);
        materalsList.get(0).setContent("<span style=\"font-size:12.6316px;white-space:normal;\">欢迎酷酷的你加入多粉星球</span><br style=\"font-size:12.6316px;white-space:normal;\" /><br style=\"font-size:12.6316px;white-space:normal;\" /><span style=\"font-size:12.6316px;white-space:normal;\">点击#星球通行证#，即刻非凡！</span><br style=\"font-size:12.6316px;white-space:normal;\" /><br style=\"font-size:12.6316px;white-space:normal;\" /><a href=\"https://member.duofriend.com/html/member/phone/index.html#/home/16549\" style=\"font-size:12.6316px;white-space:normal;\">*点我领取会员卡，会员中心可领取优惠券哦*</a><br />");
        System.out.println(materalsList.get(0).getContent());
        final String s1 = materalsList.stream().findFirst().map(WxMateralsDTO::getContent).map(s -> {
            String content = s.replaceAll("\r", "");
            content = content.replaceAll("\f", "");
            content = content.replaceAll("<br./>", "");
            content = content.replaceAll("&nbsp;", "\b");
            return content;
        }).orElse(null);
        System.out.println("更后----------------------------------------");
        System.out.println(s1);

    }

    @Data
    public class WxMateralsDTO {
        String content;
    }

    //并行流
    @Test
    public void test9() {
        //parallel方法将任何串行流转为并行
        String               wordArray[] = {"test", "parallel"};
        final Stream<String> parallel    = Stream.of(wordArray).parallel();
    }


    //peek方法产生一个与original Stream  相同的 stream

    public static Optional<Double> inverse(Double x) {
        return x == 0 ? Optional.empty() : Optional.of(1 / x);
    }

    public static Optional<Double> squareRoot(Double x) {
        return x < 0 ? Optional.empty() : Optional.of(Math.sqrt(x));
    }
}


