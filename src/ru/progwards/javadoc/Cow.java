package progwards.javadoc;

/**
 * Корова, она даёт молоко и не умеет летать
 */
public class Cow extends Animal {
    /**
     * свойство - количество молока в день
     */
    public double milk;

    /**
     * Конструктор коровы
     * @param weight - вес
     * @param milk - даёт литров молока в день
     */
    public Cow(double weight, double milk) {
        super(weight, "Корова");
        this.milk = milk;
    }

    /**
     * @throws RuntimeException Корова не летает, поэтому бросаем исключение
     */
    @Override
    public void fly() throws RuntimeException {
        throw new RuntimeException("Я корова и не умею летать");
    }

    /**
     * Определяет удойность коровы как отношение её веса к количеству молока в день
     * @return удойность коровы
     * @exception ArithmeticException Вес коровы должен быть положительным
     */
    public double milkForWeght() throws ArithmeticException {
        if (getWeight() <= 0)
            throw new ArithmeticException("У коровы не может быть вес " + getWeight());
        return milk / getWeight();
    }
}
