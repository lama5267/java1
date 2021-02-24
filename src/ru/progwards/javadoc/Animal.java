package progwards.javadoc;
/**
 * Класс животное, прародитель всех животных, в том числе, {@link Cow}
 * @author Progwards
 * @version 1.0
 * @see Cow
 *
 */

public abstract class Animal {
    /**
     * свойство - вес
     */
    private double weight;
    /**
     * свойство - название животного
     */
    private String name;

    /**
     * Конструктор по умолчанию
     * @see #Animal(double, String)
     */
    public Animal() {
    }

    /**
     * Конструктор с параметрами
     * @param weight вес животного
     * @param name название животного
     * @see Animal#Animal()
     *
     */
    public Animal(double weight, String name) {
        this.weight = weight;
        this.name = name;
    }

    /**
     * @return вес животного
     */
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Описание fly
     * @deprecated Выяснилось, что мало кто летает,
     * так что метод устарел и скоро будет удалён
     */
    @Deprecated
    abstract public void fly();
}

