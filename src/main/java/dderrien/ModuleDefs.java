package dderrien;

import com.google.inject.Binder;
import com.google.inject.Module;

import dderrien.dao.UserDao;
import dderrien.resource.UserResource;
import dderrien.service.UserService;

public class ModuleDefs implements Module {

	public void configure(final Binder binder) {
        binder.bind(UserResource.class);

        binder.bind(UserService.class);

        binder.bind(UserDao.class);
	}
}