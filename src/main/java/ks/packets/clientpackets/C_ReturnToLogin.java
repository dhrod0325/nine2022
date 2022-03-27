package ks.packets.clientpackets;

import ks.core.auth.AuthorizationUtils;
import ks.core.network.L1Client;

public class C_ReturnToLogin extends ClientBasePacket {
    public C_ReturnToLogin(byte[] decrypt, L1Client client) {
        super(decrypt);

        AuthorizationUtils.getInstance().logout(client);
    }
}
