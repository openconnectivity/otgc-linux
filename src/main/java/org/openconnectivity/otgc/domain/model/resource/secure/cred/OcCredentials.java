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

package org.openconnectivity.otgc.domain.model.resource.secure.cred;

import org.iotivity.CborEncoder;
import org.iotivity.OCRepUtil;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcCredentials {

    private List<OcCredential> credList;
    private List<String> resourceTypes;
    private List<String> interfaces;
    private String rowneruuid;

    public OcCredentials() {
    }

    public List<OcCredential> getCredList() {
        return credList;
    }

    public void setCredList(List<OcCredential> credList) {
        this.credList = credList;
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

    public String getRowneruuid() {
        return rowneruuid;
    }

    public void setRowneruuid(String rowneruuid) {
        this.rowneruuid = rowneruuid;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* creds */
        OCRepresentation credsObj = OCRepUtil.repGetObjectArray(rep, OcfResourceAttributeKey.CREDENTIALS_KEY);
        List<OcCredential> credList = new ArrayList<>();
        while (credsObj != null) {
            OcCredential cred = new OcCredential();
            cred.parseOCRepresentation(credsObj.getValue().getObject());
            credList.add(cred);

            credsObj = credsObj.getNext();
        }
        this.setCredList(credList);
        /* rowneruuid */
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

        if (this.getCredList() != null && !this.getCredList().isEmpty()) {
            CborEncoder credsArray = OCRepUtil.repOpenArray(root, OcfResourceAttributeKey.CREDENTIALS_KEY);
            for (OcCredential cred : this.getCredList()) {
                CborEncoder credObj = OCRepUtil.repBeginObject(credsArray);
                cred.parseToCbor(credObj);
                OCRepUtil.repCloseObject(credsArray, credObj);
            }

            OCRepUtil.repCloseArray(root, credsArray);
        }

        if (this.getRowneruuid() != null && !this.getRowneruuid().isEmpty()) {
            OCRepUtil.repSetTextString(root, OcfResourceAttributeKey.ROWNER_UUID_KEY, this.getRowneruuid());
        }

        OCRepUtil.repEndRootObject();

        return root;
    }
}
