package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.entity.Role;

public interface RoleService {

    Role getDefaultRole();

    Role getAdminRole();
}
