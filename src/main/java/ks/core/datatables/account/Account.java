package ks.core.datatables.account;

import ks.app.config.prop.CodeConfig;
import ks.system.grangKin.GrangKain;

import java.sql.Timestamp;

public class Account {
    private String name;
    private String password;
    private String ip;
    private Timestamp lastActive;

    private int accessLevel;

    private String host;
    private boolean banned;
    private int charSlot;
    private int gamePassword;

    private String quiz;
    private Timestamp lastLogout;

    private final GrangKain grangKain = new GrangKain();

    public int getGamePassword() {
        return gamePassword;
    }

    public void setGamePassword(int gamePassword) {
        this.gamePassword = gamePassword;
    }

    public Timestamp getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(Timestamp lastLogout) {
        this.lastLogout = lastLogout;
    }

    public boolean isGameMaster() {
        return accessLevel == CodeConfig.GM_CODE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Timestamp getLastActive() {
        return lastActive;
    }

    public void setLastActive(Timestamp lastActive) {
        this.lastActive = lastActive;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public int getCharSlot() {
        return charSlot;
    }

    public void setCharSlot(int charSlot) {
        this.charSlot = charSlot;
    }

    public boolean validatePassword(String rawPassword) {
        return password.equals(rawPassword);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GrangKain getGrangKain() {
        return grangKain;
    }
}
