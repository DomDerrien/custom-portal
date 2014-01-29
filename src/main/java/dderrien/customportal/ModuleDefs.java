package dderrien.customportal;

import com.google.inject.Binder;
import com.google.inject.Module;

import dderrien.customportal.dao.CategoryDao;
import dderrien.customportal.dao.LinkDao;
import dderrien.customportal.resource.CategoryResource;
import dderrien.customportal.resource.ConsoleResource;
import dderrien.customportal.resource.LinkResource;
import dderrien.customportal.service.CategoryService;
import dderrien.customportal.service.LinkService;

public class ModuleDefs implements Module {

    public void configure(final Binder binder) {
        binder.bind(ConsoleResource.class);

        binder.bind(CategoryResource.class);
        binder.bind(LinkResource.class);

        binder.bind(CategoryService.class);
        binder.bind(LinkService.class);

        binder.bind(CategoryDao.class);
        binder.bind(LinkDao.class);
    }
}