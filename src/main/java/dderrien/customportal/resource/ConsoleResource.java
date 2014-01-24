package dderrien.customportal.resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dderrien.common.exception.NotFoundException;
import dderrien.common.model.User;
import dderrien.common.service.UserService;

@Path("/console")
public class ConsoleResource {

    private UserService userService;
    
    @Inject
    public ConsoleResource(UserService userService) {
        this.userService = userService;
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response prepareConsole(@Context HttpServletRequest request, @Context HttpServletResponse response) throws ServletException, IOException, URISyntaxException {
        String thisURL = request.getRequestURI();
        Principal principal = request.getUserPrincipal();

        // Redirection to the login page
        if (principal == null) {
            return Response.temporaryRedirect(new URI(getLoginURL(thisURL))).build();
        }
        
        // Getting the parameters and forwarding them to the JSP
        User user = null;
        try {
            user = userService.getLoggedUser();
        }
        catch(NotFoundException ex) {
            com.google.appengine.api.users.User systemUser = userService.getSystemUserService().getCurrentUser();
            user = new User();
            user.setName(systemUser.getNickname());
            user.setEmail(systemUser.getEmail());
            user = userService.get(userService.create(user).getId(), 0L);
        }

        // Forwarding to the console page
        request.setAttribute("user", user);
        request.setAttribute("logoutURL", getLogoutURL(getLoginURL(thisURL)));
        if (userService.isLoggedAdmin()) {
            request.setAttribute("users", userService.select(null, null, null));
        }
        request.getRequestDispatcher("/WEB-INF/templates/console.jsp").forward(request, response);
        return null; // TODO: Find a way to use the JAX-RS objects to forward the request,  with parameters, to the JSP
    }
    
    protected String getLoginURL(String thisURL) {
        return userService.getSystemUserService().createLoginURL(thisURL);
    }
    
    protected String getLogoutURL(String thisURL) {
        return userService.getSystemUserService().createLogoutURL(thisURL);
    }
}
