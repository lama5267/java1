package progwards.java2.lessons.reflection;

import java.lang.reflect.*;

import static java.lang.reflect.Modifier.*;

public class GettersAndSetters {

    public static void check(String className){
        boolean hasGetter = false;
        boolean hasSetter = false;
        try {
            Field[] fields = Class.forName(className).getDeclaredFields();
            Method[] methods = Class.forName(className).getMethods();
            for (Field field: fields) {
                if (isPrivate(field.getModifiers()) && !isStatic(field.getModifiers())) {
                    for (Method method : methods) {
                        if (isStatic(method.getModifiers()) || method.getName().equals("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1)))
                            hasGetter = true;
                        if (isStatic(method.getModifiers()) || method.getName().equals("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1)))
                            hasSetter = true;
                    }
                    if (!hasGetter)
                        System.out.println("public " + field.getType().getSimpleName() + " get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
                    if (!isFinal(field.getModifiers()) && !hasSetter)
                        System.out.println("public void set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + " (" + field.getType().getSimpleName() + " " + field.getName() + ")");
                    hasGetter = false;
                    hasSetter = false;
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Не найден класс " + ex.getMessage());
        }
    }

    public static void main(String[] args){
        check("progwards.java2.lessons.reflection.Person");
    }
}
