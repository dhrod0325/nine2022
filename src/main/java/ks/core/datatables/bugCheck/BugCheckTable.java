package ks.core.datatables.bugCheck;

import ks.app.config.prop.ServerConfig;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.util.L1CommonUtils;
import ks.util.common.SqlUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import static ks.app.config.prop.ServerConfig.SERVER_PORT;

public class BugCheckTable {
    private static final Logger logger = LogManager.getLogger(BugCheckTable.class);

    private final static BugCheckTable instance = new BugCheckTable();

    public static BugCheckTable getInstance() {
        return instance;
    }

    public void update(L1ItemInstance item, int count) {
        String sql = "update character_bug_check set itemName=? ,itemCount=? ,itemBless=? ,itemId=?,itemEnchant=? where itemObjectId=? and serverType=?";
        SqlUtils.update(sql, item.getName(), count, item.getBless(), item.getItemId(), item.getEnchantLevel(), item.getId(), ServerConfig.SERVER_TYPE);
    }

    public void insertOrUpdate(L1ItemInstance item, int count) {
        if (item == null) {
            return;
        }

        if (item.getDropMobId() == 0) {
            BugCheck check = select(item);

            if (check == null) {
                insert(item, count);
            } else {
                update(item, count);
            }
        }
    }

    public void insert(L1ItemInstance item, int count) {
        try {
            String sql = "INSERT INTO character_bug_check (itemObjectId ,itemName ,itemCount ,itemBless ,itemId,itemEnchant,serverType,x,y,map) values (?,?,?,?,?,?,?,?,?,?)";
            SqlUtils.update(sql, item.getId(), item.getName(), count, item.getBless(), item.getItemId(), item.getEnchantLevel(), ServerConfig.SERVER_TYPE, item.getX(), item.getY(), item.getMapId());

        } catch (Exception e) {
            logger.error(e);
        }
    }

    public BugCheck select(L1ItemInstance item) {
        try {
            String sql = "select * from character_bug_check where itemObjectId=? and serverType=?";
            return SqlUtils.select(sql, new BeanPropertyRowMapper<>(BugCheck.class), item.getId(), ServerConfig.SERVER_TYPE);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            logger.error(e);
        }

        return null;
    }

    public void delete(L1ItemInstance item) {
        try {
            String sql = "DELETE FROM character_bug_check WHERE itemObjectId=? and serverType=?";
            SqlUtils.update(sql, item.getId(), ServerConfig.SERVER_TYPE);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void delete(L1ItemInstance item, int count) {
        try {
            if (item.getDropMobId() != 0) {
                return;
            }

            BugCheck check = select(item);

            if (check == null) {
                return;
            }

            int updateCount = check.getItemCount() - count;

            if (updateCount == 0) {
                delete(item);
            } else {
                update(item, updateCount);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void clear() {
        try {
            if (SERVER_PORT != 2000) {
                String sql = "DELETE FROM character_bug_check where serverType=?";
                SqlUtils.update(sql, ServerConfig.SERVER_TYPE);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public boolean isPickUpAble(L1ItemInstance item, int count) {
        if (item == null) {
            return true;
        }

        if (item.getDropMobId() == 0) {
            BugCheck check = select(item);

            if (check == null) {
                return false;
            } else {
                if (count > check.getItemCount()) {
                    return false;
                }

                return item.getEnchantLevel() <= check.getItemEnchant();
            }
        }

        return true;
    }

    public boolean isDropAble(L1PcInstance pc, int objectId, int count) {
        try {
            L1ItemInstance item = pc.getInventory().getItem(objectId);

            if (item == null) {
                return false;
            }

            int dbCount = SqlUtils.selectInteger("SELECT count from character_items where id=?", objectId);

            if (dbCount != -1) {
                String msg = "C_Drop 버그시도 - 아이템명 : " + item.getName() + ", 수량 : " + count + ", 캐릭명 : " + pc.getName();

                if (count < 0) {
                    return false;
                }

                if (dbCount <= 0) {
                    L1LogUtils.bugLog(msg);
                    L1CommonUtils.sendMessageToAllGm(msg);
                    pc.disconnect();
                    return false;
                }

                if (count > dbCount) {
                    if (!item.getName().contains("체력 회복제")) {
                        L1LogUtils.bugLog(msg);
                        L1CommonUtils.sendMessageToAllGm(msg);
                        pc.disconnect();
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return true;
    }
}
