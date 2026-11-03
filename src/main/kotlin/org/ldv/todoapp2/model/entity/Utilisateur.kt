package org.ldv.todoapp2.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "utilisateur")

open class Utilisateur(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    open var idTache: Long? = null,

    @Column(name = "nom", nullable = false, length = 100)
    open var nom: String = "",

    @Column(name = "prenom", nullable = false, length = 100)
    open var prenom: String = "",

    @Column(name = "email", nullable = false, length = 255)
    open var email: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "fkid_role", referencedColumnName = "id_role", nullable = false)
    open var role: Role? = null
)