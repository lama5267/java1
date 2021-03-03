package progwards.java2.lessons.annotation;

import java.util.Iterator;

/**
 * Класс итератор для {@link AvlTree}
 * @param <K> ключ
 * @param <V> значение
 */
public class AVLIterator<K extends Comparable<K>, V> implements Iterator<K> {
    /**
     * свойство - текущий
     */
    AvlTree<K,V>.TreeLeaf<K, V> current;
    /**
     * свойство - корень
     */
    AvlTree<K,V>.TreeLeaf<K, V> root;
    /**
     * свойство - левое дерево
     */
    boolean leftTree = true;

    /**
     * Конструктор с параметрами
     * @param avlTree авл дерево
     */
    public AVLIterator (AvlTree <K, V> avlTree) {
        this.root = avlTree.getRoot();
        current = root;
    }


    private void moveLeft () {
        while (current.left != null) {
            current = current.left;
        }
    }

    /**
     *
     * @return верно или не верно
     */
    @Override
    public boolean hasNext() {
        if (root == null)
            return false;

        // если у корня нет левого поддерева
        if (root.left == null && leftTree) {
            leftTree = false;
            return true;
        } else // иначе в начале уходим по самой левой ветке
            if (current == root && current.left != null && leftTree) {
                moveLeft();
                return true;
            }

        // если у корня нет правого поддерева, то обход закончен
        if (root.right == null && !leftTree)
            return false;

        // если есть правый потомок - уходим в него
        if (current.right != null) {
            current = current.right;
            if (current.left != null) {  // если у него есть левый потомок - уходим по левой ветке
                moveLeft();
                return true;
            }
        } else {
            // иначе возврат наверх
            if (current.parent.right == current) {
                while (current.parent.right == current) {
                    current = current.parent;
                    if (current == root && !leftTree) {  // конец обхода
                        return false;
                    }
                }
            }
            current = current.parent;
            if (current == root && leftTree) {   // переход на правое поддерево
                leftTree = false;
            }
        }
        return true;
    }

    /**
     *
     * @return ключ
     */
    @Override
    public K next() {
        return current.key;
    }
}
