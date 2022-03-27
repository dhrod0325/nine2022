package ks.core.auth;

import ks.app.config.prop.CodeConfig;
import ks.app.config.prop.ServerConfig;
import ks.core.GameServerFullException;
import ks.core.datatables.account.Account;
import ks.core.datatables.account.AccountTable;
import ks.core.network.L1Client;
import ks.packets.serverpackets.S_ServerMessage;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthorizationUtils {
    private static final AuthorizationUtils instance = new AuthorizationUtils();

    private final Map<String, L1Client> accountsMap = new ConcurrentHashMap<>();

    public static AuthorizationUtils getInstance() {
        return instance;
    }

    public Collection<L1Client> getAllAccounts() {
        return accountsMap.values();
    }

    public int getOnlinePlayerCount() {
        return accountsMap.size();
    }

    private void kickClient(final L1Client client) {
        if (client == null) {
            return;
        }

        if (client.getActiveChar() != null) {
            client.getActiveChar().sendPackets(new S_ServerMessage(357));
        }

        client.disconnect();
    }

    public Map<String, L1Client> getAccountsMap() {
        return accountsMap;
    }

    public void login(L1Client client, Account account) throws GameServerFullException, AccountAlreadyLoginException {
        if (getOnlinePlayerCount() >= ServerConfig.SERVER_MAX_USERS && !account.isGameMaster()) {
            throw new GameServerFullException();
        } else if (accountsMap.containsKey(account.getName())) {
            kickClient(accountsMap.remove(account.getName()));
            throw new AccountAlreadyLoginException();
        } else {
            accountsMap.put(account.getName(), client);
        }
    }

    public boolean logout(L1Client client) {
        if (client == null || client.getAccountName() == null) {
            return false;
        }
        return accountsMap.remove(client.getAccountName()) != null;
    }

    public boolean checkDuplicatedIp(String ip) {
        Collection<L1Client> list = getAllAccounts();

        int findCount = 0;

        for (L1Client client : list) {
            Account account = AccountTable.getInstance().load(client.getAccountName());

            if (account != null && account.getAccessLevel() == CodeConfig.GM_CODE) {
                continue;
            }

            if (ip.equalsIgnoreCase(client.getIp())) {
                findCount++;
            }
        }

        return findCount >= CodeConfig.MAX_CLIENT_COUNT;
    }

    public boolean isAlreadyLoginAccount(String account) {
        int findCount = 0;

        Collection<L1Client> list = getAllAccounts();

        for (L1Client client : list) {
            if (client == null) {
                continue;
            }

            if (client.getAccountName().equals(account)) {
                findCount += 1;

                if (findCount > 1) {
                    kickClient(accountsMap.remove(account));
                    return true;
                }
            }
        }

        return false;
    }
}
