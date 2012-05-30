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
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.vsphere.PrivateCloud;
import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Singleton
public class CreateAndConnectVSphereClient implements Supplier<PrivateCloud> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

//   private final Supplier<ProviderContext> providerContextSupplier;
   private final Supplier<URI> providerSupplier;
   private final Function<Supplier<ProviderContext>, PrivateCloud> providerContextToCloud;
   private transient PrivateCloud client;
//   private final Function<Supplier<NodeMetadata>, PrivateCloud> clientForNode;
//   private transient PrivateCloud client;
   private transient String identity;
   private transient String credential;

   @Inject
   public CreateAndConnectVSphereClient(Function<Supplier<ProviderContext>, PrivateCloud> providerContextToCloud,
                                        Factory runScriptOnNodeFactory,
//                                        Supplier<ProviderContext> providerContextSupplier,
                                        @Provider Supplier<URI> providerSupplier,
                                        @Nullable @Identity String identity,
                                        @Nullable @Credential String credential) {
//      this.providerContextSupplier = checkNotNull(providerContextSupplier, "host");
      this.identity = checkNotNull(identity, "userid");
      this.credential = checkNotNull(credential, "password");
      this.providerSupplier = checkNotNull(providerSupplier, "endpoint to vSphere node or vCenter server is needed");
      this.providerContextToCloud = checkNotNull(providerContextToCloud, "client");
      start();
   }

   public synchronized void start() {
      client = new PrivateCloud();
      ProviderContext ctx = new ProviderContext();

      ctx.setEndpoint(providerSupplier.get().toString());
      ctx.setAccessKeys(identity.getBytes(), credential.getBytes());

      ctx.setAccountNumber("accountNumber");
      ctx.setCloudName("cloudName");
      ctx.setProviderName(PrivateCloud.class.getName());

//      ctx.setRegionId(props.getProperty("regionId"));

/*
      Properties custom = new Properties();
      Enumeration<?> names = props.propertyNames();

      while( names.hasMoreElements() ) {
          String name = (String)names.nextElement();

          if( name.startsWith("test.") ) {
              custom.setProperty(name, props.getProperty(name));
          }
      }
      ctx.setCustomProperties(custom);
*/

//      client.get().connect(ctx);

//      client = providerContextToCloud.apply(providerContextSupplier);

      client.connect(ctx);

//      client.connect(provider.toASCIIString(), "", "");
   }

   @Override
   public PrivateCloud get() {
      checkState(client != null, "start not called");
      return client;
   }

}
