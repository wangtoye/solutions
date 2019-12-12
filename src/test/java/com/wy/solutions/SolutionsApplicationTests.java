package com.wy.solutions;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class SolutionsApplicationTests {


    @Test
    void test() {
        Map<String, Object> map1 = null;//new HashMap<>(4);
//        map1.put("test1", 1);
//        map1.put("test2", "2");
//        map1.put("test3", 3L);
//        map1.put("test4", 4D);

        Map<String, Object> map2 = new HashMap<>(4);
        map2.put("test1", 5);
        map2.put("test2", "6");
        map2.put("test5", 7L);
        map2.put("test6", 8D);

        Map<String, List<Object>> map =
                Stream.concat(Optional.ofNullable(map1).orElseGet(HashMap::new).entrySet().stream(),
                        Optional.of(map2).orElseGet(HashMap::new).entrySet().stream())
                        .collect(Collectors.toMap(Map.Entry::getKey, value -> Lists.newArrayList(value.getValue())
                                , (List<Object> list1, List<Object> list2) -> {
                                    list1.addAll(list2);
                                    return list1;
                                }));
        map.forEach((k, v) -> System.out.println(k + ":" + v));
    }
}
