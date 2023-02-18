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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Launches new coroutine that is receiving messages from its mailbox channel. The resulting object can be used to
 * [send][SendChannel.send] messages to this coroutine. This coroutine mailbox preprocesses incoming messages using
 * specified [mailbox][Mailbox] instance.
 * Uncaught exceptions in this coroutine close the channel with this exception as a cause and
 * the resulting channel becomes _failed_, so that any attempt to send to such a channel throws exception.
 *
 * @param I The type of messages accepted by the aktor mailbox.
 * @param O The type of messages actually handled by the aktor.
 *
 * @param mailbox Aktor mailbox which encapsulates preprocessing of aktor input messages before processing. See [Mailbox].
 * @param context Additional to [CoroutineScope.coroutineContext] context of the coroutine.
 * @param block The coroutine code to handle preprocessed messages.
 *
 * @return [SendChannel] to send messages to the aktor mailbox.
 */
@OptIn(ExperimentalCoroutinesApi::class)
public fun <I, O> CoroutineScope.aktor(
    mailbox: Mailbox<I, O>,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend (message: O) -> Unit,
): SendChannel<I> {
    val inputChannel = Channel<I>(
        capacity = Channel.UNLIMITED,
    )
    val mailboxHasMessagesChannel = Channel<Unit>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    launch(context) {
        while (!inputChannel.isClosedForSend) {
            mailbox.offer(inputChannel.receive())
            mailboxHasMessagesChannel.send(Unit)
        }
    }
    launch(context) {
        while (!inputChannel.isClosedForSend) {
            while (mailbox.isEmpty && !inputChannel.isClosedForSend) {
                mailboxHasMessagesChannel.receiveCatching()
            }
            mailbox.poll()?.let { message ->
                try {
                    block(message)
                    if (!mailbox.isEmpty) {
                        mailboxHasMessagesChannel.send(Unit)
                    }
                } catch (throwable: Throwable) {
                    inputChannel.close(throwable)
                    throw throwable
                }
            }
        }
    }
    return inputChannel
}