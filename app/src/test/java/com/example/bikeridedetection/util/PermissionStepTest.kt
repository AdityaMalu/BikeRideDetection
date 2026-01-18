package com.example.bikeridedetection.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionStepTest {
    @Test
    fun `RuntimePermission_createsWithCorrectProperties`() {
        val permissions = listOf("permission1", "permission2")
        val title = "Test Title"
        val message = "Test Message"

        val step =
            PermissionStep.RuntimePermission(
                permissions = permissions,
                rationaleTitle = title,
                rationaleMessage = message,
            )

        assertEquals(permissions, step.permissions)
        assertEquals(title, step.rationaleTitle)
        assertEquals(message, step.rationaleMessage)
    }

    @Test
    fun `RoleRequest_createsWithCorrectProperties`() {
        val role = "test_role"
        val title = "Test Title"
        val message = "Test Message"

        val step =
            PermissionStep.RoleRequest(
                role = role,
                rationaleTitle = title,
                rationaleMessage = message,
            )

        assertEquals(role, step.role)
        assertEquals(title, step.rationaleTitle)
        assertEquals(message, step.rationaleMessage)
    }

    @Test
    fun `RuntimePermission_isPermissionStep`() {
        val step =
            PermissionStep.RuntimePermission(
                permissions = listOf("permission"),
                rationaleTitle = "Title",
                rationaleMessage = "Message",
            )

        assertTrue(step is PermissionStep)
    }

    @Test
    fun `RoleRequest_isPermissionStep`() {
        val step =
            PermissionStep.RoleRequest(
                role = "role",
                rationaleTitle = "Title",
                rationaleMessage = "Message",
            )

        assertTrue(step is PermissionStep)
    }

    @Test
    fun `RuntimePermission_emptyPermissionsList_isValid`() {
        val step =
            PermissionStep.RuntimePermission(
                permissions = emptyList(),
                rationaleTitle = "Title",
                rationaleMessage = "Message",
            )

        assertTrue(step.permissions.isEmpty())
    }

    @Test
    fun `RuntimePermission_multiplePermissions_preservesOrder`() {
        val permissions = listOf("first", "second", "third")

        val step =
            PermissionStep.RuntimePermission(
                permissions = permissions,
                rationaleTitle = "Title",
                rationaleMessage = "Message",
            )

        assertEquals("first", step.permissions[0])
        assertEquals("second", step.permissions[1])
        assertEquals("third", step.permissions[2])
    }

    @Test
    fun `RuntimePermission_equality_worksCorrectly`() {
        val step1 =
            PermissionStep.RuntimePermission(
                permissions = listOf("permission"),
                rationaleTitle = "Title",
                rationaleMessage = "Message",
            )
        val step2 =
            PermissionStep.RuntimePermission(
                permissions = listOf("permission"),
                rationaleTitle = "Title",
                rationaleMessage = "Message",
            )

        assertEquals(step1, step2)
    }

    @Test
    fun `RoleRequest_equality_worksCorrectly`() {
        val step1 =
            PermissionStep.RoleRequest(
                role = "role",
                rationaleTitle = "Title",
                rationaleMessage = "Message",
            )
        val step2 =
            PermissionStep.RoleRequest(
                role = "role",
                rationaleTitle = "Title",
                rationaleMessage = "Message",
            )

        assertEquals(step1, step2)
    }

    @Test
    fun `RuntimePermission_copy_createsNewInstance`() {
        val original =
            PermissionStep.RuntimePermission(
                permissions = listOf("permission"),
                rationaleTitle = "Title",
                rationaleMessage = "Message",
            )

        val copy = original.copy(rationaleTitle = "New Title")

        assertEquals("New Title", copy.rationaleTitle)
        assertEquals("Title", original.rationaleTitle)
    }

    @Test
    fun `RoleRequest_copy_createsNewInstance`() {
        val original =
            PermissionStep.RoleRequest(
                role = "role",
                rationaleTitle = "Title",
                rationaleMessage = "Message",
            )

        val copy = original.copy(role = "new_role")

        assertEquals("new_role", copy.role)
        assertEquals("role", original.role)
    }
}
