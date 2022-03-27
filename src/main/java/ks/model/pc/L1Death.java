package ks.model.pc;

import ks.app.config.prop.CodeConfig;
import ks.commands.gm.GmCommands;
import ks.constants.L1ActionCodes;
import ks.constants.L1ItemId;
import ks.constants.L1SkillId;
import ks.core.datatables.MapsTable;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.die.CharacterDieTable;
import ks.core.datatables.exp.ExpTable;
import ks.core.datatables.item.ItemTable;
import ks.model.*;
import ks.model.instance.L1GuardInstance;
import ks.model.instance.L1ItemInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.*;
import ks.system.event.TimePickupEvent;
import ks.system.robot.is.L1RobotInstance;
import ks.util.L1CharPosUtils;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.util.Collection;

public class L1Death implements Runnable {
    private final L1PcInstance pc;
    private final L1Character lastAttacker;
    private final Logger logger = LogManager.getLogger(getClass());

    public L1Death(L1PcInstance pc, L1Character lastAttacker) {
        this.pc = pc;
        this.lastAttacker = lastAttacker;
    }

    public void run() {
        CharacterDieTable.getInstance().insert(pc.getId(), 0);

        if (pc.getCurrentDoll() != null) {
            pc.getCurrentDoll().deleteDoll();
        }

        pc.setCurrentHp(0);
        pc.setGresValid(false);

        if (pc.isInParty()) {
            pc.getParty().memberDie(pc);
        }

        pc.getMap().setPassable(pc.getLocation(), true);

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
            pc.setTempCharGfxAtDead(pc.getGfxId().getTempCharGfx());
        } else {
            pc.setTempCharGfxAtDead(pc.getClassId());
        }

        if (!(pc instanceof L1RobotInstance)) {
            L1SkillUtils.skillByLogin(pc, L1SkillId.CANCELLATION);
        }

        pc.removeFastMove();
        pc.removeBraveSkillEffect();
        pc.removeHasteSkillEffect();

