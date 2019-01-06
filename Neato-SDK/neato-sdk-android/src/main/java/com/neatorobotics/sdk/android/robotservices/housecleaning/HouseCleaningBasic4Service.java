package com.neatorobotics.sdk.android.robotservices.housecleaning;

/**
 * Neato
 * Created by Marco on 04/01/2017.
 * Copyright © 2016 Neato Robotics. All rights reserved.
 */

public class HouseCleaningBasic4Service extends HouseCleaningService {

    private static final String TAG = "HouseCleaningBasic2";

    @Override
    public boolean isEcoModeSupported() {
        return true;
    }

    @Override
    public boolean isTurboModeSupported() {
        return true;
    }

    @Override
    public boolean isExtraCareModeSupported() {
        return true;
    }

    @Override
    public boolean isCleaningAreaSupported() {
        return false;
    }

    @Override
    public boolean isCleaningFrequencySupported() {
        return true;
    }

}
