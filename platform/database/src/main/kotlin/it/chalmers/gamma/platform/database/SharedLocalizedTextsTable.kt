package it.chalmers.gamma.platform.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.datetime

object SharedLocalizedTextsTable : Table("g_text") {
    val id = javaUUID("text_id")
    val sv = varchar("sv", 2048)
    val en = varchar("en", 2048)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}
