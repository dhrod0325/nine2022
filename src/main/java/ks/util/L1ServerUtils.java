package ks.util;

import com.smattme.MysqlExportService;
import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.app.config.prop.ServerConfig;
import ks.commands.gm.GmCommands;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Properties;

@Component
public class L1ServerUtils {
    private static final Logger logger = LogManager.getLogger(L1ServerUtils.class);

    public static L1ServerUtils getInstance() {
        return LineageAppContext.getBean(L1ServerUtils.class);
    }

    public void init() {
        try {
            backUp("init");

            deleteTable("accounts", "AND access_level != 5842");
            deleteTable("aden_bank_account");
            deleteTable("aden_buy");
            deleteTable("aden_sell_list");

            deleteTable("characters_exclude");
            deleteTable("character_extra_warehouse");
            deleteTable("character_boss_key");
            deleteTable("character_badname");
            deleteTable("character_buddys");
            deleteTable("character_buff");
            deleteTable("character_bug_check");
            deleteTable("character_config");
            deleteTable("character_die");
            deleteTable("character_enchant");
            deleteTable("character_elf_warehouse");
            deleteTable("character_ip_connect_delay");
            deleteTable("character_items");
            deleteTable("character_quests");
            deleteTable("character_skills");
            deleteTable("character_slot_save");
            deleteTable("character_soldier");
            deleteTable("character_teleport");
            deleteTable("character_warehouse");
            deleteTable("characters", "AND char_name != '미소피아' and char_name != '메티스' ");

            //혈맹제거
            SqlUtils.update("UPDATE characters SET clanid=0,clanrank=0,clanname=''");

            deleteTable("characters_frame");
            deleteTable("characters_hack");
            deleteTable("characters_spr");

            deleteTable("characters_trade");
            deleteTable("characters_trade_info");
            deleteTable("clan_data", "AND clan_id != 1");
            deleteTable("clan_matching_apclist");
            deleteTable("clan_matching_list");
            deleteTable("clan_members");
            deleteTable("clan_warehouse");
            deleteTable("clan_warehouse_list");
            deleteTable("clan_warehouse_log");

            deleteTable("huntcheck");
            deleteTable("huntcheck_item");
            deleteTable("letter");
            deleteTable("pets");

            deleteTable("usershop");
            deleteTable("usershoploc");

            deleteTable("board", "AND board_id != 4500300");

            deleteTable("spawnlist_boss_die_history");
            deleteTable("spawnlist_boss_die_item");
            deleteTable("spawnlist_boss_hot_current");

            GmCommands cmd = GmCommands.getInstance();

            SqlUtils.update("UPDATE SHOP SET SELLING_PRICE = -1 WHERE npc_id = 460000090");
            SqlUtils.update("UPDATE SHOP SET SELLING_PRICE = -1 WHERE npc_id = 9000002");

            //테스트상인 삭제
            SqlUtils.update("UPDATE SPAWNLIST_NPC SET COUNT=0 WHERE npc_templateid = 9000002");

            cmd.handleCommands("이벤트 용갑 끔");
            cmd.handleCommands("이벤트 귀걸이 끔");
            cmd.handleCommands("이벤트 반지 끔");

            cmd.handleCommands("후원추가 원소 끔");
            cmd.handleCommands("후원추가 용갑변경 끔");
            cmd.handleCommands("후원추가 무기변경 끔");

            cmd.handleCommands("설정 만렙 90");
            cmd.handleCommands("설정 지급단 52");
            cmd.handleCommands("설정 기던 1");
            cmd.handleCommands("설정 공성선포렙 50");
            cmd.handleCommands("설정 공성보상 50000000");
            cmd.handleCommands("설정 아덴판매렙 53");
            cmd.handleCommands("설정 보스피1 1.0");
            cmd.handleCommands("설정 보스피2 1.0");
            cmd.handleCommands("설정 보스피3 1.0");

            cmd.handleCommands("리로드 용해제");
            cmd.handleCommands("용해제동기화");

        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void backUp(String dir) throws Exception {
        logger.info("[백업] : 디비 백업 시작 ");

        Properties properties = new Properties();

        String u = ServerConfig.DB_URL.replace("&serverTimezone=Asia/Seoul", "");

        properties.setProperty(MysqlExportService.JDBC_DRIVER_NAME, ServerConfig.DB_DRIVER);
        properties.setProperty(MysqlExportService.JDBC_CONNECTION_STRING, u);
        properties.setProperty(MysqlExportService.DB_USERNAME, ServerConfig.DB_LOGIN);
        properties.setProperty(MysqlExportService.DB_PASSWORD, ServerConfig.DB_PASSWORD);
        properties.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "false");
        properties.setProperty(MysqlExportService.PRESERVE_GENERATED_SQL_FILE, "true");

        String fileDir = CodeConfig.DB_BACKUP_DIR + "/" + dir;
        File file = new File(fileDir);

        if (!file.isDirectory()) {
            file.mkdirs();
        }

        String path = file.getPath();

        properties.setProperty(MysqlExportService.TEMP_DIR, path);
        MysqlExportService mysqlExportService = new MysqlExportService(properties);
        mysqlExportService.export();

        File sqlDir = new File(fileDir + "/sql");

        if (sqlDir.isDirectory()) {
            File[] fileList = sqlDir.listFiles();

            if (fileList != null && fileList.length > CodeConfig.DB_BACKUP_FILE_COUNT) {
                fileList[0].delete();
            }
        }

        logger.info("[백업] : 디비 백업완료 백업 위치 - {}", file.getAbsolutePath());
    }

    public void deleteTable(String tableName) {
        deleteTable(tableName, "");
    }

    public void deleteTable(String tableName, String where) {
        String sql = String.format("DELETE FROM %s WHERE 1=1 %s", tableName, where);

        SqlUtils.update(sql);

        logger.info("[초기화] - 테이블 : {} 정리", tableName);
    }
}
