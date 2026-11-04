package org.ldv.todoapp2.repository

import org.ldv.todoapp2.model.entity.Tache
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TacheRepository : JpaRepository<Tache, Long> {
    fun findByUtilisateur_IdUtilisateur(idUtilisateur: Long): List<Tache>
    @Query("""
            SELECT t
            FROM Tache t
            JOIN FETCH t.utilisateur
            ORDER BY t.dateCreation DESC
    """)
    fun findAllAvecUtilisateur(): List<Tache>
}