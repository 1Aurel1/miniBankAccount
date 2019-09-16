package com.example.minibankaccount.repository;

import com.example.minibankaccount.model.role.Role;
import com.example.minibankaccount.model.role.RoleName;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}

