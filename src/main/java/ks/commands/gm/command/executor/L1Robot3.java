package ks.commands.gm.command.executor;

import ks.core.ObjectIdFactory;
import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.robot.L1RobotTable;
import ks.system.robot.L1RobotType;
import ks.system.robot.ai.L1RobotTeleportAi;
import ks.system.robot.is.L1RobotInstance;
import ks.util.common.random.RandomUtils;

import java.util.StringTokenizer;

import static ks.util.L1ClassUtils.*;

public class L1Robot3 implements L1CommandExecutor {
    private static final int[] MALE_LIST = new int[]{CLASSID_KNIGHT_MALE, CLASSID_DARK_ELF_MALE, CLASSID_KNIGHT_MALE};
    private static final int[] FEMALE_LIST = new int[]{CLASSID_KNIGHT_FEMALE, CLASSID_DARK_ELF_FEMALE, CLASSID_KNIGHT_FEMALE};

    private L1Robot3() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Robot3();
    }

    static public int random(int lbound, int ubound) {
        return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);

            int robotType = Integer.parseInt(tok.nextToken());
            int count = Integer.parseInt(tok.nextToken());
            int searchCount = 0;

            L1Map map = pc.getMap();

            int x;
            int y;

            int[] loc = {-8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8};

            pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));

            while (count-- > 0) {
                String name = L1RobotTable.getInstance().findFirstRobotName();

                if (name == null) {
                    pc.sendPackets(new S_SystemMessage("더이상 생성할 이름이 존재하지않습니다."));
                    return;
                }

                L1PcInstance player = L1World.getInstance().getPlayer(name);

                if (player != null) {
                    continue;
                }

                L1RobotInstance robot = new L1RobotInstance();
                robot.setAccountName("");
                robot.setId(ObjectIdFactory.getInstance().nextId());
                robot.setName(name);

                if (robotType == 0) {
                    robot.setHighLevel(10);
                    robot.setLevel(10);
                    robot.setExp(0);
                    robot.setLawful(0);
                    robot.setClanId(0);
                    robot.setClanName("");
                    robot.setRobotType(L1RobotType.JOMBI);
                } else if (robotType == 1) {
                    int rnd = RandomUtils.nextInt(8);

                    robot.setRobotType(L1RobotType.HUNT);
                    robot.setAi(new L1RobotTeleportAi(robot));
                    robot.setHighLevel(45);//레벨구간
                    robot.setLevel(45);
                    robot.setExp(28877490);
                    robot.setClanId(0);
                    robot.setClanName("");

                    switch (rnd) {
                        case 0:
                            robot.setLawful(32767);
                            break;
                        case 1:
                            robot.setLawful(2000);
                            break;
                        case 2:
                            robot.setLawful(3000);
                            break;
                        case 3:
                            robot.setLawful(4000);
                            break;
                        case 4:
                            robot.setLawful(5000);
                            break;
                        case 5:
                            robot.setLawful(6000);
                            break;
                        case 6:
                            robot.setLawful(7000);
                            break;
                        case 7:
                            robot.setLawful(7500);
                            break;
                    }
                }

                robot.addBaseMaxHp((short) 2300);
                robot.setCurrentHp(2300);
                robot.setDead(false);
                robot.addBaseMaxMp((short) 500);
                robot.setCurrentMp(500);
                robot.getResistance().addMr(100);
                robot.getAbility().setBaseStr(35);
                robot.getAbility().setStr(35);

                robot.getAbility().setBaseCon(35);
                robot.getAbility().setCon(35);

                robot.getAbility().setBaseDex(35);
                robot.getAbility().setDex(35);

                robot.getAbility().setBaseCha(35);
                robot.getAbility().setCha(35);

                robot.getAbility().setBaseInt(35);
                robot.getAbility().setInt(35);

                robot.getAbility().setBaseWis(35);
                robot.getAbility().setWis(35);

                int sex = RandomUtils.nextInt(1);
                int type = RandomUtils.nextInt(MALE_LIST.length);
                int clazz = 0;

                switch (sex) {
                    case 0:
                        clazz = MALE_LIST[type];
                        break;
                    case 1:
                        clazz = FEMALE_LIST[type];
                        break;
                }

                robot.setClassId(clazz);
                robot.getGfxId().setTempCharGfx(clazz);
                robot.getGfxId().setGfxId(clazz);
                robot.setSex(sex);
                robot.setType(type);

                for (int i = 0; i < 17; i++) {
                    x = loc[RandomUtils.nextInt(17)];
                    y = loc[RandomUtils.nextInt(17)];
                    robot.setX(pc.getX() + x);
                    robot.setY(pc.getY() + y);
                    robot.setMap(pc.getMapId());
                    if (map.isPassable(robot.getX(), robot.getY())) {
                        break;
                    }
                }

                robot.setHeading(random(0, 7));

                robot.setFood(39);
                //robot.setClanid(0);
                //robot.setClanname("");
                robot.setClanRank(0);
                robot.setElfAttr(0);
                robot.setPkCount(0);
                robot.setExpRes(0);
                robot.setPartnerId(0);
                robot.setAccessLevel((short) 0);
                robot.setGm(false);
                robot.setHellTime(0);
                robot.setBanned(false);
                robot.setKarma(0);
                robot.setReturnStat(0);
                robot.setGmInvis(false);

                if (robot.isKnight()) {
                    robot.setCurrentWeapon(61);//집행
                    robot.getEquipSlot().set(ItemTable.getInstance().createItem(61));
                } else if (robot.isCrown()) {
                    robot.setCurrentWeapon(294);//군검
                    robot.getEquipSlot().set(ItemTable.getInstance().createItem(294));
                } else if (robot.isElf()) {
                    robot.setCurrentWeapon(189);//왕궁
                    robot.getEquipSlot().set(ItemTable.getInstance().createItem(189));

                    L1Item temp = ItemTable.getInstance().getTemplate(40748);

                    if (temp != null) {
                        L1ItemInstance item = ItemTable.getInstance().createItem(40748);
                        item.setItemOwner(robot);
                        item.setEnchantLevel(0);
                        item.setCount(100000);
                        robot.getInventory().storeItem(item);
                    }

                    robot.getInventory().setArrowId(40748);
                } else if (robot.isWizard()) {
                    robot.setCurrentWeapon(134);//수결
                    robot.getEquipSlot().set(ItemTable.getInstance().createItem(134));
                } else if (robot.isDarkElf()) {
                    robot.setCurrentWeapon(86);//붉이
                    robot.getEquipSlot().set(ItemTable.getInstance().createItem(86));
                }

                L1ItemInstance item = ItemTable.getInstance().createItem(41249);
                item.setItemOwner(robot);
                item.setEnchantLevel(2);
                item.setCount(1);

                robot.getInventory().storeItem(item);
                robot.setActionStatus(0);

                L1World.getInstance().storeObject(robot);
                L1World.getInstance().addVisibleObject(robot);

                searchCount++;
            }

            pc.sendPackets(new S_SystemMessage(searchCount + "명의 로봇 캐릭터가 배치 되었습니다."));
            pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("-------------------Robot Commands.------------------"));
            pc.sendPackets(new S_SystemMessage(" 타입 - 0:좀비"));
            pc.sendPackets(new S_SystemMessage(" 타입 - 1:공격 [허수아비 근처에선 허수아비공격]"));
            pc.sendPackets(new S_SystemMessage(".로봇 (타입) (수)"));
            pc.sendPackets(new S_SystemMessage("-------------------Robot Commands.------------------"));
        }
    }
}
