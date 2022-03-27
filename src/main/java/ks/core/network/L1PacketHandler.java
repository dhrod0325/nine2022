package ks.core.network;

import ks.model.board.*;
import ks.packets.clientpackets.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ks.core.network.opcode.L1Opcodes.*;

public class L1PacketHandler {
    private static final Logger logger = LogManager.getLogger();

    public static void handle(byte[] data, L1Client client) {
        try {
            int opcode = data[0] & 0xff;

            logger.debug("opcode : " + opcode);

            switch (opcode) {
                case C_OPCODE_EXCLUDE:
                    new C_Exclude(data, client);
                    break;
                case C_OPCODE_CHARACTERCONFIG:
                    new C_CharacterConfig(data, client);
                    break;
                case C_OPCODE_DOOR:
                    new C_Door(data, client);
                    break;
                case C_OPCODE_TITLE:
                    new C_Title(data, client);
                    break;
                case C_OPCODE_BOARDDELETE:
                    new C_BoardDelete(data, client);
                    break;
                case C_OPCODE_PLEDGE:
                    new C_Pledge(data, client);
                    break;
                case C_OPCODE_CHANGEHEADING:
                    new C_ChangeHeading(data, client);
                    break;
                case C_OPCODE_NPCACTION:
                    new C_NPCAction(data, client);
                    break;
                case C_OPCODE_USESKILL:
                    new C_UseSkill(data, client);
                    break;
                case C_OPCODE_EMBLEM:
                    new C_Emblem(data, client);
                    break;
                case C_OPCODE_TRADEADDCANCEL:
                    new C_TradeCancel(data, client);
                    break;
                case C_OPCODE_BOOKMARK:
                    new C_AddBookmark(data, client);
                    break;
                case C_OPCODE_CREATECLAN:
                    new C_CreateClan(data, client);
                    break;
                case C_OPCODE_CLIENTVERSION:
                    new C_ServerVersion(data, client);
                    break;
                case C_OPCODE_PROPOSE:
                    new C_Propose(data, client);
                    break;
                case C_OPCODE_SKILLBUY:
                    new C_SkillBuy(data, client);
                    break;
                case C_OPCODE_BOARDBACK:
                    new C_BoardBack(data, client);
                    break;
                case C_OPCODE_SHOP:
                    new C_Shop(data, client);
                    break;
                case C_OPCODE_BOARDREAD:
                    new C_BoardRead(data, client);
                    break;
                case C_OPCODE_TRADE:
                    new C_Trade(data, client);
                    break;
                case C_OPCODE_DELETECHAR:
                    new C_DeleteChar(data, client);
                    break;
                case C_OPCODE_KEEPALIVE:
                    new C_KeepAlive(data, client);
                    break;
                case C_OPCODE_ATTR:
                    new C_Attr(data, client);
                    break;
                case C_OPCODE_LOGINPACKET:
                    new C_AuthLogin(data, client);
                    break;
                case C_OPCODE_SHOP_N_WAREHOUSE:
                    new C_ShopAndWarehouse(data, client);
                    break;
                case C_OPCODE_DEPOSIT:
                    new C_Deposit(data, client);
                    break;
                case C_OPCODE_DRAWAL:
                    new C_Drawal(data, client);
                    break;
                case C_OPCODE_LOGINTOSERVEROK:
                    new C_LoginToServerOK(data, client);
                    break;
                case C_OPCODE_SKILLBUYOK:
                    new C_SkillBuyOK(data, client);
                    break;
                case C_OPCODE_TRADEADDITEM:
                    new C_TradeAddItem(data, client);
                    break;
                case C_OPCODE_ADDBUDDY:
                    new C_AddBuddy(data, client);
                    break;
                case C_OPCODE_RETURNTOLOGIN:
                    new C_ReturnToLogin(data, client);
                    break;
                case C_OPCODE_CHAT:
                case C_OPCODE_CHATGLOBAL:
                    new C_Chat(data, client);
                    break;
                case C_OPCODE_TRADEADDOK:
                    new C_TradeOK(data, client);
                    break;
                case C_OPCODE_CHECKPK:
                    new C_CheckPK(data, client);
                    break;
                case C_OPCODE_TAXRATE:
                    new C_TaxRate(data, client);
                    break;
                case C_OPCODE_RESTART:
                    new C_Restart(data, client);
                    break;
                case C_OPCODE_RESTART_WAIT:
                    new C_RestartWait(data, client);
                    break;
                case C_OPCODE_BUDDYLIST:
                    new C_Buddy(data, client);
                    break;
                case C_OPCODE_DROPITEM:
                    new C_DropItem(data, client);
                    break;
                case C_OPCODE_LEAVEPARTY:
                    new C_LeaveParty(data, client);
                    break;
                case C_OPCODE_ATTACK:
                case C_OPCODE_ARROWATTACK:
                    new C_Attack(data, client);
                    break;
                case C_OPCODE_QUITGAME:
                    new C_QuitGame(data, client);
                    break;
                case C_OPCODE_BANCLAN:
                    new C_BanClan(data, client);
                    break;
                case C_OPCODE_BOARD:
                    new C_Board(data, client);
                    break;
                case C_OPCODE_DELETEINVENTORYITEM:
                    new C_DeleteInventoryItem(data, client);
                    break;
                case C_OPCODE_CHATWHISPER:
                    new C_ChatWhisper(data, client);
                    break;
                case C_OPCODE_PARTY:
                    new C_Party(data, client);
                    break;
                case C_OPCODE_PICKUPITEM:
                    new C_PickUpItem(data, client);
                    break;
                case C_OPCODE_WHO:
                    new C_Who(data, client);
                    break;
                case C_OPCODE_GIVEITEM:
                    new C_GiveItem(data, client);
                    break;
                case C_OPCODE_MOVECHAR:
                    new C_MoveChar(data, client);
                    break;
                case C_OPCODE_BOOKMARKDELETE:
                    new C_DeleteBookmark(data, client);
                    break;
                case C_OPCODE_RESTART_AFTER_DIE:
                    new C_RestartAfterDie(data, client);
                    break;
                case C_OPCODE_LEAVECLANE:
                    new C_LeaveClan(data, client);
                    break;
                case C_OPCODE_NPCTALK:
                    new C_NPCTalk(data, client);
                    break;
                case C_OPCODE_BANPARTY:
                    new C_BanParty(data, client);
                    break;
                case C_OPCODE_DELBUDDY:
                    new C_DelBuddy(data, client);
                    break;
                case C_OPCODE_WAR:
                    new C_War(data, client);
                    break;
                case C_OPCODE_SELECT_CHARACTER:
                    new C_SelectCharacter(data, client);
                    break;
                case C_OPCODE_PRIVATESHOPLIST:
                    new C_ShopList(data, client);
                    break;
                case C_OPCODE_JOINCLAN:
                    new C_JoinClan(data, client);
                    break;
                case C_OPCODE_NOTICECLICK:
                    new C_NoticeClick(client);
                    break;
                case C_OPCODE_CREATE_CHARACTER:
                    new C_CreateNewCharacter(data, client);
                    break;
                case C_OPCODE_EXTCOMMAND:
                    new C_ExtraCommand(data, client);
                    break;
                case C_OPCODE_BOARDWRITE:
                    new C_BoardWrite(data, client);
                    break;
                case C_OPCODE_USEITEM:
                    new C_ItemUSe(data, client);
                    break;
                case C_OPCODE_CREATEPARTY:
                    new C_CreateParty(data, client);
                    break;
                case C_OPCODE_ENTERPORTAL:
                    new C_EnterPortal(data, client);
                    break;
                case C_OPCODE_AMOUNT:
                    new C_Amount(data, client);
                    break;
                case C_OPCODE_CLAN_MATCHING:
                    new C_ClanMatching(data, client);
                    break;
                case C_OPCODE_FIX_WEAPON_LIST:
                    new C_FixWeaponList(data, client);
                    break;
                case C_OPCODE_SELECTLIST:
                    new C_SelectList(data, client);
                    break;
                case C_OPCODE_EXIT_GHOST:
                    new C_ExitGhost(data, client);
                    break;
                case C_OPCODE_CALL:
                    new C_CallPlayer(data, client);
                    break;
                case C_OPCODE_SELECTTARGET:
                    new C_SelectTarget(data);
                    break;
                case C_OPCODE_PETMENU:
                    new C_PetMenu(data, client);
                    break;
                case C_OPCODE_USEPETITEM:
                    new C_UsePetItem(data, client);
                    break;
                case C_OPCODE_TELEPORT:
                    new C_Teleport(data);
                    break;
                case C_OPCODE_RESTARTMENU:
                    new C_RestartMenu(data, client);
                    break;
                case C_OPCODE_CHATPARTY:
                    new C_ChatParty(data, client);
                    break;
                case C_OPCODE_FIGHT:
                    new C_Fight(data, client);
                    break;
                case C_OPCODE_SHIP:
                    new C_Ship(data, client);
                    break;
                case C_OPCODE_MAIL:
                    new C_MailBox(data, client);
                    break;
                case C_OPCODE_BASERESET:
                    new C_ReturnStatus(data, client);
                    break;
                case C_OPCODE_WAREHOUSEPASSWORD:
                    new C_WarehousePassword(data, client);
                    break;
                case C_OPCODE_HORUN:
                    new C_Horun(data, client);
                    break;
                case C_OPCODE_HORUNOK:
                    new C_HorunOK(data, client);
                    break;
                case C_OPCODE_SOLDIERGIVEOK:
                    new C_SoldierGiveOK(data, client);
                    break;
                case C_OPCODE_CHANGEWARTIME:
                    new C_WarTimeList(data, client);
                    break;
                case C_OPCODE_WARTIMESET:
                    new C_WarTimeSet(data, client);
                    break;
                case C_OPCODE_CLANATTENTION:
                    new C_ClanAttention(data, client);
                    break;
                case C_OPCODE_CLAN:
                    new C_Clan(data, client);
                    break;
                case C_OPCODE_SECURITYSTATUS:
                    new C_SecurityStatus(data, client);
                    break;
                case C_OPCODE_SECURITYSTATUSSET:
                    new C_SecurityStatusSet(data, client);
                    break;
                case C_OPCODE_REPORT:
                    new C_Report(data, client);
                    break;
                case C_OPCODE_FISHCLICK:
                    new C_FishCancel(data, client);
                    break;
                case C_OPCODE_PLEDGECONTENT:
                    new C_PledgeContent(data, client);
                    break;
                default:
                    logger.trace("용도 불명 작동코드: " + opcode);
                    break;
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
