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
import org.iotivity.OCEncoding;
import org.iotivity.OCRepUtil;
import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.utils.constant.OcfEncoding;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;

public class OcCredPrivateData {

    private byte[] dataDer;
    private String dataPem;
    private OCEncoding encoding;

    public OcCredPrivateData() {

    }

    public byte[] getDataDer() {
        return dataDer;
    }

    public void setDataDer(byte[] dataDer) {
        this.dataDer = dataDer;
    }

    public String getDataPem() {
        return dataPem;
    }

    public void setDataPem(String dataPem) {
        this.dataPem = dataPem;
    }

    public OCEncoding getEncoding() {
        return encoding;
    }

    public void setEncoding(OCEncoding encoding) {
        this.encoding = encoding;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* data DER format */
        byte[] dataDer = OCRepUtil.repGetByteString(rep, OcfResourceAttributeKey.DATA_KEY);
        this.setDataDer(dataDer);
        /* data PEM format */
        String dataPem = OCRepUtil.repGetString(rep, OcfResourceAttributeKey.DATA_KEY);
        this.setDataPem(dataPem);
        /* encoding */
        int encoding = OCRepUtil.repGetInt(rep, OcfResourceAttributeKey.ENCODING_KEY);
        this.setEncoding(OCEncoding.swigToEnum(encoding));
    }

    public void parseToCbor(CborEncoder parent) {
        if (this.getEncoding() != null) {
            OCRepUtil.repSetTextString(parent, OcfResourceAttributeKey.ENCODING_KEY, OcfEncoding.enumEncodingToString(this.getEncoding()));
        }

        if (this.getDataPem() != null) {
            OCRepUtil.repSetTextString(parent, OcfResourceAttributeKey.DATA_KEY, this.getDataPem());
        }

        if (this.getDataDer() != null) {
            OCRepUtil.repSetByteString(parent, OcfResourceAttributeKey.DATA_KEY, this.getDataDer());
        }
    }
}
