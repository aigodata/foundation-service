package com.foundation.service.basic.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foundation.service.basic.common.web.annotation.JsonParam;
import com.foundation.service.basic.domain.Role;
import com.foundation.service.basic.model.ResultModel;
import com.foundation.service.basic.service.RoleService;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/role")
public class RoleController {
	
	@Autowired
	private RoleService roleService;

	/**
	 * 添加角色
	 * @param name 角色名称
	 * @param description 角色描述
	 * @param permissionIds 权限ID集合
	 * @return
	 */
	@PostMapping
	public ResultModel add(@JsonParam String name, @JsonParam String description, @JsonParam Integer[] permissionIds) {
		Role role = new Role();
		role.setName(name);
		role.setDescription(description);
		roleService.add(role, permissionIds);
		return ResultModel.success(role);
	}

	@PutMapping("/{id}")
	public ResultModel update(@PathVariable Integer id, @JsonParam String name, @JsonParam String description,
			@JsonParam Integer[] permissionIds) {
		Role role = new Role();
		role.setId(id);
		role.setName(name);
		role.setDescription(description);
		int count = roleService.update(role, permissionIds);
		return ResultModel.success(Maps.immutableEntry("count", count));
	}

}
