package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.Role;
import com.cesizen.cesizen_back.entity.RoleName;
import com.cesizen.cesizen_back.repository.RoleRepository;
import com.cesizen.cesizen_back.service.RoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public Role getDefaultRole() {
        return findByName(RoleName.USER);
    }

    @Override
    @Transactional(readOnly = true)
    public Role getAdminRole() {
        return findByName(RoleName.ADMIN);
    }

    // -------------------------------------------------------------------------
    // PRIVÉ
    // -------------------------------------------------------------------------

    private Role findByName(RoleName roleName) {
        return roleRepository.findByRoleName(roleName.name())
                .orElseThrow(() -> new IllegalStateException(
                        "Rôle introuvable en base : " + roleName.name() + ". Vérifiez les données d'initialisation."
                ));
    }
}