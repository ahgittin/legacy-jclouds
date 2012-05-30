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
import com.google.common.collect.ImmutableList;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;

import javax.inject.Singleton;

/**
 * @author Adrian Cole
 */
@Singleton
public class VirtualMachineToHardware implements Function<VirtualMachine, Hardware> {

   @Override
   public Hardware apply(VirtualMachine from) {
      VirtualMachineProduct product = from.getProduct();
      HardwareBuilder builder = new HardwareBuilder();
      builder.ids(product.getProductId())
         .name(product.getName())
         .processors(ImmutableList.of(new Processor(product.getCpuCount(), 1.0)))
         .ram(product.getRamInMb())
         .volumes(ImmutableList.<Volume> of(new VolumeImpl(Float.intBitsToFloat(product.getDiskSizeInGb()), true, false)));

      return builder.build();
   }

}
