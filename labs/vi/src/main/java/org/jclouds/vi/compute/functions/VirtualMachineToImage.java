/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vi.compute.functions;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineGuestOsIdentifier;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.VirtualMachine;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

import java.util.HashMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class VirtualMachineToImage implements Function<VirtualMachine, Image> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public Image apply(VirtualMachine from) {
      VirtualMachineConfigInfo vminfo = from.getConfig();
      VirtualMachineGuestOsIdentifier os = VirtualMachineGuestOsIdentifier.valueOf(vminfo.getGuestId());

      Image image = new ImageBuilder()
      .id(from.getName() + "")
      .name(vminfo.getGuestFullName())
      .description(vminfo.getAnnotation())
      .operatingSystem(
      new OperatingSystem.Builder()
         .is64Bit(os.name().contains("64"))
         .family(OsFamily.fromValue(os.name()))
         .arch(os.name())
         .version(vminfo.getVersion())
         .name(vminfo.getAlternateGuestName())
         .description(vminfo.getAnnotation())
         .build()
      )
      .location(null)
/*
         new LocationBuilder()
         .scope(LocationScope.REGION)
         .parent(regionToLocation.apply(regionLookup.apply(from.getProviderRegionId())))
         .id(from.getProviderOwnerId())
         .description(from.getDescription())
         .build()
*/

//      .userMetadata(vminfo.getDynamicProperty())
      .build();

      return image;

      }
}
