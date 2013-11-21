package dderrien.ipaddresscontrol.service;

import javax.inject.Inject;

import dderrien.ipaddresscontrol.dao.IPAddressDao;
import dderrien.ipaddresscontrol.model.IPAddress;
import dderrien.service.AbstractService;

public class IPAddressService extends AbstractService<IPAddress> {

	@Inject
	public IPAddressService(IPAddressDao dao) {
		super(dao);
	}

}
