package ks.system.autoPotion;

import ks.model.L1Character;
import ks.model.L1ItemDelay;
import ks.model.instance.L1ItemInstance;
import ks.model.item.function.RepairItem;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Paralysis;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;

import java.util.StringTokenizer;

import static ks.constants.L1SkillId.*;

public class L1AutoPotion {
    public static final int TYPE_빨 = 1;
    public static final int TYPE_농빨 = 2;
    public static final int TYPE_고빨 = 3;
    public static final int TYPE_주 = 4;
    public static final int TYPE_농주 = 5;
    public static final int TYPE_고주 = 6;
    public static final int TYPE_말 = 7;
    public static final int TYPE_농말 = 8;
    public static final int TYPE_고말 = 9;

    private final L1PcInstance pc;

    private boolean autoPotion;
    private int autoPotionPercent;
    private int autoPotionNum;

    public L1AutoPotion(L1PcInstance pc) {
        this.pc = pc;
    }

    public int getAutoPotionNum() {
        return autoPotionNum;
    }

    public void setAutoPotionNum(int autoPotionNum) {
        this.autoPotionNum = autoPotionNum;
    }

    public String getAutoPotionNumString() {
        return getAutoPotionNumString(autoPotionNum);
    }

    public String getAutoPotionNumString(int potionNum) {
        switch (potionNum) {
            case TYPE_빨:
                return "빨";
            case TYPE_주:
                return "주";
            case TYPE_말:
                return "말";
            case TYPE_농빨:
                return "농빨";
            case TYPE_농주:
                return "농주";
            case TYPE_농말:
                return "농말";
            case TYPE_고빨:
                return "고빨";
            case TYPE_고주:
                return "고주";
            case TYPE_고말:
                return "고말";
        }

        return "";
    }

    public int getAutoPotionPercent() {
        return autoPotionPercent;
    }

    public void setAutoPotionPercent(int autoPotionPercent) {
        this.autoPotionPercent = autoPotionPercent;
    }

    public boolean isAutoPotion() {
        return autoPotion;
    }

    public void setAutoPotion(boolean autoPotion) {
        this.autoPotion = autoPotion;
    }

