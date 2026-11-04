package org.ldv.todoapp2.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MesInfosController {
    @GetMapping("/mes-infos")
    fun mesInfos(): String {
        return "mes-infos"
    }
}