package ks.model;

public class L1NpcChat {
    private int npcId;
    private int chatTiming;
    private int startDelayTime;
    private String _chatId1;
    private String _chatId2;
    private String _chatId3;
    private String _chatId4;
    private String _chatId5;
    private int _chatInterval;
    private boolean _isShout;
    private boolean _isWorldChat;
    private boolean _isRepeat;
    private int _repeatInterval;
    private int _gameTime;

    public L1NpcChat() {
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int i) {
        npcId = i;
    }

    public int getChatTiming() {
        return chatTiming;
    }

    public void setChatTiming(int i) {
        chatTiming = i;
    }

    public int getStartDelayTime() {
        return startDelayTime;
    }

    public void setStartDelayTime(int i) {
        startDelayTime = i;
    }

    public String getChatId1() {
        return _chatId1;
    }

    public void setChatId1(String s) {
        _chatId1 = s;
    }

    public String getChatId2() {
        return _chatId2;
    }

    public void setChatId2(String s) {
        _chatId2 = s;
    }

    public String getChatId3() {
        return _chatId3;
    }

    public void setChatId3(String s) {
        _chatId3 = s;
    }

    public String getChatId4() {
        return _chatId4;
    }

    public void setChatId4(String s) {
        _chatId4 = s;
    }

    public String getChatId5() {
        return _chatId5;
    }

    public void setChatId5(String s) {
        _chatId5 = s;
    }

    public int getChatInterval() {
        return _chatInterval;
    }

    public void setChatInterval(int i) {
        _chatInterval = i;
    }

    public boolean isShout() {
        return _isShout;
    }

    public void setShout(boolean flag) {
        _isShout = flag;
    }

    public boolean isWorldChat() {
        return _isWorldChat;
    }

    public void setWorldChat(boolean flag) {
        _isWorldChat = flag;
    }

    public boolean isRepeat() {
        return _isRepeat;
    }

    public void setRepeat(boolean flag) {
        _isRepeat = flag;
    }

    public int getRepeatInterval() {
        return _repeatInterval;
    }

    public void setRepeatInterval(int i) {
        _repeatInterval = i;
    }

    public int getGameTime() {
        return _gameTime;
    }

    public void setGameTime(int i) {
        _gameTime = i;
    }

}
