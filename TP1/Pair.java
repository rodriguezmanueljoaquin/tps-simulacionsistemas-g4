public class Pair <T,V> {
    private T right;
    private V left;

    public Pair(T right, V left) {
        this.right = right;
        this.left = left;
    }

    public T getRight() {
        return right;
    }

    public V getLeft() {
        return left;
    }
}
