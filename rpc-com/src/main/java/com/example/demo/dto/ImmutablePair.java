package com.example.demo.dto;


/**
 * @author pikaqiu
 */
public class ImmutablePair<L, R> {
    public L left;
    public R right;
    private static final ImmutablePair NULL = of(null, null);

    public static <L, R> ImmutablePair<L, R> nullPair() {
        return NULL;
    }

    public static <L, R> ImmutablePair<L, R> of(L left, R right) {
        return new ImmutablePair(left, right);
    }

    public ImmutablePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }

    public static ImmutablePair getNULL() {
        return NULL;
    }
}
