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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Category get(@PathParam("id") Long id) {
        return service.get(id);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Category entity) throws URISyntaxException {
        Key<AbstractBase<Category>> key = service.create(entity);
        return Response.status(201).entity(get(key.getId())).location(new URI("/api/category/" + key.getId())).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Category update(@PathParam("id") Long id, Category entity) {
        return service.update(id, entity);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        service.delete(id);
    }
}