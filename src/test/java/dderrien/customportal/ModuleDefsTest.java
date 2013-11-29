package dderrien.customportal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.google.inject.Binder;

import dderrien.customportal.dao.CategoryDao;
import dderrien.customportal.dao.LinkDao;
import dderrien.customportal.resource.CategoryResource;
import dderrien.customportal.resource.ConsoleResource;
import dderrien.customportal.resource.LinkResource;
import dderrien.customportal.service.CategoryService;
import dderrien.customportal.service.LinkService;

public class ModuleDefsTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testConfigure() {
		Binder binder = mock(Binder.class);
		new ModuleDefs().configure(binder);
		
		verify(binder, times(1)).bind(CategoryDao.class);
		verify(binder, times(1)).bind(CategoryService.class);
		verify(binder, times(1)).bind(CategoryResource.class);

		verify(binder, times(1)).bind(LinkDao.class);
		verify(binder, times(1)).bind(LinkService.class);
		verify(binder, times(1)).bind(LinkResource.class);

		verify(binder, times(1)).bind(ConsoleResource.class);

		verify(binder, times(7)).bind(any(Class.class));
	}
}
