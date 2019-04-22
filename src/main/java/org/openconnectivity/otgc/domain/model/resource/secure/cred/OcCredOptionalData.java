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

import org.iotivity.OCEncoding;
import org.iotivity.OCRepresentation;

public class OcCredOptionalData {

    private static String DATA_KEY = "optdata";
    private static String ENCODING_KEY = "encoding";
    private static String REVSTAT_KEY = "revstat";

    private String optdata;
    private OCEncoding encoding;
    private boolean revstat;

    public OcCredOptionalData() {

    }

    public String getOptdata() {
        return optdata;
    }

    public void setOptdata(String data) {
        this.optdata = data;
    }

    public OCEncoding getEncoding() {
        return encoding;
    }

    public void setEncoding(OCEncoding encoding) {
        this.encoding = encoding;
    }

    public boolean isRevstat() {
        return revstat;
    }

    public void setRevstat(boolean revstat) {
        this.revstat = revstat;
    }

    public static OcCredOptionalData parseOCRepresentation(OCRepresentation rep) {
        OcCredOptionalData optionalData = new OcCredOptionalData();

        while (rep != null) {
            switch (rep.getType()) {
                case OC_REP_STRING:
                    if (rep.getName().equals(DATA_KEY)) {
                        optionalData.setOptdata(rep.getValue().getString());
                    }
                    break;
                case OC_REP_BOOL:
                    if (rep.getName().equals(REVSTAT_KEY)) {
                        optionalData.setRevstat(rep.getValue().getBool());
                    }
                    break;
                case OC_REP_INT:
                    if (rep.getName().equals(ENCODING_KEY)) {
                        optionalData.setEncoding(OCEncoding.swigToEnum((int)rep.getValue().getInteger()));
                    }
                    break;
                default:
                    break;
            }

            rep = rep.getNext();
        }

        return optionalData;
    }
}
