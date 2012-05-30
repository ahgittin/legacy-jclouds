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
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VmState;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Iso3166;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Named;
import java.util.List;

import static org.jclouds.vsphere.config.VSphereComputeServiceContextModule.*;

@Singleton
public class VirtualMachineToNodeMetadata implements Function<VirtualMachine, NodeMetadata> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public NodeMetadata apply(@Nullable VirtualMachine vm) {
      NodeState nodeState = machineToNodeState.get(vm.getCurrentState());
      if (nodeState == null)
         nodeState = NodeState.UNRECOGNIZED;

      NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder()
         .name(vm.getName())
         .ids(vm.getProviderVirtualMachineId())
//         .group(group)
         .location(new LocationBuilder()
               .id(vm.getProviderDataCenterId() != null ? vm.getProviderDataCenterId() : "")
               .scope(LocationScope.HOST)
               .description(vm.getProviderRegionId() != null ? vm.getProviderRegionId() : "")
               .build())
         .state(nodeState)
         .publicAddresses(ImmutableList.<String> copyOf(vm.getPublicIpAddresses()))
         .privateAddresses(ImmutableList.<String> copyOf(vm.getPrivateIpAddresses()))
         .hostname(vm.getPublicDnsAddress())
         .userMetadata(vm.getTags())
//         .operatingSystem(vm.)
         .credentials(new LoginCredentials(vm.getRootUser(), vm.getRootPassword(), null, true));

      return nodeMetadataBuilder.build();
   }
}
