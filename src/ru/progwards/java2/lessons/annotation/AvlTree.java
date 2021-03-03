package progwards.java2.lessons.annotation;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Класс - АВЛ дерево, состоящее из узлов {@link TreeLeaf}
 * @version 1.0
 * @param <K> ключ, с возможностью сравнения (реализующий Comparable)
 * @param <V> значение
 * @see TreeLeaf
 */
public class AvlTree <K extends Comparable<K>, V> implements Iterable<K> {
    /**
     * Статическая константа - применяется для вывода сообщения, когда ключ уже существует
     */
    private static final String KEYEXIST = "Key already exist";
    /**
     * Статическая константа - применяется для вывода сообщения, когда ключ не найден
     */
    private static final String KEYNOTEXIST = "Key not exist";

    /**
     * вставка узла в дерево PUT
     * удаление узла из дерева DEL
     */
    enum Operation {PUT, DEL}
    /**
     * Вложенный класс, описывающий узел дерева {@link AvlTree}
     * @param <K> ключ, с возможностью сравнения (реализующий Comparable)
     * @param <V> значение
     */
    class TreeLeaf <K extends Comparable<K> , V> {
        /**
         * Ключ
         */
        K key;
        /**
         * Значение
         */
        V value;
        /**
         * родитель узла
         */
        TreeLeaf<K,V> parent;
        /**
         * левый потомок узла
         */
        TreeLeaf<K,V> left;
        /**
         * правый потомок узла
         */
        TreeLeaf<K,V> right;
        /**
         * высота поддерева
         */
        int height;
        /**
         * баланс узла
         */
        int balance;

