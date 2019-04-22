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

import java.util.Arrays;
import java.util.List;

public class OcPstat {

    private OcPstatDeviceState deviceState = null;
    private Boolean isOperational;
    private Integer currentMode;
    private Integer targetMode;
    private Integer operationalMode;
    private Integer supportedMode;
    private String deviceuuid;
    private String rowneruuid;
    List<String> resourceTypes;
    List<String> interfaces;

    public OcPstat() {
    }


    public OcPstatDeviceState getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(OcPstatDeviceState deviceState) {
        this.deviceState = deviceState;
    }

    public Boolean getOperational() {
        return isOperational;
    }

    public void setOperational(Boolean operational) {
        isOperational = operational;
    }

    public Integer getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(Integer currentMode) {
        this.currentMode = currentMode;
    }

    public Integer getTargetMode() {
        return targetMode;
    }

    public void setTargetMode(Integer targetMode) {
        this.targetMode = targetMode;
    }

    public Integer getOperationalMode() {
        return operationalMode;
    }

    public void setOperationalMode(Integer operationalMode) {
        this.operationalMode = operationalMode;
    }

    public Integer getSupportedMode() {
        return supportedMode;
    }

    public void setSupportedMode(Integer supportedMode) {
        this.supportedMode = supportedMode;
    }

    public String getDeviceuuid() {
        return deviceuuid;
    }

    public void setDeviceuuid(String deviceuuid) {
        this.deviceuuid = deviceuuid;
    }

    public String getRowneruuid() {
        return rowneruuid;
    }

    public void setRowneruuid(String rowneruuid) {
        this.rowneruuid = rowneruuid;
    }

    public List<String> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<String> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* dos */
        OCRepresentation obj = OCRepUtil.repGetObject(rep, OcfResourceAttributeKey.DEVICE_STATE_KEY);
        OcPstatDeviceState dos = new OcPstatDeviceState();
        dos.parseOCRepresentation(obj);
        this.setDeviceState(dos);
        /* isop */
        boolean isop = OCRepUtil.repGetBoolean(rep, OcfResourceAttributeKey.IS_OPERATIONAL_KEY);
        this.setOperational(Boolean.valueOf(isop));
        /* cm */
        int cm = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.CURRENT_MODE_KEY);
        this.setCurrentMode(Integer.valueOf(cm));
        /* tm */
        int tm = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.TARGET_MODE_KEY);
        this.setTargetMode(Integer.valueOf(tm));
        /* om */
        int om = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.OPERATIONAL_MODE_KEY);
        this.setOperationalMode(Integer.valueOf(om));
        /* sm */
        int sm = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.SUPPORT_MODE_KEY);
        this.setSupportedMode(Integer.valueOf(sm));
        /* deviceuuid */
        String deviceuuid = OCRepUtil.repGetString(rep, OcfResourceAttributeKey.DEVICE_UUID_KEY);
        this.setDeviceuuid(deviceuuid);
        /* rowneruuid*/
        String rowneruuid = OCRepUtil.repGetString(rep, OcfResourceAttributeKey.ROWNER_UUID_KEY);
        this.setRowneruuid(rowneruuid);
        /* rt */
        String[] resourceTypes = OCRepUtil.repGetStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(Arrays.asList(resourceTypes));
        /* if */
        String[] interfaces = OCRepUtil.repGetStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setInterfaces(Arrays.asList(interfaces));
    }

    public CborEncoder parseToCbor() {
        CborEncoder root = OCRepUtil.repBeginRootObject();

        /* dos */
        if (this.getDeviceState() != null) {
            CborEncoder dos = OCRepUtil.repOpenObject(root, OcfResourceAttributeKey.DEVICE_STATE_KEY);
            this.getDeviceState().parseToCbor(dos);
            OCRepUtil.repCloseObject(root, dos);
        }
        /* om */
        if (this.getOperationalMode() != null) {
            OCRepUtil.repSetInt(root, OcfResourceAttributeKey.OPERATIONAL_MODE_KEY, this.getOperationalMode());
        }
        /* deviceuuid */
        if (this.getDeviceuuid() != null && !this.getDeviceuuid().isEmpty()) {
            OCRepUtil.repSetTextString(root, OcfResourceAttributeKey.DEVICE_UUID_KEY, this.getDeviceuuid());
        }
        /* rowneruuid */
        if (this.getRowneruuid() != null && !this.getRowneruuid().isEmpty()) {
            OCRepUtil.repSetTextString(root, OcfResourceAttributeKey.ROWNER_UUID_KEY, this.getRowneruuid());
        }

        OCRepUtil.repEndRootObject();

        return root;
    }
}
