package dderrien.ipaddresscontrol;

import com.google.inject.Binder;
import com.google.inject.Module;

import dderrien.ipaddresscontrol.dao.IPAddressDao;
import dderrien.ipaddresscontrol.resource.IPAddressResource;
import dderrien.ipaddresscontrol.service.IPAddressService;

public class ModuleDefs implements Module {

	public void configure(final Binder binder) {
        binder.bind(IPAddressResource.class);

        binder.bind(IPAddressService.class);

        binder.bind(IPAddressDao.class);
	}
}