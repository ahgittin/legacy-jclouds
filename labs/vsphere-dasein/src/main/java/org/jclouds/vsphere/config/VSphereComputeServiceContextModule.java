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
package org.jclouds.vsphere.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VmState;
import org.dasein.cloud.dc.DataCenter;
import org.dasein.cloud.dc.Region;
import org.dasein.cloud.vsphere.PrivateCloud;
import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.vsphere.compute.VSphereComputeServiceAdapter;
import org.jclouds.vsphere.functions.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

/**
 * 
 * @author Adrian Cole
 */
public class VSphereComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<VirtualMachine, Hardware, MachineImage, Location> {

   private String identity;

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<VirtualMachine, org.jclouds.compute.domain.Hardware, MachineImage, Location>>() {
      }).to(VSphereComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<org.jclouds.compute.domain.Hardware, org.jclouds.compute.domain.Hardware>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<Image, Image>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<MachineImage, Image>>() {
      }).to(VirtualMachineImageToImage.class);
      bind(new TypeLiteral<Function<VirtualMachine, org.jclouds.compute.domain.Hardware>>() {
      }).to(VirtualMachineToHardware.class);
      bind(new TypeLiteral<Function<VirtualMachine, NodeMetadata>>() {
      }).to(VirtualMachineToNodeMetadata.class);
      bind(new TypeLiteral<Function<Region, Location>>() {
      }).to(RegionToLocation.class);
      bind(new TypeLiteral<Function<String, Region>>() {
      }).to(RegionLookup.class);
      bind(new TypeLiteral<Function<DataCenter, Location>>() {
      }).to(DataCenterToLocation.class);
      bind(new TypeLiteral<Supplier<PrivateCloud>>() {
      }).to((Class) CreateAndConnectVSphereClient.class);

      // to have the compute service adapter override default locations
      install(new LocationsFromComputeServiceAdapterModule<VirtualMachine, Hardware, MachineImage, Location>(){});
   }

   @VisibleForTesting
   public static final Map<VmState, NodeState> machineToNodeState = ImmutableMap
      .<VmState, NodeState> builder()
         .put(VmState.RUNNING, NodeState.RUNNING)
         .put(VmState.PAUSED, NodeState.SUSPENDED)
         .put(VmState.PENDING, NodeState.PENDING)
         .put(VmState.TERMINATED, NodeState.TERMINATED)
         .put(VmState.REBOOTING, NodeState.UNRECOGNIZED)
         .put(VmState.STOPPING, NodeState.TERMINATED)
         .build();

   @Provides
   @Singleton
   protected Function<Supplier<ProviderContext>, PrivateCloud> client() {
      return new Function<Supplier<ProviderContext>, PrivateCloud>() {

         @Override
         public PrivateCloud apply(Supplier<ProviderContext> contextSupplier) {
            PrivateCloud client = new PrivateCloud();
            client.connect(contextSupplier.get());
            return client;
         }

         @Override
         public String toString() {
            return "createInstanceByNodeId()";
         }

      };
   }

/*
   @Provides
   @Singleton
   protected Supplier<ProviderContext> supplyProviderContext(@Named(Constants.PROPERTY_IDENTITY) final String identity,
                                                             @Named(Constants.PROPERTY_CREDENTIAL) final String credentials,
                                                             @Named(Constants.PROPERTY_ENDPOINT) final String endpoint) {
      return new Supplier<ProviderContext>() {
         @Override
         public ProviderContext get() {
            ProviderContext context = new ProviderContext();
            context.setEndpoint(endpoint);
            context.setAccessKeys(identity.getBytes(), credentials.getBytes());
            context.setAccountNumber("accountNumber");
            context.setCloudName("cloudName");
            context.setProviderName(PrivateCloud.class.getName());

            return context;
         }
      };
   }
*/

/*
   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(OsFamily.UBUNTU).osVersionMatches("11.04")
               .osArchMatches("x86");
   }
*/


}