package apiserver.services.pdf.service;

/*******************************************************************************
 Copyright (c) 2013 Mike Nimer.

 This file is part of ApiServer Project.

 The ApiServer Project is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 The ApiServer Project is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with the ApiServer Project.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

import apiserver.exceptions.ColdFusionException;
import apiserver.services.pdf.gateways.jobs.ExtractImageResult;
import apiserver.services.pdf.GridConfig;
import apiserver.workers.coldfusion.model.CollectionResult;
import apiserver.workers.coldfusion.services.pdf.MergePdfCallable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridgain.grid.Grid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * User: mnimer
 * Date: 9/18/12
 */
public class MergePdfFilesCFConfig implements Serializable
{
    private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private Grid grid;
    // Get grid-enabled executor service for nodes where attribute 'worker' is defined.
    @Autowired private ExecutorService executorService;


    private @Value("${defaultReplyTimeout}") Integer defaultTimeout;

    public Object execute(Message<?> message) throws ColdFusionException
    {
        ExtractImageResult props = (ExtractImageResult)message.getPayload();

        try
        {
            long startTime = System.nanoTime();

            Future<CollectionResult> future = executorService.submit(
                    new MergePdfCallable(props.getFile().getFileBytes(), props.getOptions())
            );


            CollectionResult _result = future.get(defaultTimeout, TimeUnit.SECONDS);
            props.setResult(_result.getCollection());


            long endTime = System.nanoTime();
            log.debug("execution times: CF=" +_result.getStats().getExecutionTime() +"ms -- total=" +(endTime-startTime)+"ms");

            return props;
        }
        catch(Exception ge){
            throw new RuntimeException(ge);
        }
    }

}
