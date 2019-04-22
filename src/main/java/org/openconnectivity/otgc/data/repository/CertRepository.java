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

package org.openconnectivity.otgc.data.repository;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.openconnectivity.otgc.utils.constant.OcfEncoding;
import org.openconnectivity.otgc.domain.model.resource.secure.csr.OcCsr;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Date;

public class CertRepository {

    @Inject
    public CertRepository() {

    }

    private Single<X509Certificate> generateCertificate(String deviceUuid, PublicKey publicKey, PrivateKey caPrivateKey, String roleId, String roleAuthority) {
        return Single.create(emitter -> {
            Security.addProvider(new BouncyCastleProvider());

            BigInteger serialNumber = new BigInteger(160, new SecureRandom());
            Date startDate = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24);
            Date expiryDate = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10));
            X500Name issuer = new X500Name("C=US, O=Open Connectivity Foundation, CN=Root CA");
            X500Name subject = new X500Name("CN=uuid:" + deviceUuid);

            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(issuer, serialNumber, startDate, expiryDate, subject, publicKey);

            if (roleId != null && !roleId.isEmpty()) {
                String dirName = "CN=" + roleId;
                dirName += (roleAuthority != null) ? ",OU=" + roleAuthority : "";
                DERSequence subjectAltName = new DERSequence(new ASN1Encodable[] {
                        new GeneralName(GeneralName.directoryName, dirName)
                });
                certBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAltName);
            }

            certBuilder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
            certBuilder.addExtension(Extension.keyUsage, true,
                    new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment |
                            KeyUsage.dataEncipherment | KeyUsage.keyAgreement));

            ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(KeyPurposeId.getInstance(new ASN1ObjectIdentifier("1.3.6.1.4.1.44924.1.6")));
            certBuilder.addExtension(Extension.extendedKeyUsage, false, extendedKeyUsage);

            ContentSigner signer = new JcaContentSignerBuilder("SHA256withECDSA")
                    .setProvider(new BouncyCastleProvider()).build(caPrivateKey);
            byte[] certBytes = certBuilder.build(signer).getEncoded();
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));

            emitter.onSuccess(cert);
        });
    }

    public Single<X509Certificate> generateIdentityCertificate(String deviceUuid, PublicKey publicKey, PrivateKey caPrivateKey) {
        return generateCertificate(deviceUuid, publicKey, caPrivateKey, null, null);
    }

    public Single<X509Certificate> generateRoleCertificate(String deviceUuid, PublicKey publicKey, PrivateKey caPrivateKey, @NonNull String roleId, String roleAuthority) {
        return generateCertificate(deviceUuid, publicKey, caPrivateKey, roleId, roleAuthority);
    }

    public Single<String> x509CertificateToPemString(X509Certificate cert) {
        return Single.create(emitter -> {
            Base64 encoder = new Base64();
            String cert_begin = "-----BEGIN CERTIFICATE-----\n";
            String end_cert = "\n-----END CERTIFICATE-----\n\\u0000";

            byte[] derCert = cert.getEncoded();
            String pemCertPre = new String(encoder.encode(derCert));
            String pemCert = cert_begin + pemCertPre + end_cert;

            emitter.onSuccess(pemCert);
        });
    }

    public Single<PKCS10CertificationRequest> getPKCS10CertRequest(OcCsr csr) {
        return Single.create(emitter -> {
            if (csr.getEncoding().equals(OcfEncoding.OC_ENCODING_DER)) {
                emitter.onSuccess(new PKCS10CertificationRequest(csr.getDerCsr()));
            } else if (csr.getEncoding().equals(OcfEncoding.OC_ENCODING_PEM)) {
                Reader csrReader = new StringReader(csr.getPemCsr());
                PEMParser pemParser = new PEMParser(csrReader);
                Object pemObj = pemParser.readObject();
                if (pemObj instanceof PKCS10CertificationRequest) {
                    emitter.onSuccess((PKCS10CertificationRequest) pemObj);
                } else {
                    emitter.onError(new Exception("Convert CSR to PKCS10CertificationRequest error"));
                }
            } else {
                emitter.onError(new NullPointerException());
            }
        });
    }

    public Single<PublicKey> getPublicKeyFromBytes(byte[] pubKey) {
        return Single.create(emitter -> {
            ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("prime256v1");
            KeyFactory kf = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
            ECNamedCurveSpec params = new ECNamedCurveSpec("prime256v1", spec.getCurve(), spec.getG(), spec.getN());
            ECPoint point =  ECPointUtil.decodePoint(params.getCurve(), pubKey);
            ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, params);
            ECPublicKey pk = (ECPublicKey) kf.generatePublic(pubKeySpec);

            emitter.onSuccess(pk);
        });
    }

    public Single<String> privateKeyToPemString(ECPrivateKey privateKey) {
        return Single.create(emitter -> {
            Base64 encoder = new Base64();
            String cert_begin = "-----BEGIN EC PRIVATE KEY-----\n";
            String end_cert = "-----END EC PRIVATE KEY-----";

            byte[] derPrivateKey = privateKey.getEncoded();
            String pemPrivateKeyPre = new String(encoder.encode(derPrivateKey));
            String pemPrivateKey = cert_begin + pemPrivateKeyPre + end_cert;

            emitter.onSuccess(pemPrivateKey);
        });
    }
}
