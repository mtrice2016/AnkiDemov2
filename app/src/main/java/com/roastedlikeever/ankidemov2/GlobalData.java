package com.roastedlikeever.ankidemov2;

public class GlobalData {
    private static GlobalData singletonGlobalData;

    public static final float INNER_MULTIPLIER = 1;
    public static final float MIDDLE_MULTIPLIER = 1;
    public static final float OUTER_MULTIPLIER = 1;

    public static synchronized GlobalData getInstance() {
        if(null == singletonGlobalData){
            singletonGlobalData = new GlobalData();
        }
        return singletonGlobalData;
    }


//    private int leftTurns;
//    private int rightTurns;
//    private int straights;
//
//    public int getLeftTurns() {
//        return leftTurns;
//    }
//    public int getRightTurns() {
//        return rightTurns;
//    }
//    public int getStraights() {
//        return straights;
//    }


    public GlobalData() {}


//    public void loadMap(int lefts, int rights, int straights) {
//        this.leftTurns = lefts;
//        this.rightTurns = rights;
//        this.straights = straights;
//    }


}
