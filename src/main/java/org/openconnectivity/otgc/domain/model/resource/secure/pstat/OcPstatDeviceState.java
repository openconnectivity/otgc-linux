/*
 * Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 * *****************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.openconnectivity.otgc.domain.model.resource.secure.pstat;

import org.iotivity.CborEncoder;
import org.iotivity.OCRepUtil;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

public class OcPstatDeviceState {

    private Integer deviceOnboardingState;
    private Boolean pendingState;

    public OcPstatDeviceState() {

    }

    public Integer getDeviceOnboardingState() {
        return deviceOnboardingState;
    }

    public void setDeviceOnboardingState(Integer deviceOnboardingState) {
        this.deviceOnboardingState = deviceOnboardingState;
    }

    public Boolean isPendingState() {
        return pendingState;
    }

    public void setPendingState(Boolean pendingState) {
        this.pendingState = pendingState;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* s */
        int s = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.DEVICE_ONBOARDING_STATE_KEY);
        this.setDeviceOnboardingState(Integer.valueOf(s));
        /* p */
        boolean p = OCRepUtil.repGetBoolean(rep, OcfResourceAttributeKey.PENDING_STATE_KEY);
        this.setPendingState(Boolean.valueOf(p));
    }

    public void parseToCbor(CborEncoder parent) {
        /* s */
        if (this.getDeviceOnboardingState() != null) {
            OCRepUtil.repSetInt(parent, OcfResourceAttributeKey.DEVICE_ONBOARDING_STATE_KEY, this.getDeviceOnboardingState());
        }
        /* p */
        if (this.isPendingState() != null) {
            OCRepUtil.repSetBoolean(parent, OcfResourceAttributeKey.PENDING_STATE_KEY, this.isPendingState());
        }
    }
}
