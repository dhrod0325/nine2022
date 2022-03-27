//package ks.model.skill.magic.skills;
//
//import ks.model.Broadcaster;
//import ks.model.L1Character;
//import ks.model.pc.L1PcInstance;
//import ks.packets.serverpackets.S_SkillBrave;
//import ks.packets.serverpackets.S_SkillSound;
//
//public class L1SkillSecondSpeed extends L1AbstractSkill {
//    private final L1Character cha;
//    private final int duration;
//
//    public L1SkillSecondSpeed(L1Character cha, int duration) {
//        this.cha = cha;
//        this.duration = duration;
//    }
//
//    @Override
//    public int runSkill(L1SkillRequest request) {
//        cha.sendPackets(new S_SkillBrave(cha.getId(), 1, duration));
//        Broadcaster.broadcastPacket(cha, new S_SkillBrave(cha.getId(), 1, 0));
//
//        cha.sendPackets(new S_SkillSound(cha.getId(), 751));
//        Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(), 751));
//
//        cha.getMoveState().setBraveSpeed(1);
//
//        return super.runSkill(request);
//    }
//
//    @Override
//    public void stopSkill(L1Character cha) {
//        if (cha instanceof L1PcInstance) {
//            L1PcInstance pc = (L1PcInstance) cha;
//            pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
//            Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0, 0));
//        }
//
//        cha.getMoveState().setBraveSpeed(0);
//    }
//
//    @Override
//    public void sendIcon(L1Character cha,int duration) {
//        cha.sendPackets(new S_SkillBrave(cha.getId(), 1, duration));
//        Broadcaster.broadcastPacket(cha, new S_SkillBrave(cha.getId(), 1, 0));
//    }
//}
