package progwards.java2.lessons.reflection;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;

public class ClassInspector{

    public static void inspectField (Class<?> clazz1, FileWriter writer) throws IOException {
        Field[] fields = clazz1.getDeclaredFields();
        for (Field f : fields)
            writer.write("\t" + Modifier.toString(f.getModifiers()) + " " + f.getType().getSimpleName() + " " + f.getName() + ";\n");
    }

    public static void inspectConstr (Class<?> clazz1, FileWriter writer) throws IOException {
        Constructor<?>[] constructors = clazz1.getDeclaredConstructors();
        for (Constructor<?> constr : constructors) {
            StringBuilder strB = new StringBuilder();
            Parameter[] param = constr.getParameters();
            strB.append("\t").append((Modifier.toString(constr.getModifiers()) + " " + constr.getDeclaringClass().getSimpleName()).trim()).append(" (");
            for (Parameter par : param)
                strB.append(par.getType().getSimpleName()).append(" ").append(par.getName()).append(", ");
            strB = strB.replace(strB.length() - 2, strB.length() - 1, ") {}\n");
            writer.write(String.valueOf(strB));
        }
    }

    public static void inspectMethod (Class<?> clazz1, FileWriter writer) throws IOException{
        Method[] methods = clazz1.getDeclaredMethods();
        boolean hasParam = false;
        for (Method method : methods) {
            StringBuilder strB = new StringBuilder();
            strB.append("\t").append(Modifier.toString(method.getModifiers())).append(" ").append(method.getName()).append("(");
            Parameter[] param = method.getParameters();
            for (Parameter par : param) {
                strB.append(par.getType().getSimpleName()).append(" ").append(par.getName()).append(", ");
                hasParam = true;
            }
            if (hasParam) {
                strB = strB.replace(strB.length() - 2, strB.length() - 1, ") {}\n");
                hasParam = false;
            } else
                strB.append(") {}\n");
            writer.write(String.valueOf(strB));
        }
    }


    public static void inspect(String clazz) {
        try {
            Class<?> clazz1 = Class.forName(clazz);
            try (FileWriter writer = new FileWriter(clazz1.getSimpleName() + ".java", false)){
                writer.write("class " + clazz1.getSimpleName() + " {\n");
                inspectField(clazz1, writer);
                inspectConstr(clazz1, writer);
                inspectMethod(clazz1, writer);
                writer.write("}");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Не найден класс " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        inspect("progwards.java2.lessons.reflection.Person");
    }
}
