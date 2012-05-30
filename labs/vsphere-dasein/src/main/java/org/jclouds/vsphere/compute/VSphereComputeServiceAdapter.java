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
package org.jclouds.vsphere.compute;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.dasein.cloud.dc.DataCenter;
import org.dasein.cloud.dc.Region;
import org.dasein.cloud.vsphere.PrivateCloud;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

@Singleton
public class VSphereComputeServiceAdapter implements ComputeServiceAdapter<VirtualMachine, Hardware, MachineImage, Location> {
   private final Supplier<PrivateCloud> client;
   private final Function<DataCenter, Location> dataCenterToLocation;
   private final Function<Region, Location> regionToLocation;

//   private final PrivateCloud client;

   @Inject
   public VSphereComputeServiceAdapter(Supplier<PrivateCloud> client, Function<DataCenter, Location> dataCenterToLocation, Function<Region, Location> regionToLocation) {
      this.dataCenterToLocation = dataCenterToLocation;
      this.regionToLocation = regionToLocation;
      this.client = checkNotNull(client, "client");
   }


   @Override
   public NodeAndInitialCredentials<VirtualMachine>  createNodeWithGroupEncodedIntoName(String tag, String name, final Template template) {
      VirtualMachineProduct product = new VirtualMachineProduct();
      product.setProductId(template.getHardware().getProcessors().size() + ":" +
            template.getHardware().getRam());
      product.setCpuCount(template.getHardware().getProcessors().size());
      product.setRamInMb(template.getHardware().getRam());
      product.setName(template.getHardware().getName());

      MachineImage image = Iterables.find(listImages(),  new Predicate<MachineImage>() {
                     @Override
                     public boolean apply(MachineImage image) {
                        return image.getName().equals(template.getImage().getName());
                     }
      });


      VirtualMachine from = null;
      try {
         from = client.get().getComputeServices().getVirtualMachineSupport()
            .launch(image.getProviderMachineImageId(),
               product,
               null,
               name,
               template.getImage().getDescription(),
               null,
               null,
               false,
               false,
               null
            );
      } catch (InternalException e) {
         propagate(e);
      } catch (CloudException e) {
         propagate(e);
      }

      return new NodeAndInitialCredentials<VirtualMachine>(from, from.getProviderVirtualMachineId() + "", LoginCredentials.builder().user(from.getRootUser())
            .password(from.getRootPassword()).build());
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      Set<org.jclouds.compute.domain.Hardware> hardware = Sets.newLinkedHashSet();
      hardware.add(new HardwareBuilder().ids("t1.micro").hypervisor("vSphere").name("t1.micro").processor(new Processor(1,1.0)).ram(512).build());
      hardware.add(new HardwareBuilder().ids("m1.small").hypervisor("vSphere").name("m1.small").processor(new Processor(1,1.0)).ram(1024).build());
      hardware.add(new HardwareBuilder().ids("m1.medium").hypervisor("vSphere").name("m1.medium").processor(new Processor(1,1.0)).ram(3840).build());
      hardware.add(new HardwareBuilder().ids("m1.large").hypervisor("vSphere").name("m1.large").processor(new Processor(1,1.0)).ram(7680).build());
      return hardware;
   }

   @Override
   public Iterable<MachineImage> listImages() {
      Iterable<MachineImage> images = null;
      try {
         images = client.get().getComputeServices().getImageSupport().listMachineImages();
      } catch (InternalException e) {
         propagate(e);
      } catch (CloudException e) {
         propagate(e);
      }
      return images;
   }
   
   @Override
   public Iterable<VirtualMachine> listNodes() {
      Iterable<VirtualMachine> vms = null;
      try {
         vms = client.get().getComputeServices().getVirtualMachineSupport().listVirtualMachines();
      } catch (InternalException e) {
         propagate(e);
      } catch (CloudException e) {
         propagate(e);
      }

      return vms;
   }
   
   @Override
   public Iterable<Location> listLocations() {
      Iterable<Region> regions = ImmutableList.of();
      Iterable<DataCenter> dataCenters = ImmutableList.of();
      try {
         regions = client.get().getDataCenterServices().listRegions();
         for (Region region : regions) {
            dataCenters = Iterables.concat(client.get().getDataCenterServices().listDataCenters(region.getProviderRegionId()), dataCenters);
         }
      } catch (InternalException e) {
         propagate(e);
      } catch (CloudException e) {
         propagate(e);
      }

      return Iterables.concat(Iterables.transform(regions, regionToLocation),Iterables.transform(dataCenters, dataCenterToLocation));
   }

   @Override
   public VirtualMachine getNode(String id) {
      VirtualMachine vm = null;
      try {
         vm = client.get().getComputeServices().getVirtualMachineSupport().getVirtualMachine(id);
      } catch (InternalException e) {
         propagate(e);
      } catch (CloudException e) {
         propagate(e);
      }

      return vm;
   }

   @Override
   public void destroyNode(String id) {
//      client.destroyServer(Integer.parseInt(id));
   }

   @Override
   public void rebootNode(String id) {
      try {
         client.get().getComputeServices().getVirtualMachineSupport().reboot(id);
      } catch (InternalException e) {
         propagate(e);
      } catch (CloudException e) {
         propagate(e);
      }
   }

   @Override
   public void resumeNode(String id) {
      try {
         client.get().getComputeServices().getVirtualMachineSupport().boot(id);
      } catch (InternalException e) {
         propagate(e);
      } catch (CloudException e) {
         propagate(e);
      }

   }

   @Override
   public void suspendNode(String id) {
      try {
         client.get().getComputeServices().getVirtualMachineSupport().pause(id);
      } catch (InternalException e) {
         propagate(e);
      } catch (CloudException e) {
         propagate(e);
      }
   }
}