        pc.sendPackets(new S_ChangeShape(pc.getId(), pc.getGfxId().getTempCharGfx()));
        Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), pc.getGfxId().getTempCharGfx()));

        pc.sendPackets(new S_DoActionGFX(pc.getId(), L1ActionCodes.ACTION_Die));
        Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), L1ActionCodes.ACTION_Die));

        //죽으면 자동물약 해제됨
        pc.getAutoPotion().setAutoPotion(false);
        pc.getAutoAttack().death();

        L1LogUtils.debugLog("[사망] {} / 공격자 : {}({}) / 위치 : {} / 라우풀 {}", pc.getName(), lastAttacker.getName(), lastAttacker.getClass(), pc.getLocation().toString(), pc.getLawful());

        if (CodeConfig.SAFE_MODE) {
            pc.sendPackets(new S_SystemMessage("안전모드중으로 사망페널티 적용을 받지 않습니다"));
            return;
        }

        if (pc.getMapId() == TimePickupEvent.MAP_ID) {
            return;
        }

        if (!CodeConfig.getMapNormalChangeList().contains(pc.getMapId())) {
            if (L1CharPosUtils.isCombatZone(pc) || L1CharPosUtils.isSafeZone(pc)) {
                return;
            }

            if (!pc.getMap().isEnabledDeathPenalty()) {
                return;
            }
        }

        pc.setGresValid(true);

        if (lastAttacker instanceof L1GuardInstance) {
            if (pc.getPkCount() > 0) {
                pc.setPkCount(pc.getPkCount() - 1);
            }

            pc.setLastPk(null);
        }

        if (lastAttacker instanceof L1PcInstance) {
            if (L1CharPosUtils.isNormalZone(pc) && L1CharPosUtils.isNormalZone(lastAttacker)) {
                String msg = "\\fY[전투] : \\fW" + lastAttacker.getName() + "[KILL] \\fYvs \\fT" + pc.getName() + "[DEATH]";
                pc.sendAllMessage(msg);

                String mapName = MapsTable.getInstance().getMapName(pc.getMapId());

                if (!StringUtils.isEmpty(mapName)) {
                    pc.sendAllMessage("\\fY[전투] : \\f3사망지역 - " + mapName);
                }

                if (pc.getLevel() > CodeConfig.DEATH_KILL_UP_MIN_LEVEL) {
                    lastAttacker.setKillCount(lastAttacker.getKillCount() + 1);
                    pc.setDeathCount(pc.getDeathCount() + 1);
                } else {
                    pc.sendPackets(new S_SystemMessage(CodeConfig.DEATH_KILL_UP_MIN_LEVEL + "이하 캐릭은 킬데스가 증가하거나 줄어들지 않습니다."));
                }
            }
        }

        if (L1CastleLocation.isNowWarByArea(pc)) {
            return;
        }

        deathPenalty();

        if (lastAttacker instanceof L1PcInstance) {
            L1PcInstance killer = (L1PcInstance) lastAttacker;
            killer.setLastPk();

            int lawful;

            if (pc.isPinkName()) {
                lawful = killer.getLawful();
            } else if (pc.getLawful() >= 0) {
                lawful = killer.getLawful() - (killer.getLevel() * 240);
            } else {
                lawful = killer.getLawful();
            }

            if (lawful <= -32767) {
                lawful = -32767;
            }

            if (lawful >= 32767) {
                lawful = 32767;
            }

            killer.setLawful(lawful);
            killer.sendPackets(new S_Lawful(killer.getId(), killer.getLawful()));
            Broadcaster.broadcastPacket(killer, new S_Lawful(killer.getId(), killer.getLawful()));

            if (pc.isPinkName()) {
                pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_PINK_NAME);
                pc.setPinkName(false);
            }

            if (L1CharPosUtils.isNormalZone(pc) && L1CharPosUtils.isNormalZone(killer)) {
                if (pc.getHuntCount() > 0) {
                    int price = pc.getHuntPrice();
                    int count = price / 2;

                    killer.getInventory().storeItem(L1ItemId.ADENA, count);

                    L1LogUtils.debugLog("[사망] {} / 공격자 : {}({}) / 수배비 : -{}", pc.getName(), lastAttacker.getName(), lastAttacker.getClass(), count);

                    pc.setHuntCount(0);
                    pc.setHuntPrice(0);
                    pc.setReasonToHunt(null);
                    pc.sendAllMessage("\\fY[전투] : \\fU" + killer.getName() + "님 현상금 " + NumberFormat.getInstance().format(count) + " 획득");
                    pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_HUNT);
                }
            }
        }

        if (simWarResult()) {
            return;
        }

        serverGaHoDrop();

        dieItemAndSkillDrop();

        logger.trace("다음꺼 실행");
    }

    private void serverGaHoDrop() {
        int gaHoItemId = L1ItemId.SERVER_GAHO;
        int offGaHoItemId = L1ItemId.SERVER_GAHO_OFF;

        if (pc.getInventory().checkItem(gaHoItemId)) {
            L1ItemInstance item = pc.getInventory().findItemId(gaHoItemId);
            pc.getInventory().removeItem(item, 1);
            pc.sendPackets(new S_ServerMessage(638, item.getLogName(1)));

            L1LogUtils.debugLog("[사망] {} / 공격자 : {}({}) / 가호 : -{}", pc.getName(), lastAttacker.getName(), lastAttacker.getClass(), item.getLogName(1));

            L1ItemInstance dropItem = ItemTable.getInstance().createItem(offGaHoItemId);
            dropItem.setCount(1);
            dropItem.setIdentified(true);

            L1GroundInventory inv = L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId());
            inv.storeItem(dropItem);
        }
    }

    private void dieItemAndSkillDrop() {
        if (pc.getLawful() >= 32767) {
            return;
        }

        int count;
        int skillCount = 0;
        int rand;

        if (pc.getLawful() <= -32767) {
            count = RandomUtils.nextInt(1, 3);
            skillCount = 1;
            rand = 90;
        } else if (pc.getLawful() <= -30000) {
            count = RandomUtils.nextInt(1, 3);
            skillCount = 1;
            rand = 80;
        } else if (pc.getLawful() <= -20000) {
            count = RandomUtils.nextInt(1, 2);
            skillCount = 1;
            rand = 75;
        } else if (pc.getLawful() <= -10000) {
            count = RandomUtils.nextInt(1, 2);
            skillCount = 1;
            rand = 70;
        } else if (pc.getLawful() <= 0) {
            count = 1;
            skillCount = 1;
            rand = 65;
        } else if (pc.getLawful() <= 10000) {
            count = 1;
            rand = 60;
        } else if (pc.getLawful() <= 20000) {
            count = 1;
            rand = 55;
        } else if (pc.getLawful() <= 30000) {
            count = 1;
            rand = 50;
        } else {
            count = 1;
            skillCount = 1;
            rand = 2;
        }

        caoPenaltyResult(count, rand);
        caoPenaltySkill(skillCount, rand / 2);
    }

    public void caoPenaltyResult(int count, int dropPercent) {
        Integer o = GmCommands.getInstance().isNextDropItem(pc.getName());

        if (o != null) {
            L1ItemInstance item = pc.getInventory().findItemObjId(o);

            if (item != null) {
                pc.getInventory().setEquipped(item, false);
                pc.getInventory().tradeItem(item, item.isStackable() ? item.getCount() : 1, L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()));
                pc.sendPackets(new S_ServerMessage(638, item.getLogName()));

                L1LogUtils.debugLog("[사망] {} / 공격자 : {}({}) / 확정드랍 : {}", pc.getName(), lastAttacker.getName(), lastAttacker.getClass(), L1LogUtils.logItemName(item));

                return;
            }
        }

        for (int i = 0; i < count; i++) {
            int rand = RandomUtils.nextInt(100) + 1;

            if (rand > dropPercent) {
                continue;
            }

            L1ItemInstance item = pc.getInventory().caoPenaltyDropItem();

            if (item != null) {
                if (item.getBless() == 128) {
                    continue;
                }

                if (item.getBless() > 3) {
                    pc.getInventory().removeItem(item, item.isStackable() ? item.getCount() : 1);
                    pc.sendPackets(new S_ServerMessage(158, item.getLogName()));
                    L1LogUtils.debugLog("[사망] {} / 공격자 : {}({}) / 증발 : {}", pc.getName(), lastAttacker.getName(), lastAttacker.getClass(), L1LogUtils.logItemName(item));
                } else {
                    if (item.getItem().isWeapon()) {
                        if (RandomUtils.isWinning(100, 30)) {
                            pc.getInventory().setEquipped(item, false);
                            pc.getInventory().tradeItem(item, item.isStackable() ? item.getCount() : 1, L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()));
                            pc.sendPackets(new S_ServerMessage(638, item.getLogName()));
                            L1LogUtils.debugLog("[사망] {} / 공격자 : {}({}) / 차렷 : {}", pc.getName(), lastAttacker.getName(), lastAttacker.getClass(), L1LogUtils.logItemName(item));
                        }
                    } else {
                        pc.getInventory().setEquipped(item, false);
                        pc.getInventory().tradeItem(item, item.isStackable() ? item.getCount() : 1, L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()));
                        pc.sendPackets(new S_ServerMessage(638, item.getLogName()));

                        L1LogUtils.debugLog("[사망] {} / 공격자 : {}({}) / 드랍 : {}", pc.getName(), lastAttacker.getName(), lastAttacker.getClass(), L1LogUtils.logItemName(item));
                    }
                }
            }
        }
    }

    public void caoPenaltySkill(int skillDropCount, int dropPercent) {
        Integer o = GmCommands.getInstance().isNextDropSkill(pc.getName());

        if (o != null) {
            L1Skills skill = SkillsTable.getInstance().getTemplate(o);

            pc.sendPackets(new S_ServerMessage(638, skill.getName()));

            L1LogUtils.debugLog("[사망] {} / 공격자 : {}({}) / 확정드랍스킬 : {}", pc.getName(), lastAttacker.getName(), lastAttacker.getClass(), skill.getName());

            dropSkill(o);

            return;
        }

        int skillId = 0;

        if (pc.isCrown()) {
            skillId = RandomUtils.nextInt(16) + 1;
        } else if (pc.isKnight()) {
            skillId = RandomUtils.nextInt(8) + 1;
        } else if (pc.isElf()) {
            skillId = RandomUtils.nextInt(48) + 1;
        } else if (pc.isDarkElf()) {
            skillId = RandomUtils.nextInt(23) + 1;
        } else if (pc.isWizard()) {
            skillId = RandomUtils.nextInt(80) + 1;
        }

        for (int i = 0; i < skillDropCount; i++) {
            int rand = RandomUtils.nextInt(100) + 1;

            if (rand > dropPercent) {
                continue;
            }

            dropSkill(skillId);
        }
    }

    private void dropSkill(int skillId) {
        int lv1 = 0;
        int lv2 = 0;
        int lv3 = 0;
        int lv4 = 0;
        int lv5 = 0;
        int lv6 = 0;
        int lv7 = 0;
        int lv8 = 0;
        int lv9 = 0;
        int lv10 = 0;
        int lv11 = 0;
        int lv12 = 0;
        int lv13 = 0;
        int lv14 = 0;
        int lv15 = 0;
        int lv16 = 0;
        int lv17 = 0;
        int lv18 = 0;
        int lv19 = 0;
        int lv20 = 0;
        int lv21 = 0;
        int lv22 = 0;
        int lv23 = 0;
        int lv24 = 0;
        int lv25 = 0;
        int lv26 = 0;
        int lv27 = 0;
        int lv28 = 0;

        if (!SkillsTable.getInstance().spellCheck(pc.getId(), skillId)) {
            return;
        }

        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        switch (skill.getSkillLevel()) {
            case 1:
                lv1 = skillId;
                break;
            case 2:
                lv2 = skillId;
                break;
            case 3:
                lv3 = skillId;
                break;
            case 4:
                lv4 = skillId;
                break;
            case 5:
                lv5 = skillId;
                break;
            case 6:
                lv6 = skillId;
                break;
            case 7:
                lv7 = skillId;
                break;
            case 8:
                lv8 = skillId;
                break;
            case 9:
                lv9 = skillId;
                break;
            case 10:
                lv10 = skillId;
                break;
            case 11:
                lv11 = skillId;
                break;
            case 12:
                lv12 = skillId;
                break;
            case 13:
                lv13 = skillId;
                break;
            case 14:
                lv14 = skillId;
                break;
            case 15:
                lv15 = skillId;
                break;
            case 16:
                lv16 = skillId;
                break;
            case 17:
                lv17 = skillId;
                break;
            case 18:
                lv18 = skillId;
                break;
            case 19:
                lv19 = skillId;
                break;
            case 20:
                lv20 = skillId;
                break;
            case 21:
                lv21 = skillId;
                break;
            case 22:
                lv22 = skillId;
                break;
            case 23:
                lv23 = skillId;
                break;
            case 24:
                lv24 = skillId;
                break;
            case 25:
                lv25 = skillId;
                break;
            case 26:
                lv26 = skillId;
                break;
            case 27:
                lv27 = skillId;
                break;
            case 28:
                lv28 = skillId;
                break;
        }

        SkillsTable.getInstance().spellLost(pc.getId(), skillId);

        int result = lv1 + lv2 + lv3 + lv4 + lv5 + lv6 + lv7 + lv8 + lv9 + lv10 + lv11 + lv12 + lv13 + lv14 + lv15 + lv16 + lv17 + lv18 + lv19 + lv20 + lv21 + lv22 + lv23 + lv24 + lv25 + lv26 + lv27 + lv28;

        if (result > 0) {
            L1LogUtils.debugLog("[사망] {} / 공격자 : {}({}) / 드랍스킬 : {}", pc.getName(), lastAttacker.getName(), lastAttacker.getClass(), skill.getName());

            pc.sendPackets(new S_DelSkill(
                    lv1, lv2, lv3, lv4, lv5, lv6, lv7, lv8, lv9, lv10,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                    //lv11, lv12, lv13, lv14, lv15, lv16, lv17, lv18, lv19, lv20, lv21, lv22, lv23, lv24, lv25, lv26, lv27, lv28
            ));
        }
    }

    public boolean simWarResult() {
        if (pc.getCastleIn()) {
            return false;
        }

        if (pc.getClanId() == 0) {
            return false;
        }

        if (CodeConfig.WAR_PENALTY) {
            return false;
        }

        L1PcInstance attacker = L1CommonUtils.getAttackerToPc(lastAttacker);

        if (attacker == null)
            return false;

        boolean sameWar = false;

        Collection<L1War> warList = L1World.getInstance().getWarList();

        for (L1War war : warList) {
            if (war == null)
                continue;

            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

            int warType = war.getWarType();

            boolean isInWar = war.checkClanInWar(pc.getClanName());

            if (attacker.getClanId() != 0) {
                sameWar = war.checkClanInSameWar(pc.getClanName(), attacker.getClanName());
            }

            if (pc.getId() == clan.getLeaderId() && warType == 2 && isInWar) {
                String enemyClanName = war.getEnemyClanName(pc.getClanName());
                if (enemyClanName != null) {
                    war.ceaseWar(pc.getClanName(), enemyClanName);
                }
            }

            if (warType == 2 && sameWar) {
                return true;
            }
        }

        return false;
    }

    public void deathPenalty() {
        int oldLevel = pc.getLevel();
        int needExp = ExpTable.getInstance().getNeedExpNextLevel(oldLevel);
        int exp = 0;

        if (oldLevel >= 11 && oldLevel < 45)
            exp = (int) (needExp * 0.1);
        else if (oldLevel == 45)
            exp = (int) (needExp * 0.09);
        else if (oldLevel == 46)
            exp = (int) (needExp * 0.08);
        else if (oldLevel == 47)
            exp = (int) (needExp * 0.07);
        else if (oldLevel == 48)
            exp = (int) (needExp * 0.06);
        else if (oldLevel >= 49)
            exp = (int) (needExp * 0.05);

        if (exp == 0)
            return;

        pc.addExp(-exp);

        if (pc.getExpRes() == 0) {
            boolean check = lastAttacker instanceof L1PcInstance && (lastAttacker.getLevel() - pc.getLevel()) >= 10;

            if (!check) {
                pc.setExpRes(1);
            }
        }
    }

}
