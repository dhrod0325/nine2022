package ks.core.network.util;

import ks.core.network.L1Client;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class L1ClientManager {
    private static final L1ClientManager instance = new L1ClientManager();

    private final List<L1Client> clients = new CopyOnWriteArrayList<>();

    public static L1ClientManager getInstance() {
        return instance;
    }

    public void add(L1Client client) {
        if (!clients.contains(client)) {
            clients.add(client);
        }
    }

    public L1Client findByAccountName(String accountName) {
        List<L1Client> list = getAllClients();

        for (L1Client client : list) {
            if (accountName.equalsIgnoreCase(client.getAccountName())) {
                return client;
            }
        }

        return null;
    }

    public void remove(L1Client client) {
        clients.remove(client);
    }

    public List<L1Client> getAllClients() {
        return clients.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}
