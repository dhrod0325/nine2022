package ks.core.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class L1HcPacket extends Thread {
    private final Logger logger = LogManager.getLogger();

    private final L1Client client;

    private final Queue<byte[]> queue;

    public L1HcPacket(L1Client client, int capacity) {
        this.client = client;
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    public void receive(byte[] data) {
        if (data != null) {
            boolean q = queue.offer(data);

            if (q) {
                queueNotify();
            }
        }
    }

    public void queueNotify() {
        synchronized (queue) {
            queue.notify();
        }
    }

    @Override
    public void run() {
        while (!client.isClosed()) {
            try {
                if (queue.isEmpty()) {
                    synchronized (queue) {
                        queue.wait();
                    }
                }

                byte[] data = queue.poll();

                if (data != null) {
                    L1PacketHandler.handle(data, client);
                } else {
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }

        queue.clear();
    }
}
