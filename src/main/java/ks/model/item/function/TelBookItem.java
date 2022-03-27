package ks.model.item.function;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1Teleport;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;


@SuppressWarnings("serial")
public class TelBookItem extends L1ItemInstance {
    public TelBookItem(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = useItem.getItemId();

            switch (itemId) {
                case 560025:
                case 560026:
                    final int[][] TownAddBook = {
                            {34060, 32281, 4},   // 오렌
                            {33079, 33390, 4},   // 은기사
                            {32750, 32439, 4},   // 오크숲
                            {32612, 33188, 4},   // 윈다우드
                            {33720, 32492, 4},   // 웰던
                            {32872, 32912, 304}, // 침묵의 동굴
                            {32612, 32781, 4},   // 글루디오
                            {33067, 32803, 4},   // 켄트
                            {33933, 33358, 4},   // 아덴
                            {33601, 33232, 4},   // 하이네
                            {32574, 32942, 0},   // 말하는 섬
                            {33430, 32815, 4},}; // 기란
                    int[] TownAddBookList = TownAddBook[packet.readC()];
                    L1Teleport.teleport(pc, TownAddBookList[0], TownAddBookList[1], (short) TownAddBookList[2], pc.getHeading(), true);
                    pc.getInventory().removeItem(useItem, 1);
                    break;
                case 560027:
                    final int[][] DungeonAddBook = {
                            {32732, 32798, 101}, // 오만1
                            {32762, 32845, 77}, // 오랜3
                            {32710, 32788, 59}, // 에바 1
                            {32738, 32799, 49}, // 개굴
                            {32925, 32804, 430}, // 정무
                            {32929, 32995, 410}, // 마신
                            {34267, 32189, 4}, //  그신
                            {32760, 33461, 4}, // 욕망
                            {32685, 32798, 450}, // 라스타바드 마을
                            {32843, 32693, 550}, // 선박 지상층
                    };

                    int[] DungeonAddBookList = DungeonAddBook[packet.readC()];

                    L1Teleport.teleport(pc, DungeonAddBookList[0], DungeonAddBookList[1], (short) DungeonAddBookList[2], pc.getHeading(), true);
                    pc.getInventory().removeItem(useItem, 1);

                    break;
                case 560028:
                    final int[][] OmanTowerAddBook = {
                            {32755, 32743, 110},    // 오만10
                            {32751, 32741, 120},    // 오만20
                            {32752, 32739, 130},    // 오만30
                            {32750, 32739, 140},    // 오만40
                            {32750, 32739, 150},    // 오만50
                            {32746, 32816, 160},    // 오만60
                            {32633, 32787, 170},    // 오만70
                            {32633, 32787, 180},    // 오만80
                            {32633, 32787, 190},    // 오만90
                            {32730, 32854, 200}     // 오만100
                    };

                    int[] telList = OmanTowerAddBook[packet.readC()];

                    L1Teleport.teleport(pc, telList[0], telList[1], (short) telList[2], pc.getHeading(), true);
                    pc.getInventory().removeItem(useItem, 1);

                    break;
                case 30282:
                    final int[][] omanList = {
                            /*{ 32782, 32781, 101 }, // 오만1
                            { 33766, 32863, 106 }, // 오만6
                            { 32631, 32935 ,111 }, // 오만11
                            { 32744, 32862, 116 }, // 오만16
                            { 32631, 32935, 121 }, // 오만21
                            { 32744, 32862, 126 }, // 오만26
                            { 32631, 32935, 131 }, // 오만31
                            { 32744, 32862, 136 }, // 오만36
                            { 32631, 32935, 141 }, // 오만41
                            { 32744, 32862, 146 }, // 오만46
                            { 32669, 32814, 151 }, // 오만51
                            { 32739, 32798, 156 }, // 오만56
                            { 32669, 32814, 161 }, // 오만61
                            { 32739, 32798, 166 }, // 오만66
                            { 32669, 32814, 171 }, // 오만71
                            { 32739, 32798, 176 }, // 오만76
                            { 32669, 32814, 181 }, // 오만81
                            { 32739, 32798, 186 }, // 오만86
                            { 32669, 32814, 191 }, // 오만91
                            { 32739, 32798, 196 }}; // 오만96*/ //기존꺼
                            {32732, 32798, 101}, // 오만1
                            {32726, 32803, 102}, // 오만2
                            {32726, 32803, 103}, // 오만3
                            {32613, 32863, 104}, // 오만4
                            {32597, 32867, 105}, // 오만5
                            {32607, 32865, 106}, // 오만6
                            {32618, 32866, 107}, // 오만7
                            {32598, 32867, 108}, // 오만8
                            {32609, 32866, 109}, // 오만9
                            {32726, 32803, 110}, // 오만10
                            {32732, 32857, 200}, // 오만정상시작
                            {32634, 32968, 200}}; // 오만정상중간

                    int[] location = omanList[packet.readC()];
                    L1Teleport.teleport(pc, location[0], location[1], (short) location[2], pc.getHeading(), true);
                    break;
            }
        }
    }
}

