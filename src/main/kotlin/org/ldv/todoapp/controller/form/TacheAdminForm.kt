package org.ldv.todoapp.controller.form

// Le backoffice reprend les champs d'une tâche et ajoute le choix du propriétaire.
class TacheAdminForm : TacheForm() {
    var utilisateurId: Long? = null
}
