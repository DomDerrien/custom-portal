package dderrien.customportal.resource;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.googlecode.objectify.Key;

import dderrien.common.model.AbstractBase;
import dderrien.customportal.model.Link;
import dderrien.customportal.service.LinkService;

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
    @Path("/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Link get(@PathParam("id") Long id) {
        return service.get(id, 0L);
    }

    @GET
    @Path("/{id:\\d+}/{version:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Link getVersioned(@PathParam("id") Long id, @PathParam("version") Long version) {
        return service.get(id, version);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createForIdOnly(Link entity, @Context UriInfo uriInfo) throws URISyntaxException {
        Key<AbstractBase<Link>> key = service.create(entity);
        return Response.status(201).location(new URI(uriInfo.getPath() + key.getId())).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createForIdAndEntity(Link entity, @Context UriInfo uriInfo) throws URISyntaxException {
        Key<AbstractBase<Link>> key = service.create(entity);
        return Response.status(201).entity(get(key.getId())).location(new URI(uriInfo.getPath() + key.getId())).build();
    }

    @PUT
    @Path("/{id:\\d+}/{version:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Link update(@PathParam("id") Long id, @PathParam("version") Long version, Link entity) {
        return service.update(id, version, entity);
    }

    @DELETE
    @Path("/{id:\\d+}/{version:\\d+}")
    public void delete(@PathParam("id") Long id, @PathParam("version") Long version) {
        service.delete(id, version);
    }
}