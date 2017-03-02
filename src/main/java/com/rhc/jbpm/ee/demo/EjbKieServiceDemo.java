package com.rhc.jbpm.ee.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dcnorris
 */
@Path("services")
public class EjbKieServiceDemo {

    private static final Logger LOG = LoggerFactory.getLogger(EjbKieServiceDemo.class);

    @Inject
    private DeploymentService deploymentService;

    @Inject
    private ProcessService processService;

    @Inject
    private RuntimeDataService runtimeDataService;

    @GET
    @Path("start/{gav}/{processId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response startProcess(@PathParam("gav") String gav, @PathParam("processId") String processId, @QueryParam("arg") final List<String> params) {
        if (!deploymentService.isDeployed(gav)) {
            LOG.info("Deploying " + gav);
            String[] tokens = gav.split(":");
            KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(tokens[0], tokens[1], tokens[2]);
            deploymentService.deploy(deploymentUnit);
        }
        if (params != null && !params.isEmpty()) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            for (String kv : params) {
                String[] tokens = kv.split(":");
                parameters.put(tokens[0], tokens[1]);
            }
            Long processInstanceId = processService.startProcess(gav, processId, parameters);
            return Response.ok(processInstanceId).build();
        } else {

            Long processInstanceId = processService.startProcess(gav, processId);
            return Response.ok(processInstanceId).build();
        }
    }

}
