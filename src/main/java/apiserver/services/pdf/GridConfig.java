package apiserver.services.pdf;

import apiserver.ApiServerConstants;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.marshaller.optimized.OptimizedMarshaller;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Configuration for classes that use the Grid Gain Executor Service to invoke a method on a remote server
 * <p/>
 * Created by mnimer on 6/10/14.
 */
@Configuration
public class GridConfig implements Serializable
{
    private static Ignite grid = null;


    @Bean
    public Ignite grid()
    {
        try {
            Ignite grid = Ignition.start(getGridConfiguration());
            return grid;
        }
        catch (IgniteException ge) {
            if (ge.getMessage().contains("Grid instance was not properly started or was already stopped")) {
                Ignition.restart(true);
            }
            ge.printStackTrace();
            throw new RuntimeException(ge);
        }

    }


    @Bean
    public ExecutorService executorService()
    {
        try {
            // Get grid-enabled executor service for nodes where attribute 'worker' is defined.
            ClusterGroup projection = grid().cluster().forAttribute("ROLE", "connector-coldfusion");

            if (projection.nodes().size() == 0) {  //todo, test that it returns null if CF is not running
                throw new IgniteException("ColdFusion-Worker Grid Node is not running or accessible");
            }

            return projection.ignite().executorService();
        }catch(IgniteException ex){
            throw new RuntimeException(ex);
        }
    }


    private IgniteConfiguration getGridConfiguration()
    {
        Map<String, String> userAttr = new HashMap<String, String>();
        userAttr.put("ROLE", "ApiAppEngine");
        userAttr.put("ROLE", "image-pdf");


        OptimizedMarshaller gom = new OptimizedMarshaller();
        gom.setRequireSerializable(false);

        TcpDiscoveryMulticastIpFinder fndr = new TcpDiscoveryMulticastIpFinder();
        fndr.setLocalAddress("127.0.0.1");

        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        spi.setIpFinder(fndr);


        IgniteConfiguration gc = new IgniteConfiguration();
        gc.setGridName(ApiServerConstants.GRID_NAME);
        gc.setPeerClassLoadingEnabled(true);
        gc.setUserAttributes(userAttr);
        gc.setMarshaller(gom);
        gc.setDiscoverySpi(spi);


        /**
         * Configure grid to use multicast based discovery.
         */

        //GridCacheConfiguration gcc = new GridCacheConfiguration();
        //gcc.setCacheMode(GridCacheMode.PARTITIONED);
        //gcc.setName("documentcache");
        //gcc.setSwapEnabled(true);
        //gcc.setAtomicityMode(GridCacheAtomicityMode.ATOMIC);
        //gcc.setQueryIndexEnabled(true);
        //gcc.setBackups(0);
        //gcc.setStartSize(200000);

        //gc.setCacheConfiguration(gcc);

        return gc;
    }
}
