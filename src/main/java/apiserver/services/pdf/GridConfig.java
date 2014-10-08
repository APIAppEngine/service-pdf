package apiserver.services.pdf;

import apiserver.ApiServerConstants;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfiguration;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.GridProjection;
import org.gridgain.grid.kernal.managers.discovery.GridDiscoveryManager;
import org.gridgain.grid.marshaller.optimized.GridOptimizedMarshaller;
import org.gridgain.grid.spi.discovery.GridDiscoverySpi;
import org.gridgain.grid.spi.discovery.tcp.GridTcpDiscoverySpi;
import org.gridgain.grid.spi.discovery.tcp.ipfinder.multicast.GridTcpDiscoveryMulticastIpFinder;
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
    private static Grid grid = null;


    @Bean
    public Grid grid()
    {
        try {
            Grid grid = GridGain.start(getGridConfiguration());
            return grid;
        }
        catch (GridException ge) {
            if (ge.getMessage().contains("Grid instance was not properly started or was already stopped")) {
                GridGain.restart(true);
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
            GridProjection projection = grid().forAttribute("ROLE", "connector-coldfusion");

            if (projection.nodes().size() == 0) {  //todo, test that it returns null if CF is not running
                throw new GridException("ColdFusion-Worker Grid Node is not running or accessible");
            }

            return projection.forRandom().compute().executorService();
        }catch(GridException ex){
            throw new RuntimeException(ex);
        }
    }


    private GridConfiguration getGridConfiguration()
    {
        Map<String, String> userAttr = new HashMap<String, String>();
        userAttr.put("ROLE", "ApiAppEngine");
        userAttr.put("ROLE", "image-pdf");


        GridOptimizedMarshaller gom = new GridOptimizedMarshaller();
        gom.setRequireSerializable(false);

        GridTcpDiscoveryMulticastIpFinder fndr = new GridTcpDiscoveryMulticastIpFinder();
        fndr.setLocalAddress("127.0.0.1");

        GridTcpDiscoverySpi spi = new GridTcpDiscoverySpi();
        spi.setIpFinder(fndr);


        GridConfiguration gc = new GridConfiguration();
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
