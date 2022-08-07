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

    public void setLeft(T left) {
        this.left = left;
    }

    public void setRight(V right) {
        this.right = right;
    }

    public Pair<T,V> setNewValues(T left, V right){
        setLeft(left);
        setRight(right);
        return this;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
