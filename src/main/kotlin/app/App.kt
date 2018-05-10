package app

import org.jetbrains.exposed.sql.SchemaUtils
import java.io.File


fun main(args: Array<String>) {
    initConfigs(if (args.isNotEmpty() && File(args[0]).isDirectory) args[0] else null)
    //    init()
    //    transactionWithLogger {
    //        val tables = arrayOf(Users, Animals)
    //        //        println(Animals.ddl)
    //        println(IntEntity::class.nestedClasses)
    //        SchemaUtils.createMissingTablesAndColumns()
    //        //        //SchemaUtils.create(*tablesList)
    //        //        with(TransactionManager.current()) {
    //        //            val statements = SchemaUtils.createStatements(*tables)
    //        //            for (statement in statements) {
    //        //                println(statement)
    //        //            }
    //        //        }
    //    }
    val tables = getManagedTables()
    transactionWithLogger {
        SchemaUtils.drop(*tables)
        SchemaUtils.create(*tables)
    }
}

/**
 * Set up the structure
 */
fun init() {
    val file = File("migrations")
    if (file.exists() && file.isDirectory) {
        println("Migrations dir exists already!")
        return
    }

    file.mkdirs()
}