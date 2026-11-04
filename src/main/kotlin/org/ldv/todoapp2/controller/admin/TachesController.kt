package org.ldv.todoapp2.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class TachesController {
    @GetMapping("/admin/taches")
    fun taches(): String {
        return "admin/taches"
    }
}