    public void autoPotion() {
        if (pc == null) {
            return;
        }

        if (!pc.getAutoPotion().isAutoPotion()) {
            return;
        }

        if (pc.isTeleport()) {
            return;
        }

        if (pc.isDead()) {
            return;
        }

        if (pc.getParalysisStatus().isOn(S_Paralysis.TYPE_STUN)) {
            return;
        }

        if (L1CommonUtils.isStandByServer(pc)) {
            return;
        }

        if (!pc.getMap().isUsableItem()) {
            if (pc.getAutoPotion().isAutoPotion()) {
                pc.getAutoPotion().setAutoPotion(false);
                pc.sendPackets("자동물약 사용이 불가한 지역입니다. 자동물약이 해제되었습니다");
            }
        }

        int hp = (int) (((double) pc.getCurrentHp() / (double) pc.getMaxHp()) * 100.0);

        if (hp < pc.getAutoPotion().getAutoPotionPercent()) {
            L1ItemInstance item = null;

            switch (pc.getAutoPotion().getAutoPotionNum()) {
                case TYPE_빨:
                    if (pc.getInventory().findItemId(40010) != null) {
                        item = pc.getInventory().findItemId(40010);
                    } else if (pc.getInventory().findItemId(40029) != null) {
                        item = pc.getInventory().findItemId(40029);
                    } else if (pc.getInventory().findItemId(140010) != null) {
                        item = pc.getInventory().findItemId(140010);
                    } else if (pc.getInventory().findItemId(240010) != null) {
                        item = pc.getInventory().findItemId(240010);
                    }
                    break;
                case TYPE_농빨:
                    if (pc.getInventory().findItemId(40019) != null) {
                        item = pc.getInventory().findItemId(40019);
                    }
                    break;
                case TYPE_고빨:
                    if (pc.getInventory().findItemId(40022) != null) {
                        item = pc.getInventory().findItemId(40022);
                    }
                    break;
                case TYPE_주:
                    if (pc.getInventory().findItemId(40011) != null) {
                        item = pc.getInventory().findItemId(40011);
                    } else if (pc.getInventory().findItemId(140011) != null) {
                        item = pc.getInventory().findItemId(140011);
                    }
                    break;
                case TYPE_농주:
                    if (pc.getInventory().findItemId(40020) != null) {
                        item = pc.getInventory().findItemId(40020);
                    }
                    break;
                case TYPE_고주:
                    if (pc.getInventory().findItemId(40023) != null) {
                        item = pc.getInventory().findItemId(40023);
                    }
                    break;
                case TYPE_말:
                    if (pc.getInventory().findItemId(40012) != null) {
                        item = pc.getInventory().findItemId(40012);
                    } else if (pc.getInventory().findItemId(140012) != null) {
                        item = pc.getInventory().findItemId(140012);
                    }
                    break;
                case TYPE_농말:
                    if (pc.getInventory().findItemId(40021) != null) {
                        item = pc.getInventory().findItemId(40021);
                    }
                    break;
                case TYPE_고말:
                    if (pc.getInventory().findItemId(60001302) != null) {
                        item = pc.getInventory().findItemId(60001302);
                    } else if (pc.getInventory().findItemId(40024) != null) {
                        item = pc.getInventory().findItemId(40024);
                    }

                    break;
                default:
                    break;
            }

            if (item != null && item.getCount() > 0) {
                useItem(pc, item);
            }
        }

        //비취물약
        if (pc.getPoison() != null) {
            L1ItemInstance item = null;

            if (pc.getInventory().findItemId(40507) != null) {
                item = pc.getInventory().findItemId(40507);
            } else if (pc.getInventory().findItemId(40017) != null) {
                item = pc.getInventory().findItemId(40017);
            } else if (pc.getInventory().findItemId(6000085) != null) {
                item = pc.getInventory().findItemId(6000085);
            }

            if (item != null && item.getCount() > 0) {
                useItem(pc, item);
            }
        }

        //자동숫돌
        if (pc.getWeapon() != null && pc.getWeapon().getDurability() > 0) {
            if (pc.getInventory().findItemId(40317) != null) {
                L1ItemInstance item = pc.getInventory().findItemId(40317);

                if (item != null && item.getCount() > 0) {
                    if (item instanceof RepairItem) {
                        if (!L1ItemDelay.hasItemDelay(pc, item)) {
                            RepairItem repairItem = (RepairItem) item;
                            repairItem.repair(pc, item, pc.getWeapon());
                            L1ItemDelay.onItemUse(pc, item);
                        }
                    }
                }
            }
        }

        if (!pc.getEquipSlot().isEquiedHasteItem()) {
            //촐기
            if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HASTE)) {
                L1ItemInstance item = null;

                if (pc.getInventory().findItemId(40013) != null) {
                    item = pc.getInventory().findItemId(40013);
                } else if (pc.getInventory().findItemId(40018) != null) {
                    item = pc.getInventory().findItemId(40018);
                } else if (pc.getInventory().findItemId(140018) != null) {
                    item = pc.getInventory().findItemId(140018);
                } else if (pc.getInventory().findItemId(40030) != null) {
                    item = pc.getInventory().findItemId(40030);
                } else if (pc.getInventory().findItemId(140013) != null) {
                    item = pc.getInventory().findItemId(140013);
                } else if (pc.getInventory().findItemId(50018) != null) {
                    item = pc.getInventory().findItemId(50018);
                }

                if (item != null && item.getCount() > 0) {
                    useItem(pc, item);
                }
            }
        }

        if (pc.isCrown()) {
            if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) {
                L1ItemInstance item = null;

                if (pc.getInventory().findItemId(40031) != null) {
                    item = pc.getInventory().findItemId(40031);
                }

                if (pc.getInventory().findItemId(40031) != null) {
                    item = pc.getInventory().findItemId(40031);
                }

                if (pc.getInventory().findItemId(60001428) != null) {
                    item = pc.getInventory().findItemId(60001428);
                }

                if (item != null && item.getCount() > 0) {
                    useItem(pc, item);
                }
            }
        }

        //자동용기
        if (pc.isKnight()) {
            if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) {
                L1ItemInstance item = null;

                if (pc.getInventory().findItemId(40014) != null) {
                    item = pc.getInventory().findItemId(40014);
                } else if (pc.getInventory().findItemId(140014) != null) {
                    item = pc.getInventory().findItemId(140014);
                } else if (pc.getInventory().findItemId(41415) != null) {
                    item = pc.getInventory().findItemId(41415);
                } else if (pc.getInventory().findItemId(50014) != null) {
                    item = pc.getInventory().findItemId(50014);
                }

                if (item != null && item.getCount() > 0) {
                    useItem(pc, item);
                }
            }
        }

        if (pc.isElf()) {
            if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_ELFBRAVE)
                    && !pc.getSkillEffectTimerSet().hasSkillEffect(WIND_WALK)
                    && !pc.getSkillEffectTimerSet().hasSkillEffect(DANCING_BLADES)) {

                L1ItemInstance item = null;

                if (pc.getInventory().findItemId(40068) != null) {
                    item = pc.getInventory().findItemId(40068);
                } else if (pc.getInventory().findItemId(140068) != null) {
                    item = pc.getInventory().findItemId(140068);
                } else if (pc.getInventory().findItemId(50015) != null) {
                    item = pc.getInventory().findItemId(50015);
                }

                if (item != null && item.getCount() > 0) {
                    useItem(pc, item);
                }
            }
        }

        //파란물약
        if (pc.isWizard()) {
            if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_BLUE_POTION)) {
                L1ItemInstance item = null;

                if (pc.getInventory().findItemId(40015) != null) {
                    item = pc.getInventory().findItemId(40015);
                } else if (pc.getInventory().findItemId(50017) != null) {
                    item = pc.getInventory().findItemId(50017);
                }

                if (item != null && item.getCount() > 0) {
                    useItem(pc, item);
                }
            }

            if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_WISDOM_POTION)) {
                L1ItemInstance item = null;

                if (pc.getInventory().findItemId(40016) != null) {
                    item = pc.getInventory().findItemId(40016);
                } else if (pc.getInventory().findItemId(50016) != null) {
                    item = pc.getInventory().findItemId(50016);
                }

                if (item != null && item.getCount() > 0) {
                    useItem(pc, item);
                }
            }
        }
    }

    private void useItem(L1PcInstance pc, L1ItemInstance item) {
        if (L1ItemDelay.hasItemDelay(pc, item))
            return;

        item.clickItem(pc, null);

        L1ItemDelay.onItemUse(pc, item);
    }

    public void damaged(L1Character attacker) {
        if (pc.isGm()) {
            return;
        }

        if (attacker instanceof L1PcInstance) {
            String msg = "PvP로인해 [자동물약]기능이 해제되었습니다.";

            L1PcInstance attackPc = (L1PcInstance) attacker;

            if (attackPc.getAutoPotion().isAutoPotion()) {
                attackPc.getAutoPotion().setAutoPotion(false);
                attackPc.sendPackets(msg);
            }

            if (pc.getAutoPotion().isAutoPotion()) {
                pc.getAutoPotion().setAutoPotion(false);
                pc.sendPackets(msg);
            }
        }
    }

    public void stop() {
        if (pc.getAutoPotion().isAutoPotion()) {
            pc.getAutoPotion().setAutoPotion(false);
            pc.sendPackets(new S_SystemMessage("\\fY[자동물약기능]이 해제되었습니다."));
        }
    }

    public void commandAutoPotion(String potion, StringTokenizer st) {
        try {
            int potionNum = 0;

            if ("빨".equals(potion)) {
                potionNum = TYPE_빨;
            } else if ("농빨".equals(potion)) {
                potionNum = TYPE_농빨;
            } else if ("고빨".equals(potion)) {
                potionNum = TYPE_고빨;
            } else if ("주".equals(potion)) {
                potionNum = TYPE_주;
            } else if ("농주".equals(potion)) {
                potionNum = TYPE_농주;
            } else if ("고주".equals(potion)) {
                potionNum = TYPE_고주;
            } else if ("말".equals(potion)) {
                potionNum = TYPE_말;
            } else if ("농말".equals(potion)) {
                potionNum = TYPE_농말;
            } else if ("고말".equals(potion)) {
                potionNum = TYPE_고말;
            }

            if (pc.getAutoPotion().isAutoPotion()) {
                pc.getAutoPotion().setAutoPotion(false);
                pc.sendPackets(new S_SystemMessage("\\fY[자동물약기능]이 해제되었습니다."));

                return;
            }

            int percent;

            try {
                percent = Integer.parseInt(st.nextToken());
            } catch (Exception e) {
                percent = 90;
            }

            pc.getAutoPotion().setAutoPotionPercent(percent);

            pc.getAutoPotion().setAutoPotionNum(potionNum);
            pc.getAutoPotion().setAutoPotion(true);
            pc.sendPackets(new S_SystemMessage("\\fY[자동물약기능]이 설정되었습니다.PvP시 해제됩니다."));
            pc.sendPackets(new S_SystemMessage("\\fY[자동물약상태] : " + potion + " " + percent));
            pc.sendPackets(new S_SystemMessage("\\fY[자동물약종료] : .자동물약끔"));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("\\fYEX)." + potion + " 50"));
            pc.sendPackets(new S_SystemMessage("\\fYEX).빨 .농빨 .고빨"));
        }
    }

    public void checkSkill(int skillId) {
        if (skillId == ABSOLUTE_BARRIER) {
            if (isAutoPotion()) {
                setAutoPotion(false);
                pc.sendPackets(new S_SystemMessage("\\fY[자동물약기능]이 해제되었습니다."));
            }
        }
    }
}
