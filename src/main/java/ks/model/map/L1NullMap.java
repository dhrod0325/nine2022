package ks.model.map;

import ks.model.types.Point;

public class L1NullMap extends L1Map {
    public L1NullMap() {
    }

    @Override
    public int getId() {
        return 516;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getTile(int x, int y) {
        return 0;
    }

    @Override
    public int getOriginalTile(int x, int y) {
        return 0;
    }

    @Override
    public boolean isInMap(int x, int y) {
        return false;
    }

    @Override
    public boolean isInMap(Point pt) {
        return false;
    }

    @Override
    public boolean isPassable(int x, int y) {
        return false;
    }

    @Override
    public boolean isPassable(Point pt) {
        return false;
    }

    @Override
    public boolean isPassable(int x, int y, int heading) {
        return false;
    }

    @Override
    public boolean isPassable(Point pt, int heading) {
        return false;
    }

    @Override
    public void setPassable(int x, int y, boolean isPassable) {
    }

    @Override
    public void setPassable(Point pt, boolean isPassable) {
    }

    @Override
    public boolean isSafetyZone(int x, int y) {
        return false;
    }

    @Override
    public boolean isSafetyZone(Point pt) {
        return false;
    }

    @Override
    public boolean isCombatZone(int x, int y) {
        return false;
    }

    @Override
    public boolean isCombatZone(Point pt) {
        return false;
    }

    @Override
    public boolean isNormalZone(int x, int y) {
        return false;
    }

    @Override
    public boolean isNormalZone(Point pt) {
        return false;
    }

    @Override
    public boolean isArrowPassable(int x, int y) {
        return false;
    }

    @Override
    public boolean isArrowPassable(Point pt) {
        return false;
    }

    @Override
    public boolean isArrowPassable(int x, int y, int heading) {
        return false;
    }

    @Override
    public boolean ismPassable(int x, int y, int heading) {
        return false;
    }

    @Override
    public boolean isArrowPassable(Point pt, int heading) {
        return false;
    }

    @Override
    public boolean isUnderwater() {
        return false;
    }

    @Override
    public boolean isMarkAble() {
        return false;
    }

    @Override
    public boolean isTeleportAble() {
        return false;
    }

    @Override
    public void setTeleportAble(boolean teleportAble) {

    }

    @Override
    public boolean isEscapable() {
        return false;
    }

    @Override
    public boolean isUseResurrection() {
        return false;
    }

    @Override
    public boolean isUsePainWand() {
        return false;
    }

    @Override
    public boolean isEnabledDeathPenalty() {
        return false;
    }

    @Override
    public boolean isTakePets() {
        return false;
    }

    @Override
    public boolean isRecallPets() {
        return false;
    }

    @Override
    public boolean isUsableItem() {
        return false;
    }

    @Override
    public boolean isUsableSkill() {
        return false;
    }

    @Override
    public boolean isFishingZone(int x, int y) {
        return false;
    }

    @Override
    public boolean isExistDoor(int x, int y) {
        return false;
    }

    @Override
    public String toString(Point pt) {
        return "null";
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean isCloseZone(int x, int y) {
        return false;
    }

    @Override
    public L1V1Map copyMap(int id) {
        return null;
    }
}
