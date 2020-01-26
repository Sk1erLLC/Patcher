package club.sk1er.patcher.util;

public class Tuple<E, K> {

    private final E objectOne;
    private final K objectTwo;

    public Tuple(E objectOne, K objectTwo) {
        this.objectOne = objectOne;
        this.objectTwo = objectTwo;
    }

    public E getObjectOne() {
        return objectOne;
    }

    public K getObjectTwo() {
        return objectTwo;
    }
}