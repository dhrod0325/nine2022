package ks.system.adenBoard.model;

import ks.util.common.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

public class AdenBoard {
    private static final Logger logger = LogManager.getLogger();

    private int id;
    private String name;
    private Long days;
    private String subject;
    private String memo;
    private String status;

    public static AdenBoard create(Map<String, Object> data) {
        try {
            long aden = Long.parseLong((String) data.get("aden"));
            long cash = Long.parseLong((String) data.get("cash"));
            String status = String.valueOf(data.get("status"));

            AdenBoard v = new AdenBoard();
            v.setId(Math.toIntExact((Long) data.get("id")));
            v.setName((String) data.get("name"));
            Date date = (Date) data.get("reg_date");
            v.setDays(date.getTime());

            StringBuilder sb = new StringBuilder();
            String statusNm = "";
            String subject = String.format("[%s] %s만 팝니다.", cash + "원", (aden / 10000) + "");

            if ("1".equalsIgnoreCase(status)) {
                statusNm = "판매중";
            } else if ("2".equalsIgnoreCase(status)) {
                statusNm = "거래중";
            } else if ("3".equalsIgnoreCase(status)) {
                statusNm = "거래완료";
            }

            if (!"1".equalsIgnoreCase(status)) {
                subject = "[" + statusNm + "]" + subject;
            }

            DecimalFormat df = new DecimalFormat("###,###");

            sb.append("현재상태 : ").append(statusNm).append("\r\n");
            sb.append("물품번호 : ").append(v.getId()).append("\r\n");
            sb.append("\r\n");
            sb.append("아 데 나 : ").append(df.format(aden)).append("\r\n");
            sb.append("판매금액 : ").append(df.format(cash)).append("\r\n");

            v.setSubject(subject);
            v.setMemo(sb.toString());

            return v;
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }

    public String toStringDays() {
        int y = DateUtils.getYear(days) - 100;
        int m = DateUtils.getMonth(days);
        int d = DateUtils.getDate(days);

        StringBuilder sb = new StringBuilder();
        if (y < 10)
            sb.append("0");
        sb.append(y);
        if (m < 10)
            sb.append("0");
        sb.append(m);
        if (d < 10)
            sb.append("0");
        sb.append(d);

        return sb.toString();
    }
}
