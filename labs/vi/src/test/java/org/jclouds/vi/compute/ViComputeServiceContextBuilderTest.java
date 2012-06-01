package org.jclouds.vi.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Properties;

import com.vmware.vim25.InvalidLogin;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.vmware.vim25.mo.ServiceInstance;

/**
 * 
 * @author andrea.turli
 * 
 */
@Test(groups = "unit")
public class ViComputeServiceContextBuilderTest {

   @Test
   public void testCanBuildWithContextSpec() {
      ComputeServiceContext context = new ComputeServiceContextFactory().createContext(new ViComputeServiceContextSpec(
            "https://192.168.134.224/sdk", "root", "vmware"));

      context.close();
   }

   @Test
   public void testCanBuildWithContextSpec2() {
      Properties overrides = new Properties();
      overrides.setProperty("vi.endpoint", "https://192.168.134.224/sdk");
      overrides.setProperty("vi.identity", "root");
      overrides.setProperty("vi.credential", "vmware");
      overrides.setProperty("vi.trust-all-certs", "TRUE");

      ComputeServiceContext context = new ComputeServiceContextFactory().createContext("vi", null, overrides);

      context.close();
   }

   @Test
   public void testAuthFailureWithContextSpec() {
      ComputeServiceContext context = null;
      try {
         context = new ComputeServiceContextFactory().createContext(new ViComputeServiceContextSpec(
               "https://192.168.134.224/sdk", "root", "xxxxxx"));
      } catch (Exception ilex) {
      }
      assertNull(context, "Auth passed, but should have failed");

      if (context != null) {
         context.close();
      }
   }

   @Test
   public void testInvalidEndpointFailureWithContextSpec() {
      ComputeServiceContext context = null;
      try {
         context = new ComputeServiceContextFactory().createContext(new ViComputeServiceContextSpec(
            "https://192.168.134.229/sdk", "root", "vmware"));
      } catch (Exception ex) {
      }

      assertNull(context, "Connection should have filed");

      if (context != null) {
         context.close();
      }
   }

   @Test
   public void testCanBuildWithRestProperties() {
      Properties restProperties = new Properties();
      restProperties.setProperty("vi.contextbuilder", ViComputeServiceContextBuilder.class.getName());
      restProperties.setProperty("vi.propertiesbuilder", ViPropertiesBuilder.class.getName());
      restProperties.setProperty("vi.endpoint",  "https://192.168.134.224/sdk");
      restProperties.setProperty("vi.trust-all-certs",  "FALSE");

      ComputeServiceContext context = new ComputeServiceContextFactory(restProperties).createContext("vi",
            "root", "vmware");
      context.close();
   }

   @Test(enabled = false)
   public void testProviderSpecificContextIsCorrectType() {
      ComputeServiceContext context = new ViComputeServiceContextBuilder(new Properties()).buildComputeServiceContext();
      RestContext<ServiceInstance, ServiceInstance> providerContext = context.getProviderSpecificContext();

      assertEquals(providerContext.getApi().getClass(), ServiceInstance.class);

      context.close();
   }
}
