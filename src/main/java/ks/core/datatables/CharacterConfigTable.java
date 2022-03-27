package ks.core.datatables;

import ks.util.common.SqlUtils;

import java.util.HashMap;
import java.util.Map;

public class CharacterConfigTable {
    private static final CharacterConfigTable instance = new CharacterConfigTable();

    public static CharacterConfigTable getInstance() {
        return instance;
    }

    public void storeCharacterConfig(int objectId, int length, byte[] data) {
        SqlUtils.update("INSERT INTO character_config SET object_id=?, length=?, data=?",
                objectId,
                length,
                data
        );
    }

    public void updateCharacterConfig(int objectId, int length, byte[] data) {
        SqlUtils.update("UPDATE character_config SET length=?, data=? WHERE object_id=?",
                length,
                data,
                objectId
        );
    }

    public Map<String, Object> loadCharacterConfig(int objectId) {
        return SqlUtils.select("SELECT * FROM character_config WHERE object_id=?", (rs, i) -> {
            Map<String, Object> result = new HashMap<>();

            int length = rs.getInt("length");
            byte[] data = rs.getBytes("data");

            result.put("length", length);
            result.put("data", data);

            return result;
        }, objectId);
    }

    public int countCharacterConfig(int objectId) {
        return SqlUtils.selectInteger("SELECT count(*) as cnt FROM character_config WHERE object_id=?", objectId);
    }
}
