package ks.util.common;

import ks.model.L1Item;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class L1ItemOutputStream extends OutputStream {
    private final ByteArrayOutputStream bao = new ByteArrayOutputStream();

    public void writeDamage(int SmallDmg, int LargeDmg) {
        writeC(1);
        writeC(SmallDmg);
        writeC(LargeDmg);
    }

    //손상도
    public void writeDurability(int value) {
        writeC(3);
        writeC(value);
    }

    //추가 대미지
    public void writeAddDamage(int value) {
        writeC(39);
        writeS("추가 대미지 +" + value);
    }

    //STR
    public void writeAddStr(int value) {
        writeC(8);
        writeC(value);
    }

    //DEX
    public void writeAddDex(int value) {
        writeC(9);
        writeC(value);
    }

    //CON
    public void writeAddCon(int value) {
        writeC(10);
        writeC(value);
    }

    //WIS
    public void writeAddWis(int value) {
        writeC(11);
        writeC(value);
    }

    //INT
    public void writeAddInt(int value) {
        writeC(12);
        writeC(value);
    }

    //CHA
    public void writeAddCha(int value) {
        writeC(13);
        writeC(value);
    }

    //최대 HP
    public void writeAddMaxHp(int value) {
        writeC(14);
        writeH(value);
    }

    //MR
    public void writeAddMr(int value) {
        writeC(15);
        writeH(value);
    }

    //MP 흡수
    public void writeMpDrain() {
        writeC(16);
    }

    //SP
    public void writeAddSp(int value) {
        writeC(17);
        writeC(value);
    }

    //헤이스트 효과
    public void writeHaste() {
        writeC(18);
    }

    public void writeAc(int value) {
        writeC(19);
        int ac = value;
        if (ac < 0) ac = -ac;
        writeC(ac);
    }

    public void writeAcType2(int ac, int material, int grade) {
        writeAc(ac);
        writeC(material);
        writeC(grade);
    }

    //밝기
    public void writeLightRange(int value) {
        writeC(22);
        writeH(value);
    }

    //재질
    public void writeMaterial(int material, int weight) {
        writeC(23);
        writeC(material);
        writeD(weight);
    }

    //불 속성 저항
    public void writeRegistFire(int value) {
        writeC(27);
        writeC(value);
    }

    //물 속성 저항
    public void writeRegistWater(int value) {
        writeC(28);
        writeC(value);
    }

    //바람 속성 저항
    public void writeRegistWind(int value) {
        writeC(29);
        writeC(value);
    }

    //불 속성 저항
    public void writeRegistEarth(int value) {
        writeC(30);
        writeC(value);
    } //바람 속성 저항

    public void writeRegistAll(int value) {
        writeC(39);
        writeS("속성저항 : +" + value);
    }

    //최대 MP
    public void writeAddMaxMP(int value) {
        writeAddMsg("최대 MP +" + value);
    }

    //HP 흡수
    public void writeHpDrain() {
        writeC(34);
    }

    //원거리 대미지
    public void writeBowDmgUp(int value) {
        writeC(35);
        writeC(value);
    }

    //원거리 명중
    public void writeBowHitUp(int value) {
        writeC(24);
        writeC(value);
    }

    //경험치 보너스
    public void writeAddExp(int value) {
        writeC(39);
        writeS("경험치 획득 : +" + value + "%");
    }

    //HP 회복
    public void writeAddHpRegen(int value) {
        writeC(37);
        writeC(value);
    }

    //MP 회복
    public void writeAddMpRegen(int value) {
        writeC(38);
        writeC(value);
    }

    //스턴적중
    public void writeAddStunHit(int AddStunHit) {
        writeC(39);
        writeS(String.format("스턴 적중 : +%d", AddStunHit));
    }

    public void writeAddMsg(String msg) {
        writeC(39);
        writeS(msg);
    }

    public void writeAddMagicChance(String msg) {
        writeAddMsg("발동 확률 : " + msg);
    }

    //근거리 대미지
    public void writeDmgUp(int value) {
        writeC(39);
        writeS("근거리 대미지 : +" + value);
    }

    //근거리 명중
    public void writeHitUp(int value) {
        writeC(5);
        writeC(value);
    }

    //추가 방어력
    public void writeAddAc(int value) {
        writeC(39);
        writeS("AC : +" + value);
    }

    //대미지 감소
    public void writeAddReduction(int value) {
        writeC(39);
        writeS("대미지 감소 : +" + value);
    }

    public void writeRegistStun(int value) {
        writeC(39);
        writeS("스턴 내성 : +" + value);
    }

    public void writeEnchantLevel(int value) {
        writeC(2);
        writeC(value);
    }

    public void writeAddAttrDamage(int i) {
        writeAddMsg("속성 대미지 : +" + i);
    }

    public void writeClass(L1Item item) {
        int bit = 0;
        bit |= item.isUseRoyal() ? 1 : 0;
        bit |= item.isUseKnight() ? 2 : 0;
        bit |= item.isUseElf() ? 4 : 0;
        bit |= item.isUseMage() ? 8 : 0;
        bit |= item.isUseDarkElf() ? 16 : 0;
        bit |= item.isUseDragonKnight() ? 32 : 0;
        bit |= item.isUseBlackWizard() ? 64 : 0;
        bit |= item.isUseHighPet() ? 128 : 0;

        writeC(7);
        writeC(bit);
    }

    public void writeSafeEnchant(int safeEnchant) {
        writeAddMsg("안전인챈 : +" + safeEnchant);
    }

    public void writePetInfo(int type1, int type2, int level, int hp) {
        writeC(25);// 종류
        writeC(type1);
        writeC(type2);
        writeC(26);// 레벨
        writeH(level);
        writeC(31);// hp
        writeH(hp);
    }

    public void writeWeaponInfo(int dmgSmall, int dmgLarge, int material) {
        writeC(1);
        writeC(dmgSmall);
        writeC(dmgLarge);
        writeC(material);
    }

    public void writeRegistInfo(int regist, int value) {
        writeC(15);
        writeH(regist);
        writeC(33);
        writeC(value);
    }

    public void writeAddWeightReduction(int weightReduction) {
        writeAddMsg("소지무게증가 : +" + weightReduction);
    }

    public void writeAddHitUp(int addHitUpByArmor) {
        writeAddMsg("공격성공 : +" + addHitUpByArmor);
    }

    public void writeAddPvpDamage(int pvpDamage) {
        writeAddMsg("PvP대미지 : +" + pvpDamage);
    }

    public void writeAddPvpReduction(int pvpReduction) {
        writeAddMsg("PvP리덕션 : +" + pvpReduction);
    }

    public void writeAddMagicHitUp(int addMagicHitUp) {
        writeAddMsg("마법적중 : +" + addMagicHitUp);
    }

    @Override
    public void write(int b) throws IOException {
        bao.write(b);
    }

    public void writeD(int value) {
        bao.write(value & 0xff);
        bao.write(value >> 8 & 0xff);
        bao.write(value >> 16 & 0xff);
        bao.write(value >> 24 & 0xff);
    }

    public void writeH(int value) {
        bao.write(value & 0xff);
        bao.write(value >> 8 & 0xff);
    }

    public void writeC(int value) {
        bao.write(value & 0xff);
    }

    public void writeS(String text) {
        try {
            if (text != null) {
                bao.write(text.getBytes("EUC-KR"));
            }
        } catch (Exception ignored) {
        }

        bao.write(0);
    }

    public int getLength() {
        return bao.size() + 2;
    }

    public byte[] getBytes() {
        return bao.toByteArray();
    }
}
