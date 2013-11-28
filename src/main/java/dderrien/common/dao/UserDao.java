package dderrien.common.dao;

import com.googlecode.objectify.ObjectifyService;

import dderrien.common.model.User;
import dderrien.common.util.IntrospectionHelper;

public class UserDao extends AbstractDao<User> {

    static {
        ObjectifyService.register(IntrospectionHelper.getFirstTypeArgument(UserDao.class));
    }

    public UserDao () {
        super();
    }
}