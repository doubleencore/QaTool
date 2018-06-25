package com.tour.qatool

import com.tour.qatool.data.JsonRelationshipRepository
import com.tour.qatool.data.JsonRepository
import com.tour.qatool.data.tables.json.JsonSchema
import com.tour.qatool.workspace.QaToolWorkspace
import javafx.stage.Stage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import tornadofx.*
import kotlin.reflect.KClass


@SpringBootApplication
open class QaToolApp : App(QaToolWorkspace::class) {
    lateinit var springContext: ConfigurableApplicationContext

    @Autowired
    lateinit var jsonSchemaRepository: JsonRepository

    @Autowired
    lateinit var jsonRelationshipRepository: JsonRelationshipRepository

    override fun init() {
        super.init()
        springContext = SpringApplication.run(QaToolApp::class.java)
        springContext.autowireCapableBeanFactory.autowireBean(this)
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>): T = springContext.getBean(type.java)
        }
    }

    override fun start(stage: Stage) {
        super.start(stage)

        stage.scene.stylesheets.add(QaToolApp::class.java.getResource("file_tree_styles.css").toExternalForm() )

    }


    override fun stop() {
        super.stop()
        springContext.close()
    }


}

