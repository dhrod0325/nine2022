package ks.core;

import ks.util.common.SqlUtils;

public class ObjectIdFactory {
    private static final ObjectIdFactory instance = new ObjectIdFactory();

    private static final int FIRST_ID = 268435456;
    private int curId;

    private ObjectIdFactory() {
        loadState();
    }

    public static ObjectIdFactory getInstance() {
        return instance;
    }

    public synchronized int nextId() {
        return curId++;
    }

    private void loadState() {
        StringBuilder sb = new StringBuilder();
        sb.append("   SELECT   ");
        sb.append("   	max( id )+ 1 AS nextid    ");
        sb.append("   FROM   ");
        sb.append("   	(   ");
        sb.append("   	SELECT   ");
        sb.append("   		id    ");
        sb.append("   	FROM   ");
        sb.append("   		character_items UNION ALL   ");
        sb.append("   	SELECT   ");
        sb.append("   		id    ");
        sb.append("   	FROM   ");
        sb.append("   		character_teleport UNION ALL   ");
        sb.append("   	SELECT   ");
        sb.append("   		id    ");
        sb.append("   	FROM   ");
        sb.append("   		character_warehouse UNION ALL   ");
        sb.append("   	SELECT   ");
        sb.append("   		id    ");
        sb.append("   	FROM   ");
        sb.append("   		character_elf_warehouse UNION ALL   ");
        sb.append("   	SELECT   ");
        sb.append("   		objid AS id    ");
        sb.append("   	FROM   ");
        sb.append("   		characters UNION ALL   ");
        sb.append("   	SELECT   ");
        sb.append("   		clan_id AS id    ");
        sb.append("   	FROM   ");
        sb.append("   		clan_data UNION ALL   ");
        sb.append("   	SELECT   ");
        sb.append("   		id    ");
        sb.append("   	FROM   ");
        sb.append("   		clan_warehouse UNION ALL   ");
        sb.append("   	SELECT   ");
        sb.append("   		objid AS id    ");
        sb.append("   	FROM   ");
        sb.append("   	pets    ");
        sb.append("   	) t   ");

        int id = SqlUtils.selectInteger(sb.toString());

        if (id < FIRST_ID) {
            id = FIRST_ID;
        }

        curId = id;
    }
}