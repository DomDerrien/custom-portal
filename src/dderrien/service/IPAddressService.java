package dderrien.service;

import javax.inject.Inject;

import dderrien.dao.IPAddressDao;
import dderrien.model.IPAddress;

public class IPAddressService extends AbstractService<IPAddress> {

	@Inject
	public IPAddressService(IPAddressDao dao) {
		super(dao);
	}

}
