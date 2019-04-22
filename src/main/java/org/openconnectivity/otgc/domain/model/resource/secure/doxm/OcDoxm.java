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

package org.openconnectivity.otgc.domain.model.resource.secure.doxm;

import org.iotivity.CborEncoder;
import org.iotivity.OCOxmType;
import org.iotivity.OCRepUtil;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcDoxm {

    private List<OCOxmType> oxms;
    private OCOxmType oxmsel;
    private Integer supportedCredential;
    private Boolean owned;
    private String deviceuuid;
    private String devowneruuid;
    private String rowneruuid;
    private List<String> resourceTypes;
    private List<String> interfaces;

    public OcDoxm() {
    }

    public List<OCOxmType> getOxms() {
        return oxms;
    }

    public void setOxms(List<OCOxmType> oxms) {
        this.oxms = oxms;
    }

    public OCOxmType getOxmsel() {
        return oxmsel;
    }

    public void setOxmsel(OCOxmType oxmsel) {
        this.oxmsel = oxmsel;
    }

    public Integer getSupportedCredential() {
        return supportedCredential;
    }

    public void setSupportedCredential(Integer supportedCredential) {
        this.supportedCredential = supportedCredential;
    }

    public Boolean getOwned() {
        return owned;
    }

    public void setOwned(Boolean owned) {
        this.owned = owned;
    }

    public String getDeviceuuid() {
        return deviceuuid;
    }

    public void setDeviceuuid(String deviceuuid) {
        this.deviceuuid = deviceuuid;
    }

    public String getDevowneruuid() {
        return devowneruuid;
    }

    public void setDevowneruuid(String devowneruuid) {
        this.devowneruuid = devowneruuid;
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
        /* oxms */
        int[] oxmsValue = OCRepUtil.repGetIntArray(rep, OcfResourceAttributeKey.OXMS_KEY);
        List<OCOxmType> oxmTypes = new ArrayList<>();
        for (int value : oxmsValue) {
            oxmTypes.add(OCOxmType.swigToEnum(value));
        }
        this.setOxms(oxmTypes);
        /* oxmsel */
        int oxms = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.OXMSEL_KEY);
        this.setOxmsel(oxms == -1 ? oxmTypes.get(0): OCOxmType.swigToEnum(oxms));
        /* sct */
        int sct = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.SUPPORTED_CREDENTIAL_KEY);
        this.setSupportedCredential(Integer.valueOf(sct));
        /* owned */
        boolean owned = OCRepUtil.repGetBoolean(rep, OcfResourceAttributeKey.OWNED_KEY);
        this.setOwned(Boolean.valueOf(owned));
        /* deviceuuid */
        String deviceuuid = OCRepUtil.repGetString(rep, OcfResourceAttributeKey.DEVICE_UUID_KEY);
        this.setDeviceuuid(deviceuuid);
        /* devowneruuid */
        String devowneruuid = OCRepUtil.repGetString(rep, OcfResourceAttributeKey.DEVOWNER_UUID_KEY);
        this.setDevowneruuid(devowneruuid);
        /* rowneruuid */
        String rowneruuid = OCRepUtil.repGetString(rep, OcfResourceAttributeKey.ROWNER_UUID_KEY);
        this.setRowneruuid(rowneruuid);
        /* rt */
        String[] resourceTypes = OCRepUtil.repGetStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(Arrays.asList(resourceTypes));
        /* if */
        String[] interfaces = OCRepUtil.repGetStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setResourceTypes(Arrays.asList(interfaces));
    }

    public CborEncoder parseToCbor() {
        CborEncoder root = OCRepUtil.repBeginRootObject();

        /* oxms */
        if (this.getOxms() != null && !this.getOxms().isEmpty()) {
            int[] intArray = new int[this.getOxms().size()];
            int i = 0;
            for (OCOxmType value : this.getOxms()) {
                intArray[i] = value.swigValue();
                i++;
            }
            OCRepUtil.repSetIntArray(root, OcfResourceAttributeKey.OXMS_KEY, intArray);
        }

        /* oxmsel */
        if (this.getOxmsel() != null) {
            OCRepUtil.repSetInt(root, OcfResourceAttributeKey.OXMSEL_KEY, this.getOxmsel().swigValue());
        }
        /* sct */
        if (this.getSupportedCredential() != null) {
            OCRepUtil.repSetInt(root, OcfResourceAttributeKey.SUPPORTED_CREDENTIAL_KEY, this.getSupportedCredential());
        }
        /* owned */
        if (this.getOwned() != null) {
            OCRepUtil.repSetBoolean(root, OcfResourceAttributeKey.OWNED_KEY, this.getOwned());
        }
        /* deviceuuid */
        if (this.getDeviceuuid() != null && !this.getDeviceuuid().isEmpty()) {
            OCRepUtil.repSetTextString(root, OcfResourceAttributeKey.DEVICE_UUID_KEY, this.getDeviceuuid());
        }
        /* devowneruuid */
        if (this.getDevowneruuid() != null && !this.getDevowneruuid().isEmpty()) {
            OCRepUtil.repSetTextString(root, OcfResourceAttributeKey.DEVOWNER_UUID_KEY, this.getDevowneruuid());
        }
        /* rowneruuid */
        if (this.getRowneruuid() != null && !this.getRowneruuid().isEmpty()) {
            OCRepUtil.repSetTextString(root, OcfResourceAttributeKey.ROWNER_UUID_KEY, this.getRowneruuid());
        }
        /* rt */
        if (this.getResourceTypes() != null && !this.getResourceTypes().isEmpty()) {
            OCRepUtil.repSetStringArray(root, OcfResourceAttributeKey.RESOURCE_TYPES_KEY, this.getResourceTypes().toArray(new String[0]));
        }
        /* if */
        if (this.getInterfaces() != null && !this.getInterfaces().isEmpty()) {
            OCRepUtil.repSetStringArray(root, OcfResourceAttributeKey.INTERFACES_KEY, this.getInterfaces().toArray(new String[0]));
        }

        OCRepUtil.repEndRootObject();

        return root;
    }
}
