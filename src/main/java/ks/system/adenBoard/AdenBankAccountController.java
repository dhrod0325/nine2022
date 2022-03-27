package ks.system.adenBoard;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ItemId;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_MailBox;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.adenBoard.database.AdenBankAccountTable;
import ks.system.adenBoard.model.AdenBankAccount;
import ks.system.adenBoard.model.AdenBuy;
import ks.system.adenBoard.model.AdenSell;
import ks.util.L1LetterUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AdenBankAccountController {
    private static final AdenBankAccountController instance = new AdenBankAccountController();

    public static AdenBankAccountController getInstance() {
        return instance;
    }

    private final AdenBankAccountTable dao = AdenBankAccountTable.getInstance();

    public void registerBankAccount(L1PcInstance pc, AdenBankAccount vo) {
        dao.insertOrUpdateBankAccount(vo);
        pc.sendPackets(new S_SystemMessage("계좌설정이 완료되었습니다."));
    }

    public void checkBankAccount(L1PcInstance pc, String accountId) {
        AdenBankAccount v = dao.getBankAccount(accountId);

        if (v == null) {
            pc.sendPackets(new S_SystemMessage("등록된 계좌가 없습니다."));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("계좌번호 : ").append(v.getBank_no()).append("\r\n");
            sb.append("예금주   : ").append(v.getBank_owner_name()).append("\r\n");
            sb.append("은행명   : ").append(v.getBank_name()).append("\r\n");
            sb.append("연락처   : ").append(v.getPhone()).append("\r\n");

            pc.sendPackets(new S_SystemMessage(sb.toString()));
        }
    }

    public void registerAden(L1PcInstance pc, int aden, int cash) {
        if (dao.getBankAccount(pc.getAccountName()) == null) {
            pc.sendPackets(new S_SystemMessage("계좌를 먼저 등록하셔야 합니다."));
            return;
        }

        if (dao.isAlreadyRegisted(pc.getAccountName())) {
            pc.sendPackets(new S_SystemMessage("이미 판매중인 아데나가 존재합니다."));
            return;
        }

        if (pc.getLevel() < CodeConfig.ADEN_SELL_MIN_LEVEL) {
            pc.sendPackets(new S_SystemMessage(CodeConfig.ADEN_SELL_MIN_LEVEL + "레벨 미만은 아데나를 등록할 수 있습니다."));
            return;
        }

        L1ItemInstance pcAden = pc.getInventory().findItemId(L1ItemId.ADENA);

        if (pcAden.getCount() < aden) {
            pc.sendPackets(new S_SystemMessage("소지하고있는 아데나가 부족합니다."));
            return;
        }

        if (aden < CodeConfig.ADEN_SELL_MIN) {
            pc.sendPackets(new S_SystemMessage(NumberFormat.getInstance().format(CodeConfig.ADEN_SELL_MIN) + " 미만은 등록하실수 없습니다"));
            return;
        }

        pc.getInventory().consumeItem(L1ItemId.ADENA, aden);

        AdenSell v = new AdenSell();
        v.setAccount_id(pc.getAccountName());
        v.setName(pc.getName());
        v.setAden(aden);
        v.setCash(cash);
        v.setStatus("1");

        dao.insertAdenSell(v);

        pc.sendPackets(new S_SystemMessage("물품이 등록되었습니다."));
    }

    public void buyAden(L1PcInstance pc, int id) {
        AdenSell adenSell = dao.getAdenSell(id);

        if (adenSell != null) {
            if (pc.getAccountName().equals(adenSell.getAccount_id())) {
                pc.sendPackets("자신이 등록한 물품을 구매할 수 없습니다");
                return;
            }

            if (!"1".equalsIgnoreCase(adenSell.getStatus())) {
                pc.sendPackets(new S_SystemMessage("거래중이거나 판매종료된 물품 입니다."));
                return;
            }

            AdenBankAccount bankAccount = dao.getBankAccount(adenSell.getAccount_id());

            if (bankAccount == null) {
                pc.sendPackets(new S_SystemMessage("판매자 계좌번호가 등록되어있지 않습니다."));
                return;
            }

            AdenBuy adenBuy = new AdenBuy();
            adenBuy.setAden_sell_id(id);
            adenBuy.setBuyer_id(pc.getAccountName());
            adenBuy.setBuyer_name(pc.getName());

            dao.insertAdenBuy(adenBuy);
            dao.updateAdenSellStatus("2", id);

            DecimalFormat df = new DecimalFormat("###,###");

            StringBuilder sb = new StringBuilder();
            sb.append("판 매 자 : ").append(adenSell.getName()).append("\r\n");
            sb.append("구 매 자 : ").append(pc.getName()).append("\r\n");
            sb.append("구매번호 : ").append(id).append("\r\n");
            sb.append("구매아덴 : ").append(df.format(adenSell.getAden())).append("\r\n");
            sb.append("구매금액 : ").append(df.format(adenSell.getCash())).append("\r\n");

            L1PcInstance adenSeller = L1World.getInstance().getPlayer(adenSell.getName());

            L1LetterUtils.sendLetter(pc, adenSell.getName(), "물품 구매신청", sb.toString(), C_MailBox.TYPE_PRIVATE_MAIL, C_MailBox.SIZE_PRIVATE_MAILBOX);

            sb.append("\r\n");
            sb.append("계좌번호 : ").append(bankAccount.getBank_no()).append("\r\n");
            sb.append("은 행 명 : ").append(bankAccount.getBank_name()).append("\r\n");
            sb.append("연 락 처 : ").append(bankAccount.getPhone());

            L1LetterUtils.sendLetter(adenSeller, pc.getName(), "물품 구매신청", sb.toString(), C_MailBox.TYPE_PRIVATE_MAIL, C_MailBox.SIZE_PRIVATE_MAILBOX);

            pc.sendPackets(new S_SystemMessage("물품구매신청완료."));
        }
    }

    public void sellAden(L1PcInstance pc, int id) {
        AdenSell adenSell = dao.getAdenSell(id);

        if (adenSell != null) {
            if (!pc.getAccountName().equalsIgnoreCase(adenSell.getAccount_id()) || !pc.getName().equalsIgnoreCase(adenSell.getName())) {
                pc.sendPackets(new S_SystemMessage("잘못된 물품번호를 입력하셨습니다."));
                return;
            }

            if (!"2".equalsIgnoreCase(adenSell.getStatus())) {
                pc.sendPackets(new S_SystemMessage("거래중인 물품이 아닙니다."));
                return;
            }

            AdenBuy adenBuy = dao.getAdenBuy(id);

            if (adenBuy == null) {
                pc.sendPackets(new S_SystemMessage("오류가 발생했습니다.운영자에게 문의해주세요."));
                return;
            }

            L1PcInstance buyer = L1World.getInstance().getPlayer(adenBuy.getBuyer_name());

            if (buyer == null) {
                pc.sendPackets(new S_SystemMessage("구매자가 접속해 있지 않습니다."));
                return;
            }

            dao.updateAdenSellStatus("3", id);

            DecimalFormat df = new DecimalFormat("###,###");

            StringBuilder sb = new StringBuilder();
            sb.append("판 매 자 : ").append(pc.getName()).append("\r\n");
            sb.append("구 매 자 : ").append(adenBuy.getBuyer_name()).append("\r\n");
            sb.append("구매번호 : ").append(id).append("\r\n");
            sb.append("구매아덴 : ").append(df.format(adenSell.getAden())).append("\r\n");
            sb.append("구매금액 : ").append(df.format(adenSell.getCash())).append("\r\n");

            L1LetterUtils.sendLetter(pc, buyer.getName(), "물품 거래완료", sb.toString(), C_MailBox.TYPE_PRIVATE_MAIL, C_MailBox.SIZE_PRIVATE_MAILBOX);
            L1LetterUtils.sendLetter(buyer, pc.getName(), "물품 거래완료", sb.toString(), C_MailBox.TYPE_PRIVATE_MAIL, C_MailBox.SIZE_PRIVATE_MAILBOX);

            buyer.getInventory().storeItem(L1ItemId.ADENA, adenSell.getAden());
            pc.sendPackets(new S_SystemMessage("물품판매가 완료되었습니다."));
        }
    }

    public void cancelAden(L1PcInstance pc, int id) {
        AdenSell adenSell = dao.getAdenSell(id);

        if (adenSell == null) {
            pc.sendPackets(new S_SystemMessage("잘못된 물품번호를 입력하셨습니다."));
            return;
        }

        if (!pc.isGm()) {
            if (!pc.getAccountName().equalsIgnoreCase(adenSell.getAccount_id()) || !pc.getName().equalsIgnoreCase(adenSell.getName())) {
                pc.sendPackets(new S_SystemMessage("잘못된 물품번호를 입력하셨습니다."));
                return;
            }

            long diffMill = Math.abs(new Date().getTime() - adenSell.getReg_date().getTime());
            long diff = TimeUnit.MINUTES.convert(diffMill, TimeUnit.MILLISECONDS);

            if (diff < CodeConfig.ADEN_CANCEL_WAIT_TIME) {
                pc.sendPackets(new S_SystemMessage(CodeConfig.ADEN_CANCEL_WAIT_TIME + "분 동안 취소할수 없습니다. " + (CodeConfig.ADEN_CANCEL_WAIT_TIME - diff) + "분 후에 시도하시기 바랍니다."));
                return;
            }

            if (!adenSell.getStatus().equalsIgnoreCase("1")) {
                pc.sendPackets(new S_SystemMessage("거래중이거나 판매완료되었습니다."));
                return;
            }
        }

        dao.deleteAdenSell(id);
        pc.sendPackets(new S_SystemMessage("물품 판매가 취소되었습니다."));
        pc.getInventory().storeItem(L1ItemId.ADENA, adenSell.getAden());
    }
}
