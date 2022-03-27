package ks.model.instance;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ActionCodes;
import ks.core.datatables.NPCTalkDataTable;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.*;
import ks.scheduler.npc.NpcRestScheduler;

import java.util.List;
import java.util.Random;

public class L1GuardianInstance extends L1NpcInstance {
    private static final long REST_SEC = 1000 * 10;
    private static final long RESTORE_SEC = 1000 * 36;

    private final Random _random = new Random(System.nanoTime());
    public long restoreTime = 0;

    public L1GuardianInstance(L1Npc template) {
        super(template);

        synchronized (this) {
            if (getTemplate().getNpcId() == 70848 || getTemplate().getNpcId() == 70850) { // 엔트
                restoreTime = System.currentTimeMillis() + RESTORE_SEC;
                NpcRestScheduler.getInstance().addGuardian(this);
            }
        }
    }

    @Override
    public void searchTarget() {
        L1PcInstance targetPlayer = null;
        List<L1PcInstance> list = L1World.getInstance().getVisiblePlayer(this);

        for (L1PcInstance pc : list) {
            if (pc == null || pc.getCurrentHp() <= 0 || pc.isDead()
                    || pc.isGm()) {
                continue;
            }
            if (!pc.isInvisible() || getTemplate().isAgrocoi()) { // 인비지체크
                if (!pc.isElf()) { // 요정이아니면
                    targetPlayer = pc;
                    Broadcaster.wideBroadcastPacket(this, new S_NpcChatPacket(this, "$804", 2));
                    break;
                }
            }
        }
        if (targetPlayer != null) {
            hateList.add(targetPlayer, 0);
            setTarget(targetPlayer);
        }
    }

    // 링크의 설정
    @Override
    public void setLink(L1Character cha) {
        if (cha != null && hateList.isEmpty()) { // 타겟이 없는 경우만 추가
            hateList.add(cha, 0);
            checkTarget();
        }
    }

