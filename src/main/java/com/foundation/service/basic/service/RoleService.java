package com.foundation.service.basic.service;

import com.foundation.service.basic.domain.Role;

public interface RoleService {

	public Role add(Role role, Integer[] permissionIds);

	public int update(Role role, Integer[] permissionIds);

}
