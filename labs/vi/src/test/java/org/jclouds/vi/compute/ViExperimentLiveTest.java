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

package org.jclouds.vi.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Properties;
import java.util.Set;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "vsphere.ViExperimentLiveTest")
public class ViExperimentLiveTest {
	
	protected String provider = "vi";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   @Test(enabled = false)
   public void testAndExperiment() {
      ComputeServiceContext context = null;

      try {


         Properties restProperties = new Properties();
         restProperties.setProperty("vi.contextbuilder", ViComputeServiceContextBuilder.class.getName());
         restProperties.setProperty("vi.propertiesbuilder", ViPropertiesBuilder.class.getName());
         restProperties.setProperty("vi.endpoint",  "https://192.168.134.224/sdk");
         restProperties.setProperty("vi.trust-all-certs",  "FALSE");

         context = new ComputeServiceContextFactory(restProperties).createContext("vi",
               "root", "vmware");

//         context = new ComputeServiceContextFactory().createContext(new ViComputeServiceContextSpec(endpoint, identity,
//                  credential), ImmutableSet.<Module>of(new Log4JLoggingModule()), new ViPropertiesBuilder().build());

/*
         Set<? extends Location> locations = context.getComputeService().listAssignableLocations();
         Set<? extends Hardware> hardwares = context.getComputeService().listHardwareProfiles();
         Set<? extends Image> images = context.getComputeService().listImages();
         Set<? extends ComputeMetadata> nodes = context.getComputeService().listNodes();

         System.out.println("Locations:");
         for (Location location : locations) {
            System.out.println("\tid: " + location.getId() + " - desc: " + location.getDescription());
         }

         System.out.println("Images:");
         for (Image image : images) {
            System.out.println("\tid: " + image.getId() + " - name:" + image.getName());
         }

         System.out.println("Nodes:");
         for (ComputeMetadata node : nodes) {
            System.out.println("\tid: " + node.getId() + " - name:" + node.getName());
         }

         System.out.println("Hardware Profiles:");
         for (Hardware hardware : hardwares) {
            System.out.println("\tid: " + hardware.getId() + " - name: " + hardware.getName());
         }
*/

         //

         //
         // NodeMetadata node = context.getComputeService().getNodeMetadata("MyWinServer");
         // System.out.println(node);

         /*
          * We will probably make a default template out of properties at some point You can control
          * the default template via overriding a method in standalonecomputeservicexontextmodule
          */

         Template template = context.getComputeService().templateBuilder().imageId("ubuntu1004-jclouds-template").minRam(64).build(); // hardwareId("vm-1221").imageId("winNetEnterprise64Guest") //.locationId("") .build();
         System.out.println("default: " + template);

         context.getComputeService().createNodesInGroup("vitest", 1, template); //imageId()TemplateOptions.Builder.blockOnComplete(true));



            //context.getComputeService().destroyNode(nodeMetadata.getId()); }
      } catch (Exception e) {
         e.printStackTrace();

      } finally {
         if (context != null)
            context.close();
      }
   }

   @Test
   public void testExistingNode() {
      ComputeServiceContext context = null;

      try {

         Properties restProperties = new Properties();
         restProperties.setProperty("vi.contextbuilder", ViComputeServiceContextBuilder.class.getName());
         restProperties.setProperty("vi.propertiesbuilder", ViPropertiesBuilder.class.getName());
         restProperties.setProperty("vi.endpoint",  endpoint);
         restProperties.setProperty("vi.trust-all-certs",  "FALSE");

         context = new ComputeServiceContextFactory(restProperties).createContext("vi",
               identity, credential);

         for (ComputeMetadata nodemeta : context.getComputeService().listNodes()) {
            System.out.println(nodemeta);
         }

//         context.getComputeService().runScriptOnNode("vitest-aa8", "uname -a");
//         context.getComputeService().destroyNode("vitest-aa8");
      } catch (Exception e) {
         e.printStackTrace();

      } finally {
         if (context != null)
            context.close();
      }
   }


}