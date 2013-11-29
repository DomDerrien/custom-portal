package dderrien.ipaddresscontrol;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.google.inject.Binder;

import dderrien.ipaddresscontrol.dao.IPAddressDao;
import dderrien.ipaddresscontrol.resource.IPAddressResource;
import dderrien.ipaddresscontrol.service.IPAddressService;

public class ModuleDefsTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testConfigure() {
		Binder binder = mock(Binder.class);
		new ModuleDefs().configure(binder);
		
		verify(binder, times(1)).bind(IPAddressDao.class);
		verify(binder, times(1)).bind(IPAddressService.class);
		verify(binder, times(1)).bind(IPAddressResource.class);

		verify(binder, times(3)).bind(any(Class.class));
	}
}
