package org.ldv.todoapp.controller.form

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

// Seules les propriétés proposées à la saisie sont reçues depuis le navigateur.
open class TacheForm {
    var titre: String = ""
    var description: String = ""

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var dateEcheance: LocalDate? = null

    // Proposé en modification personnelle et dans le backoffice.
    var terminee: Boolean = false
}
