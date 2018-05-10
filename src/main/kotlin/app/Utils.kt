package app

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.reflections.Reflections

fun <T> transactionWithLogger(statement: Transaction.() -> T): T {
    return transaction {
        logger.addLogger(StdOutSqlLogger)
        statement()
    }
}

fun getManagedTables(): Array<Table> {
    return (if (config.v("USE_ANNOTATIONS") == "1") getManagedClassesByAnnotation() else getManagedClassesBySubType()).map {
        it.kotlin.objectInstance as Table
    }.toTypedArray()
}

private fun getManagedClassesByAnnotation(): List<Class<*>> {
    return Reflections().getTypesAnnotatedWith(Managed::class.java).filter { it is Table }
}

private fun getManagedClassesBySubType(): List<Class<*>> {
    //filter the jetbrains ones
    return Reflections().getSubTypesOf(Table::class.java).filterNot { it.`package`.name.contains("org.jetbrains.exposed") }
}
