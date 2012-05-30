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

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.internal.ContextImpl;
import org.jclouds.vsphere.VSphereApiMetadata;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit", testName = "ServerManagerContextBuilderTest")
public class VSphereComputeServiceContextBuilderTest {


   @Test
   public void testCanBuildWithApiMetadata() {
      ComputeServiceContext context = ContextBuilder.newBuilder(
            new VSphereApiMetadata()).build(ComputeServiceContext.class);
      assertEquals(context.unwrap().getProviderMetadata().getApiMetadata().getName(), "vSphere API");
      context.close();
   }

   @Test
   public void testCanBuildById() {
      ComputeServiceContext context = ContextBuilder.newBuilder("vsphere").build(ComputeServiceContext.class);
      context.close();
   }

   @Test
   public void testCanBuildWithOverridingProperties() {
      Properties overrides = new Properties();
      overrides.setProperty("vsphere.endpoint", "http://host");
      overrides.setProperty("vsphere.api-version", "99");

      ComputeServiceContext context = ContextBuilder.newBuilder("vsphere")
            .overrides(overrides).build(ComputeServiceContext.class);

      assertEquals(context.unwrap().getProviderMetadata().getEndpoint(), "http://host");
      assertEquals(context.unwrap().getProviderMetadata().getApiMetadata().getVersion(), "99");

      context.close();
   }

   @Test
   public void testCanBuildWithBuilder() {

      ComputeServiceContext context = ContextBuilder.newBuilder("vsphere")
            .endpoint("http://host")
            .apiVersion("99")
            .build(ComputeServiceContext.class);

      assertEquals(context.unwrap().getProviderMetadata().getEndpoint(), "http://host");
      assertEquals(context.unwrap().getProviderMetadata().getApiMetadata().getVersion(), "99");

      context.close();
   }

   @Test
   public void testUnwrapIsCorrectType() {
      ComputeServiceContext context = ContextBuilder.newBuilder("vsphere").build(ComputeServiceContext.class);

      assertEquals(context.unwrap().getClass(), ContextImpl.class);

      context.close();
   }
}
