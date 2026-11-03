package org.ldv.todoapp2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TodoApp2Application

fun main(args: Array<String>) {
    runApplication<TodoApp2Application>(*args)
}
