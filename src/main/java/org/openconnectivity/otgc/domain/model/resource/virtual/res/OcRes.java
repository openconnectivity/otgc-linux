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

package org.openconnectivity.otgc.domain.model.resource.virtual.res;

import org.iotivity.OCRepresentation;
import org.openconnectivity.otgc.domain.model.resource.OcResourceBase;

import java.util.ArrayList;
import java.util.List;

public class OcRes {

    private List<OcResource> resourceList;

    public OcRes() {

    }

    public List<OcResource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<OcResource> resourceList) {
        this.resourceList = resourceList;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        List<OcResource> resources = new ArrayList<>();

        while (rep != null) {
            OCRepresentation resourceObj = rep.getValue().getObject();
            OcResource resource = new OcResource();
            resource.parseOCRepresentation(resourceObj);
            resources.add(resource);

            rep = rep.getNext();
        }

        this.setResourceList(resources);
    }
}
