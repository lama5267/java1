package progwards.java2.lessons.reflection;

public class Person {
    private String name;
    private int age;
    public final boolean hasChild;
    protected int weight;
    public static String bornPlace;
    private static final boolean man = true;

    Person (String name, boolean hasChild, String bornPlace) {
        this.name = name;
        this.hasChild = hasChild;
        Person.bornPlace = bornPlace;
    }

    private Person(String name, int age, boolean hasChild) {
        this.name = name;
        this.age = age;
        this.hasChild = hasChild;
    }

    public Person(String name, int age, boolean hasChild, int weight) {
        this.name = name;
        this.age = age;
        this.hasChild = hasChild;
        this.weight = weight;
    }



    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hasChild=" + hasChild +
                ", weight=" + weight +
                '}';
    }
}
