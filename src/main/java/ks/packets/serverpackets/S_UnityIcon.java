package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;

public class S_UnityIcon extends ServerBasePacket {
    public S_UnityIcon(int DECREASE, int DECAY_POTION, int SILENCE, int VENOM_RESIST, int WEAKNESS, int DISEASE,
                       int DRESS_EVASION, int BERSERKERS, int NATURES_TOUCH, int WIND_SHACKLE,
                       int ERASE_MAGIC, int ADDITIONAL_FIRE, int ELEMENTAL_FALL_DOWN, int ELEMENTAL_FIRE,
                       int STRIKER_GALE, int SOUL_OF_FLAME, int POLLUTE_WATER,
                       int EXP_POTION, int SCROLL, int SCROLLTPYE,
                       int TIKALBOSSDIE,
                       int CONCENTRATION, int INSIGHT, int PANIC,
                       int MORTAL_BODY, int HORROR_OF_DEATH, int FEAR,
                       int PATIENCE, int GUARD_BREAK, int DRAGON_SKIN, int STATUS_FRUIT,
                       int COMA, int COMA_TYPE, int FEATHER_BUFF, int FEATHER_TYPE, int MAAN_TIME, int MAAN) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(20);
        writeC(0x67);
        writeC(0x00);
        writeC(0x00);
        writeD(0);


        writeC(DECREASE); // 디크리즈 웨이트 DECREASE
        writeC(DECAY_POTION); // 디케이 포션
        writeC(0x00);
        writeC(SILENCE); // 사일런스
        writeC(VENOM_RESIST); // 베놈 레지스트
        // 10
        writeC(WEAKNESS); // 위크니스
        writeC(DISEASE); // 디지즈
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(DRESS_EVASION); // 드레스이베이전 !
        // 20
        writeC(BERSERKERS); // 버서커스 !
        writeC(NATURES_TOUCH); // 네이쳐스터치
        writeC(WIND_SHACKLE); // 윈드셰클
        writeC(ERASE_MAGIC); // 이레이즈매직
        writeC(0x00); // 디지즈아이콘인데 설명은 카운터미러효과라고
        // 되있음
        writeC(ADDITIONAL_FIRE); // 어디셔널 파이어
        writeC(ELEMENTAL_FALL_DOWN); // 엘리맨탈폴다운
        writeC(0x00);
        writeC(ELEMENTAL_FIRE); // 엘리맨탈 파이어
        writeC(0x00);
        // 30
        writeC(0x00); // 기척을지워 괴물들이 눈치채지못하게합니다???아이콘도이상함
        writeC(0x00);
        writeC(STRIKER_GALE); // 스트라이커게일
        writeC(SOUL_OF_FLAME); // 소울오브 프레임
        writeC(POLLUTE_WATER); // 플루투워터
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00); // 속성저항력 10?
        writeC(0x00);
        // 40
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00); // sp
        writeC(EXP_POTION); // exp
        writeC(SCROLL); // 전투강화주문서 123 다있음?
        writeC(SCROLLTPYE); // 0-hp50hpr4, 1-mp40mpr4, 2-추타3공성3sp3
        // 50
        writeC(0x00);
        writeC(0x00);
        writeC(TIKALBOSSDIE);// writeC(0xa2); 상아탑의 축복
        writeC(0x22);// writeC(0x22); 상아탑의 축복
        writeC(CONCENTRATION); // 컨센트레이션
        writeC(INSIGHT); // 인사이트
        writeC(PANIC); // 패닉
        writeC(MORTAL_BODY); // 모탈바디
        writeC(HORROR_OF_DEATH); // 호어오브데스
        writeC(FEAR); // 피어
        // 60
        writeC(PATIENCE); // 페이션스
        writeC(GUARD_BREAK); // 가드브레이크
        writeC(DRAGON_SKIN); // 드래곤스킨
        writeC(STATUS_FRUIT); // 유그드라
        writeC(0x14);
        writeC(0x00);
        writeC(COMA);// 시간
        writeC(COMA_TYPE);// 타입
        writeC(0x00);
        writeC(0x00);
        // 70
        writeC(0x1a);
        writeC(0x35);
        writeC(0x0d);
        writeC(0x00);
        writeC(0xf4);
        writeC(0xa5);
        writeC(0xdc);
        writeC(0x4a);
        writeC(0x00);
        writeC(0x00);
        // 80
        writeC(MAAN_TIME);    //(int)(codetest+0.5) / 32
        writeC(MAAN);    // 46지룡, 47수룡, 48풍룡, 49화룡, 50지룡,수룡 51지룡,수룡,풍룡 52지룡,수룡,풍룡,화룡

        writeC(0xa1);
        writeC(0x09);
        writeC(0x35);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        // 90
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(FEATHER_BUFF);//운세에 따른 깃털 버프
        writeC(FEATHER_TYPE); // 0x46 매우좋은 0x47 좋은 0x48 보통 0x49 나쁜
        writeC(0x00);
        writeC(0x00);
        writeC(0x04);
        writeC(0x16);
        //100
        writeC(0x65);
        writeC(0x01);
        writeC(0x09);
        writeC(0x80);
    }
}