    @Override
    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }
        setActivated(false);
        startAI();
    }

    @Override
    public void onAction(L1PcInstance player) {
        if (player == null)
            return;

        if (player.isElf()) {
            L1AttackRun attack = new L1AttackRun(player, this);
            attack.action();
            attack.commit();

            if (attack.getAttackParam().isHitUp() && attack.getAttackParam().getDamage() <= 15 && (player.getGfxId().getTempCharGfx() == 37 || player.getGfxId().getTempCharGfx() == 138)) {
                if (getTemplate().getNpcId() == 70848) { // 엔트
                    int chance = _random.nextInt(100) + 1;

                    if (getInventory().checkItem(40499)) { // 버포->껍질
                        player.sendPackets(new S_ServerMessage(143, "$755", "$770" + " (" + getInventory().findItemId(40499).getCount() + ")"));
                        player.getInventory().storeItem(40505, getInventory().findItemId(40499).getCount());
                        getInventory().consumeItem(40499, getInventory().findItemId(40499).getCount());
                    } else {

                        int entCount = 6 * CodeConfig.RATE_CRAFT;

                        if (getInventory().checkItem(40507, entCount)) {
                            if (chance <= 20) {
                                player.getInventory().storeItem(40507, entCount);
                                getInventory().consumeItem(40507, entCount);
                                player.sendPackets(new S_ServerMessage(143, "$755", "$763" + " (" + entCount + ")"));
                            }
                        } else {
                            if (getInventory().checkItem(40506, 1)) {
                                if (chance <= 10) {
                                    getInventory().consumeItem(40506, 1);
                                    player.getInventory().storeItem(40506, 5);
                                    player.sendPackets(new S_ServerMessage(143, "$755", "$794"));
                                }
                            } else {
                                if (chance <= 40) {
                                    Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, "$822", 0));
                                }
                            }
                        }
                    }
                }

                if (getTemplate().getNpcId() == 70850) { // 판
                    int tCount = 5 * CodeConfig.RATE_CRAFT;//판의 갈기털

                    int chance = _random.nextInt(100) + 1;

                    if (getInventory().checkItem(40519, tCount)) {
                        if (chance <= 20) {
                            getInventory().consumeItem(40519, tCount);
                            player.getInventory().storeItem(40519, tCount);
                            player.sendPackets(new S_ServerMessage(143, "$753", "$760" + " (" + tCount + ")"));
                        }
                    } else {
                        if (chance <= 40) {
                            Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, "$824", 0));
                        }
                    }
                }

                if (getTemplate().getNpcId() == 70846) {
                    if (getInventory().checkItem(40507, 2)) {
                        getInventory().consumeItem(40507, 2);
                        player.getInventory().storeItem(40503, 1);

                        player.sendPackets(new S_ServerMessage(143, "$752", "$769"));
                    }
                }
            }

        } else if (getCurrentHp() > 0 && !isDead()) {
            L1AttackRun attack = new L1AttackRun(player, this);
            attack.action();
            attack.commit();
        }
    }

    @Override
    public void onTalkAction(L1PcInstance player) {
        if (player == null)
            return;

        int objid = getId();
        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getTemplate().getNpcId());
        L1Object object = L1World.getInstance().findObject(getId());
        if (object == null)
            return;

        L1NpcInstance target = (L1NpcInstance) object;

        if (talking != null) {
            int pcx = player.getX(); // PC의 X좌표
            int pcy = player.getY(); // PC의 Y좌표
            int npcx = target.getX(); // NPC의 X좌표
            int npcy = target.getY(); // NPC의 Y좌표

            int heading = 0;
            if (pcx > npcx && pcy < npcy)
                heading = 1;
            else if (pcx > npcx && pcy == npcy)
                heading = 2;
            else if (pcx > npcx)
                heading = 3;
            else if (pcx == npcx && pcy > npcy)
                heading = 4;
            else if (pcx < npcx && pcy > npcy)
                heading = 5;
            else if (pcx < npcx && pcy == npcy)
                heading = 6;
            else if (pcx < npcx)
                heading = 7;

            setHeading(heading);
            Broadcaster.broadcastPacket(this, new S_ChangeHeading(this));

            if (player.getLawful() < -1000) {
                player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
            } else {
                player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
            }

            synchronized (this) {
                if (isRest()) {
                    restTime = System.currentTimeMillis() + REST_SEC;
                } else {
                    setRest(true);
                    restTime = System.currentTimeMillis() + REST_SEC;
                    NpcRestScheduler.getInstance().addNpc(this);
                }
            }
        }
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        if (attacker == null)
            return;
        if (attacker instanceof L1PcInstance && damage > 0) {
            L1PcInstance pc = (L1PcInstance) attacker;
            if (pc.getType() == 2 && pc.getCurrentWeapon() == 0) {

            } else {
                if (getCurrentHp() > 0 && !isDead()) {
                    setHate(attacker, damage);
                    L1SkillUtils.removeSleep(this);
                    onNpcAI();

                    searchLink(pc, getTemplate().getFamily());
                    pc.setPetTarget(this);

                    int newHp = getCurrentHp() - damage;
                    if (newHp <= 0 && !isDead()) {
                        setCurrentHp(0);
                        setDead(true);
                        setActionStatus(L1ActionCodes.ACTION_Die);
                        death();
                    }
                    if (newHp > 0) {
                        setCurrentHp(newHp);
                    }
                } else if (!isDead()) {
                    setDead(true);
                    setActionStatus(L1ActionCodes.ACTION_Die);
                    death();
                }
            }
        }
    }

    @Override
    public void setCurrentHp(int i) {
        super.setCurrentHp(i);

        if (getMaxHp() > getCurrentHp()) {
            startHpRegeneration();
        }
    }

    @Override
    public void setCurrentMp(int i) {
        super.setCurrentMp(i);

        if (getMaxMp() > getCurrentMp()) {
            startMpRegeneration();
        }
    }

    public void death() {
        try {
            NpcRestScheduler.getInstance().removeGuardian(this);
            setDeathProcessing(true);
            getInventory().clearItems();
            setCurrentHp(0);
            setDead(true);
            setActionStatus(L1ActionCodes.ACTION_Die);
            int targetobjid = getId();
            getMap().setPassable(getLocation(), true);
            Broadcaster.broadcastPacket(L1GuardianInstance.this, new S_DoActionGFX(targetobjid, L1ActionCodes.ACTION_Die));
            setDeathProcessing(false);
            allTargetClear();
            startDeleteTimer();
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void onGetItem(L1ItemInstance item) {
        refineItem();
        getInventory().shuffle();
        if (getTemplate().getDigestItem() > 0) {
            setDigestItem(item);
        }
    }

    @Override
    public void onFinalAction(L1PcInstance player, String action) {
    }

}
