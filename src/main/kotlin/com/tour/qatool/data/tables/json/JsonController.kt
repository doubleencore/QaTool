package com.tour.qatool.data.tables.json

import com.tour.qatool.data.JsonRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("customers")
class JsonController(val repository: JsonRepository) {

    @GetMapping("")
    fun findAll() = repository.findAll()
}