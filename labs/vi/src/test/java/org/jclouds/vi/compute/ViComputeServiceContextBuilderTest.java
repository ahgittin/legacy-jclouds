package org.jclouds.vi.compute;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

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
      context.getComputeService().listNodes();

      context.close();
   }

   @Test
   public void testCanBuildWithRestProperties() {
      Properties restProperties = new Properties();
      restProperties.setProperty("vi.contextbuilder", ViComputeServiceContextBuilder.class.getName());
      restProperties.setProperty("vi.propertiesbuilder", ViPropertiesBuilder.class.getName());
      restProperties.setProperty("vi.endpoint",  "https://192.168.134.224/sdk");

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
