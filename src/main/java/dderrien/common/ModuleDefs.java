package dderrien.common;

import com.google.inject.Binder;
import com.google.inject.Module;

import dderrien.common.dao.UserDao;
import dderrien.common.resource.UserResource;
import dderrien.common.service.UserService;

public class ModuleDefs implements Module {

    public void configure(final Binder binder) {
        binder.bind(UserResource.class);

        binder.bind(UserService.class);

        binder.bind(UserDao.class);
    }
}