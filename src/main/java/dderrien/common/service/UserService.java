package dderrien.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.appengine.api.users.UserServiceFactory;

import dderrien.common.dao.UserDao;
import dderrien.common.exception.ConflictException;
import dderrien.common.exception.NotFoundException;
import dderrien.common.exception.UnauthorizedException;
import dderrien.common.model.User;

public class UserService extends AbstractService<User> {

    @Inject
    public UserService(UserDao dao) {
        super(dao);
    }
    
    public com.google.appengine.api.users.UserService getSystemUserService() {
    	return UserServiceFactory.getUserService();
    }

    public User getLoggedUser() {
        com.google.appengine.api.users.User currentUser = getSystemUserService().getCurrentUser();
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
        return getSystemUserService().isUserAdmin();
    }
}