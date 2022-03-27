package ks.commands.user;

import ks.commands.gm.command.executor.L1Describe2;
import ks.constants.L1ItemId;
import ks.core.datatables.account.Account;
import ks.core.datatables.account.AccountTable;
import ks.core.datatables.npc.NpcTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Npc;
import ks.model.instance.L1MonsterInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_ShowCCHtml;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.boss.L1BossSpawnManager;
import ks.util.common.StringUtils;

import java.util.StringTokenizer;

public class UserCommands {
    private static final UserCommands instance = new UserCommands();

    public static UserCommands getInstance() {
        return instance;
    }

    public void handleCommands(L1PcInstance pc, String cmdLine) {
        StringTokenizer token = new StringTokenizer(cmdLine);
        String cmd = token.nextToken();

        if (cmd.equalsIgnoreCase("도움말")) {
            showHelp(pc);
        } else if (cmd.equalsIgnoreCase("암호변경")) {
            changePassword(pc, token);
        } else if (cmd.equalsIgnoreCase("퀴즈설정")) {
            quiz(pc, token);
        } else if (cmd.equalsIgnoreCase("퀴즈삭제")) {
            quizDelete(pc, token);
        } else if (cmd.equalsIgnoreCase("나이")) {
            age(pc, token);
        } else if (cmd.equalsIgnoreCase("보스")) {
            bossCheck(pc);
        } else if (cmd.equalsIgnoreCase("정보")) {
            L1Describe2.getInstance().execute(pc, "", pc.getName());
        } else {
            String msg = "커멘드 " + cmd + "는 존재하지 않습니다";
            pc.sendPackets(msg);
        }
    }


    private void showHelp(L1PcInstance pc) {
        pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_cmd"));
    }

