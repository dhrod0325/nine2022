package bill.database.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BillGift {
    private final Logger logger = LogManager.getLogger();

    private String transRefKey;
    private String transDT;
    private String transRemark;
    private Integer charId;
    private String charName;
    private String deposit = "";
    private Date regDate;
    private Date giftDate;
    private boolean gift = false;

    public void setCharName(String charName) {
        this.charName = charName;
    }

    public boolean isGift() {
        return gift;
    }

    public void setGift(boolean gift) {
        this.gift = gift;
    }

    public String getCharName() {
        return charName;
    }

    public String getTransDT() {
        return transDT;
    }

    public Date getTransDate() {
        try {
            return new SimpleDateFormat("yyyyMMddHHmm").parse(transDT);
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }

    public void setTransDT(String transDT) {
        this.transDT = transDT;
    }

    public String getTransRemark() {
        return transRemark;
    }

    public void setTransRemark(String transRemark) {
        this.transRemark = transRemark;
    }

    public int getCharId() {
        return charId;
    }

    public void setCharId(int charId) {
        this.charId = charId;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getGiftDate() {
        return giftDate;
    }

    public void setGiftDate(Date giftDate) {
        this.giftDate = giftDate;
    }

    public String getTransRefKey() {
        return transRefKey;
    }

    public void setTransRefKey(String transRefKey) {
        this.transRefKey = transRefKey;
    }
}
