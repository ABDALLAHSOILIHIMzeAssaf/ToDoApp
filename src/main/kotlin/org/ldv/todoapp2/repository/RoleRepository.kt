package org.ldv.todoapp.model.repository

import org.ldv.todoapp2.model.entity.Role
import org.springframework.data.jpa.repository.JpaRepository


interface RoleRepository : JpaRepository<Role, Long> {

    /*
    fun fingById(id) {}
    fun findAll(){}
    fun findAllById(ids) {}
    fun existsById(id) {}
    fun count()
    */

    /*
    fun save(entity)
    fun saveAll(entities)
    */

    /*
    fun deleteById(id)
    fun delete(entity)
    fun deleteAllById(ids)
    fun deleteAll(entities)
    fun deleteAll()
    */



}