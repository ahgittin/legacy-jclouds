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

package org.jclouds.vi.compute.config;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.rmi.RemoteException;

import javax.inject.Singleton;

import com.google.inject.Injector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.Provider;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.vi.compute.functions.DatacenterToLocation;
import org.jclouds.vi.compute.functions.VirtualMachineToImage;
import org.jclouds.vi.compute.functions.VirtualMachineToHardware;
import org.jclouds.vi.compute.functions.VirtualMachineToNodeMetadata;
import org.jclouds.vi.compute.strategy.ViComputeServiceAdapter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * 
 * @author Adrian Cole
 */
public class ViComputeServiceContextModule
         extends
         ComputeServiceAdapterContextModule<ServiceInstance, ServiceInstance, VirtualMachine, Hardware, Image, Datacenter> {

   public ViComputeServiceContextModule() {
      super(ServiceInstance.class, ServiceInstance.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<VirtualMachine, Hardware, Image, Datacenter>>() {
      }).to(ViComputeServiceAdapter.class);
//      bind(new TypeLiteral<Supplier<Location>>() {
//      }).to(OnlyLocationOrFirstZone.class);
      bind(new TypeLiteral<Function<Image, Image>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<VirtualMachine, NodeMetadata>>() {
      }).to(VirtualMachineToNodeMetadata.class);
      bind(new TypeLiteral<Function<VirtualMachine, Image>>() {
      }).to(VirtualMachineToImage.class);
      bind(new TypeLiteral<Function<VirtualMachine, Hardware>>() {
      }).to(VirtualMachineToHardware.class);
      bind(new TypeLiteral<Function<Datacenter, Location>>() {
      }).to(DatacenterToLocation.class);
   }

   @Singleton
   @Provides
   protected ServiceInstance createConnection(
//            JcloudsWSClient client,
            @Provider Supplier<URI> endpoint,
            @Identity String identity,
            @Credential String credential) throws MalformedURLException, RemoteException {

//               MalformedURLException, URISyntaxException{

      ServiceInstance si = null;
      try {
         si = new ServiceInstance(endpoint.get().toURL(), identity, credential, true);
      } catch (Exception ex) {
         propagate(ex);
      }

      return si;

   }

}