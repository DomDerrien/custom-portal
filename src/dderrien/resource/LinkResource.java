package dderrien.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;

import com.googlecode.objectify.Key;

import dderrien.model.AbstractBase;
import dderrien.model.Link;
import dderrien.service.LinkService;

@Path("/api/link")
public class LinkResource {

    private LinkService service;
    
    @Inject
    public LinkResource(LinkService service) {
        this.service = service;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Link> select(@QueryParam("ownerId") Long ownerId, @QueryParam("categoryId") Long categoryId) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (ownerId != null && ownerId != 0L) {
            params.put("ownerId", ownerId);
        }
        if (categoryId != null && categoryId != 0L) {
            params.put("categoryId", categoryId);
        }
        return service.select(params, null, null);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Link get(@PathParam("id") Long id) {
        return service.get(id);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Link entity) throws URISyntaxException {
        Key<AbstractBase<Link>> key = service.create(entity);
        return Response.status(201).entity(get(key.getId())).location(new URI("/api/link/" + key.getId())).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Link update(@PathParam("id") Long id, Link entity) {
        return service.update(id, entity);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        service.delete(id);
    }
}