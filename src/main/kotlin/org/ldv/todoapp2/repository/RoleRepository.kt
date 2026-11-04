package org.ldv.todoapp2.repository

import org.ldv.todoapp2.model.entity.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {
    fun findByNom(nom: String): Role?
}

