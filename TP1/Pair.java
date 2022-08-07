public class Pair <T,V> {
    private T left;
    private V right;

    public Pair(T left, V right) {
        this.right = right;
        this.left = left;
    }

    public T getLeft() {
        return left;
    }

    public V getRight() {
        return right;
    }

}
