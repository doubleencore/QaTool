package com.tour.qatool.data

import com.tour.qatool.data.tables.json.JsonSchema
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JsonRepository : CrudRepository<JsonSchema, String> {
}


