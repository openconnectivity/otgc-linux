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

package org.openconnectivity.otgc.domain.model.resource.secure.acl;

import org.iotivity.CborEncoder;
import org.iotivity.OCRepUtil;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcAcl {

    private List<OcAce> aceList;
    private String rownerUuid;
    private List<String> resourceTypes;
    private List<String> interfaces;

    public OcAcl() {
        this.aceList = new ArrayList<>();
        this.resourceTypes = new ArrayList<>();
        this.interfaces = new ArrayList<>();
    }

    public List<OcAce> getAceList() {
        return this.aceList;
    }

    public void setAceList(List<OcAce> aceList) {
        this.aceList = aceList;
    }

    public String getRownerUuid() {
        return this.rownerUuid;
    }

    public void setRownerUuid(String rownerUuid) {
        this.rownerUuid = rownerUuid;
    }

    public List<String> getResourceTypes() {
        return this.resourceTypes;
    }

    public void setResourceTypes(List<String> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    public List<String> getInterfaces() {
        return this.interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* aclist2 */
        OCRepresentation aclist2Obj = OCRepUtil.repGetObjectArray(rep, OcfResourceAttributeKey.ACE_LIST_KEY);
        List<OcAce> aceList = new ArrayList<>();
        while (aclist2Obj != null) {
            OcAce ace = new OcAce();
            ace.parseOCRepresentation(aclist2Obj.getValue().getObject());
            aceList.add(ace);
            aclist2Obj = aclist2Obj.getNext();
        }
        this.setAceList(aceList);
        /* rowneruuid */
        String rowneruuid = OCRepUtil.repGetString(rep, OcfResourceAttributeKey.ROWNER_UUID_KEY);
        this.setRownerUuid(rowneruuid);
        /* rt */
        String[] resourceTypes = OCRepUtil.repGetStringArray(rep, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
        this.setResourceTypes(Arrays.asList(resourceTypes));
        /* if */
        String[] interfaces = OCRepUtil.repGetStringArray(rep, OcfResourceAttributeKey.INTERFACES_KEY);
        this.setInterfaces(Arrays.asList(interfaces));
    }

    public CborEncoder parseToCbor() {
        CborEncoder root = OCRepUtil.repBeginRootObject();

        /* aclist2 */
        if (this.getAceList() != null && !this.getAceList().isEmpty()) {
            CborEncoder aclist2 = OCRepUtil.repOpenArray(root, OcfResourceAttributeKey.ACE_LIST_KEY);
            for (OcAce ace : this.getAceList()) {
                ace.parseToCbor(aclist2);
            }
            OCRepUtil.repCloseArray(root, aclist2);
        }

        if (this.getRownerUuid() != null && !this.getRownerUuid().isEmpty()) {
            OCRepUtil.repSetTextString(root, OcfResourceAttributeKey.ROWNER_UUID_KEY, this.getRownerUuid());
        }

        if (this.getResourceTypes() != null && !this.getResourceTypes().isEmpty()) {
            CborEncoder resourceType = OCRepUtil.repOpenArray(root, OcfResourceAttributeKey.RESOURCE_TYPES_KEY);
            for (String rtStr : this.getResourceTypes()) {
                OCRepUtil.repAddTextString(resourceType, rtStr);
            }
            OCRepUtil.repCloseArray(root, resourceType);
        }

        if (this.getInterfaces() != null && !this.getInterfaces().isEmpty()) {
            CborEncoder interfaces = OCRepUtil.repOpenArray(root, OcfResourceAttributeKey.INTERFACES_KEY);
            for (String ifStr : this.getInterfaces()) {
                OCRepUtil.repAddTextString(interfaces, ifStr);
            }
            OCRepUtil.repCloseArray(root, interfaces);
        }

        OCRepUtil.repEndRootObject();

        return root;
    }

}
