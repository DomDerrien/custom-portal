package dderrien;

import com.google.inject.Binder;
import com.google.inject.Module;

import dderrien.dao.CategoryDao;
import dderrien.dao.LinkDao;
import dderrien.dao.UserDao;
import dderrien.resource.CategoryResource;
import dderrien.resource.ConsoleResource;
import dderrien.resource.LinkResource;
import dderrien.resource.UserResource;
import dderrien.service.CategoryService;
import dderrien.service.LinkService;
import dderrien.service.UserService;

public class CustomPortalModule implements Module {

	public void configure(final Binder binder) {
        binder.bind(ConsoleResource.class);

        binder.bind(UserResource.class);
        binder.bind(CategoryResource.class);
        binder.bind(LinkResource.class);

        binder.bind(UserService.class);
        binder.bind(CategoryService.class);
        binder.bind(LinkService.class);

        binder.bind(UserDao.class);
        binder.bind(CategoryDao.class);
        binder.bind(LinkDao.class);
	}
}