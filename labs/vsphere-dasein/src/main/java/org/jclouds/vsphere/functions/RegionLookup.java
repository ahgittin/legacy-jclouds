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
import org.dasein.cloud.dc.Region;
import org.dasein.cloud.vsphere.PrivateCloud;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Throwables.propagate;

@Singleton
public class RegionLookup implements Function<String, Region> {
   private final Supplier<PrivateCloud> client;

   @Inject
   public RegionLookup(Supplier<PrivateCloud> client) {
      this.client = client;
   }

   @Override
   public Region apply(String from) {
      Region region = null;

      try {
         region = client.get().getDataCenterServices().getRegion(from);
      } catch (InternalException e) {
         propagate(e);
      } catch (CloudException e) {
         propagate(e);
      }

      return region;
   }

}
