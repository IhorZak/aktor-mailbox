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

import kotlin.test.*

/**
 * [PriorityMailbox] unit tests.
 */
class PriorityMailboxTest {
    @Test
    fun `isEmpty in initial state should return true`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)

        val result = mailbox.isEmpty

        assertTrue(result)
    }

    @Test
    fun `isEmpty after offer() should return false`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        mailbox.offer(1)

        val result = mailbox.isEmpty

        assertFalse(result)
    }

    @Test
    fun `isEmpty after offer() and poll() should return true`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        mailbox.offer(1)
        mailbox.poll()

        val result = mailbox.isEmpty

        assertTrue(result)
    }

    @Test
    fun `isEmpty after offer() and peek() should return false`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        mailbox.offer(1)
        mailbox.peek()

        val result = mailbox.isEmpty

        assertFalse(result)
    }

    @Test
    fun `isEmpty after offer(), poll() and offer() should return false`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        mailbox.offer(1)
        mailbox.poll()
        mailbox.offer(2)

        val result = mailbox.isEmpty

        assertFalse(result)
    }

    @Test
    fun `isFull in initial state should return false`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)

        val result = mailbox.isFull

        assertFalse(result)
    }

    @Test
    fun `isFull after offer() should return false`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        mailbox.offer(1)

        val result = mailbox.isFull

        assertFalse(result)
    }

    @Test
    fun `isFull after offer() and poll() should return false`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        mailbox.offer(1)
        mailbox.poll()

        val result = mailbox.isFull

        assertFalse(result)
    }

    @Test
    fun `isFull after offer() and peek() should return false`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        mailbox.offer(1)
        mailbox.peek()

        val result = mailbox.isFull

        assertFalse(result)
    }

    @Test
    fun `peek() in initial state should return null`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)

        val result = mailbox.peek()

        assertNull(result)
    }

    @Test
    fun `peek() after offer() should return value`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        val message = 1
        mailbox.offer(message)

        val result = mailbox.peek()

        assertEquals(
            expected = message,
            actual = result,
        )
    }

    @Test
    fun `peek() after offer() and peek() should return value`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        val message = 1
        mailbox.offer(message)
        mailbox.peek()

        val result = mailbox.peek()

        assertEquals(
            expected = message,
            actual = result,
        )
    }

    @Test
    fun `peek() after offer() and poll() should return null`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        val message = 1
        mailbox.offer(message)
        mailbox.poll()

        val result = mailbox.peek()

        assertNull(result)
    }

    @Test
    fun `peek() after offer(), offer() and offer() should return value according to comparator`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        val message1 = 3
        val message2 = 1
        val message3 = 2
        mailbox.offer(message1)
        mailbox.offer(message2)
        mailbox.offer(message3)

        val result = mailbox.peek()

        assertEquals(
            expected = minOf(message1, message2, message3),
            actual = result,
        )
    }

    @Test
    fun `poll() in initial state should return null`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)

        val result = mailbox.poll()

        assertNull(result)
    }

    @Test
    fun `poll() after offer() should return value`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        val message = 1
        mailbox.offer(message)

        val result = mailbox.poll()

        assertEquals(
            expected = message,
            actual = result,
        )
    }

    @Test
    fun `poll() after offer() and peek() should return value`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        val message = 1
        mailbox.offer(message)
        mailbox.peek()

        val result = mailbox.poll()

        assertEquals(
            expected = message,
            actual = result,
        )
    }

    @Test
    fun `poll() after offer() and poll() should return null`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        val message = 1
        mailbox.offer(message)
        mailbox.poll()

        val result = mailbox.poll()

        assertNull(result)
    }

    @Test
    fun `poll() after offer(), offer() and offer() should return values according to comparator`() {
        val mailbox = PriorityMailbox<Int>(Int::compareTo)
        val message1 = 3
        val message2 = 1
        val message3 = 2
        mailbox.offer(message1)
        mailbox.offer(message2)
        mailbox.offer(message3)

        val resultList = buildList {
            while (!mailbox.isEmpty) {
                add(mailbox.poll())
            }
        }

        assertEquals(
            expected = listOf(message1, message2, message3).sorted(),
            actual = resultList,
        )
    }
}