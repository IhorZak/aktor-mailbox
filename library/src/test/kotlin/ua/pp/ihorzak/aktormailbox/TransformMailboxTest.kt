/*
 * Copyright 2023 Ihor Zakhozhyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.pp.ihorzak.aktormailbox

import org.junit.jupiter.api.Assertions.assertFalse
import org.mockito.Mockito.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * [TransformMailbox] unit tests.
 */
class TransformMailboxTest {
    @Test
    fun `isEmpty in initial state should return true`() {
        val mailbox = TransformMailbox<String, String> { input -> "Processed $input" }

        val result = mailbox.isEmpty

        assertTrue(result)
    }

    @Test
    fun `isEmpty after offer() should return false`() {
        val mailbox = TransformMailbox<String, String> { input -> "Processed $input" }
        mailbox.offer("Message 1")

        val result = mailbox.isEmpty

        assertFalse(result)
    }

    @Test
    fun `isEmpty after offer() and poll() should return true`() {
        val mailbox = TransformMailbox<String, String> { input -> "Processed $input" }
        mailbox.offer("Message 1")
        mailbox.poll()

        val result = mailbox.isEmpty

        assertTrue(result)
    }

    @Test
    fun `isEmpty after offer(), poll() and offer() should return false`() {
        val mailbox = TransformMailbox<String, String> { input -> "Processed $input" }
        mailbox.offer("Message 1")
        mailbox.poll()
        mailbox.offer("Message 2")

        val result = mailbox.isEmpty

        assertFalse(result)
    }

    @Test
    fun `offer() should call transform`() {
        val transform: (String) -> String = spy { input -> "Processed $input" }
        val mailbox = TransformMailbox(transform)
        val message = "Message 1"

        mailbox.offer(message)

        verify(transform, times(1)).invoke(message)
    }

    @Test
    fun `poll() in initial state should return null`() {
        val transform: (String) -> String = { input -> "Processed $input" }
        val mailbox = TransformMailbox(transform)

        val result = mailbox.poll()

        assertNull(result)
    }

    @Test
    fun `poll() after offer() should return transformed using transform value`() {
        val transform: (String) -> String = { input -> "Processed $input" }
        val mailbox = TransformMailbox(transform)
        val message = "Message 1"
        mailbox.offer(message)

        val result = mailbox.poll()

        assertEquals(
            expected = transform(message),
            actual = result,
        )
    }

    @Test
    fun `poll() after offer() and poll() should return null`() {
        val transform: (String) -> String = { input -> "Processed $input" }
        val mailbox = TransformMailbox(transform)
        val message = "Message 1"
        mailbox.offer(message)
        mailbox.poll()

        val result = mailbox.poll()

        assertNull(result)
    }
}