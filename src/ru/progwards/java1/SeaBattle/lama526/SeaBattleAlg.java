package ru.progwards.java1.SeaBattle.lama526;

import ru.progwards.java1.SeaBattle.SeaBattle;
import ru.progwards.java1.SeaBattle.SeaBattle.FireResult;

public class SeaBattleAlg {
    // Тестовое поле создаётся конструктором
    //     SeaBattle seaBattle = new SeaBattle(true);
    //
    // Обычное поле создаётся конструктором по умолчанию:
    //     SeaBattle seaBattle = new SeaBattle();
    //     SeaBattle seaBattle = new SeaBattle(false);
    //
    // Посомтреть результаты стрельбы можно в любой момент,
    // выведя объект класса SeaBattle на консоль. Например так:
    //     System.out.println(seaBattle);
    //
    //
    // Вид тестового поля:
    //
    //           0 1 2 3 4 5 6 7 8 9    координата x
    //         0|.|.|.|.|.|.|.|X|.|.|
    //         1|.|.|.|.|.|X|.|.|.|.|
    //         2|X|X|.|.|.|.|.|.|.|.|
    //         3|.|.|.|.|.|.|.|X|X|X|
    //         4|.|.|.|.|X|.|.|.|.|.|
    //         5|.|.|.|.|X|.|.|Х|.|.|
    //         6|.|.|.|.|.|.|.|Х|.|X|
    //         7|X|.|X|.|.|.|.|Х|.|X|
    //         8|X|.|.|.|.|.|.|X|.|.|
    //         9|X|.|.|.|X|.|.|.|.|.|

    private static char[][] field;// поле боя. " " пустая ячейка (EMPTY), "*" - выстрел мимо (MISS), "х"- попали (HIT), "Х" - убит (DESTROYED)

    private void init(SeaBattle seaBattle) {
        field = new char[seaBattle.getSizeX()+1][seaBattle.getSizeY()+1];
        for (int i = 0; i < field[0].length; i++) {
            for (int j = 0; j < field.length; j++) {
                field[i][j] = ' ';
            }
        }
    }

    public void battleAlgorithm(SeaBattle seaBattle) {
        init(seaBattle);
        int hits = 0;
        for (int y = 0; y < seaBattle.getSizeX(); y++) {
            for (int x = 0; x < seaBattle.getSizeY(); x++) {
                if (field[x][y] != 'X') {
                    SeaBattle.FireResult fireResult = seaBattle.fire(x, y);
                    if (fireResult != FireResult.MISS) {
                        hits++;
                        if (fireResult == FireResult.HIT) {
                            field[x][y] = 'X';
                            field[x + 1][y + 1] = 'X';

                        }

                            if (fireResult == FireResult.DESTROYED) {
                            field[x][y] = 'X';
                            field[x + 1][y] = 'X';
                            field[x][y + 1] = 'X';
                            field[x + 1][y + 1] = 'X';
                        }
                    }
                    if (hits >= 20) {
                        return;
                    }
                }
            }
        }
    }


    static void test() {
        System.out.println("Sea battle");
        SeaBattle seaBattle = new SeaBattle(true);
        new SeaBattleAlg().battleAlgorithm(seaBattle);
        System.out.println(seaBattle.getResult());
    }

    static void comp() {
        SeaBattleAlg alg = new SeaBattleAlg();
        int result = 0;
        for (int i = 0; i < 1000; i++) {
            SeaBattle seaBattle = new SeaBattle();
            alg.battleAlgorithm(seaBattle);
            result += seaBattle.getResult();
        }
        System.out.println(result/1000);
    }

    // функция для отладки
    public static void main(String[] args) {
        comp();
    }
}