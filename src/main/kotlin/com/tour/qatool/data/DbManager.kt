package com.tour.qatool.data

import com.mysql.cj.jdbc.MysqlDataSource
import sun.util.resources.TimeZoneNames
import java.sql.DriverManager
import javax.sql.CommonDataSource
import java.io.FileInputStream
import java.io.File
import java.util.*


object Db {
    //const val DB_URL = "jdbc:mysql://localhost/qatool"
    //const val DRIVER = "com.mysql.jdbc.Driver"
    const val USER = "root"
    const val PASS = "qa12321qa"


//    val INSTANCE: KotlinEntityDataStore<Persistable> by lazy {
//        val dataSource = MysqlDataSource().apply {
//            serverName = "localhost"
//            portNumber = 3306
//            user = USER
//            setPassword(PASS)
//            databaseName = "qatool_db"
//        }
//        val model = Models.DEFAULT
//        val configuration = KotlinConfiguration(model, dataSource)
//        configuration.useDefaultLogging
//        System.out.println(SchemaModifier(configuration).tableCreateStatement(JsonNodeEntity.`$TYPE`, TableCreationMode.DROP_CREATE))
//        SchemaModifier(configuration).createTables(TableCreationMode.DROP_CREATE)
//
//        KotlinEntityDataStore<Persistable>(configuration)
//    }

}