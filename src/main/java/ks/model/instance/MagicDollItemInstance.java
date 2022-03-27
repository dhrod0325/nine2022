package ks.model.instance;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ItemId;
import ks.constants.L1SkillIcon;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1Npc;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.*;
import ks.util.L1CommonUtils;

import java.util.Collection;

public class MagicDollItemInstance extends L1ItemInstance {
    private final L1Npc npcTemplate;
    private final int consumeCount;
    private final int dollTime;
    private int npcId = 0;
    private int itemId;
    private boolean appear;

    public MagicDollItemInstance(L1Item item) {
        super(item);

        this.itemId = item.getItemId();

        switch (itemId) {
            case L1ItemId.DOLL_서큐: // 서큐버스
                npcId = 80107;
                break;
            case L1ItemId.DOLL_늑대인간: // 늑대인간
                npcId = 80108;
                break;
            case L1ItemId.DOLL_시댄서: // 시댄서
                npcId = 4500153;
                break;
            case L1ItemId.DOLL_에티: // 에티
                npcId = 4500154;
                break;
            case L1ItemId.DOLL_코카: // 코카트리스
                npcId = 4500155;
                break;
            case L1ItemId.DOLL_라미아:// 라미아
                npcId = 4500160;
                break;
            case L1ItemId.DOLL_스파: // 스켈
                npcId = 75022;
                break;
            case L1ItemId.DOLL_허수아비:// 허수아비
                npcId = 41916;
                break;
            case L1ItemId.DOLL_에틴:
                npcId = 41917;
                break;
            case L1ItemId.DOLL_초보:
                npcId = 460000072;
                break;
            case L1ItemId.DOLL_자이언트:
                npcId = 460000080;
                break;
            case L1ItemId.DOLL_파푸:
                npcId = 460000081;
                break;
            case L1ItemId.DOLL_안타:
                npcId = 460000084;
                break;
            case L1ItemId.DOLL_린드:
                npcId = 460000083;
                break;
            case L1ItemId.DOLL_발라:
                npcId = 460000082;
                break;
            case L1ItemId.DOLL_데스:
                npcId = 460000085;
                break;
            case L1ItemId.DOLL_데몬:
                npcId = 460000086;
                break;
            case L1ItemId.DOLL_돌골렘: // 장로
                npcId = 4500150;
                break;
            case L1ItemId.DOLL_버그베어: // 장로
                npcId = 460000138;
                break;
            case L1ItemId.DOLL_목각: // 장로
                npcId = 460000139;
                break;
            case L1ItemId.DOLL_크러스트시안: // 장로
                npcId = 460000140;
                break;
            case L1ItemId.DOLL_장로: // 장로
                npcId = 460000141;
                break;
            case L1ItemId.DOLL_눈사람: // 장로
                npcId = 460000142;
                break;
            case L1ItemId.DOLL_인어: // 장로
                npcId = 460000143;
                break;
            case L1ItemId.DOLL_라바골렘: // 장로
                npcId = 460000144;
                break;
            case L1ItemId.DOLL_다이아몬드골렘: // 장로
                npcId = 460000145;
                break;
            case L1ItemId.DOLL_킹버그베어: // 장로
                npcId = 460000146;
                break;
            case L1ItemId.DOLL_드레이크: // 장로
                npcId = 460000147;
                break;
            case L1ItemId.DOLL_서큐버스퀸: // 장로
                npcId = 460000148;
                break;
            case L1ItemId.DOLL_흑장로: // 장로
                npcId = 460000149;
                break;
            case L1ItemId.DOLL_축서큐버스퀸: // 장로
                npcId = 460000150;
                break;
            case L1ItemId.DOLL_축흑장로: // 장로
                npcId = 460000151;
                break;
            case L1ItemId.DOLL_축자이언트: // 장로
                npcId = 460000152;
                break;
            case L1ItemId.DOLL_축드레이크: // 장로
                npcId = 460000153;
                break;
            case L1ItemId.DOLL_축킹버그베어: // 장로
                npcId = 460000154;
                break;
            case L1ItemId.DOLL_축다이아몬드골렘: // 장로
                npcId = 460000155;
                break;
            case L1ItemId.DOLL_리치:
                npcId = 460000156;
                break;
            case L1ItemId.DOLL_사이클롭스:
                npcId = 460000157;
                break;
            case L1ItemId.DOLL_나이트발드:
                npcId = 460000158;
                break;
            case L1ItemId.DOLL_시어:
                npcId = 460000159;
                break;
            case L1ItemId.DOLL_아이리스:
                npcId = 460000160;
                break;
            case L1ItemId.DOLL_뱀파이어:
                npcId = 460000161;
                break;
            case L1ItemId.DOLL_머미로드:
                npcId = 460000162;
                break;
            case L1ItemId.DOLL_축리치:
                npcId = 460000163;
                break;
            case L1ItemId.DOLL_축사이클롭스:
                npcId = 460000164;
                break;
            case L1ItemId.DOLL_축나이트발드:
                npcId = 460000165;
                break;
            case L1ItemId.DOLL_축시어:
                npcId = 460000166;
                break;
            case L1ItemId.DOLL_축아이리스:
                npcId = 460000167;
                break;
            case L1ItemId.DOLL_축뱀파이어:
                npcId = 460000168;
                break;
            case L1ItemId.DOLL_축머미로드:
                npcId = 460000169;
                break;
            case L1ItemId.DOLL_타락: // 돌골렘
                npcId = 460000087;
                break;
            case L1ItemId.DOLL_커츠:
                npcId = 460000088;
                break;
            case L1ItemId.DOLL_얼음여왕: // 장로
                npcId = 460000089;
                break;
            case L1ItemId.DOLL_바란카: // 장로
                npcId = 460000170;
                break;
            case L1ItemId.DOLL_바포메트: // 장로
                npcId = 460000171;
                break;
            case L1ItemId.DOLL_축바란카: // 장로
                npcId = 460000172;
                break;
            case L1ItemId.DOLL_축타락: // 장로
                npcId = 460000173;
                break;
            case L1ItemId.DOLL_축바포메트: // 장로
                npcId = 460000174;
                break;
            case L1ItemId.DOLL_축얼음여왕: // 장로
                npcId = 460000175;
                break;
            case L1ItemId.DOLL_축커츠: // 장로
                npcId = 460000176;
                break;

            case L1ItemId.DOLL_축데스: // 장로
                npcId = 460000196;
                break;
            case L1ItemId.DOLL_축데몬: // 장로
                npcId = 460000197;
                break;
            case L1ItemId.DOLL_축안타: // 장로
                npcId = 460000198;
                break;
            case L1ItemId.DOLL_축발라: // 장로
                npcId = 460000200;
                break;
            case L1ItemId.DOLL_축린드: // 장로
                npcId = 460000201;
                break;
            case L1ItemId.DOLL_축파푸:
                npcId = 460000199;
                break;
        }

        consumeCount = 50;
        dollTime = 1800;

        this.npcTemplate = NpcTable.getInstance().getTemplate(npcId);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            useMagicDoll(pc, getId());
        }
    }

    private void useMagicDoll(L1PcInstance pc, int itemObjectId) {
        if (pc.isInvisible()) {
            return;
        }

        if (pc.getMapId() == L1Map.MAP_FISHING) {
            pc.sendPackets("이곳에서 마법인형을 소환할 수 없습니다");
            return;
        }

        Collection<L1DollInstance> dollList = pc.getDollList().values();

        L1DollInstance doll = L1CommonUtils.getDoll(pc, itemObjectId);

        if (doll == null) {
            if (!pc.getInventory().checkItem(41246, consumeCount)) {
                pc.sendPackets(new S_ServerMessage(337, "$5240"));
                return;
            }

            if (dollList.size() >= CodeConfig.MAX_DOLL_COUNT) {
                pc.sendPackets(new S_ServerMessage(319));
                return;
            }

            doll = new L1DollInstance(npcTemplate, pc, this, dollTime * 1000);

            pc.getInventory().consumeItem(41246, consumeCount);
            pc.sendPackets(new S_SkillSound(doll.getId(), 3940));
            pc.sendPackets(new S_SkillSound(doll.getId(), 5935));
            pc.sendPackets(new S_SkillIconGFX(L1SkillIcon.인형, dollTime));
            pc.sendPackets(new S_ServerMessage(1143));
        } else {
            doll.deleteDoll();
        }

        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_SPMR(pc));
    }

    public boolean isAppear() {
        return appear;
    }

    public void setAppear(boolean appear) {
        this.appear = appear;
    }

    @Override
    public String getViewName() {
        String result = super.getViewName();

        if (isAppear()) {
            result += " ($117)";
        }

        return result;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    @Override
    public int getItemId() {
        return itemId;
    }

    @Override
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

}
