package bill;

import ks.model.pc.L1PcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

public class BillCommand {

    private final Logger logger = LogManager.getLogger();

    private final static BillCommand instance = new BillCommand();

    public static BillCommand getInstance() {
        return instance;
    }

    public static class BillException extends Exception {
        public BillException(String message) {
            super(message);
        }
    }

    //.후원지급 년-월-일 시:분:초 입금자명 (ex .후원지급 2021-11-12 20:00 메티스
    public boolean command(L1PcInstance pc, String cmd, StringTokenizer st) {
        /*
        if ("후원".equalsIgnoreCase(cmd)) {
            try {
                pc.sendGreenMessage("악의적인 용도로 명령어 사용시 경고 없이 벤처리 됩니다");

                int count = BillGiftTable.getInstance().findListCountByCharId(pc.getId());

                if (count > 3) {
                    throw new BillException("후원신청은 최대 3건입니다 지급된 이후 이용하세요");
                }

                int yyyyMMdd = Integer.parseInt(st.nextToken().replace("-", ""));
                int hhmm = Integer.parseInt(st.nextToken().replace(":", ""));

                String yyyyMMddStr = yyyyMMdd + "";
                String hhmmStr = hhmm + "";

                if (yyyyMMddStr.length() != 8) {
                    throw new BillException("년월일을 올바르게 입력하세요 yyyyMMdd 형식");
                }

                if (hhmmStr.length() != 4) {
                    throw new BillException("시분을 올바르게 입력하세요 00:00 형식");
                }


                if (hhmmStr.startsWith("24")) {
                    hhmmStr = hhmmStr.replaceFirst("24", "00");
                }

                String transDT = yyyyMMddStr + hhmmStr;
                String transRemark = st.nextToken();

                BillGift check = BillGiftTable.getInstance().findByTransRefKey(transDT, transRemark);

                if (check != null) {
                    throw new BillException("이미 신청함 본인이 아닌경우 운영자 문의");
                }

                BillGift billGift = new BillGift();
                billGift.setGift(false);
                billGift.setCharId(pc.getId());
                billGift.setCharName(pc.getName());
                billGift.setTransDT(transDT);
                billGift.setTransRemark(transRemark);
                billGift.setRegDate(new Date());

                BillGiftTable.getInstance().insertOrUpdate(billGift);

                pc.sendPackets("후원요청이 완료되었습니다. 10분 이내에 처리됩니다");
                pc.sendPackets("상태확인은 .후원조회 명령어를 이용하세요");
            } catch (BillException e) {
                pc.sendPackets(e.getMessage());
            } catch (Exception e) {
                pc.sendPackets(".후원 년-월-일 시:분 캐릭명");
                pc.sendPackets("ex) .후원 2021-11-12 20:00 메티스");
            }

            return true;
        } else if ("후원조회".equalsIgnoreCase(cmd)) {
            try {
                List<BillGift> list = BillGiftTable.getInstance().findListByCharId(pc.getId());

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                for (BillGift b : list) {
                    if (b.isGift())
                        continue;

                    pc.sendPackets(String.format("%s %s %s %s",
                            format.format(b.getTransDate()),
                            b.getDeposit(),
                            b.getTransRemark(),
                            b.isGift() ? "지급" : "미지급"
                    ));
                }
            } catch (Exception e) {
                pc.sendPackets(".후원조희");
            }

            return true;
        }
        */
        return false;
    }
}