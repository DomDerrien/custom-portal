package dderrien.resource;

import java.io.IOException;
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
    public void prepareConsole(@Context HttpServletRequest request, @Context HttpServletResponse response) throws ServletException, IOException {
        String thisURL = request.getRequestURI();
        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            String loginURL = getLoginURL(thisURL);
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", loginURL);
            response.sendRedirect(loginURL);
        }
        else {
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
    
            request.setAttribute("user", user);
            request.setAttribute("logoutURL", getLogoutURL(getLoginURL(thisURL)));
            
            if (userService.isLoggedAdmin()) {
                request.setAttribute("users", userService.select(null, null, null));
            }
            
            request.getRequestDispatcher("/templates/console.jsp").forward(request, response);
        }
    }
    
    protected String getLoginURL(String thisURL) {
        return UserServiceFactory.getUserService().createLoginURL(thisURL);
    }
    
    protected String getLogoutURL(String thisURL) {
        return UserServiceFactory.getUserService().createLogoutURL(thisURL);
    }
}
