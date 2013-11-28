package dderrien.ipaddresscontrol.service;

import javax.inject.Inject;

import dderrien.common.service.AbstractService;
import dderrien.ipaddresscontrol.dao.IPAddressDao;
import dderrien.ipaddresscontrol.model.IPAddress;

public class IPAddressService extends AbstractService<IPAddress> {

	@Inject
	public IPAddressService(IPAddressDao dao) {
		super(dao);
	}

}
