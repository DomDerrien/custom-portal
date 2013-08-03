package dderrien.resource;

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

import com.google.appengine.api.users.UserServiceFactory;

import dderrien.exception.NotFoundException;
import dderrien.model.User;
import dderrien.service.UserService;

@Path("/console")
public class ConsoleResource {

    private UserService userService;
    
    @Inject
    public ConsoleResource(UserService userService) {
        this.userService = userService;
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response prepareConsole(@Context HttpServletRequest request, @Context HttpServletResponse response/*, @Context InternalDispatcher dispatcher*/) throws ServletException, IOException, URISyntaxException {
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
            com.google.appengine.api.users.User systemUser = UserServiceFactory.getUserService().getCurrentUser();
            user = new User();
            user.setName(systemUser.getNickname());
            user.setEmail(systemUser.getEmail());
            user = userService.get(userService.create(user).getId());
        }

        /* TODO: Understand how to register the JSP as a resource for the dispatcher! Otherwise this return 404...
        MockHttpRequest mockRequest = InternalDispatcher.createRequest("/templates/game.jsp", "GET");
        mockRequest.setAttribute("user", user);
        mockRequest.setAttribute("logoutURL", getLogoutURL(getLoginURL(thisURL)));
        if (userService.isLoggedAdmin()) {
            mockRequest.setAttribute("users", userService.select(null, null, null));
        }
        
        return dispatcher.getResponse(mockRequest);
        */

        // TODO: remove this legacy code once the registration of the JSP is OK
        request.setAttribute("user", user);
        request.setAttribute("logoutURL", getLogoutURL(getLoginURL(thisURL)));
        if (userService.isLoggedAdmin()) {
            request.setAttribute("users", userService.select(null, null, null));
        }
            
        request.getRequestDispatcher("/templates/console.jsp").forward(request, response);
        return null;
    }
    
    protected String getLoginURL(String thisURL) {
        return UserServiceFactory.getUserService().createLoginURL(thisURL);
    }
    
    protected String getLogoutURL(String thisURL) {
        return UserServiceFactory.getUserService().createLogoutURL(thisURL);
    }
}
