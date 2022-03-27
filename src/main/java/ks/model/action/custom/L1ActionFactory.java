package ks.model.action.custom;

import ks.model.L1Object;
import ks.model.action.custom.impl.npc.*;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;

public class L1ActionFactory {
    private static L1ActionExecutor createFromNpcId(String action, L1PcInstance pc, L1Object obj, String param) {
        L1ActionExecutor executor = null;

        if (obj instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            int npcId = npc.getNpcId();

            switch (npcId) {
                case 80076:
                    executor = new ActionVoyager(action, pc, obj);
                    break;
                case 80088:
                    executor = new ActionPetMatch(action, pc, obj, param);
                    break;
                case 80091:
                    executor = new ActionRafons(action, pc, obj);
                    break;
                case 80049:
                case 80050:
                case 80052:
                case 80053:
                case 80055:
                case 80056:
                case 80057:
                case 80059:
                case 80060:
                case 80061:
                case 80062:
                case 80063:
                case 80064:
                case 80066:
                case 80071:
                case 80072:
                case 80073:
                case 80074:
                    executor = new ActionKarma(action, pc, obj, param);
                    break;
                case 71119:
                    executor = new ActionRequest(action, pc, obj);
                    break;
                case 71168:
                    executor = new ActionDantes(action, pc, obj);
                    break;
                case 80082:
                case 80083:
                    executor = new ActionFish(action, pc, obj);
                    break;
                case 4204000:
                    executor = new ActionRobinHood(action, pc, obj);
                    break;
                case 4210000:
                    executor = new ActionZybril(action, pc, obj);
                    break;
                case 4200003:
                case 4200007:
                    executor = new ActionJoeGolem(action, pc, obj);
                    break;
                case 777835:
                    executor = new ActionZendor(action, pc, obj);
                    break;
                case 4200016:
                    executor = new ActionSusChef(action, pc, obj);
                    break;
                case 460000178:
                    executor = new ActionBossTranning(action, pc, obj);
                    break;
                case 4212000:
                    executor = new ActionJoeGolemCreate(action, pc, obj);
                    break;
                case 4200088:
                    executor = new ActionExpGive(action, pc, obj);
                    break;
                case 4213001:
                    executor = new ActionYuris(action, pc, obj);
                    break;
                case 460000194:
                    executor = new ActionDragonT(action, pc, obj);
                    break;
                case 460000099:
                    executor = new ActionSnapperRing(action, pc, obj);
                    break;
                case 460000100:
                    executor = new ActionEarRing(action, pc, obj);
                    break;
            }
        }

        return executor;
    }

