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

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Timeout
import org.mockito.kotlin.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

private const val TEST_TIMEOUT_VALUE_SECONDS = 10L

/**
 * [CoroutineScope] extensions unit tests.
 */
@OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalStdlibApi::class,
)
class CoroutineScopeExtensionTest {
    @Test
    @Timeout(
        value = TEST_TIMEOUT_VALUE_SECONDS,
        unit = TimeUnit.SECONDS,
    )
    fun `Messages which are sent to aktor() SendChannel should be processed`() = runTest {
        val processedMessageChannel = Channel<Int>(capacity = Channel.UNLIMITED)
        val sendChannel = aktor(
            mailbox = createStubMailbox(),
        ) { message ->
            processedMessageChannel.send(message)
        }
        val messageList = listOf(
            1000,
            1001,
            1002,
            1003,
            1004,
            1005,
            1006,
            1007,
            1008,
            1009,
        )
        val processedMessageList: MutableList<Int> = LinkedList()

        for (message in messageList) {
            sendChannel.send(message)
        }
        while (processedMessageList.size < messageList.size) {
            processedMessageList.add(processedMessageChannel.receive())
        }
        sendChannel.close()

        assertEquals(
            expected = messageList,
            actual = processedMessageList,
        )
    }

    @Test
    @Timeout(
        value = TEST_TIMEOUT_VALUE_SECONDS,
        unit = TimeUnit.SECONDS,
    )
    fun `aktor() SendChannel should call Mailbox isEmpty, offer() and poll() while processing messages`() = runTest {
        val mailbox = spy(createStubMailbox())
        val processedMessageChannel = Channel<Int>(capacity = Channel.UNLIMITED)
        val sendChannel = aktor(
            mailbox = mailbox,
        ) { message ->
            processedMessageChannel.send(message)
        }
        val messageList = listOf(
            1000,
            1001,
            1002,
            1003,
            1004,
        )
        val processedMessageList: MutableList<Int> = LinkedList()

        for (message in messageList) {
            sendChannel.send(message)
        }
        while (processedMessageList.size < messageList.size) {
            processedMessageList.add(processedMessageChannel.receive())
        }
        sendChannel.close()

        verify(mailbox, atLeastOnce()).isEmpty
        verify(mailbox, times(messageList.size)).offer(any())
        verify(mailbox, atLeast(messageList.size)).poll()
    }

    @Test
    @Timeout(
        value = TEST_TIMEOUT_VALUE_SECONDS,
        unit = TimeUnit.SECONDS,
    )
    fun `aktor() SendChannel should process messages on specified dispatcher`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val processingDispatcherChannel = Channel<CoroutineDispatcher?>(capacity = Channel.UNLIMITED)
        val sendChannel = aktor(
            mailbox = createStubMailbox(),
            context = dispatcher,
        ) { _ ->
            processingDispatcherChannel.send(currentCoroutineContext()[CoroutineDispatcher])
        }

        sendChannel.send(1)
        val processingDispatcher = processingDispatcherChannel.receive()
        sendChannel.close()

        assertEquals(
            expected = dispatcher,
            actual = processingDispatcher,
        )
    }

    @Test
    fun `aktor() SendChannel send() should fail with ClosedSendChannelException after close() without cause`() = runTest {
        val sendChannel = aktor(
            mailbox = createStubMailbox(),
            block = {},
        )

        sendChannel.close()

        assertFailsWith<ClosedSendChannelException> {
            sendChannel.send(1)
        }
    }

    @Test
    fun `aktor() SendChannel send() should fail with cause after close() with cause`() = runTest {
        val sendChannel = aktor(
            mailbox = createStubMailbox(),
            block = {}
        )
        class TestException : Exception()

        sendChannel.close(TestException())

        assertFailsWith<TestException> {
            sendChannel.send(1)
        }
    }

    @Test
    fun `aktor() SendChannel send() should fail after CoroutineScope cancel()`() = runBlocking<Unit> {
        val scope = CoroutineScope(Job())
        val sendChannel = scope.aktor(
            mailbox = createStubMailbox(),
            block = {}
        )

        scope.cancel()
        scope.coroutineContext[Job]?.join()

        assertFails {
            sendChannel.send(1)
        }
    }

    private fun createStubMailbox(): Mailbox<Int, Int> = object : Mailbox<Int, Int> {
        private val queue: Queue<Int> = LinkedList()

        override val isEmpty: Boolean
            get() = queue.isEmpty()

        override fun offer(message: Int) {
            queue.offer(message)
        }

        override fun poll(): Int? = queue.poll()
    }
}