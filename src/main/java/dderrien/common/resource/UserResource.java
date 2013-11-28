package dderrien.common.resource;

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

import org.apache.commons.lang3.StringUtils;

import com.googlecode.objectify.Key;

import dderrien.common.model.AbstractBase;
import dderrien.common.model.User;
import dderrien.common.service.UserService;

@Path("/api/user")
public class UserResource {

    private UserService service;

    @Inject
    public UserResource(UserService service) {
        this.service = service;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> select(@QueryParam("name") String name) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(name)) {
            params.put("name", name);
        }
        return service.select(params, null, null);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User get(@PathParam("id") Long id) {
        return service.get(id);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(User entity) throws URISyntaxException {
        Key<AbstractBase<User>> key = service.create(entity);
        return Response.status(201).entity(get(key.getId())).location(new URI("/api/user/" + key.getId())).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User update(@PathParam("id") Long id, User entity) {
        return service.update(id, entity);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        service.delete(id);
    }
}