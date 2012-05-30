/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vsphere.functions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.dc.DataCenter;
import org.dasein.cloud.dc.Region;
import org.dasein.cloud.vsphere.PrivateCloud;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Adrian Cole
 */
@Singleton
public class DataCenterToLocation implements Function<DataCenter, Location> {
   private final Supplier<PrivateCloud> client;
   private final Function<Region, Location> regionToLocation;

   @Inject
   public DataCenterToLocation(Supplier<PrivateCloud> client, Function<Region, Location> regionToLocation) {
      this.client = client;
      this.regionToLocation = regionToLocation;
   }

   @Override
   public Location apply(DataCenter from) {
      Region parentRegion = null;
      try {
         parentRegion = client.get().getDataCenterServices().getRegion(from.getRegionId());
      } catch (InternalException e) {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      } catch (CloudException e) {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }

      return new LocationBuilder()
         .scope(LocationScope.ZONE)
         .id(from.getProviderDataCenterId() + "")
         .description(from.getName())
         .parent(regionToLocation.apply(parentRegion))
         .build();
   }

}
