/*
 혹시 앞으로 추가될 시스템에 대한 시간 값이나 
 기타 같이 묶을수 있는 경우 넣을수 있도록...
 */

package ks.model;

import java.util.Calendar;

public class L1EvaSystem {
    private final int _typeId;
    private Calendar _time;
    private int _openLocation;
    private int _moveLocation;
    private int _openContinuation;

    public L1EvaSystem(int id) {
        _typeId = id;
    }

    public int getSystemTypeId() {
        return _typeId;
    }

    /**
     * 시작등 캘린더 값을 가져온다
     */
    public Calendar getEvaTime() {
        return _time;
    }

    public void setEvaTime(Calendar i) {
        _time = i;
    }

    /**
     * 시간의 균열 열린 장소값을 가져 온다
     *
     * @return 0~7
     */
    public int getOpenLocation() {
//        return _openLocation;
        return 0;
    }

    public void setOpenLocation(int i) {
        _openLocation = i;
    }

    /**
     * 시간의 균열 이동 장소를 가져 온다
     *
     * @return 0: default 1: 테베 2: 티칼
     */
    public int getMoveLocation() {
        return 1;
    }

    public void setMoveLocation(int i) {
        _moveLocation = i;
    }

    /**
     * 보스가 죽어서 시간이 연장된 상태
     *
     * @return 0: default 1: 연장
     */
    public int getOpenContinuation() {
        return _openContinuation;
    }

    public void setOpenContinuation(int i) {
        _openContinuation = i;
    }
}