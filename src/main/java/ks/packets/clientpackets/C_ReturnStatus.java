package ks.packets.clientpackets;

import ks.core.datatables.exp.ExpTable;
import ks.core.network.L1Client;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1CalcStat;
import ks.model.L1Teleport;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;
import ks.util.L1CommonUtils;

public class C_ReturnStatus extends ClientBasePacket {
    public C_ReturnStatus(byte[] decrypt, L1Client client) {
        super(decrypt);

        int type = readC();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        if (type == 1) {
            short initHp = L1CalcStat.calcInitHp(pc);
            short initMp = L1CalcStat.calcInitMp(pc);
            int str = readC();
            int intel = readC();
            int wis = readC();
            int dex = readC();
            int con = readC();
            int cha = readC();
            int total = str + dex + con + wis + cha + intel;

            if (!pc.getAbility().isNormalAbility(pc.getClassId(), pc.getLevel(), pc.getHighLevel(), total)) {
                return;
            }

            pc.getAbility().reset();

            pc.getAbility().setBaseStr((byte) str);
            pc.getAbility().setBaseInt((byte) intel);
            pc.getAbility().setBaseWis((byte) wis);
            pc.getAbility().setBaseDex((byte) dex);
            pc.getAbility().setBaseCon((byte) con);
            pc.getAbility().setBaseCha((byte) cha);

            pc.setLevel(1);

            pc.addBaseMaxHp((short) (initHp - pc.getBaseMaxHp()));
            pc.addBaseMaxMp((short) (initMp - pc.getBaseMaxMp()));
            pc.getAC().setAc(10);
            pc.sendPackets(new S_SPMR(pc));
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
        } else if (type == 2) {
            int levelup = readC();
            if (ExpTable.getInstance().getLevelByExp(pc.getExp()) < pc.getLevel()) {
                pc.disconnect();
                sendMessage("버그 시도: [" + pc.getName() + "]");
                return;
            }

            if (checkAbility(pc)) {
                int stup = 1;

                switch (levelup) {
                    case 0:
                        statUp(pc);
                        pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
                        checkOverStat(pc);
                        break;
                    case 1:
                        checkStatusBug2(pc);
                        pc.getAbility().addStr((byte) stup);//변경
                        statUp(pc);
                        pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
                        break;
                    case 2:
                        checkStatusBug2(pc);
                        pc.getAbility().addInt((byte) stup);//변경
                        statUp(pc);
                        pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
                        break;
                    case 3:
                        checkStatusBug2(pc);
                        pc.getAbility().addWis((byte) stup);//변경
                        statUp(pc);
                        pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
                        break;
                    case 4:
                        checkStatusBug2(pc);
                        pc.getAbility().addDex((byte) stup);//변경
                        statUp(pc);
                        pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
                        break;
                    case 5:
                        checkStatusBug2(pc);
                        pc.getAbility().addCon((byte) stup);//변경
                        statUp(pc);
                        pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
                        break;
                    case 6:
                        checkStatusBug2(pc);
                        pc.getAbility().addCha((byte) stup);//변경
                        statUp(pc);
                        pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
                        break;
                    case 7:
                        if (pc.getLevel() + 10 < pc.getHighLevel()) {
                            for (int m = 0; m < 10; m++) {
                                statUp(pc);
                            }
                            pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
                        }
                        checkOverStat(pc);
                        break;
                    case 8:
                        int statusup = readC();

                        switch (statusup) {
                            case 1:
                                checkStat(pc);
                                pc.getAbility().addStr((byte) stup);//변경
                                break;
                            case 2:
                                checkStat(pc);
                                pc.getAbility().addInt((byte) stup);//변경
                                break;
                            case 3:
                                checkStat(pc);
                                pc.getAbility().addWis((byte) stup);//변경
                                break;
                            case 4:
                                checkStat(pc);
                                pc.getAbility().addDex((byte) stup);//변경
                                break;
                            case 5:
                                checkStat(pc);
                                pc.getAbility().addCon((byte) stup);//변경
                                break;
                            case 6:
                                checkStat(pc);
                                pc.getAbility().addCha((byte) stup);//변경
                                break;
                        }

                        if (pc.getAbility().getElixirCount() > 0) {
                            pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.END));
                        } else {
                            try {
                                if (pc.getLevel() >= 51) {
                                    pc.getAbility().setBonusAbility(pc.getLevel() - 50);
                                } else {
                                    pc.getAbility().setBonusAbility(0);
                                }

                                if (pc.getLevel() >= 51) {
                                    pc.setExp(pc.getReturnStat());
                                    pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.END));
                                    pc.sendPackets(new S_OwnCharStatus(pc));
                                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                                    pc.sendPackets(new S_SPMR(pc));
                                    pc.setCurrentHp(pc.getMaxHp());
                                    pc.setCurrentMp(pc.getMaxHp());
                                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                                    L1Teleport.teleport(pc, 32612, 32734, (short) 4, 5, true);
                                    pc.setReturnStat(0);
                                    pc.setStatReturnCheck(false);
                                    pc.save();
                                    pc.checkStatus();
                                } else {
                                    return;
                                }
                            } catch (Exception e) {
                                logger.error(e);
                            }

                            break;
                        }
                }
            } else {
                pc.setReturnStat(1);
                pc.disconnect();
            }
        } else if (type == 3) {
            try {
                int str = readC();
                int intel = readC();
                int wis = readC();
                int dex = readC();
                int con = readC();
                int cha = readC();

                int Str = pc.getAbility().getStr();
                int Int = pc.getAbility().getInt();
                int Wis = pc.getAbility().getWis();
                int Dex = pc.getAbility().getDex();
                int Con = pc.getAbility().getCon();
                int Cha = pc.getAbility().getCha();

                if (con < Con || str < Str || intel < Int || wis < Wis || dex < Dex || cha < Cha) {
                    pc.setReturnStat(1);
                    pc.disconnect();
                } else {
                    pc.getAbility().addStr((byte) (str - pc.getAbility().getStr()));
                    pc.getAbility().addInt((byte) (intel - pc.getAbility().getInt()));
                    pc.getAbility().addWis((byte) (wis - pc.getAbility().getWis()));
                    pc.getAbility().addDex((byte) (dex - pc.getAbility().getDex()));
                    pc.getAbility().addCon((byte) (con - pc.getAbility().getCon()));
                    pc.getAbility().addCha((byte) (cha - pc.getAbility().getCha()));

                    if (pc.getLevel() >= 51)
                        pc.getAbility().setBonusAbility(pc.getLevel() - 50);
                    else {
                        pc.getAbility().setBonusAbility(0);
                    }

                    if (pc.getLevel() >= 51) {
                        pc.setExp(pc.getReturnStat());
                        pc.sendPackets(new S_OwnCharStatus(pc));
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.setCurrentMp(pc.getMaxHp());
                        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                        pc.sendPackets(new S_ReturnedStat(pc, 4));
                        L1Teleport.teleport(pc, 32612, 32734, (short) 4, 5, true);
                        pc.setReturnStat(0);
                        pc.setStatReturnCheck(false);
                        pc.save();
                        pc.checkStatus();
                    }

                    returnToLogin(pc);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public boolean checkAbility(L1PcInstance pc) {
        boolean result = true;
        int maxAbility = 35;
        int minStr, minDex, minCon, minWis, minCha, minInt;
        int str = pc.getAbility().getStr();
        int dex = pc.getAbility().getDex();
        int con = pc.getAbility().getCon();
        int wis = pc.getAbility().getWis();
        int cha = pc.getAbility().getCha();
        int intel = pc.getAbility().getInt();

        int statusCount = str + dex + con + wis + cha + intel;

        switch (pc.getType()) {
            case 0:
                minStr = 13;
                minDex = 10;
                minCon = 10;
                minWis = 11;
                minCha = 13;
                minInt = 10;
                break;
            case 1:
                minStr = 16;
                minDex = 12;
                minCon = 14;
                minWis = 9;
                minCha = 12;
                minInt = 8;
                break;
            case 2:
                minStr = 11;
                minDex = 12;
                minCon = 12;
                minWis = 12;
                minCha = 9;
                minInt = 12;
                break;
            case 3:
                minStr = 8;
                minDex = 7;
                minCon = 12;
                minWis = 12;
                minCha = 8;
                minInt = 12;
                break;
            case 4:
                minStr = 12;
                minDex = 15;
                minCon = 8;
                minWis = 10;
                minCha = 9;
                minInt = 11;
                break;
            case 5:
                minStr = 13;
                minDex = 11;
                minCon = 14;
                minWis = 12;
                minCha = 8;
                minInt = 11;
                break;
            case 6:
                minStr = 11;
                minDex = 10;
                minCon = 12;
                minWis = 12;
                minCha = 8;
                minInt = 12;
                break;
            default:
                return false;
        }

        if (pc.getLevel() <= 49 && statusCount > 75) {
            result = false;
        }

        if (pc.getAbility().getBaseStr() < minStr || pc.getAbility().getStr() > maxAbility) result = false;
        if (pc.getAbility().getBaseDex() < minDex || pc.getAbility().getDex() > maxAbility) result = false;
        if (pc.getAbility().getBaseCon() < minCon || pc.getAbility().getCon() > maxAbility) result = false;
        if (pc.getAbility().getBaseWis() < minWis || pc.getAbility().getWis() > maxAbility) result = false;
        if (pc.getAbility().getBaseCha() < minCha || pc.getAbility().getCha() > maxAbility) result = false;
        if (pc.getAbility().getBaseInt() < minInt || pc.getAbility().getInt() > maxAbility) result = false;

        return result;
    }

    public void checkNoEnd(L1PcInstance pc) {
        pc.setReturnStat(1);
        pc.disconnect();
    }

    public void checkOverStat(L1PcInstance pc) {
        int[] BaseStat2 = {pc.getAbility().getBaseStr(),
                pc.getAbility().getBaseDex(), pc.getAbility().getBaseCon(),
                pc.getAbility().getBaseWis(), pc.getAbility().getBaseCha(),
                pc.getAbility().getBaseInt()};

        int[] masBase2 = {21, 19, 19, 19, 19, 19};
        for (int i = 0; i < BaseStat2.length; i++) {
            if (BaseStat2[i] > masBase2[i]) {
                checkNoEnd(pc);
                logger.warn("기본 스탯  버그 : " + pc.getName() + " )");
            }
        }
    }

    public void checkStat(L1PcInstance pc) {
        int[] BaseStat = {pc.getAbility().getBaseStr(),
                pc.getAbility().getBaseDex(), pc.getAbility().getBaseCon(),
                pc.getAbility().getBaseWis(), pc.getAbility().getBaseCha(),
                pc.getAbility().getBaseInt()};

        int[] masBase = null;

        String[] BaseStat5 = {" 힘  ", " 덱  ", " 콘  ", " 위즈 ", " 카리 ", " 인트 "};

        int maxBase = 35;

        /* 힘 덱 콘 위즈 카리 인트 */
        switch (pc.getType()) {
            case 0:// 군주
                masBase = new int[]{13, 10, 10, 11, 13, 10};// 군주
                break;
            case 1:// 기사
                masBase = new int[]{16, 12, 14, 9, 12, 8};// 기사
                break;
            case 2:// 요정
                masBase = new int[]{11, 12, 12, 12, 9, 12};// 요정
                break;
            case 3:// 마법사
                masBase = new int[]{8, 7, 12, 12, 8, 12};// 마법사
                break;
            case 4:// 다엘
                masBase = new int[]{12, 15, 8, 10, 9, 11};// 다엘
                break;
            case 5:// 용기사
                masBase = new int[]{13, 11, 14, 12, 8, 11};// 용기사
                break;
            case 6:// 환술사
                masBase = new int[]{11, 10, 12, 12, 8, 12};// 환술사
                break;
        }

        if (masBase == null)
            return;

        for (int i = 0; i < masBase.length; i++) {
            if (BaseStat[i] < masBase[i] || BaseStat[i] > maxBase) {
                checkNoEnd(pc);
                logger.warn("초기화 버그 / 아이디 : " + pc.getName() + " )");
                break;
            } else {
                logger.info("[" + BaseStat5[i] + "] /(" + BaseStat[i] + ") /초기화 정상종료  아이디 : " + pc.getName() + " )");
            }
        }
    }

    public void returnToLogin(L1PcInstance pc) {
        L1CommonUtils.returnSelectCharacters(pc.getClient());
    }

    public void checkStatusBug2(L1PcInstance pc) {
        int _Elixir = pc.getAbility().getElixirCount();
        int status1 = pc.getHighLevel() - 50;
        int status2 = 75 + pc.getAbility().getElixirCount();
        int status3 = pc.getAbility().getDex() + pc.getAbility().getCha()
                + pc.getAbility().getCon() + pc.getAbility().getInt()
                + pc.getAbility().getStr() + pc.getAbility().getWis();

        int allbase = pc.getAbility().getBaseStr()
                + pc.getAbility().getBaseDex() + pc.getAbility().getBaseCon()
                + pc.getAbility().getBaseWis() + pc.getAbility().getBaseCha()
                + pc.getAbility().getBaseInt(); // 현재

        if (pc.getHighLevel() <= 50) {
            if (status2 > allbase + _Elixir) {
                checkNoEnd(pc);
            }
        } else if (pc.getHighLevel() >= 51) { // 51이상일경우
            if (status3 + _Elixir > status2 + status1) {
                checkNoEnd(pc);
            }
        }
    }

    private void sendMessage(String msg) {
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            pc.sendPackets(new S_ChatPacket(pc, msg, L1Opcodes.S_OPCODE_MSG, 18));
        }
    }

    public void statUp(L1PcInstance pc) {
        pc.setLevel(pc.getLevel() + 1);

        int statHp = L1CalcStat.calcStatHp(pc.getType(), pc.getBaseMaxHp(), pc.getAbility().getCon());
        int statMp = L1CalcStat.calcStatMp(pc.getType(), pc.getBaseMaxMp(), pc.getAbility().getWis());

        pc.getPcExpManager().resetAc();

        pc.addBaseMaxHp((short) statHp);
        pc.addBaseMaxMp((short) statMp);
    }
}