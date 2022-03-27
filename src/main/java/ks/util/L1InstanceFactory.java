package ks.util;

import ks.model.L1Npc;
import ks.model.instance.*;
import ks.system.dogFight.L1DogFightInstance;

public class L1InstanceFactory {
    public static L1NpcInstance createInstance(L1Npc npc) {
        return createInstance(npc.getImpl(), npc);
    }

    public static L1NpcInstance createInstance(String impl, L1Npc npc) {
        switch (impl) {
            case "L1AuctionBoard":
                return new L1AuctionBoardInstance(npc);
            case "L1Board":
                return new L1BoardInstance(npc);
            case "L1CastleGuard":
                return new L1CastleGuardInstance(npc);
            case "L1Cata":
                return new L1CataInstance(npc);
            case "L1Crown":
                return new L1CrownInstance(npc);
            case "L1Door":
                return new L1DoorInstance(npc);
            case "L1Dwarf":
                return new L1DwarfInstance(npc);
            case "L1Effect":
                return new L1EffectInstance(npc);
            case "L1EventTower":
                return new L1EventTowerInstance(npc);
            case "L1FieldObject":
                return new L1FieldObjectInstance(npc);
            case "L1Furniture":
                return new L1FurnitureInstance(npc);
            case "L1Guard":
                return new L1GuardInstance(npc);
            case "L1Guardian":
                return new L1GuardianInstance(npc);
            case "L1Housekeeper":
                return new L1HousekeeperInstance(npc);
            case "L1Merchant":
                return new L1MerchantInstance(npc);
            case "L1Model":
                return new L1ModelInstance(npc);
            case "L1Monster":
                return new L1MonsterInstance(npc);
            case "L1NearTeleporter":
                return new L1NearTeleporterInstance(npc);
            case "L1Npc":
                return new L1NpcInstance(npc);
            case "L1People":
                return new L1PeopleInstance(npc);
            case "L1Quest":
                return new L1QuestInstance(npc);
            case "L1Scarecrow":
                return new L1ScarecrowInstance(npc);
            case "L1Signboard":
                return new L1SignboardInstance(npc);
            case "L1Teleporter":
                return new L1TeleporterInstance(npc);
            case "L1Tower":
                return new L1TowerInstance(npc);
            case "L1AntCaveGuardian":
                return new L1AntCaveGuardianInstance(npc);
            case "L1DogFight":
                return new L1DogFightInstance(npc);
            case "L1Race":
                return new L1RaceInstance(npc);
        }

        return null;
    }
}
