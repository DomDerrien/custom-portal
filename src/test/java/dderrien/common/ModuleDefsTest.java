package dderrien.common;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.google.inject.Binder;

import dderrien.common.dao.UserDao;
import dderrien.common.resource.UserResource;
import dderrien.common.service.UserService;

public class ModuleDefsTest {

	@Test
	public void testConfigure() {
		Binder binder = mock(Binder.class);
		new ModuleDefs().configure(binder);
		
		verify(binder, times(1)).bind(UserDao.class);
		verify(binder, times(1)).bind(UserService.class);
		verify(binder, times(1)).bind(UserResource.class);
	}

}
