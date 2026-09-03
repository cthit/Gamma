package it.chalmers.gamma.organization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class OrganizationTypesTest {
    @Test
    fun `organization names preserve existing generated names`() {
        assertEquals("digit2026", OrganizationName("digit2026").value)
        assertEquals("p-rit", OrganizationName("p-rit").value)
    }

    @Test
    fun `organization names reject unsafe or display text`() {
        listOf("IT", "digIT", "digit_2026", "<b>digit</b>").forEach { invalid ->
            assertFailsWith<IllegalArgumentException>(invalid) { OrganizationName(invalid) }
        }
    }

    @Test
    fun `organization names enforce exact length boundaries`() {
        assertEquals(3, OrganizationName("abc").value.length)
        assertEquals(30, OrganizationName("a".repeat(30)).value.length)
        assertFailsWith<IllegalArgumentException> { OrganizationName("ab") }
        assertFailsWith<IllegalArgumentException> { OrganizationName("a".repeat(31)) }
    }

    @Test
    fun `localized text and pretty names reject html`() {
        assertEquals("P.R.I.T.", PrettyName("P.R.I.T.").value)
        assertEquals("Ordförande", LocalizedText.of("Ordförande", "Chairman").sv.value)
        assertFailsWith<IllegalArgumentException> { PrettyName("<i>digIT</i>") }
        assertFailsWith<IllegalArgumentException> { LocalizedTextValue("a".repeat(2049)) }
    }

    @Test
    fun `pretty and localized text enforce every persisted boundary`() {
        assertEquals(2, PrettyName("ab").value.length)
        assertEquals(50, PrettyName("a".repeat(50)).value.length)
        assertFailsWith<IllegalArgumentException> { PrettyName("a") }
        assertFailsWith<IllegalArgumentException> { PrettyName("a".repeat(51)) }
        assertEquals("", LocalizedTextValue("").value)
        assertEquals(2048, LocalizedTextValue("a".repeat(2048)).value.length)
        assertFailsWith<IllegalArgumentException> { LocalizedTextValue("unsafe&text") }
    }

    @Test
    fun `super group types accept lowercase storage names only`() {
        assertEquals("committee", SuperGroupType("committee").value)
        assertEquals("abc", SuperGroupType("abc").value)
        assertEquals(30, SuperGroupType("a".repeat(30)).value.length)
        listOf("ab", "a".repeat(31), "Committee", "committee-2").forEach { value ->
            assertFailsWith<IllegalArgumentException>(value) { SuperGroupType(value) }
        }
    }

    @Test
    fun `email prefixes accept empty words and dotted words`() {
        assertEquals("", EmailPrefix("").value)
        assertEquals("chair", EmailPrefix("chair").value)
        assertEquals("vice.chair", EmailPrefix("vice.chair").value)
        listOf("two words", ".chair", "chair.", "chair..vice", "chair-name").forEach { value ->
            assertFailsWith<IllegalArgumentException>(value) { EmailPrefix(value) }
        }
    }

    @Test
    fun `post order cannot be negative`() {
        assertEquals(0, PostOrder(0).value)
        assertFailsWith<IllegalArgumentException> { PostOrder(-1) }
    }

    @Test
    fun `optional unofficial post name remains nullable`() {
        assertEquals(null, UnofficialPostName(null).value)
        assertEquals("root", UnofficialPostName("root").value)
        assertFailsWith<IllegalArgumentException> { UnofficialPostName("") }
        assertFailsWith<IllegalArgumentException> { UnofficialPostName("<b>root</b>") }
    }

    @Test
    fun `unofficial post names enforce the maximum length`() {
        assertEquals(50, UnofficialPostName("a".repeat(50)).value?.length)
        assertFailsWith<IllegalArgumentException> { UnofficialPostName("a".repeat(51)) }
    }

    @Test
    fun `organization identifiers round trip and generated ids are unique`() {
        val raw = "047ac437-a789-4cc5-bb6e-ba50efd7c509"

        assertEquals(raw, GroupId.parse(raw).value.toString())
        assertEquals(raw, SuperGroupId.parse(raw).value.toString())
        assertEquals(raw, PostId.parse(raw).value.toString())
        assertNotEquals(GroupId.generate(), GroupId.generate())
        assertNotEquals(SuperGroupId.generate(), SuperGroupId.generate())
        assertNotEquals(PostId.generate(), PostId.generate())
    }
}
