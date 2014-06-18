package apiserver.services.pdf.grid;

import apiserver.ApiServerConstants;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfiguration;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.GridProjection;
import org.gridgain.grid.marshaller.optimized.GridOptimizedMarshaller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Configuration for classes that use the Grid Gain Executor Service to invoke a method on a remote server
 *
 * Created by mnimer on 6/10/14.
 */
public class GridService implements Serializable
{
    private static Grid grid = null;


    protected Grid verifyGridConnection()
    {
        if( grid == null )
        {
            try {
                grid = GridGain.start(getGridConfiguration());
            }catch(GridException ge){
                throw new RuntimeException(ge);
            }
        }

        return grid;
    }




    public ExecutorService getColdFusionExecutor() throws GridException {

        // Get grid-enabled executor service for nodes where attribute 'worker' is defined.
        GridProjection projection = grid.forAttribute("ROLE", "coldfusion-worker");

        if( projection.nodes().size() == 0 ) {  //todo, test that it returns null if CF is not running
            throw new GridException("ColdFusion-Worker Grid Node is not running or accessible");
        }

        return projection.forRandom().compute().executorService();
    }



    private GridConfiguration getGridConfiguration() {
        Map<String, String> userAttr = new HashMap<String, String>();
        userAttr.put("ROLE", "image-pdf");


        GridOptimizedMarshaller gom = new GridOptimizedMarshaller();
        gom.setRequireSerializable(false);

        GridConfiguration gc = new GridConfiguration();
        gc.setGridName( ApiServerConstants.GRID_NAME );
        gc.setPeerClassLoadingEnabled(false);
        gc.setRestEnabled(false);
        gc.setUserAttributes(userAttr);
        gc.setMarshaller(gom);


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
