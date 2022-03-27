package ks.core.network;

public interface L1Server {
    void start(int port) throws Exception;

    void shutDown();
}
