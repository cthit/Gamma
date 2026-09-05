package it.chalmers.gamma.platform.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.util.PropertyElf

/** The bean property names accepted by the Hikari version used by the database adapter. */
object HikariConfigurationProperties {
    val names: Set<String> = PropertyElf.getPropertyNames(HikariConfig::class.java).toSet()
}
