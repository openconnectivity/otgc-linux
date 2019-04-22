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

import org.iotivity.OCEncoding;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class OcfEncoding {

    private OcfEncoding() {
        throw new NotImplementedException();
    }

    // Encoding
    private static String OC_ENCODING_PREFIX = "oic.sec.encoding.";
    public static final String OC_ENCODING_BASE64 = OC_ENCODING_PREFIX + "base64";
    public static final String OC_ENCODING_RAW = OC_ENCODING_PREFIX + "raw";
    public static final String OC_ENCODING_PEM = OC_ENCODING_PREFIX + "pem";
    public static final String OC_ENCODING_DER = OC_ENCODING_PREFIX + "der";
    public static final String OC_ENCODING_UNSUPPORTED = "";

    public static String enumEncodingToString(OCEncoding encoding)
    {
        if (encoding == OCEncoding.OC_ENCODING_BASE64) {
            return OC_ENCODING_BASE64;
        } else if (encoding == OCEncoding.OC_ENCODING_RAW) {
            return OC_ENCODING_RAW;
        } else if (encoding == OCEncoding.OC_ENCODING_PEM) {
            return OC_ENCODING_PEM;
        } else if (encoding == OCEncoding.OC_ENCODING_DER) {
            return OC_ENCODING_DER;
        } else {
            return OC_ENCODING_UNSUPPORTED;
        }
    }

    public static OCEncoding stringToEnumEncoding(String encoding) {
        if (encoding.equals(OC_ENCODING_BASE64)) {
            return OCEncoding.OC_ENCODING_BASE64;
        } else if (encoding.equals(OC_ENCODING_RAW)) {
            return OCEncoding.OC_ENCODING_RAW;
        } else if (encoding.equals(OC_ENCODING_PEM)) {
            return OCEncoding.OC_ENCODING_PEM;
        } else if (encoding.equals(OC_ENCODING_DER)) {
            return OCEncoding.OC_ENCODING_DER;
        } else {
            return OCEncoding.OC_ENCODING_UNSUPPORTED;
        }
    }
}
