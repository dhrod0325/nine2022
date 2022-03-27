package ks.core.datatables.notice;

import java.util.Date;
import java.util.StringTokenizer;

public class Notice {
    private int id;
    private String message;
    private Date regDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getMessageLine() {
        StringBuilder sb = new StringBuilder();
        StringTokenizer s = new StringTokenizer(message, "^");

        while (s.hasMoreElements()) {
            sb.append(s.nextToken()).append("\n");
        }

        return sb.toString();
    }
}
