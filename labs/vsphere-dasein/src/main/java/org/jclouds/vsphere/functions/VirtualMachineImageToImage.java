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

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.dc.Region;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class VirtualMachineImageToImage implements Function<MachineImage, Image> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private Function<Region, Location> regionToLocation;
   private Function<String, Region> regionLookup;

   @Inject
   public VirtualMachineImageToImage(Function<Region, Location> regionToLocation, Function<String, Region> regionLookup) {
      this.regionToLocation = regionToLocation;
      this.regionLookup = regionLookup;
   }


   @Override
   public Image apply(MachineImage from) {

      Image image = new ImageBuilder()
         .ids(from.getProviderMachineImageId() + "")
         .name(from.getName())
         .description(from.getDescription())
         .location(null
/*
         new LocationBuilder()
         .scope(LocationScope.REGION)
         .parent(regionToLocation.apply(regionLookup.apply(from.getProviderRegionId())))
         .id(from.getProviderOwnerId())
         .description(from.getDescription())
         .build()
*/
         )
         .operatingSystem(
         new OperatingSystem.Builder()
         .is64Bit(from.getArchitecture().equals(Architecture.I64))
         .family(OsFamily.fromValue(from.getPlatform().toString()))
         .arch(from.getArchitecture().toString())
         .version("unknown")
         .name(from.getSoftware())
         .description(from.getDescription())
         .build()
         )
         .build();

      return image;
   }

}
