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

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.log4j.Logger;
import org.iotivity.*;
import org.openconnectivity.otgc.domain.model.resource.virtual.p.OcPlatformInfo;
import org.openconnectivity.otgc.domain.model.resource.virtual.res.OcEndpoint;
import org.openconnectivity.otgc.domain.model.resource.virtual.res.OcRes;
import org.openconnectivity.otgc.domain.model.resource.virtual.res.OcResource;
import org.openconnectivity.otgc.utils.constant.OcfResourceType;
import org.openconnectivity.otgc.data.entity.DeviceEntity;
import org.openconnectivity.otgc.data.persistence.DatabaseManager;
import org.openconnectivity.otgc.data.persistence.dao.DeviceDao;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.devicelist.DeviceType;
import org.openconnectivity.otgc.domain.model.resource.virtual.d.OcDeviceInfo;
import org.openconnectivity.otgc.utils.constant.OcfResourceUri;
import org.openconnectivity.otgc.utils.constant.OtgcConstant;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Singleton
public class IotivityRepository {

    private final Logger LOG = Logger.getLogger(ProvisionRepository.class);

    private static final List<String> RESOURCE_TYPES_TO_FILTER;
    static {
        RESOURCE_TYPES_TO_FILTER = new ArrayList<>();

        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.DOXM);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.PSTAT);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.ACL2);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.CRED);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.CRL);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.CSR);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.ROLES);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.SECURITY_PROFILES);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.DEVICE);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.PLATFORM);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.INTROSPECTION);
        RESOURCE_TYPES_TO_FILTER.add(OcfResourceType.DEVICE_CONF);
    }

    private List<Device> unownedDevices = new ArrayList<>();
    private List<Device> ownedDevices = new ArrayList<>();
    private List<Device> allDevices = new ArrayList<>();

    private final SettingRepository settingRepository;
    private final DeviceDao deviceDao;

    @Inject
    public IotivityRepository(SettingRepository settingRepository, DeviceDao deviceDao) {
        this.settingRepository = settingRepository;
        this.deviceDao = deviceDao;
    }

    public Completable initOICStack() {
        return Completable.create(emitter -> {
            LOG.debug("initOICStack");

            File directory = new File(OtgcConstant.OTGC_CREDS_DIR);
            if (! directory.exists()) {
                directory.mkdir();
            }

            LOG.debug("Storage Config PATH : " + directory.getPath());
            if (OCStorage.storageConfig(directory.getPath()) != 0) {
                LOG.error("Failed to setup Storage Config.");
            }

            OCMain.setIntrospectionFile(0 /* First device */, OtgcConstant.INTROSPECTION_CBOR_FILE);

            int ret = OCMain.mainInit(new OCMainInitHandler() {
                @Override
                public int initialize() {
                    LOG.debug("In OCMainInitHandler.initilize()");
                    int ret = OCMain.initPlatform("OCF");
                    ret |= OCMain.addDevice("/oic/d", "oic.d.phone", "OTGC", "ocf.1.0.0", "ocf.res.1.3.0");
                    return ret;
                }

                @Override
                public void registerResources() {
                    LOG.debug("In OCMainInitHandler.registerResources()");
                }

                @Override
                public void requestEntry() {
                    LOG.debug("In OCMainInitHandler.requestEntry()");
                    OCObt.init();

                    emitter.onComplete();
                }
            });
            if (ret < 0) {
                String error = "OCMain.mainInit has failed with result: " + ret;
                LOG.error(error);
                emitter.onError(new Exception(error));
            }
        });
    }

    public Single<String> getDeviceId() {
        return Single.create(emitter -> {
            OCSecurityDoxm doxm = OCDoxm.getOwnDoxm(0 /* First registered device */);
            emitter.onSuccess(OCUuidUtil.uuidToString(doxm.getDeviceuuid()));
        });
    }

    public Completable setFactoryResetHandler(OCSetFactoryPresetsHandler handler) {
        return Completable.create(emitter -> {
            OCMain.setFactoryPresetsCallback(handler);
            emitter.onComplete();
        });
    }

    public Observable<Device> scanUnownedDevices() {
        return Completable.create(emitter -> {
            LOG.debug("Discovering unowned devices");
            unownedDevices.clear();

            OCObtDiscoveryHandler handler = (uuid, endpoints) -> {
                String deviceId = OCUuidUtil.uuidToString(uuid);
                LOG.debug("Discovered unowned device: " + deviceId);

                DeviceEntity device = deviceDao.findById(deviceId);
                if (device == null) {
                    deviceDao.insert(new DeviceEntity(deviceId, "", new ArrayList<>()));
                } else {
                    deviceDao.insert(new DeviceEntity(device.getId(), device.getName(), new ArrayList<>()));
                }

                unownedDevices.add(new Device(DeviceType.UNOWNED, deviceId, new OcDeviceInfo(), endpoints));
            };

            int ret = OCObt.discoverUnownedDevices(handler);
            if (ret < 0) {
                String error = "ERROR discovering un-owned Devices.";
                LOG.error(error);
                emitter.onError(new Exception(error));
            }
        }).timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
        .onErrorComplete()
        .andThen(Observable.fromIterable(unownedDevices));
    }

    public Observable<Device> scanOwnedDevices() {
        return Completable.create(emitter -> {
            LOG.debug("Discovering owned devices");
            ownedDevices.clear();

            OCObtDiscoveryHandler handler = (uuid, endpoints) -> {
                String deviceId = OCUuidUtil.uuidToString(uuid);
                LOG.debug("Discovered owned device: "+ deviceId);

                DeviceEntity device = deviceDao.findById(deviceId);
                if (device == null) {
                    deviceDao.insert(new DeviceEntity(deviceId, "", new ArrayList<>()));
                } else {
                    deviceDao.insert(new DeviceEntity(device.getId(), device.getName(), new ArrayList<>()));
                }

                ownedDevices.add(new Device(DeviceType.OWNED_BY_SELF, deviceId, new OcDeviceInfo(), endpoints));
            };

            int ret = OCObt.discoverOwnedDevices(handler);
            if (ret < 0) {
                String error = "ERROR discovering owned Devices.";
                LOG.error(error);
                emitter.onError(new Exception(error));
            }
        }).timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
        .onErrorComplete()
        .andThen(Observable.fromIterable(ownedDevices));
    }

    public Completable scanHosts() {
        return Completable.create(emitter -> {
            // Clear all devices list for devices owned by other
            allDevices.clear();

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcRes res = new OcRes();
                    res.parseOCRepresentation(response.getPayload());

                    OcResource resource = res.getResourceList().get(0);
                    String deviceId = resource.getAnchor().replace("ocf://", "");
                    List<String> endpoints = new ArrayList<>();
                    for (OcEndpoint ep : resource.getEndpoints()) {
                        endpoints.add(ep.getEndpoint());
                    }

                    DeviceEntity device = deviceDao.findById(deviceId);
                    if (device == null) {
                        deviceDao.insert(new DeviceEntity(deviceId, "", endpoints));
                    } else {
                        deviceDao.insert(new DeviceEntity(deviceId, device.getName(), endpoints));
                    }
                    allDevices.add(new Device(DeviceType.OWNED_BY_OTHER, deviceId, new OcDeviceInfo(), endpoints));
                }
            };

            if (!OCMain.doIPMulticast(OcfResourceUri.RES_URI, null, handler)) {
                emitter.onError(new Exception("Error scanning hosts"));
            }

        }).timeout(getDiscoveryTimeout(), TimeUnit.SECONDS)
                .onErrorComplete();
    }

    public Observable<Device> scanOwnedByOtherDevices() {
        return scanHosts()
                .andThen(Observable.fromIterable(allDevices))
                .filter(device -> {
                    boolean isNotUnowned = true;
                    for (Device d : unownedDevices) {
                        if (d.getDeviceId().equals(device.getDeviceId())) {
                            isNotUnowned = false;
                        }
                    }

                    return isNotUnowned;
                })
                .filter(device -> {
                    boolean isNotOwned = true;
                    for (Device d : ownedDevices) {
                        if (d.getDeviceId().equals(device.getDeviceId())) {
                            isNotOwned = false;
                        }
                    }

                    return isNotOwned;
                })
                .filter(device -> !device.getDeviceId().equals(getDeviceId().blockingGet()));
    }

    public Single<String> getNonSecureEndpoint(Device device) {
        return Single.create(emitter -> {
            String endpoint = device.getIpv6Host();
            if (endpoint == null) {
                endpoint = device.getIpv4Host();
            }
            emitter.onSuccess(endpoint);
        });
    }

    public Single<String> getSecureEndpoint(Device device) {
        return Single.create(emitter -> {
            String endpoint = device.getIpv6SecureHost();
            if (endpoint == null) {
                endpoint = device.getIpv4SecureHost();
            }
            emitter.onSuccess(endpoint);
        });
    }

    public Single<String> getNonSecureEndpoint(List<String> endpoints) {
        return Single.create(emitter -> {
            String ep = null;

            for (String endpoint : endpoints) {
                if (endpoint.startsWith("coap")
                        && endpoint.contains(".")) {
                    ep = endpoint;
                } else if (endpoint.startsWith("coap")) {
                    ep =endpoint;
                    break;
                }
            }

            emitter.onSuccess(ep);
        });
    }

    public Single<String> getSecureEndpoint(List<String> endpoints) {
        return Single.create(emitter -> {
            String ep = null;

            for (String endpoint : endpoints) {
                if (endpoint.startsWith("coaps")
                        && endpoint.contains(".")) {
                    ep = endpoint;
                } else if (endpoint.startsWith("coaps")) {
                    ep =endpoint;
                    break;
                }
            }

            emitter.onSuccess(ep);
        });
    }

    public Single<OcDeviceInfo> getDeviceInfo(String endpoint) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(endpoint, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcDeviceInfo deviceInfo = new OcDeviceInfo();
                    deviceInfo.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(deviceInfo);
                } else {
                    emitter.onError(new Exception("Get device info error - code: " + code));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.DEVICE_INFO_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Get device info error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Single<OcPlatformInfo> getPlatformInfo(String endpoint) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(endpoint, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcPlatformInfo platformInfo = new OcPlatformInfo();
                    platformInfo.setOCRepresentation(response.getPayload());
                    emitter.onSuccess(platformInfo);
                } else {
                    emitter.onError(new Exception("Get device platform error - code: " + code));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.PLATFORM_INFO_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Get device platform error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Single<String> getDeviceName(String deviceId) {
        return Single.create(emitter -> {
            DeviceEntity device = deviceDao.findById(deviceId);
            emitter.onSuccess(device.getName());
        });
    }

    public Completable setDeviceName(String deviceId, String deviceName) {
        return Completable.fromAction(() -> {
            deviceDao.updateDeviceName(deviceId, deviceName);
        });
    }

    public int getDiscoveryTimeout() {
        return Integer.parseInt(settingRepository.get(SettingRepository.DISCOVERY_TIMEOUT_KEY,
                                                        SettingRepository.DISCOVERY_TIMEOUT_DEFAULT_VALUE));
    }

    public Single<List<OcResource>> findVerticalResources(String host) {
        return findResources(host)
                .map(ocRes -> {
                    List<OcResource> resourceList = new ArrayList<>();
                    for (OcResource resource : ocRes.getResourceList()) {
                        for (String resourceType : resource.getResourceTypes()) {
                            if (!RESOURCE_TYPES_TO_FILTER.contains(resourceType)
                                    && !resourceType.startsWith("oic.d.")) {
                                resourceList.add(resource);
                                break;
                            }
                        }
                    }

                    return resourceList;
                });
    }

    public Single<OcRes> findResources(String host) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(host, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcRes ocRes = new OcRes();
                    ocRes.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(ocRes);
                } else {
                    emitter.onError(new Exception("Find resources error - code: " + code));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.RES_URI, ep, null, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Find resources error"));
            }

            try {
                Thread.sleep(getDiscoveryTimeout() * 1_000L);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOG.error(ex.getMessage());
            }
        });
    }

    public Single<OcRes> findResource(String host, String resourceType) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(host, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK) {
                    OcRes res = new OcRes();
                    res.parseOCRepresentation(response.getPayload());
                    emitter.onSuccess(res);
                } else {
                    emitter.onError(new Exception("Find resource error - code: " + code));
                }
            };

            if (!OCMain.doGet(OcfResourceUri.RES_URI, ep, OcfResourceUri.RESOURCE_TYPE_FILTER + resourceType, handler, OCQos.HIGH_QOS)) {
                emitter.onError(new Exception("Find resource error"));
            }

            try {
                Thread.sleep(getDiscoveryTimeout() * 1_000L);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOG.error(ex.getMessage());
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    public Single<OCRepresentation> get(String host, String uri) {
        return Single.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(host, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code.equals(OCStatus.OC_STATUS_OK)) {
                    emitter.onSuccess(response.getPayload());
                } else {
                    emitter.onError(new Exception("GET request error - code: " + code));
                }
            };

            if (!OCMain.doGet(uri, ep, null, handler, OCQos.LOW_QOS)) {
                emitter.onError(new Exception("Error in GET request"));
            }
        });
    }

    public Completable post(String host, String uri, OCRepresentation rep, Object valueArray) {
        return Completable.create(emitter -> {
            OCEndpoint ep = OCEndpointUtil.newEndpoint();
            OCEndpointUtil.stringToEndpoint(host, ep, new String[1]);

            OCResponseHandler handler = (OCClientResponse response) -> {
                OCStatus code = response.getCode();
                if (code == OCStatus.OC_STATUS_OK
                        || code == OCStatus.OC_STATUS_CHANGED) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception("POST " + uri + " error - code: " + code));
                }
            };

            if (OCMain.initPost(uri, ep, null, handler, OCQos.HIGH_QOS)) {
                CborEncoder root = OCRepUtil.repBeginRootObject();
                parseOCRepresentionToCbor(root, rep, valueArray);
                OCRepUtil.repEndRootObject();

                if (!OCMain.doPost()) {
                    emitter.onError(new Exception("Do POST " + uri + " error"));
                }
            } else {
                emitter.onError(new Exception("Init POST " + uri + " error"));
            }

            OCEndpointUtil.freeEndpoint(ep);
        });
    }

    private void parseOCRepresentionToCbor(CborEncoder parent, OCRepresentation rep, Object valueArray) {
        while (rep != null) {
            switch (rep.getType()) {
                case OC_REP_BOOL:
                    OCRepUtil.repSetBoolean(parent, rep.getName(), rep.getValue().getBool());
                    break;
                case OC_REP_INT:
                    OCRepUtil.repSetInt(parent, rep.getName(), (int)rep.getValue().getInteger());
                    break;
                case OC_REP_DOUBLE:
                    OCRepUtil.repSetDouble(parent, rep.getName(), rep.getValue().getDouble());
                    break;
                case OC_REP_STRING:
                    OCRepUtil.repSetTextString(parent, rep.getName(), rep.getValue().getString());
                    break;
                case OC_REP_INT_ARRAY:
                    OCRepUtil.repSetIntArray(parent, rep.getName(), (int[])valueArray);
                    break;
                case OC_REP_DOUBLE_ARRAY:
                    OCRepUtil.repSetDoubleArray(parent, rep.getName(), (double[])valueArray);
                    break;
                case OC_REP_STRING_ARRAY:
                    OCRepUtil.repSetStringArray(parent, rep.getName(), (String[])valueArray);
                    break;
                default:
                    break;
            }

            rep = rep.getNext();
        }
    }

    public void close() {
        LOG.debug("Calling OCMain.mainShutdown()");
        OCMain.mainShutdown();

        DatabaseManager.closeEntityManager();
        DatabaseManager.closeEntityManagerFactory();
    }
}