    private void age(L1PcInstance pc, StringTokenizer st) {
        try {
            int age = Integer.parseInt(st.nextToken());

            if (age > 99) {
                pc.sendPackets(new S_SystemMessage("입력하신 나이는 올바른 값이 아닙니다."));
                return;
            }

            pc.setAge(age);
            pc.save();
            pc.sendPackets(new S_SystemMessage(pc.getName() + "님의 나이가 " + age + "세로 설정되었습니다."));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".나이 [숫자]로 입력하세요"));
        }
    }

    private void toChangePasswd(L1PcInstance pc, String passwd) {
        String accountName = AccountTable.getInstance().selectAccountNameByCharName(pc.getName());
        AccountTable.getInstance().updatePassword(accountName, passwd);
        pc.sendPackets(new S_ChatPacket(pc, "암호변경정보 :(" + passwd + ")가 설정이 완료되었습니다.", L1Opcodes.S_OPCODE_NORMALCHAT, 2));
        pc.sendPackets(new S_SystemMessage("암호 변경이 성공적으로 완료되었습니다."));
    }

    private void changePassword(L1PcInstance pc, StringTokenizer st) {
        try {
            String passwd = st.nextToken();

            Account account = AccountTable.getInstance().load(pc.getAccountName()); // 추가

            if (account.getQuiz() != null) {
                pc.sendPackets(new S_SystemMessage("용사님 퀴즈를 삭제하지 않으면 변경할 수 없습니다."));
                return;
            }

            if (passwd.length() < 4) {
                pc.sendPackets(new S_SystemMessage("용사님 입력하신 암호의 자릿수가 너무 짧습니다."));
                pc.sendPackets(new S_SystemMessage("용사님 최소 4자 이상 입력하시기 바랍니다."));
                return;
            }

            if (passwd.length() > 12) {
                pc.sendPackets(new S_SystemMessage("용사님 입력하신 암호의 자릿수가 너무 깁니다."));
                pc.sendPackets(new S_SystemMessage("용사님 최대 12자 이하로 입력하시기 바랍니다."));
                return;
            }

            if (!StringUtils.isDisitAlpha(passwd)) {
                pc.sendPackets(new S_SystemMessage("용사님 암호에 허용되지 않는 문자가 포함 되어 있습니다."));
                return;
            }

            toChangePasswd(pc, passwd);
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("(.암호변경) 변경할 암호를 입력하시기 바랍니다."));
        }
    }

    private void quiz(L1PcInstance pc, StringTokenizer st) {
        try {
            String quiz = st.nextToken();
            Account account = AccountTable.getInstance().load(pc.getAccountName());

            if (quiz.length() < 4) {
                pc.sendPackets(new S_SystemMessage("용사님 최소 4자 이상 입력하시기 바랍니다."));
                return;
            }

            if (quiz.length() > 12) {
                pc.sendPackets(new S_SystemMessage("용사님 최대 12자 이하로 입력하시기 바랍니다."));
                return;
            }

            if (!StringUtils.isDisitAlpha(quiz)) {
                pc.sendPackets(new S_SystemMessage("용사님 퀴즈에 허용되지 않는 문자가 포함 되었습니다."));
                return;
            }

            if (account.getQuiz() != null) {
                pc.sendPackets(new S_SystemMessage("용사님 이미 퀴즈가 설정되어 있습니다."));
                return;
            }

            account.setQuiz(quiz);
            AccountTable.getInstance().updateQuiz(account);
            pc.sendPackets(new S_SystemMessage("\\fT용사님의 Quiz Password (" + quiz + ")가 설정 되었습니다."));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".퀴즈설정 (숫자 입력)을 입력 하시기 바랍니다."));
        }
    }

    private void quizDelete(L1PcInstance pc, StringTokenizer st) {
        try {
            String quiz = st.nextToken();
            Account account = AccountTable.getInstance().load(pc.getAccountName());

            if (quiz.length() < 4) {
                pc.sendPackets(new S_SystemMessage("용사님 입력하신 퀴즈의 자릿수가 너무 짧습니다."));
                pc.sendPackets(new S_SystemMessage("용사님 최소 4자 이상 입력하시기 바랍니다."));
                return;
            }

            if (quiz.length() > 12) {
                pc.sendPackets(new S_SystemMessage("용사님 입력하신 퀴즈의 자릿수가 너무 깁니다."));
                pc.sendPackets(new S_SystemMessage("용사님 최대 12자 이하로 입력하시기 바랍니다."));
                return;
            }

            if (account.getQuiz() == null || account.getQuiz().equals("")) {
                pc.sendPackets(new S_SystemMessage("용사님 퀴즈가 설정되어 있지 않습니다."));
                return;
            }

            if (!quiz.equals(account.getQuiz())) {
                pc.sendPackets(new S_SystemMessage("용사님 설정된 퀴즈와 일치하지 않습니다."));
                return;
            }

            if (!StringUtils.isDisitAlpha(quiz)) {
                pc.sendPackets(new S_SystemMessage("용사님 퀴즈에 허용되지 않는 문자가 포함 되었습니다."));
                return;
            }

            account.setQuiz(null);
            AccountTable.getInstance().updateQuiz(account);
            pc.sendPackets(new S_SystemMessage("퀴즈가 삭제 되었습니다."));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("사용 예).퀴즈삭제 암호(퀴즈)"));
        }
    }

    private void bossCheck(L1PcInstance pc) {
        try {
            if (!pc.getInventory().checkItem(L1ItemId.ADENA, 100000)) {
                pc.sendPackets(new S_SystemMessage("보스 명령어를 사용하려면 100,000아데나가 필요합니다."));

                return;
            }

            pc.getInventory().consumeItem(L1ItemId.ADENA, 100000);

            StringBuilder sb = new StringBuilder();

            sb.append("---------------스폰된 보스-------------\n");

            for (L1MonsterInstance bs : L1BossSpawnManager.getInstance().getBossList()) {
                String bsName;

                if (bs.getTransformPrevNpcId() != 0) {
                    L1Npc npc = NpcTable.getInstance().getTemplate(bs.getTransformPrevNpcId());
                    bsName = npc.getName();
                } else {
                    bsName = bs.getName();
                }

                sb.append(String.format("%s \n", bsName));
            }

            sb.append("---------------------------------------\n");

            pc.sendPackets(new S_SystemMessage(sb.toString()));

            pc.sendPackets(new S_SystemMessage("100,000 아데나가 소모 되었습니다"));
        } catch (Exception e) {
            pc.sendPackets(".보스");
        }
    }
}