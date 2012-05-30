package org.jclouds.vsphere;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.vsphere.config.VSphereComputeServiceContextModule;

/**
 * Implementation of {@link ApiMetadata} for an example of library integration (VSphereManager)
 * 
 * @author Adrian Cole
 */
public class VSphereApiMetadata extends BaseApiMetadata {

   private static final long serialVersionUID = 7050419752716105398L;

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromApiMetadata(this));
   }

   public VSphereApiMetadata() {
      super(builder());
   }

   protected VSphereApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseApiMetadata.Builder {

      protected Builder(){
         id("vsphere")
         .name("vSphere API")
         .identityName("User")
         .defaultIdentity("rootx")
         .credentialName("Password")
         .defaultCredential("vmwarex")
         .defaultEndpoint("http://localhost/sdk")
//         .defaultEndpoint("https://192.168.134.224/sdk")
         .documentation(URI.create("http://www.jclouds.org/documentation/userguide/compute"))
         .view(ComputeServiceContext.class)
         .defaultModule(VSphereComputeServiceContextModule.class);
      }

      @Override
      public VSphereApiMetadata build() {
         return new VSphereApiMetadata(this);
      }

   }
}