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

package org.openconnectivity.otgc.domain.usecase.link;

import io.reactivex.Completable;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.iotivity.OCDosType;
import org.openconnectivity.otgc.data.repository.*;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.utils.constant.OtgcConstant;

import javax.inject.Inject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public class LinkRoleForClientUseCase {
    private final IotivityRepository iotivityRepository;
    private final PstatRepository pstatRepository;
    private final CmsRepository cmsRepository;
    private final CertRepository certRepository;
    private final IORepository ioRepository;

    @Inject
    public LinkRoleForClientUseCase(IotivityRepository iotivityRepository,
                                    PstatRepository pstatRepository,
                                    CmsRepository cmsRepository,
                                    CertRepository certRepository,
                                    IORepository ioRepository)
    {
        this.iotivityRepository = iotivityRepository;
        this.pstatRepository = pstatRepository;
        this.cmsRepository = cmsRepository;
        this.certRepository = certRepository;
        this.ioRepository = ioRepository;
    }

    public Completable execute(Device device, String roleId, String roleAuthority) {
        return iotivityRepository.getSecureEndpoint(device)
                .flatMapCompletable(endpoint ->
                        pstatRepository.changeDeviceStatus(endpoint, OCDosType.OC_DOS_RFPRO)
                                .andThen(cmsRepository.retrieveCsr(endpoint))
                                .flatMapCompletable(csr -> {
                                    // Convert CSR
                                    PKCS10CertificationRequest certRequest = certRepository.getPKCS10CertRequest(csr).blockingGet();

                                    // Get Public Key from CSR
                                    SubjectPublicKeyInfo publicKeyInfo = certRequest.getSubjectPublicKeyInfo();
                                    PublicKey publicKey = certRepository.getPublicKeyFromBytes(publicKeyInfo.getPublicKeyData().getBytes()).blockingGet();
                                    // Get Private Key of Root CA
                                    PrivateKey caPrivateKey = ioRepository.getAssetAsPrivateKey(OtgcConstant.ROOT_PRIVATE_KEY).blockingGet();

                                    // Generate the certificate in PEM format
                                    X509Certificate cert = certRepository.generateRoleCertificate(device.getDeviceId(), publicKey, caPrivateKey, roleId, roleAuthority).blockingGet();
                                    String roleCert = certRepository.x509CertificateToPemString(cert).blockingGet();

                                    return cmsRepository.provisionRoleCertificate(endpoint, device.getDeviceId(), roleCert, roleId, roleAuthority);
                                })
                                .andThen(pstatRepository.changeDeviceStatus(endpoint, OCDosType.OC_DOS_RFNOP)));
    }
}
