package com.neatorobotics.sdk.android.robotservices.housecleaning;

/**
 * Neato
 * Created by Marco on 04/01/2017.
 * Copyright © 2016 Neato Robotics. All rights reserved.
 */

public class HouseCleaningServiceFactory {
    public static HouseCleaningService get(String serviceVersion) {
        if("basic-1".equalsIgnoreCase(serviceVersion)) return new HouseCleaningBasic1Service();
        else if("basic-2".equalsIgnoreCase(serviceVersion)) return new HouseCleaningBasic2Service();
        else if("basic-3".equalsIgnoreCase(serviceVersion)) return new HouseCleaningBasic3Service();
        else if("basic-4".equalsIgnoreCase(serviceVersion)) return new HouseCleaningBasic4Service();
        else if("minimal-2".equalsIgnoreCase(serviceVersion)) return new HouseCleaningMinimal2Service();
        else return null;
    }
}
