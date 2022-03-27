package ks.core.auth;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.core.GameServerFullException;
import ks.core.datatables.account.Account;
import ks.core.datatables.account.AccountTable;
import ks.core.network.L1Client;
import ks.packets.clientpackets.C_NoticeClick;
import ks.packets.serverpackets.S_LoginResult;
import ks.packets.serverpackets.S_Notice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class Authorization {
    private final Logger logger = LogManager.getLogger();

    private final AuthorizationCacheManager authorizationManager = AuthorizationCacheManager.getInstance();

    public static Authorization getInstance() {
        return LineageAppContext.getBean(Authorization.class);
    }

    public void auth(L1Client client, String accountName, String password, String ip, String host) {
        try {
            Account account = AccountTable.getInstance().load(accountName);

            if (CodeConfig.SERVER_STATUS == 1) {
                if (account == null) {
                    return;
                }

                if (!account.isGameMaster()) {
                    client.sendPacket(new S_Notice("서버에 접근가능한 상태가 아닙니다."));
                    LineageAppContext.commonTaskScheduler().schedule(client::disconnect, Instant.now().plusMillis(1000));
                    return;
                }
            }

            if (checkDuplicatedIPConnection(ip)) {
                logger.info("더이상 계정을 생성하지 못하거나 동일한 IP의 중복 로그인을 거부했습니다. 계정 : {} IP : {} HOST : {}", account, ip, host);
                client.sendPacket(new S_Notice("더이상 계정을 생성하지 못하거나 동일한 IP의 중복 로그인을 거부했습니다"));
                LineageAppContext.commonTaskScheduler().schedule(client::disconnect, Instant.now().plusMillis(1000));
                return;
            }

            if (account == null) {
                if (CodeConfig.AUTO_CREATE_ACCOUNTS) {
                    if (AccountTable.getInstance().checkLoginIp(ip)) {
                        client.sendPacket(new S_Notice("동일 IP로 생성한 계정이 2개 있습니다"));
                        LineageAppContext.commonTaskScheduler().schedule(client::disconnect, Instant.now().plusMillis(1000));
                    } else {
                        if (!(isValidAccount(accountName))) {
                            client.sendPacket(new S_LoginResult(9));
                            return;
                        }

                        if (!isValidPassword(password)) {
                            client.sendPacket(new S_LoginResult(0x0a));
                            return;
                        }

                        AccountTable.getInstance().insert(accountName, password, ip, host);
                        account = AccountTable.getInstance().load(accountName);
                    }
                }
            }

            if (account == null || !account.validatePassword(password)) {
                client.setClientLoginCheck(true);
                client.sendPacket(new S_LoginResult(S_LoginResult.REASON_USER_OR_PASS_WRONG));

                int wrongCount = authorizationManager.getPassWordWrongCount(accountName);

                if (wrongCount >= 5) {
                    client.sendPacket(new S_Notice("패스워드 인증에 실패하였습니다"));
                    LineageAppContext.commonTaskScheduler().schedule(client::disconnect, Instant.now().plusMillis(1000));
                    authorizationManager.setPassWordWrongCount(accountName, 0);
                    return;
                }

                authorizationManager.setPassWordWrongCount(accountName, ++wrongCount);

                logger.info("[패스워드 인증실패] 계정 : {} / 아이피 : {} / 실패횟수 : {}", accountName, ip, wrongCount);

                return;
            }

            if (account.isBanned()) {
                logger.info("BAN 계정의 로그인을 거부했습니다. account = " + accountName + " host = " + host);
                client.sendPacket(new S_Notice("서버에서 벤당하신 계정입니다. 운영자에게 문의 하시기 바랍니다."));
                LineageAppContext.commonTaskScheduler().schedule(client::disconnect, Instant.now().plusMillis(1000));
                return;
            }

            try {
                authorizationManager.remove(accountName);

                AuthorizationUtils.getInstance().login(client, account);
                AccountTable.getInstance().updateLastActive(account.getName());
                client.setAccount(account);
                sendNotice(client);

                logger.info("[로그인] 계정 : {} / IP : {}", accountName, client.getIp());
            } catch (GameServerFullException e) {
                client.sendPacket(new S_Notice("서버 접속 인원이 많아 접속이 지연되고있습니다.\n \n 잠시후에 다시 접속을 시도해주시기바랍니다."));
                client.disconnectNow();
                logger.info("접속 인원수를 초과하였습니다. (" + client.getHostname() + ")의 접속 시도를 강제 종료했습니다.");
            } catch (AccountAlreadyLoginException e) {
                logger.info("동일한 계정의 중복 로그인을 거부했습니다. account=" + accountName + " host : " + host);
                client.sendPacket(new S_Notice("이미 접속 중 입니다"));
                client.disconnectNow();
            } catch (Exception e) {
                logger.error(e);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void sendNotice(L1Client client) {
        if (AuthorizationUtils.getInstance().isAlreadyLoginAccount(client.getAccountName())) {
            return;
        }

        new C_NoticeClick(client);
    }

    private boolean isValidPassword(String password) {
        try {
            int k = 0;

            for (int i = 0; i < password.length(); i++) {
                if (Character.isDigit(password.charAt(i))) {
                    k++;
                }
            }

            if (k == password.length() || k == 0 || password.length() < 6) {
                return false;
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return true;
    }

    private boolean isValidAccount(String accountName) {
        if (accountName.length() < 5) {
            return false;
        }
        char[] arrayOfChar = accountName.toCharArray();

        for (char c : arrayOfChar)
            if (!(Character.isLetterOrDigit(c))) {
                return false;
            }
        return true;
    }

    private boolean checkDuplicatedIPConnection(String ip) {
        if (CodeConfig.MAX_CLIENT_COUNT > 0) {
            return AuthorizationUtils.getInstance().checkDuplicatedIp(ip);
        }

        return false;
    }
}