    private static L1ActionExecutor createFromAction(String action, L1PcInstance pc, L1Object obj, String param) {
        L1ActionExecutor executor = null;

        switch (action) {
            case "tax":
                executor = new ActionTax(action, pc, obj);
                break;
            case "withdrawnpc":
            case "changename":
            case "attackchr":
                executor = new ActionPet(action, pc, obj, param);
                break;
            case "select":
                executor = new ActionSelect(action, pc, obj, param);
                break;
            case "map":
                executor = new ActionMap(action, pc, obj, param);
                break;
            case "apply":
                executor = new ActionApply(action, pc, obj, param);
                break;
            case "chg":
                executor = new ActionEriel(action, pc, obj);
                break;
            case "buy":
                executor = new ActionBuy(action, pc, obj);
                break;
            case "sell":
                executor = new ActionSell(action, pc, obj);
                break;
            case "skeleton nbmorph":
            case "lycanthrope nbmorph":
            case "shelob nbmorph":
            case "ghoul nbmorph":
            case "ghast nbmorph":
            case "atuba orc nbmorph":
            case "skeleton axeman nbmorph":
            case "troll nbmorph":
                executor = new ActionNbMorph(action, pc, obj);
                break;
            case "fullheal":
                executor = new ActionFullHeal(action, pc, obj);
                break;
            case "haste":
                executor = new ActionHaste(action, pc, obj);
                break;
            case "enca":
            case "encw":
                executor = new ActionEnc(action, pc, obj);
                break;
            case "openigate":
            case "closeigate":
            case "castlegate":
            case "healegate_giran outer gatef":
            case "healegate_giran outer gatel":
            case "healegate_giran inner gatef":
            case "healegate_giran inner gatel":
            case "healegate_giran inner gater":
            case "healigate_giran castle house door":
            case "hhealegate_iron door a":
            case "hhealegate_iron door b":
            case "autorepairon":
            case "autorepairoff":
                executor = new ActionGate(action, pc, obj);
                break;
            case "askwartime":
                executor = new ActionAskWarTime(action, pc, obj);
                break;
            case "name":
                executor = new ActionName(action, pc, obj);
                break;
            case "tel0":
            case "tel1":
            case "tel2":
            case "tel3":
                executor = new ActionTel(action, pc, obj);
                break;
            case "ent":
                executor = new ActionEnt(action, pc, obj);
                break;
            case "enter":
                executor = new ActionInn(action, pc, obj);
                break;
            case "upgrade":
                executor = new ActionUpgrade(action, pc, obj);
                break;
            case "open":
            case "close":
                executor = new ActionDoor(action, pc, obj);
                break;
            case "expel":
                executor = new ActionExpel(action, pc, obj);
                break;
            case "material":
                executor = new ActionMaterial(action, pc, obj);
                break;
            case "hall":
                executor = new ActionHall(action, pc, obj);
                break;
            case "pk":
                executor = new ActionPk(action, pc, obj);
                break;
            case "exp":
                executor = new ActionExp(action, pc, obj);
                break;
            case "init":
                executor = new ActionInit(action, pc, obj);
                break;
            case "fire":
            case "water":
            case "air":
            case "earth":
                executor = new ActionElfSkill(action, pc, obj);
                break;
            case "history":
                executor = new ActionHistory(action, pc, obj);
                break;
            case "retrieve":
                executor = new ActionRetrieve(action, pc, obj);
                break;
            case "retrieve-elven":
                executor = new ActionRetrieveElven(action, pc, obj);
                break;
            case "retrieve-pledge":
                executor = new ActionRetrievePledge(action, pc, obj);
                break;
            case "inex":
                executor = new ActionInex(action, pc, obj);
                break;
            case "stdex":
                executor = new ActionStdex(action, pc, obj);
                break;
            case "withdrawal":
                executor = new ActionWithDrawal(action, pc, obj);
                break;
            case "cdeposit":
                executor = new ActionCdeposit(action, pc, obj);
                break;
            case "employ":
                executor = new ActionEmploy(action, pc, obj);
                break;
            case "depositnpc":
                executor = new ActionDepositNpc(action, pc, obj);
                break;
            case "status":
                executor = new ActionRaceStatus(action, pc, obj);
                break;

        }

        if (action.equalsIgnoreCase("pandora6")
                || action.equalsIgnoreCase("cold6")
                || action.equalsIgnoreCase("balsim3")
                || action.equalsIgnoreCase("arieh6")
                || action.equalsIgnoreCase("andyn3")
                || action.equalsIgnoreCase("ysorya3")
                || action.equalsIgnoreCase("luth3")
                || action.equalsIgnoreCase("catty3")
                || action.equalsIgnoreCase("mayer3")
                || action.equalsIgnoreCase("vergil3")
                || action.equalsIgnoreCase("stella6")
                || action.equalsIgnoreCase("ralf6")
                || action.equalsIgnoreCase("berry6")
                || action.equalsIgnoreCase("jin6")
                || action.equalsIgnoreCase("defman3")
                || action.equalsIgnoreCase("mellisa3")
                || action.equalsIgnoreCase("mandra3")
                || action.equalsIgnoreCase("bius3")
                || action.equalsIgnoreCase("momo6")
                || action.equalsIgnoreCase("ashurEv7")
                || action.equalsIgnoreCase("elmina3")
                || action.equalsIgnoreCase("glen3")
                || action.equalsIgnoreCase("mellin3")
                || action.equalsIgnoreCase("orcm6")
                || action.equalsIgnoreCase("jackson3")
                || action.equalsIgnoreCase("britt3")
                || action.equalsIgnoreCase("old6")
                || action.equalsIgnoreCase("shivan3")) {
            executor = new ActionHtml(action, pc, obj);
        }

        return executor;
    }

    public static L1ActionExecutor create(String action, L1PcInstance pc, L1Object obj, String param) {
        L1ActionExecutor executor = createFromNpcId(action, pc, obj, param);

        if (executor != null) {
            return executor;
        } else {
            return createFromAction(action, pc, obj, param);
        }
    }
}
