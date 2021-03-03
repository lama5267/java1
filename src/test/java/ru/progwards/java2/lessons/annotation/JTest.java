package ru.progwards.java2.lessons.annotation;

import progwards.java2.lessons.annotation.After;
import progwards.java2.lessons.annotation.Before;
import progwards.java2.lessons.annotation.Test;

import java.lang.reflect.*;

public class JTest {
    void run(String name) throws RuntimeException, ClassNotFoundException {
        Class<?> clazz = Class.forName(name);
        Method [] methods = clazz.getDeclaredMethods();
        Method [] methodsTest = new Method[12];  // массив методов в порядке обработки. По условию задачи до 10 тест методов, а также before и after
        // поэтому сразу установил размер массива - 12
        try {
            for (Method m: methods) {
                if (m.isAnnotationPresent(Before.class)) {
                    if (methodsTest[0] == null)
                        methodsTest[0] = m;
                    else throw new RuntimeException("Две аннотации Before в классе");
                }
                if (m.isAnnotationPresent(After.class)) {
                    if (methodsTest[methodsTest.length-1] == null)
                        methodsTest[methodsTest.length-1] = m;
                    else throw new RuntimeException("Две аннотации After в классе");
                }
                if (m.isAnnotationPresent(Test.class)) {
                    methodsTest[m.getAnnotation(Test.class).Priority()] = m;
                }
            }

            CalculatorTest calcTest = new CalculatorTest();
            for (Method method : methodsTest) {
                if (method != null)
                    method.invoke(calcTest);
            }
            calcTest = null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JTest jTest = new JTest();
        try {
            jTest.run("ru.progwards.java2.lessons.annotation.CalculatorTest");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
