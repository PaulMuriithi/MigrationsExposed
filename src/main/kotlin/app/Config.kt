package app

import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.collections.HashMap

private val TAG = "Config"
var configsDir = "config/"
var appConfigsPath = "${configsDir}app.properties"
var debugMode = true
val config: HashMap<String, String> by lazy { conf() }

fun initConfigs(configDir: String?) {
    if (!configDir.isNullOrBlank()) {
        //configs path
        var configDirPath = configDir
        if (!configDir!!.endsWith(File.separator)) {
            configDirPath += File.separator
        }
        appConfigsPath = "${configDirPath}app.properties"
    }

    // setup debug/otherwise
    val debug = config["APP_DEBUG"]
    debugMode = (!debug.isNullOrBlank() && debug == "1")

    //init db
    initDb(config)
}

private fun conf(): HashMap<String, String> {
    val configs: HashMap<String, String> = HashMap()
    try {
        FileInputStream(appConfigsPath).use {
            val props = Properties()
            props.load(it)
            props.forEach { k, v -> configs[k.toString()] = v.toString() }
        }
    } catch (e: Exception) {
        throw Exception("Exception getting configs:\n${e.localizedMessage}")
    }
    return configs
}

fun <K, V> Map<K, V>.v(key: K): V = try {
    getValue(key)
} catch (e: NoSuchElementException) {
    throw NoSuchElementException("Config $key is missing! Check config.properties or the database configs")
}

fun initDb(configs: Map<String, String>) {
    val dataSource = HikariDataSource()
    dataSource.jdbcUrl = configs.v("DB_URL")
    dataSource.username = configs.v("DB_USERNAME")
    dataSource.password = configs.v("DB_PASSWORD")
    Database.connect(dataSource)
}
