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

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.*;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.Location;
import org.jclouds.vsphere.VSphereApiMetadata;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ServerManagerExperimentLiveTest")
public class VSphereExperimentLiveTest extends BaseComputeServiceContextLiveTest {

   public VSphereExperimentLiveTest() {
      provider = "vsphere";
   }

   @Test
   public void testAndExperiment() {
      ComputeServiceContext context = null;
      try {
         context = ContextBuilder.newBuilder(new VSphereApiMetadata())
            .endpoint("https://192.168.134.224/sdk")
            .credentials("root", "vmware")
            .build(ComputeServiceContext.class);
         System.out.println("We got here, where is the log?");
//         context.getComputeService().resumeNode("503b24dc-a991-08bf-a1cc-aba277b6a754");


//       for (ComputeMetadata computeMetadata : Iterables.filter(context.getComputeService().listNodesDetailsMatching(NodePredicates.all()), NodePredicates.RUNNING)) {
         for (ComputeMetadata computeMetadata : context.getComputeService().listNodesDetailsMatching(NodePredicates.all())) {
            System.out.println("ComputeMetaData: " + computeMetadata.toString());
            for (String tag : computeMetadata.getTags()) {
               System.out.println("\tTag: " + tag);
            }
         }

         for (Hardware hardware : context.getComputeService().listHardwareProfiles()) {
            System.out.println("Hardware: " + hardware.toString());
            for (String tag : hardware.getTags()) {
               System.out.println("\tTag: " + tag);
            }
         }

         System.out.println("\nLocations:");
         for (Location location : context.getComputeService().listAssignableLocations()) {
            System.out.println("\n\tLocation: " + location.toString());
         }

         System.out.println("\nImages:");
         for (Image image : context.getComputeService().listImages()) {
            System.out.println("\n\tImage: " + image);
         }

         System.out.println("Create");
         Template template = context.getComputeService().templateBuilder()
            .options(TemplateOptions.Builder.blockOnComplete(true))
//            .osFamily(OsFamily.UBUNTU)
            .imageNameMatches("^ubuntu.*")
//            .minCores(1)
//            .minRam(256)
            .build(); //.options(inboundPorts(22, 8080))

/*
         try {
            context.getComputeService().createNodesInGroup("test", 1, template); //imageId()TemplateOptions.Builder.blockOnComplete(true));
         } catch (RunNodesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }
*/

         assertEquals(true, false, "Forced failure to see logs");

         System.out.println("And we're all done");

      } finally {
         if (context != null)
            context.close();
      }
   }
}
