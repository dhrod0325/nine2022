package ks.core.network;

public interface L1Session {
    void write(Object o);

    void setAttribute(String key, Object o);

    Object getAttribute(String key);

    void close();

    void close(boolean now);

    boolean isClosed();

    boolean isConnected();

    String getRemoteAddress();

    boolean isPacketAttack();
}
