package dderrien.dao;

import com.googlecode.objectify.ObjectifyService;

import dderrien.model.User;
import dderrien.util.IntrospectionHelper;

public class UserDao extends AbstractDao<User> {

    static {
        ObjectifyService.register(IntrospectionHelper.getFirstTypeArgument(UserDao.class));
    }

    public UserDao () {
        super();
    }
}