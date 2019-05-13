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

package org.openconnectivity.otgc.domain.usecase;

import io.reactivex.Single;
import org.openconnectivity.otgc.data.repository.IotivityRepository;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.data.repository.DoxsRepository;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class OffboardDeviceUseCase {

    private final IotivityRepository iotivityRepository;
    private final DoxsRepository doxsRepository;

    @Inject
    public OffboardDeviceUseCase(IotivityRepository iotivityRepository,
                                 DoxsRepository doxsRepository) {
        this.iotivityRepository = iotivityRepository;
        this.doxsRepository = doxsRepository;
    }

    public Single<Device> execute(Device deviceToOffboard) {
        final Single<Device> getUpdatedOcSecureResource =
                iotivityRepository.scanUnownedDevices()
                    .filter(device -> (device.getDeviceId().equals(deviceToOffboard.getDeviceId())
                                                    || device.equalsHosts(deviceToOffboard)))
                    .singleOrError();

        return doxsRepository.resetDevice(deviceToOffboard.getDeviceId())
                .delay(1, TimeUnit.SECONDS)
                .andThen(getUpdatedOcSecureResource)
                .onErrorResumeNext(error -> getUpdatedOcSecureResource
                        .retry(2)
                        .onErrorResumeNext(Single.error(error)));
    }
}
