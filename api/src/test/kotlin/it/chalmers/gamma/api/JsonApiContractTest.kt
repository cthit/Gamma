package it.chalmers.gamma.api

import it.chalmers.gamma.apiaccess.views.AccountScaffoldGroup
import it.chalmers.gamma.apiaccess.views.AccountScaffoldPost
import it.chalmers.gamma.apiaccess.views.AccountScaffoldSuperGroup
import it.chalmers.gamma.apiaccess.views.AccountScaffoldUser
import it.chalmers.gamma.apiaccess.views.AccountScaffoldUserPost
import it.chalmers.gamma.apiaccess.views.AllowListAddedResponse
import it.chalmers.gamma.apiaccess.views.ApiError
import it.chalmers.gamma.apiaccess.views.ApiUser
import it.chalmers.gamma.apiaccess.views.InfoBlobMember
import it.chalmers.gamma.apiaccess.views.InfoBlobPost
import it.chalmers.gamma.apiaccess.views.InfoBlobResponse
import it.chalmers.gamma.apiaccess.views.InfoBlobSuperGroup
import it.chalmers.gamma.apiaccess.views.InfoBlobSuperGroupDetails
import it.chalmers.gamma.apiaccess.views.InfoGroup
import it.chalmers.gamma.apiaccess.views.InfoPost
import it.chalmers.gamma.apiaccess.views.InfoSuperGroup
import it.chalmers.gamma.apiaccess.views.InfoUserGroup
import it.chalmers.gamma.apiaccess.views.InfoUserResponse
import it.chalmers.gamma.oauth.views.ClientApiGroup
import it.chalmers.gamma.oauth.views.ClientApiMembership
import it.chalmers.gamma.oauth.views.ClientApiPost
import it.chalmers.gamma.oauth.views.ClientApiSuperGroup
import it.chalmers.gamma.oauth.views.ClientApiUser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonApiContractTest {
    @Test
    fun `client v1 responses match the released Jackson contract`() {
        val superGroup = ClientApiSuperGroup(SUPER_GROUP_ID, "digit", "digIT", "committee", "Svenska", "English")
        val user = ClientApiUser("mscott", "Boss", "Michael", "Scott", USER_ID, 2005)
        val post = ClientApiPost(POST_ID, 4, "Ordförande", "President")

        assertGolden(
            "client-v1.json",
            buildJsonObject {
                put(
                    "groups",
                    JSON.encodeToJsonElement(listOf(ClientApiGroup(GROUP_ID, "digit2026", "digIT2026", superGroup))),
                )
                put("superGroups", JSON.encodeToJsonElement(listOf(superGroup)))
                put("users", JSON.encodeToJsonElement(listOf(user)))
                put("user", JSON.encodeToJsonElement(user))
                put(
                    "memberships",
                    JSON.encodeToJsonElement(
                        listOf(ClientApiMembership(GROUP_ID, "digit2026", "digIT2026", superGroup, post)),
                    ),
                )
                put("authorities", JSON.encodeToJsonElement(listOf("admin")))
                put("notFound", JSON.encodeToJsonElement(ApiError(404, "Not Found", "User Not Found Or Unauthorized")))
            },
        )
    }

    @Test
    fun `info v1 responses match the released Jackson contract`() {
        val user = ApiUser("mscott", "Boss", "Michael", "Scott", USER_ID, 2005)
        val superGroup = InfoSuperGroup(SUPER_GROUP_ID, 7, "digit", "digIT", "committee", "Svenska", "English")
        val group = InfoGroup(GROUP_ID, "digit2026", "digIT2026", superGroup, 3)
        val post = InfoPost(POST_ID, 4, "Ordförande", "President", "digit", 0)
        val blobSuperGroup =
            InfoBlobSuperGroupDetails(SUPER_GROUP_ID, "digit", "digIT", "committee", "Svenska", "English")

        assertGolden(
            "info-v1.json",
            buildJsonObject {
                put("user", JSON.encodeToJsonElement(InfoUserResponse(user, listOf(InfoUserGroup(group, post)))))
                put(
                    "blob",
                    JSON.encodeToJsonElement(
                        listOf(
                            InfoBlobResponse(
                                "committee",
                                listOf(
                                    InfoBlobSuperGroup(
                                        blobSuperGroup,
                                        hasBanner = true,
                                        hasAvatar = false,
                                        listOf(
                                            InfoBlobMember(
                                                user,
                                                InfoBlobPost(POST_ID, "Ordförande", "President", "digit"),
                                                null,
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                )
                put("notFound", JSON.encodeToJsonElement(ApiError(404, "Not Found", "USER_NOT_FOUND_RESPONSE")))
            },
        )
    }

    @Test
    fun `account scaffold v1 responses match the released Jackson contract`() {
        val post = AccountScaffoldPost(POST_ID, "Ordförande", "President", "digit")
        val user = AccountScaffoldUser("mscott@example.org", "mscott", "Michael", "Scott", "Boss")

        assertGolden(
            "account-scaffold-v1.json",
            buildJsonObject {
                put(
                    "supergroups",
                    JSON.encodeToJsonElement(
                        listOf(
                            AccountScaffoldSuperGroup(
                                "digit",
                                "digIT",
                                "committee",
                                listOf(
                                    AccountScaffoldGroup(
                                        "digit2026",
                                        "digIT2026",
                                        listOf(AccountScaffoldUserPost(post, user)),
                                    ),
                                ),
                                useManagedAccount = true,
                            ),
                        ),
                    ),
                )
                put("users", JSON.encodeToJsonElement(listOf(user)))
            },
        )
    }

    @Test
    fun `allow list v1 responses match the released Jackson contract`() {
        assertGolden(
            "allow-list-v1.json",
            buildJsonObject {
                put("added", JSON.encodeToJsonElement(AllowListAddedResponse("ALLOW_LIST_ADDED_RESPONSE", 200)))
                put("partialFailures", JSON.encodeToJsonElement(listOf("duplicate-cid")))
                put("forbidden", JSON.encodeToJsonElement(ApiError(403, "Forbidden", "FORBIDDEN")))
            },
        )
    }

    private fun assertGolden(
        resourceName: String,
        actual: JsonElement,
    ) {
        val expectedText =
            checkNotNull(javaClass.getResource("/contracts/$resourceName")) {
                "Missing JSON contract resource $resourceName"
            }.readText()
        assertEquals(JSON.parseToJsonElement(expectedText), actual)
    }

    companion object {
        private val JSON = Json { explicitNulls = true }
        private const val USER_ID = "88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f"
        private const val SUPER_GROUP_ID = "aed27030-ad90-4526-855c-1e909b1dcecb"
        private const val GROUP_ID = "2abe2264-fd61-4899-ba46-851279d85229"
        private const val POST_ID = "20000000-0000-0000-0000-000000000001"
    }
}
