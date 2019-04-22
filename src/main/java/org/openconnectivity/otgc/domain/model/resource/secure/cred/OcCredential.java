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

import org.iotivity.*;
import org.openconnectivity.otgc.utils.constant.OcfCredUsage;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

public class OcCredential {

    private Integer credid;
    private String subjectuuid;
    private OcCredRole roleid;
    private OCCredType credtype;
    private OCCredUsage credusage;
    private OcCredPublicData publicData;
    private OcCredPrivateData privateData;
    private OcCredOptionalData optionalData;
    private String period;

    public OcCredential() {}

    public Integer getCredid() {
        return credid;
    }

    public void setCredid(Integer credid) {
        this.credid = credid;
    }

    public String getSubjectuuid() {
        return subjectuuid;
    }

    public void setSubjectuuid(String subjectuuid) {
        this.subjectuuid = subjectuuid;
    }

    public OcCredRole getRoleid() {
        return roleid;
    }

    public void setRoleid(OcCredRole roleid) {
        this.roleid = roleid;
    }

    public OCCredType getCredtype() {
        return credtype;
    }

    public void setCredtype(OCCredType credtype) {
        this.credtype = credtype;
    }

    public OCCredUsage getCredusage() {
        return credusage;
    }

    public void setCredusage(OCCredUsage credusage) {
        this.credusage = credusage;
    }

    public OcCredPublicData getPublicData() {
        return publicData;
    }

    public void setPublicData(OcCredPublicData publicData) {
        this.publicData = publicData;
    }

    public OcCredPrivateData getPrivateData() {
        return privateData;
    }

    public void setPrivateData(OcCredPrivateData privateData) {
        this.privateData = privateData;
    }

    public OcCredOptionalData getOptionalData() {
        return optionalData;
    }

    public void setOptionalData(OcCredOptionalData optionalData) {
        this.optionalData = optionalData;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* credid */
        Integer credid = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.CRED_ID_KEY);
        this.setCredid(credid);
        /* credtype */
        Integer credtype = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.CRED_TYPE_KEY);
        this.setCredtype(OCCredType.swigToEnum(credtype));
        /* credusage */
        Integer credusage = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.CRED_USAGE_KEY);
        this.setCredusage(credusage != null ? OCCredUsage.swigToEnum(credusage) : null);
        /* subjectuuid */
        String subjectuuid = OCRepUtil.repGetString(rep, OcfResourceAttributeKey.SUBJECTUUID_KEY);
        this.setSubjectuuid(subjectuuid);
        /* period */
        String period = OCRepUtil.repGetString(rep, OcfResourceAttributeKey.PERIOD_KEY);
        this.setPeriod(period);
        /* publicdata */
        OCRepresentation publicdataObj = OCRepUtil.repGetObject(rep, OcfResourceAttributeKey.PUBLIC_DATA_KEY);
        if (publicData != null) {
            OcCredPublicData publicData = new OcCredPublicData();
            publicData.parseOCRepresentation(publicdataObj);
            this.setPublicData(publicData);
        }
        /* optionaldata */
        OCRepresentation optionaldataObj = OCRepUtil.repGetObject(rep, OcfResourceAttributeKey.OPTIONAL_DATA_KEY);
        if (optionalData != null) {
            OcCredOptionalData optionalData = new OcCredOptionalData();
            optionalData.parseOCRepresentation(optionaldataObj);
            this.setOptionalData(optionalData);
        }
        /* roleid */
        OCRepresentation roleidObj = OCRepUtil.repGetObject(rep, OcfResourceAttributeKey.ROLE_ID_KEY);
        if (roleidObj != null) {
            OcCredRole roleid = new OcCredRole();
            roleid.parseOCRepresentation(roleidObj);
            this.setRoleid(roleid);
        }
    }

    public void parseToCbor(CborEncoder parent) {
        if (this.getCredid() != null) {
            OCRepUtil.repSetInt(parent, OcfResourceAttributeKey.CRED_ID_KEY, this.getCredid());
        }

        if (this.getSubjectuuid() != null && !this.getSubjectuuid().isEmpty()) {
            OCRepUtil.repSetTextString(parent, OcfResourceAttributeKey.SUBJECTUUID_KEY, this.getSubjectuuid());
        }

        if (this.getRoleid() != null) {
            CborEncoder roleId = OCRepUtil.repOpenObject(parent, OcfResourceAttributeKey.ROLE_ID_KEY);
            this.getRoleid().parseToCbor(roleId);
            OCRepUtil.repCloseObject(parent, roleId);
        }

        if (this.getCredtype() != null) {
            OCRepUtil.repSetInt(parent, OcfResourceAttributeKey.CRED_TYPE_KEY, this.getCredtype().swigValue());
        }

        if (this.getCredusage() != null) {
            OCRepUtil.repSetTextString(parent, OcfResourceAttributeKey.CRED_USAGE_KEY, OcfCredUsage.enumCredUsageToString(this.getCredusage()));
        }

        if (this.getPublicData() != null) {
            CborEncoder publicObj = OCRepUtil.repOpenObject(parent, OcfResourceAttributeKey.PUBLIC_DATA_KEY);
            this.getPublicData().parseToCbor(publicObj);
            OCRepUtil.repCloseObject(parent, publicObj);
        }

        if (this.getPrivateData() != null) {
            CborEncoder privateObj = OCRepUtil.repOpenObject(parent, OcfResourceAttributeKey.PRIVATE_DATA_KEY);
            this.getPrivateData().parseToCbor(privateObj);
            OCRepUtil.repCloseObject(parent, privateObj);
        }

        if (this.getOptionalData() != null) {
            // TODO
        }

        if (this.getPeriod() != null && !this.getPeriod().isEmpty()) {
            // TODO
        }
    }


}
