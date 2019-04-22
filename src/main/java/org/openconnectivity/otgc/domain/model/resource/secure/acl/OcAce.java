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
import java.util.List;

public class OcAce {

    private Integer aceid;
    private Integer permission;
    private OcAceSubject subject;
    private List<OcAceResource> resources;

    public OcAce() {
        this.resources = new ArrayList<>();
    }

    public Integer getAceid() {
        return aceid;
    }

    public void setAceid(Integer aceid) {
        this.aceid = aceid;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public OcAceSubject getSubject() {
        return subject;
    }

    public void setSubject(OcAceSubject subject) {
        this.subject = subject;
    }

    public List<OcAceResource> getResources() {
        return resources;
    }

    public void setResources(List<OcAceResource> resources) {
        this.resources = resources;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* aceid */
        int aceid = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.ACE_ID_KEY);
        this.setAceid(aceid);
        /* permission */
        int permission = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.PERMISSION_KEY);
        this.setPermission(permission);
        /* subject */
        OCRepresentation subjectObj = OCRepUtil.repGetObject(rep, OcfResourceAttributeKey.SUBJECT_KEY);
        OcAceSubject subject = new OcAceSubject();
        subject.parseOCRepresentation(subjectObj);
        this.setSubject(subject);
        /* resources */
        OCRepresentation resourcesObj = OCRepUtil.repGetObjectArray(rep, OcfResourceAttributeKey.RESOURCES_KEY);
        List<OcAceResource> resources = new ArrayList<>();
        while (resourcesObj != null) {
            OcAceResource resource = new OcAceResource();
            resource.parseOCRepresentation(resourcesObj.getValue().getObject());
            resources.add(resource);
            resourcesObj = resourcesObj.getNext();
        }
        this.setResources(resources);
    }

    public void parseToCbor(CborEncoder aclist2) {
        CborEncoder aceObj = OCRepUtil.repBeginObject(aclist2);

        /* aceid */
        if (this.getAceid() !=  null) {
            OCRepUtil.repSetInt(aceObj, OcfResourceAttributeKey.ACE_ID_KEY, this.getAceid());
        }
        /* subject */
        if (this.getSubject() != null) {
            CborEncoder subjectObj = OCRepUtil.repOpenObject(aceObj, OcfResourceAttributeKey.SUBJECT_KEY);
            this.getSubject().parseToCbor(subjectObj);
            OCRepUtil.repCloseObject(aceObj, subjectObj);
        }
        /* resources */
        if (this.getResources() != null) {
            CborEncoder resArray = OCRepUtil.repOpenArray(aceObj, OcfResourceAttributeKey.RESOURCES_KEY);
            for (OcAceResource res : this.getResources()) {
                res.parseToCbor(resArray);
            }
            OCRepUtil.repCloseArray(aceObj, resArray);
        }
        /* permission */
        if (this.getPermission() != 0) {
            OCRepUtil.repSetInt(aceObj, OcfResourceAttributeKey.PERMISSION_KEY, this.getPermission());
        }

        OCRepUtil.repCloseObject(aclist2, aceObj);
    }
}
