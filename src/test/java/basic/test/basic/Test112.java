package basic.test.basic;

public class Test112 {
    public static void main(String[] args) {
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

        System.out.println(sb.toString());
    }
}
