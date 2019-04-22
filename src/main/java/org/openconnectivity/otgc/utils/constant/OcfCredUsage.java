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

package org.openconnectivity.otgc.utils.constant;

import org.iotivity.OCCredUsage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class OcfCredUsage {

    private OcfCredUsage() {
        throw new NotImplementedException();
    }

    // OcCredential usage
    private static final String OC_CREDUSAGE_PREFIX = "oic.sec.cred.";
    public static final String OC_CREDUSAGE_TRUSTCA = OC_CREDUSAGE_PREFIX + "trustca";
    public static final String OC_CREDUSAGE_CERT = OC_CREDUSAGE_PREFIX + "cert";
    public static final String OC_CREDUSAGE_ROLECERT = OC_CREDUSAGE_PREFIX + "rolecert";
    public static final String OC_CREDUSAGE_MFGTRUSTCA = OC_CREDUSAGE_PREFIX + "mfgtrustca";
    public static final String OC_CREDUSAGE_MFGCERT = OC_CREDUSAGE_PREFIX + "mfgcert";
    public static final String OC_CREDUSAGE_UNSUPPORTED = "";

    public static String enumCredUsageToString(OCCredUsage credusage)
    {
        if (credusage == OCCredUsage.OC_CREDUSAGE_TRUSTCA) {
            return OC_CREDUSAGE_TRUSTCA;
        } else if (credusage == OCCredUsage.OC_CREDUSAGE_IDENTITY_CERT) {
            return OC_CREDUSAGE_CERT;
        } else if (credusage == OCCredUsage.OC_CREDUSAGE_ROLE_CERT) {
            return OC_CREDUSAGE_ROLECERT;
        } else if (credusage == OCCredUsage.OC_CREDUSAGE_MFG_TRUSTCA) {
            return OC_CREDUSAGE_MFGTRUSTCA;
        } else if (credusage == OCCredUsage.OC_CREDUSAGE_MFG_CERT) {
            return OC_CREDUSAGE_MFGCERT;
        } else {
            return OC_CREDUSAGE_UNSUPPORTED;
        }
    }

    public static OCCredUsage stringToEnumCredUsage(String credusage) {
        if (credusage.equals(OC_CREDUSAGE_TRUSTCA)){
            return OCCredUsage.OC_CREDUSAGE_TRUSTCA;
        } else if (credusage.equals(OC_CREDUSAGE_CERT)) {
            return OCCredUsage.OC_CREDUSAGE_IDENTITY_CERT;
        } else if (credusage.equals(OC_CREDUSAGE_ROLECERT)) {
            return OCCredUsage.OC_CREDUSAGE_ROLE_CERT;
        } else if (credusage.equals(OC_CREDUSAGE_MFGTRUSTCA)) {
            return OCCredUsage.OC_CREDUSAGE_MFG_TRUSTCA;
        } else if (credusage.equals(OC_CREDUSAGE_MFGCERT)) {
            return OCCredUsage.OC_CREDUSAGE_MFG_CERT;
        } else {
            return OCCredUsage.OC_CREDUSAGE_NULL;
        }
    }
}
