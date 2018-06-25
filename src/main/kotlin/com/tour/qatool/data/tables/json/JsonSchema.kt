package com.tour.qatool.data.tables.json

import javax.persistence.*


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER)
abstract class JsonSchema() /*: BaseIdEntity()*/ {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    /*
     * This could be null in the case of a root or Array.additionalItems property
     */
    @field:OneToOne(fetch = FetchType.EAGER/*LAZY*/)
    @field:JoinColumn(name = "parent_relationship_id")
    var parentRelationship: JsonSchemaRelationship? = null

    abstract fun type(): SchemaType

    @Entity
    @DiscriminatorValue(STRING_INT.toString())
    class String(var regexPattern: kotlin.String = "") : JsonSchema() {
        override fun type() = SchemaType.STRING

        override fun toString(): kotlin.String {
            return "${super.toString()}, regexPattern=$regexPattern"
        }
    }

    @Entity
    @DiscriminatorValue(NUMBER_INT.toString())
    class Number(var min: Int,
                 var max: Int) : JsonSchema() {

        override fun type() = SchemaType.NUMBER
    }

    @Entity
    @DiscriminatorValue(ARRAY_INT.toString())
    class Array(@field:OneToMany(mappedBy = "schemaParent", cascade = arrayOf(CascadeType.ALL), orphanRemoval = true, targetEntity = JsonSchemaRelationship::class, fetch = FetchType.EAGER)
                @field:OrderBy("arrayIndex")
                var items: MutableList<JsonSchemaRelationship.ArrayItem> = mutableListOf(),

                var uniqueItems: Boolean = false,

                /*
                 * Represents an overall schema that must be adhered to by all items.
                 * This allows for more items than specifically listed in items as long as it conforms to this schema
                 *
                 * NOTE: This cascade is not yet properly setup so manual deletes are necessary
                 */
                @field:OneToOne(cascade = arrayOf(CascadeType.ALL))
                @field:JoinColumn(name = "additional_items_id")
                var additionalItems: JsonSchema? = null) : JsonSchema() {
        override fun type() = SchemaType.ARRAY

        fun addItem(jsonSchema: JsonSchema, index: Int? = null) {
            val arrayItem = JsonSchemaRelationship.ArrayItem(this, jsonSchema, index
                    ?: items.size)
            arrayItem.schemaChild.parentRelationship = arrayItem

            if (index != null && index < items.size) {
                items.add(index, arrayItem)
                reindexItems()
            } else {
                items.add(arrayItem)
            }
        }

        fun removeItemAt(index: Int) {
            if (index in items.indices) {
                items.removeAt(index)
                reindexItems()
            }
        }

        fun removeItem(item: JsonSchemaRelationship.ArrayItem) {
            val listItem = items.find { item == it }

            if (listItem != null && items.remove(listItem)) {
                reindexItems()
            } else { System.out.println("Could not locate: $item") }
        }


        private fun reindexItems() {
            items.forEachIndexed { index, arrayItem ->
                arrayItem.arrayIndex = index
            }
        }

        override fun toString(): kotlin.String {
            return "${super.toString()}, items.size=${items.count()} items=$items"
        }
    }

    @Entity
    @DiscriminatorValue(OBJECT_INT.toString())
    class Object(@field:ElementCollection(fetch = FetchType.EAGER)
                 @field:CollectionTable(name = "RequiredKeys", joinColumns = arrayOf(JoinColumn(name = "key_id")))
                 var requiredKeys: MutableSet<@JvmSuppressWildcards kotlin.String> = mutableSetOf()) : JsonSchema() {
        override fun type() = SchemaType.OBJECT

        @field:OneToMany(mappedBy = "schemaParent", cascade = arrayOf(CascadeType.ALL), orphanRemoval = true, targetEntity = JsonSchemaRelationship::class, fetch = FetchType.EAGER)
        private var allProperties: MutableList<JsonSchemaRelationship.ObjectProperty> = mutableListOf()

        @field:Transient
        var properties: List<JsonSchemaRelationship.ObjectProperty.Regular> = mutableListOf()
            get() = allProperties.filterIsInstance<JsonSchemaRelationship.ObjectProperty.Regular>()

        @field:Transient
        var patternProperties: List<JsonSchemaRelationship.ObjectProperty.Pattern> = mutableListOf()
            get() = allProperties.filterIsInstance<JsonSchemaRelationship.ObjectProperty.Pattern>()

        /*
         * Note: This function will delete the old schema if it replaces something
         */
        fun putRegularProperty(key: kotlin.String, jsonSchema: JsonSchema) {
            val newProperty = JsonSchemaRelationship.ObjectProperty.Regular(this, jsonSchema, key)
            putProperty(key, newProperty)
        }

        /*
         * Note: This function will delete the old schema if it replaces something
         */
        fun putPatternProperty(regexKey: kotlin.String, jsonSchema: JsonSchema) {
            val newProperty = JsonSchemaRelationship.ObjectProperty.Pattern(this, jsonSchema, regexKey)
            putProperty(regexKey, newProperty)
        }

        private fun putProperty(key: kotlin.String, newProperty: JsonSchemaRelationship.ObjectProperty) {
            allProperties.removeIf {
                it is JsonSchemaRelationship.ObjectProperty.Pattern && newProperty is JsonSchemaRelationship.ObjectProperty.Pattern && it.regexKey == key ||
                        it is JsonSchemaRelationship.ObjectProperty.Regular && newProperty is JsonSchemaRelationship.ObjectProperty.Regular && it.key == key
            }
            newProperty.schemaChild.parentRelationship = newProperty
            allProperties.add(newProperty)
        }
    }

    override fun toString(): kotlin.String {
        return "id=$id type=${javaClass.simpleName}"
    }

    companion object {
        const val STRING_INT = 0
        const val NUMBER_INT = 1
        const val ARRAY_INT = 2
        const val OBJECT_INT = 3

        enum class SchemaType(val value: Int) {
            STRING(STRING_INT),
            NUMBER(NUMBER_INT),
            ARRAY(ARRAY_INT),
            OBJECT(OBJECT_INT);
        }
    }
}








