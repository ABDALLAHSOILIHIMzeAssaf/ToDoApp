package org.ldv.todoapp.model.repository

import org.ldv.todoapp.model.entity.Tache
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TacheRepository : JpaRepository<Tache, Long> {

    fun countByUtilisateur_IdUtilisateur(idUtilisateur: Long): Long


    @Query("SELECT t FROM Tache t JOIN FETCH t.utilisateur WHERE t.idTache = :id")
    fun findByIdAvecUtilisateur(@Param("id") id: Long): Tache?

    fun findByIdTacheAndUtilisateur_IdUtilisateur(
        idTache: Long,
        idUtilisateur: Long
    ): Tache?

    fun findByReferenceDemo(referenceDemo: String): Tache?

    fun findFirstByUtilisateur_IdUtilisateurAndTitreAndReferenceDemoIsNullOrderByIdTacheAsc(
        idUtilisateur: Long,
        titre: String
    ): Tache?

    fun findByUtilisateur_IdUtilisateur(
        idUtilisateur: Long
    ): List<Tache>

    @Query("""
        SELECT t
        FROM Tache t
        JOIN FETCH t.utilisateur
        ORDER BY t.dateCreation DESC
    """)
    fun findAllAvecUtilisateur(): List<Tache>
}
