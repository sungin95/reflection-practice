package org.example;

import org.example.annotation.Controller;
import org.example.annotation.Service;
import org.example.model.User;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Controller 애노테이션이 설정돼 있는 모든 클래스를 찾아서 출력한다.
 */
public class ReflectionTest {
    private static final Logger logger = LoggerFactory.getLogger(ReflectionTest.class);
    @Test
    void controllerScan() {
        Set<Class<?>> beans = getTypesAnnotatedWith(List.of(Controller.class, Service.class));

        logger.debug("beans: [{}]", beans);
    }

    @Test
    void showClass() {
        Class<User> clazz = User.class;
        logger.debug(clazz.getName());

        logger.debug("User all declared fields: [{}]", Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList()));
        // [private java.lang.String org.example.model.User.userId, private java.lang.String org.example.model.User.name]

        logger.debug("User all declared constructors: [{}]", Arrays.stream(clazz.getDeclaredConstructors()).collect(Collectors.toList()));
        // [public org.example.model.User(java.lang.String,java.lang.String)]

        logger.debug("User all declared methods: [{}]", Arrays.stream(clazz.getDeclaredMethods()).collect(Collectors.toList()));
        // [public boolean org.example.model.User.equals(java.lang.Object), public int org.example.model.User.hashCode(), public boolean org.example.model.User.equalUser(org.example.model.User)]
    }

    /**
     * 힙 영역에 로드돼 있는 클래스 타입의 객체를 통해 필드/메소드/생성자를 접근 제어자와 상관 없이 사용할 수 있도록 지원하는 API
     */
    @Test
    void load() throws ClassNotFoundException {
        // 1
        Class<User> clazz = User.class;

        // 2
        User user = new User("serverwizard", "홍종완");
        Class<? extends User> clazz2 = user.getClass();

        // 3
        Class<?> clazz3 = Class.forName("org.example.model.User");

        logger.debug("clazz: [{}]", clazz);
        logger.debug("clazz2: [{}]", clazz2);
        logger.debug("clazz3: [{}]", clazz3);

        assertThat(clazz == clazz2).isTrue();
        assertThat(clazz2 == clazz3).isTrue();
        assertThat(clazz3 == clazz).isTrue();

    }

    private static Set<Class<?>> getTypesAnnotatedWith(List<Class<? extends Annotation>> annotations) {
        Reflections reflections =  new Reflections("org.example"); // org.example 아래 모든 파일에 대해서 reflection 사용 의미

        Set<Class<?>> beans = new HashSet<>();
//        System.out.println(annotations); => [interface org.example.annotation.Controller, interface org.example.annotation.Service]
        annotations.forEach(annotation -> beans.addAll(reflections.getTypesAnnotatedWith(annotation)));

        return beans;
    }
}
