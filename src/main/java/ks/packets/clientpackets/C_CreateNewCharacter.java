package ks.packets.clientpackets;

import ks.core.ObjectIdFactory;
import ks.core.datatables.BeginnerTable;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.account.Account;
import ks.core.datatables.account.AccountTable;
import ks.core.datatables.exp.ExpTable;
import ks.core.datatables.pc.CharacterTable;
import ks.core.network.L1Client;
import ks.model.L1CalcStat;
import ks.model.L1Skills;
import ks.model.L1World;
import ks.model.item.characterTrade.CharacterTradeDao;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_AddSkill;
import ks.packets.serverpackets.S_CharCreateStatus;
import ks.packets.serverpackets.S_NewCharPacket;
import ks.util.L1BadNameUtils;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class C_CreateNewCharacter extends ClientBasePacket {
    public static final int[] MALE_LIST = new int[]{0, 61, 138, 734, 2786, 6658, 6671};
    public static final int[] FEMALE_LIST = new int[]{1, 48, 37, 1186, 2796, 6661, 6650};
    public static final int[] START_LOC_X = new int[]{
            33439, 33440, 33417
    };

    public static final int[] START_LOC_Y = new int[]{
            32796, 32815, 32810
    };

    public static final int START_MAP = 4;

    public C_CreateNewCharacter(byte[] data, L1Client client) throws Exception {
        super(data);
        String name = readS();

        L1PcInstance pc = new L1PcInstance();

        byte str, dex, con, intel, wis, cha;
        int total;

        if (name.length() > 50) {
            client.disconnect();
            return;
        }

        if (L1CommonUtils.isInValidName(name)) {
            client.sendPacket(new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME));
            return;
        }

        if (name.length() == 0) {
            client.sendPacket(new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME));
            return;
        }

        if (L1BadNameUtils.getInstance().isBadName(name)) {
            logger.info("생성 금지된 캐릭터 이름 : " + name + ", 생성실패");
            client.sendPacket(new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME));
            return;
        }

        if (L1CommonUtils.isInvalidName(name)) {
            client.sendPacket(new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME));
            return;
        }

        if (CharacterTable.getInstance().doesCharNameExist(name) || CharacterTradeDao.getInstance().isExistCharTrade(name)) {
            logger.info("charname: " + pc.getName() + " already exists. creation failed.");
            client.sendPacket(new S_CharCreateStatus(S_CharCreateStatus.REASON_ALREADY_EXSISTS));
            return;
        }

        L1PcInstance player = L1World.getInstance().getPlayer(name);

        if (player != null) {
            logger.info("charname: " + pc.getName() + " already exists. creation failed.");
            client.sendPacket(new S_CharCreateStatus(S_CharCreateStatus.REASON_ALREADY_EXSISTS));
            return;
        }

        int slot = AccountTable.getInstance().countCharacters(client.getAccount());

        if (slot >= 8) {
            logger.info("account: " + client.getAccountName() + " 8를 넘는 캐릭터 작성 요구. ");
            client.sendPacket(new S_CharCreateStatus(S_CharCreateStatus.REASON_WRONG_AMOUNT));
            return;
        }

        pc.setName(name);
        pc.setType(readC());
        pc.setSex(readC());

        //용기사나 환술사 생성 불가
        if (pc.getType() == 5 || pc.getType() == 6) {
            /**
            Account account = client.getAccount();

            if (!account.isGameMaster()) {
                client.sendPacket(new S_CharCreateStatus(S_CharCreateStatus.REASON_WRONG_AMOUNT));
                return;
            }
             */
        }

        if (pc.getSex() == 0)
            pc.setClassId(MALE_LIST[pc.getType()]);
        else
            pc.setClassId(FEMALE_LIST[pc.getType()]);

        pc.setHighLevel(1);
        str = (byte) readC();
        dex = (byte) readC();
        con = (byte) readC();
        wis = (byte) readC();
        cha = (byte) readC();
        intel = (byte) readC();
        total = str + dex + con + wis + cha + intel;

        pc.getAbility().setBaseStr(str);
        pc.getAbility().setBaseDex(dex);
        pc.getAbility().setBaseCon(con);
        pc.getAbility().setBaseWis(wis);
        pc.getAbility().setBaseCha(cha);
        pc.getAbility().setBaseInt(intel);

        if (!pc.getAbility().isNormalAbility(pc.getClassId(), pc.getLevel(), pc.getHighLevel(), total)) {
            logger.info("Character have wrong value");
            client.sendPacket(new S_CharCreateStatus(S_CharCreateStatus.REASON_WRONG_AMOUNT));
            return;
        }

        logger.info("charname: " + pc.getName() + " classId: " + pc.getClassId());
        client.sendPacket(new S_CharCreateStatus(S_CharCreateStatus.REASON_OK));

        initNewChar(client, pc);
    }

    private static void initNewChar(L1Client client, L1PcInstance pc) {
        short initHp = L1CalcStat.calcInitHp(pc);
        short initMp = L1CalcStat.calcInitMp(pc);

        int startPos = RandomUtils.nextInt(3);

        pc.setId(ObjectIdFactory.getInstance().nextId());

        pc.setX(START_LOC_X[startPos]);
        pc.setY(START_LOC_Y[startPos]);
        pc.setMap((short) START_MAP);

        pc.setHeading(0);
        pc.setLawful(0);
        pc.addBaseMaxHp(initHp);
        pc.setCurrentHp(initHp);
        pc.addBaseMaxMp(initMp);
        pc.setCurrentMp(initMp);

        pc.setExp(ExpTable.getInstance().getStartExp());

        pc.setAinHasad(2000000);
        pc.getPcExpManager().resetAc();
        pc.setTitle("");
        pc.setClanId(0);
        pc.setClanRank(0);
        pc.setFood(39); // 17%
        pc.setAccessLevel((short) 0);
        pc.setGm(false);
        pc.setGmInvis(false);
        pc.setActionStatus(0);
        pc.setClanName("");
        pc.getAbility().setBonusAbility(0);
        pc.getPcExpManager().resetMr();
        pc.setElfAttr(0);
        pc.setPkCount(0);
        pc.setExpRes(0);
        pc.setPartnerId(0);
        pc.setOnlineStatus(0);
        pc.setBanned(false);
        pc.setKarma(0);
        pc.setMarkCount(100);
        pc.setReturnStat(0);

        pc.setIvoryTimer(0);//상아탑지하 영혼석
        pc.setElfGrave(0);//상아탑지하 영혼석
        pc.setDream_Timer(0);//몽환의섬

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

        pc.setBirthDay(Integer.parseInt(s.format(Calendar.getInstance().getTime())));

        if (pc.isWizard()) {
            pc.sendPackets(new S_AddSkill(3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            int object_id = pc.getId();
            L1Skills l1skills = SkillsTable.getInstance().getTemplate(4); // EB
            String skill_name = l1skills.getName();
            int skill_id = l1skills.getSkillId();
            SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DB에
            // 등록
        }
        if (pc.isElf()) { // 요정 텔리포터투마더 캐릭생성시 추가
            pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            int object_id = pc.getId();
            L1Skills l1skills = SkillsTable.getInstance().getTemplate(131); // 텔레포투마더
            String skill_name = l1skills.getName();
            int skill_id = l1skills.getSkillId();

            SkillsTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DB에 등록
        }

        pc.setAccountName(client.getAccountName());
        BeginnerTable.getInstance().giveItem(pc);

        pc.getPcExpManager().refreshAtCreateCharacter();

        pc.setCurrentHp(pc.getBaseMaxHp());
        pc.setCurrentMp(pc.getBaseMaxMp());
        pc.setMaxHp(pc.getBaseMaxHp());
        pc.setMaxMp(pc.getBaseMaxMp());

        CharacterTable.getInstance().storeNewCharacter(pc);

        pc.save();

        client.sendPacket(new S_NewCharPacket(pc));
    }

}
