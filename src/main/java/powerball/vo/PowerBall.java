package powerball.vo;

public class PowerBall {
    private String date;
    private String round;
    private String todayRound;

    private String powerball;
    private String powerballPeriod;
    private String powerballUnderOver;

    private String number;
    private String numberSum;
    private String numberSumPeriod;
    private String numberPeriod;
    private String numberUnderOver;

    private String time;

    public String getPowerball() {
        return powerball;
    }

    public void setPowerball(String powerball) {
        this.powerball = powerball;
    }

    public String getNumberSumPeriod() {
        return numberSumPeriod;
    }

    public void setNumberSumPeriod(String numberSumPeriod) {
        this.numberSumPeriod = numberSumPeriod;
    }

    public String getNumberUnderOver() {
        return numberUnderOver;
    }

    public void setNumberUnderOver(String numberUnderOver) {
        this.numberUnderOver = numberUnderOver;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPowerballUnderOver() {
        return powerballUnderOver;
    }

    public void setPowerballUnderOver(String powerballUnderOver) {
        this.powerballUnderOver = powerballUnderOver;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getNumberSum() {
        return numberSum;
    }

    public void setNumberSum(String numberSum) {
        this.numberSum = numberSum;
    }

    public String getPowerballPeriod() {
        return powerballPeriod;
    }

    public void setPowerballPeriod(String powerballPeriod) {
        this.powerballPeriod = powerballPeriod;
    }

    public String getTodayRound() {
        return todayRound;
    }

    public void setTodayRound(String todayRound) {
        this.todayRound = todayRound;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNumberPeriod() {
        return numberPeriod;
    }

    public void setNumberPeriod(String numberPeriod) {
        this.numberPeriod = numberPeriod;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeDate() {
        return date + " " + time.replace("24", "00");
    }
}
