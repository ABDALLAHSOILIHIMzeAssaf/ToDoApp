package org.ldv.todoapp2.controller.admin

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UtilisateursController {
    @GetMapping("/admin/utilisateurs")
    fun utilisateurs(): String {
        return "admin/utilisateurs"
    }
}