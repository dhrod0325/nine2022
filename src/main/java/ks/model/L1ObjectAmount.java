package ks.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class L1ObjectAmount<T> {
    private T obj;

    private int amount;

    public L1ObjectAmount(T obj, int amount) {
        this.obj = obj;
        this.amount = amount;
    }

    public T getObject() {
        return obj;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
