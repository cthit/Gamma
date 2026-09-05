package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class DeleteSuperGroupIntegrationTest {
    @Test
    fun `deletes an unused super group and its owned description`() =
        withGroupDatabase { database, queries ->
            val textCount =
                database.commitTransaction(
                    readOnly = true,
                ) { SharedLocalizedTextsTable.selectAll().count() }
            val id =
                CreateSuperGroup(database).create(
                    groupAdministrator,
                    NewSuperGroup(
                        OrganizationName("deleted-super-group"),
                        PrettyName("Deleted super group"),
                        SuperGroupType("committee"),
                        LocalizedText.of("Beskrivning", "Description"),
                    ),
                )
            DeleteSuperGroup(database).delete(groupAdministrator, id)
            assertNull(queries.superGroupDetails(id)?.superGroup)
            assertEquals(
                textCount,
                database.commitTransaction(readOnly = true) { SharedLocalizedTextsTable.selectAll().count() },
            )
        }

    @Test
    fun `competing deletions report exactly one committed removal`() =
        withGroupDatabase { database, queries ->
            val id =
                CreateSuperGroup(database).create(
                    groupAdministrator,
                    NewSuperGroup(
                        OrganizationName("competing-deletions"),
                        PrettyName("Competing deletions"),
                        SuperGroupType("committee"),
                        LocalizedText.of("Beskrivning", "Description"),
                    ),
                )
            val ready = CountDownLatch(2)
            Executors.newFixedThreadPool(2).use { workers ->
                val deletions =
                    (1..2).map {
                        workers.submit<OrganizationNotFound?> {
                            ready.countDown()
                            check(ready.await(10, TimeUnit.SECONDS))
                            try {
                                DeleteSuperGroup(database).delete(groupAdministrator, id)
                                null
                            } catch (missing: OrganizationNotFound) {
                                missing
                            }
                        }
                    }
                val outcomes = deletions.map { it.get(10, TimeUnit.SECONDS) }
                assertEquals(1, outcomes.count { it == null })
                assertEquals(1, outcomes.count { it is OrganizationNotFound })
            }
            assertNull(queries.superGroupDetails(id)?.superGroup)
        }

    @Test
    fun `denied missing and still used super groups cannot remove existing data`() =
        withGroupDatabase { database, queries ->
            val before = queries.listSuperGroups()
            val groups = queries.listGroups(existingSuperGroupId)
            val operation = DeleteSuperGroup(database)
            assertFailsWith<AccessDenied> { operation.delete(ordinaryGroupUser, existingSuperGroupId) }
            assertFailsWith<OrganizationNotFound> { operation.delete(groupAdministrator, SuperGroupId.generate()) }
            assertFailsWith<OrganizationConflict> { operation.delete(groupAdministrator, existingSuperGroupId) }
            assertEquals(before, queries.listSuperGroups())
            assertEquals(groups, queries.listGroups(existingSuperGroupId))
        }
}
