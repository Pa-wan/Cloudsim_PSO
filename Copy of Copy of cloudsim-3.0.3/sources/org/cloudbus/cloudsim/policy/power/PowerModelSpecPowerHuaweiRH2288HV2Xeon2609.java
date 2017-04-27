package org.cloudbus.cloudsim.policy.power;

import org.cloudbus.cloudsim.power.models.PowerModelSpecPower;


public class PowerModelSpecPowerHuaweiRH2288HV2Xeon2609 extends PowerModelSpecPower {

        /** The power. */
        private final double[] power = { 68.7, 78.3, 84.0, 88.4, 92.5, 97.3, 104, 111, 121, 131, 137 };

        @Override
        protected double getPowerData(int index) {
            return power[index];
        }
}
