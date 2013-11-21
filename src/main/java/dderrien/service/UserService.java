package dderrien.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.appengine.api.users.UserServiceFactory;

import dderrien.dao.UserDao;
import dderrien.exception.ConflictException;
import dderrien.exception.NotFoundException;
import dderrien.exception.UnauthorizedException;
import dderrien.model.User;

public class UserService extends AbstractService<User> {

    @Inject
    public UserService(UserDao dao) {
        super(dao);
    }
    
    public User getLoggedUser() {
        com.google.appengine.api.users.User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Missing logged user information");
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", currentUser.getEmail());
        List<User> users = super.selectSilent(params, null, null);
        if (users.size() == 0) {
            throw new NotFoundException("Logged user email is unknown");
        }
        if (1 < users.size()) {
            throw new ConflictException("Logged user email matches with: " + users.size() + " user records");
        }

        return users.get(0);
    }
    
    public boolean isLoggedAdmin() {
        return UserServiceFactory.getUserService().isUserAdmin();
    }
}