        /**
         * Конструктор узла дерева с параметрами
         * @param key - ключ узла
         * @param value - значение узла
         */
        public TreeLeaf(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Поиск в дереве от заданного узла (вызвавшего метод)
         * @param key ключ, который ищем в дереве
         * @return узел, с заданным ключом
         * @throws TreeException выбрасывается, если необходимый ключ в дереве не найден
         */
        private TreeLeaf<K, V> find(K key) throws TreeException {
            TreeLeaf<K, V> isFind = this;
            int cmp = 0;
            do {
                cmp = key.compareTo(isFind.key);
                if (cmp > 0)
                    if (right != null)
                        isFind = isFind.right;
                    else
                        throw new TreeException(KEYNOTEXIST);
                if (cmp < 0)
                    if (left != null)
                        isFind = isFind.left;
                    else
                        throw new TreeException(KEYNOTEXIST);
            } while (cmp != 0);
            return isFind;
        }
        /**
         * Поиск максимального значения в поддереве
         * @return максимальное значение в поддереве
         */
        private TreeLeaf<K, V> findMax () { //если правый не пусто то ищем правый максимальный
            TreeLeaf<K, V> isFind = this;
            while (isFind.right != null)
                isFind = isFind.right;
            return isFind;
        }
        /**
         * Поиск минимального значения в поддереве
         * @return минимальное значение в поддереве
         */
        private TreeLeaf<K, V> findMin () {//если левый не пусто ищем левый минемальный
            TreeLeaf<K, V> isFind = this;
            while (isFind.left != null)
                isFind = isFind.left;
            return isFind;
        }
        /**
         * Поиск узла, который будет вместо удаляемого, используя методы {@link #findMax()} и {@link #findMin()}
         * @return узел дерева, на который будет заменен удаляемый узел
         * @see #internaldDelete(Comparable)
         */
        // поиск замены удаляемому элементу
        private TreeLeaf<K,V> findExchange () {
            TreeLeaf<K,V> node;
            if (balance > 0)
                node = left.findMax();
            else
                node = right.findMin();
            return node;
        }
        /**
         * Удаление терминального узла или узла у которого только один потомок (терминальный) с заменой всех связей.
         * После удаления узла производится проверка необходимости балансировки {@link #balanceIsNorm(Operation)}
         */
        // удаление терминального или у кого только один потомок
        private void deleteTerm () {
            balanceOK = false;
            TreeLeaf<K,V> temp;
            if (right == null && left == null)
                temp = null;
            else if (right!=null)
                temp = right;
            else temp = left;

            if (this.equals(root)) {
                root = (AvlTree.TreeLeaf) temp;
                return;
            }

            if (parent.right != null && parent.right.equals(this))
                parent.right = temp;
            else parent.left = temp;
            if (temp != null)
                temp.parent = parent;
            TreeLeaf<K,V> current = parent;
            while (!balanceOK) {
                if (current.equals(root))
                    balanceOK = true;
                current = current.balanceIsNorm(Operation.DEL);
            }
        }
        /**
         * Проверка необходимости балансировки пр вставке или удалении. Перед этим производится пересчет высоты узла {@link #newHeight()} и баланса {@link #checkBalance()}
         * При необходимости (баланс 2 или -2) выполняется балансировка {@link #doBalance()}
         * @param oper вид операции {@link Operation}
         * @return узел, являющийся родителем текущего (поднимаемся вверх к корню)
         * @see #put(TreeLeaf)
         * @see #deleteTerm()
         * @see #doBalance()
         */
        // проверка баланса
        private TreeLeaf<K, V> balanceIsNorm(Operation oper) {
            newHeight();
            checkBalance();
            if (balance == 2 || balance == -2)
                doBalance();
            else if (balance == 1 || balance == -1) {
                if (oper == Operation.DEL || (oper == Operation.PUT && this.equals(root)))
                    balanceOK = true;
            } else if (balance == 0) {
                if (oper == Operation.PUT)
                    balanceOK = true;
            }
            return parent;
        }
        /**
         * Вставка в дерево нового узла, после вставки проверяется необходимость балансировки {@link TreeLeaf#balanceIsNorm(Operation)},
         * значение Operation при этом передается {@link Operation#PUT}
         * @param leaf новый узел, который необходимо вставить
         * @throws TreeException исключение выбрасывается, если в дереве уже есть узел с данным ключом {@value #KEYEXIST}
         * @see TreeLeaf#balanceIsNorm(Operation)
         */
        private void put (TreeLeaf<K, V> leaf) throws TreeException {
            TreeLeaf<K, V> putAt = this;
            boolean putOK = false;
            while (!putOK) {
                int cmp = leaf.key.compareTo(putAt.key);
                if (cmp == 0)
                    throw new TreeException(KEYEXIST);
                if (cmp > 0)
                    if (putAt.right != null) {
                        putAt = putAt.right;
                    } else {
                        putAt.right = leaf;
                        leaf.parent = putAt;
                        putOK = true;
                    }
                else {
                    if (putAt.left != null) {
                        putAt = putAt.left;
                    } else {
                        putAt.left = leaf;
                        leaf.parent = putAt;
                        putOK = true;
                    }
                }
            }
            while (!balanceOK)
                putAt = putAt.balanceIsNorm(Operation.PUT);
        }
        /**
         * Балансировка - определяется какой из четырех поворотов необходим и производится вращение (при балансе 2 - большой или малый правый, а при -2 большой или малый левый)
         * @see #smallRight()
         * @see #smallLeft()
         */
        // сделать балансировку
        private void doBalance () {
            if (balance == 2) {
                if (left.balance < 0)
                    left.smallLeft();
                smallRight();
            } else if (balance == -2){
                if (right.balance > 0)
                    right.smallRight();
                smallLeft();
            }
        }
        /**
         * Малое правое вращение. переопределение ссылок и пересчет высоты и баланса
         * @see #heiAndBal()
         */
        // правый поворот
        public void smallRight () {
            TreeLeaf<K, V> temp = left;
            left = temp.right;
            temp.right = this;
            if (this.equals(root)) {       // если это корень
                temp.parent = null;
                root = (AvlTree.TreeLeaf) temp;
            } else {
                temp.parent = parent;
                if (parent.right != null && parent.right.equals(this))
                    parent.right = temp;
                else parent.left = temp;
            }
            parent = temp;
            if (left != null)
                left.parent = this;
            heiAndBal();
        }
        /**
         * Малое левое вращение. переопределение ссылок и пересчет высоты и баланса
         * @see #heiAndBal()
         */
        // левый поворот
        public void smallLeft () {
            TreeLeaf<K,V> temp = right;
            right = temp.left;
            temp.left = this;
            if (this.equals(root)) {
                temp.parent = null;
                root = (AvlTree.TreeLeaf) temp;
            } else {
                temp.parent = parent;
                if (parent.right != null && parent.right.equals(this))
                    parent.right = temp;
                else parent.left = temp;
            }
            parent = temp;
            if (right != null)
                right.parent = this;
            heiAndBal();
        }
        /**
         * Пересчет высоты и баланса после поворота
         */
        // пересчет высоты и баланса после поворотов
        public void heiAndBal () {
            newHeight();
            checkBalance();
            parent.newHeight();
            parent.checkBalance();
        }

        public String toString() {
            return "("+key+","+value+")";
        }
        /**
         * Прямой обход дерева
         * @param consumer объект, реализующий интерфейс Consumer, для выполнения действия над узлом во время обхода дерева
         */
        public void process(Consumer<TreeLeaf<K,V>> consumer) {
            if (left != null)
                left.process(consumer);
            consumer.accept(this);
            if (right != null)
                right.process(consumer);
        }
        /**
         * Вычисление баланса для узла
         */
        // пересчет баланса
        public void checkBalance () {
            balance =  right == null ? (left == null ? 0 : left.height - (-1)) : (left == null ? (-1) - right.height : left.height - right.height);
        }
        /**
         * Вычисление высоты для узла
         */
        // пересчет высоты
        public void newHeight () {
            if (left != null && right != null)
                height = (left.height >= right.height) ? left.height + 1 : right.height + 1;
            else
                height = (right == null) ? (left == null ? 0 : left.height + 1) : right.height + 1;
        }
    }
    /**
     * Корневой узел
     */
    TreeLeaf<K, V> root;
    /**
     * Требуется ли дальнейшая балансировка (true - Да, false - нет)
     */
    boolean balanceOK;
    /**
     * Поиск узла по заданному ключу. Использует метод {@link TreeLeaf#find(Comparable)}
     * @param key ключ узла, который необходимо найти в дереве.
     * @return значение найденного узла. Если дерево пустое - null
     * @throws TreeException выбрасывается если ключ не найден {@value #KEYNOTEXIST}
     * @see TreeLeaf#find(Comparable)
     */
    public V find(K key) throws TreeException {
        if (root == null)
            return null;
        return root.find(key).value;
    }
    /**
     * Вставка узла в дерево. Использует метод {@link TreeLeaf#put(TreeLeaf)}
     * @param leaf узел, который вставляем
     * @throws TreeException выбрасывается, если узел с таким ключом уже существует {@value #KEYEXIST}
     * @see TreeLeaf#put(TreeLeaf)
     */
    public void put (TreeLeaf<K, V> leaf) throws TreeException {
        if (root == null)
            root = leaf;
        else {
            balanceOK = false;
            root.put(leaf);
        }
    }
    /**
     * Создание узла и вставка с использованием метода {@link #put(TreeLeaf)}
     * @param key ключ нового узла
     * @param value значение новго узла
     * @throws TreeException выбрасывается, если узел с таким ключом уже существует {@value #KEYEXIST}
     */
    public void put (K key, V value) throws TreeException {
        put (new TreeLeaf<>(key, value));
    }
    /**
     * Удаление узла, использует внутренний метод {@link #internaldDelete(Comparable)}
     * @param key ключ удалямого узла
     * @throws TreeException выбрасывается если ключ не найден {@value #KEYNOTEXIST}
     */
    public void delete(K key) throws TreeException {
        internaldDelete(key);
    }
    /**
     * Удаление узла
     * @param key ключ удаляемого узла
     * @return узел, который был удален
     * @throws TreeException выбрасывается если ключ не найден {@value #KEYNOTEXIST}
     * @see TreeLeaf#find(Comparable)
     * @see TreeLeaf#findExchange()
     * @see TreeLeaf#deleteTerm()
     * @see TreeLeaf#newHeight()
     * @see TreeLeaf#checkBalance()
     */
    public TreeLeaf<K, V> internaldDelete(K key) throws TreeException {
        if (root == null)
            throw new TreeException(KEYNOTEXIST);
        TreeLeaf<K,V> foundDel = root.find(key);
        TreeLeaf<K,V> node;
        if (foundDel.left != null && foundDel.right != null) {  // если это не терминальный узел
            node = foundDel.findExchange();
            node.deleteTerm();
            node.right = foundDel.right;                    //меняем ссылки на потомков
            node.left = foundDel.left;
            if (node.left != null)
                node.left.parent = node;
            if (node.right != null)
                node.right.parent = node;
            if (foundDel.parent == null)
                root = node;
            else if (node.key.compareTo(foundDel.parent.key) > 0)  // меняем у родителя
                foundDel.parent.right = node;
            else
                foundDel.parent.left = node;
            node.parent = foundDel.parent;                 // меняем ссылку на родителя у заменяемого
            node.newHeight();
            node.checkBalance();
        } else                                         // если удаляемый узел - терминальный или один потомок
            foundDel.deleteTerm();
        return foundDel;
    }
    /**
     * Изменение ключей у узла (удаляется узел со старым ключом и вставляется узел с новым)
     * @param oldKey старый ключ
     * @param newKey новый ключ
     * @throws TreeException выбрасывается - при удалении старого,если ключ не найден {@value #KEYNOTEXIST}, при вставке нового, если узел с таким ключом уже существует {@value #KEYEXIST}
     * @see #internaldDelete(Comparable)
     * @see #put(TreeLeaf)
     */
    public void change(K oldKey, K newKey) throws TreeException {
        TreeLeaf<K, V> current = internaldDelete(oldKey);
        current.key = newKey;
        put(current);
    }
    /**
     * Прямой обход дерева
     * @param consumer объект, реализующий интерфейс Consumer, для выполнения действия над узлом во время обхода дерева
     * @see TreeLeaf#process(Consumer)
     */
    public void process(Consumer<TreeLeaf<K,V>> consumer) {
        if (root != null)
            root.process(consumer);
    }

    /**
     *
     * @return корень
     */
    TreeLeaf<K, V> getRoot() {
        return root;
    }

    /**
     *
     * @return авлитератор
     */
    public AVLIterator<K,V> getIterator() {
        return new AVLIterator<>(this);
    }

    @Override
    public Iterator<K> iterator() {
        return getIterator();
    }
}