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
import dderrien.customportal.model.Category;
import dderrien.customportal.service.CategoryService;

@Path("/api/category")
public class CategoryResource {

    private CategoryService service;

    @Inject
    public CategoryResource(CategoryService service) {
        this.service = service;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Category> select(@QueryParam("ownerId") Long ownerId) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (ownerId != null && ownerId != 0L) {
            params.put("ownerId", ownerId);
        }
        // TODO: find a way to convey the 'sort(+order)' parameter!
        return service.select(params, null, null);
    }

    @GET
    @Path("/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Category get(@PathParam("id") Long id) {
        return service.get(id, 0L);
    }

    @GET
    @Path("/{id:\\d+}/{version:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Category getVersioned(@PathParam("id") Long id, @PathParam("version") long version) {
        return service.get(id, version);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createForIdOnly(Category entity, @Context UriInfo uriInfo) throws URISyntaxException {
        Key<AbstractBase<Category>> key = service.create(entity);
        return Response.status(201).location(new URI(uriInfo.getPath() + key.getId())).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createForIdAndEntity(Category entity, @Context UriInfo uriInfo) throws URISyntaxException {
        Key<AbstractBase<Category>> key = service.create(entity);
        return Response.status(201).entity(get(key.getId())).location(new URI(uriInfo.getPath() + key.getId())).build();
    }

    @PUT
    @Path("/{id:\\d+}/{version:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Category update(@PathParam("id") Long id, @PathParam("version") long version, Category entity) {
        return service.update(id, version, entity);
    }

    @DELETE
    @Path("/{id:\\d+}/{version:\\d+}")
    public void delete(@PathParam("id") Long id, @PathParam("version") long version) {
        service.delete(id, version);
    }
}