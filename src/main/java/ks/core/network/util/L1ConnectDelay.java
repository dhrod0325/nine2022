package ks.core.network.util;

import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class L1ConnectDelay {
    public static final L1ConnectDelay instance = new L1ConnectDelay();
    private static final Logger logger = LogManager.getLogger(L1ConnectDelay.class);
    private final Map<String, Ip> ipList = new HashMap<>();

    public static L1ConnectDelay getInstance() {
        return instance;
    }

    public void insert(String ip, int count, long time) {
        SqlUtils.update("insert into character_ip_connect_delay (ip,count,time) values (?,?,?)", ip, count, new Date(time));
    }

    public void insert(String key) {
        Ip ip = ipList.get(key);
        insert(ip.ip, ip.count, ip.time);
    }

    public boolean isManyApplyConnection(String ip) {
        Ip checkIp = ipList.get(ip);

        if (checkIp == null) {
            checkIp = new Ip(ip, System.currentTimeMillis());
            ipList.put(checkIp.ip, checkIp);
        } else {
            if (checkIp.block) {
                if (System.currentTimeMillis() > checkIp.time + (1000 * 60)) {
                    logger.warn("[공격의심 사용자 60초 지나서 차단 해제 했습니다] IP : " + checkIp.ip);
                    checkIp.block = false;
                    checkIp.time = System.currentTimeMillis();
                    return false;
                } else {
                    return true;
                }
            } else {
                if (System.currentTimeMillis() < checkIp.time + 100) {
                    if (checkIp.count > 5) {
                        checkIp.block = true;
                        logger.warn("[공격 의심 IP 차단] IP : " + checkIp.ip);
                        insert(checkIp.ip);
                        return true;
                    } else {
                        checkIp.count++;
                    }
                } else {
                    checkIp.count = 0;
                }

                checkIp.time = System.currentTimeMillis();
            }
        }

        return false;
    }

    public Ip getIp(String ip) {
        return ipList.get(ip);
    }

    public static class Ip {
        public String ip;

        public int count;

        public long time;

        public boolean block;

        public Ip(String ip, long time) {
            this.ip = ip;
            this.time = time;
        }
    }
}