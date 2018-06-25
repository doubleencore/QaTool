package com.tour.qatool.data

import com.tour.qatool.data.tables.json.JsonSchemaRelationship
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JsonRelationshipRepository : CrudRepository<JsonSchemaRelationship, String> {
}