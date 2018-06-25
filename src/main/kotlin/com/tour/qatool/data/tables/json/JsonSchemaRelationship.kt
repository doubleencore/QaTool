package com.tour.qatool.data.tables.json

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER)
abstract class JsonSchemaRelationship(@field:ManyToOne(optional = false)
                                      var schemaParent: JsonSchema,

                                      @field:OneToOne(optional = false, mappedBy = "parentRelationship", cascade = arrayOf(CascadeType.ALL))
                                      var schemaChild: JsonSchema) /*: BaseIdEntity()*/ {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Entity
    @DiscriminatorValue(ARRAY_ITEM_DISCRIMINATOR.toString())
    class ArrayItem(arrayParent: JsonSchema.Array, schemaChild: JsonSchema, var arrayIndex: Int) : JsonSchemaRelationship(arrayParent, schemaChild) {

        var arrayParent: JsonSchema.Array
            get() = schemaParent as JsonSchema.Array
            set(schema) {
                schemaParent = schema
            }

        override fun toString(): String {
            return "${super.toString()}:ArrayItem(arrayIndex=$arrayIndex)"
        }
    }

    sealed class ObjectProperty(objectParent: JsonSchema.Object, schemaChild: JsonSchema) : JsonSchemaRelationship(objectParent, schemaChild) {

        var objectParent: JsonSchema.Object
            get() = schemaParent as JsonSchema.Object
            set(schema) {
                schemaParent = schema
            }

        @Entity
        @DiscriminatorValue(OBJECT_PROPERTY_DISCRIMINATOR.toString())
        class Regular(objectParent: JsonSchema.Object,
                      schemaChild: JsonSchema,

                      @field:Column(name = "object_key")
                      var key: String = "") : ObjectProperty(objectParent, schemaChild) {


            override fun toString(): String {
                return "${super.toString()}:ObjectProperty.Regular(key='$key')"
            }
        }

        @Entity
        @DiscriminatorValue(OBJECT_PATTERN_PROPERTY_DISCRIMINATOR.toString())
        class Pattern(objectParent: JsonSchema.Object,
                      schemaChild: JsonSchema,

                      @field:Column(name = "object_key")
                      var regexKey: String = "") : ObjectProperty(objectParent, schemaChild) {


            override fun toString(): String {
                return "${super.toString()}:ObjectProperty.Pattern(regexKey='$regexKey')"
            }
        }
    }

    override fun toString(): String {
        return "JsonSchemaRelationship(id=$id,parent=${schemaParent.id}, child=$schemaChild)"
    }

    companion object {
        const val ARRAY_ITEM_DISCRIMINATOR = 0
        const val OBJECT_PROPERTY_DISCRIMINATOR = 1
        const val OBJECT_PATTERN_PROPERTY_DISCRIMINATOR = 2
    }
}





