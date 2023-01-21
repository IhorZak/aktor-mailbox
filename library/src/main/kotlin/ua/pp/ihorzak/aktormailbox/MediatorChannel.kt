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

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.SelectClause1
import kotlinx.coroutines.selects.SelectClause2

/**
 * Utility channel to stand in between input receive and output send channels to perform intermediate processing
 * of messages mailbox.
 *
 * @param I Type of messages supported by input channel.
 * @param O Type of messages supported by output channel.
 *
 * @param scope Coroutine scope to launch coroutine which will perform messages mailbox processing.
 * @param inputChannel Channel to receive input messages.
 * @param outputChannel Channel to send output message.
 * @param mailbox Messages mailbox processing strategy.
 *
 * @see Mailbox
 */
@ExperimentalCoroutinesApi
internal class MediatorChannel<I, O>(
    scope: CoroutineScope,
    private val inputChannel: ReceiveChannel<I>,
    private val outputChannel: SendChannel<O>,
    private val mailbox: Mailbox<I, O>,
) : ReceiveChannel<I>, SendChannel<O> {
    init {
        scope.launch {
            try {
                while (!inputChannel.isClosedForReceive) {
                    receiveInput()
                    sendOutput()
                }
            } finally {
                sendOutput()
                outputChannel.close()
            }
        }
    }

    // Input channel properties begin
    override val isClosedForReceive: Boolean
        get() = inputChannel.isClosedForReceive

    override val isEmpty: Boolean
        get() = inputChannel.isEmpty

    override val onReceive: SelectClause1<I>
        get() = inputChannel.onReceive

    override val onReceiveCatching: SelectClause1<ChannelResult<I>>
        get() = inputChannel.onReceiveCatching
    // Input channel properties end

    // Output channel properties start
    override val isClosedForSend: Boolean
        get() = outputChannel.isClosedForSend

    override val onSend: SelectClause2<O, SendChannel<O>>
        get() = outputChannel.onSend
    // Output channel properties end

    // Input channel functions start
    @Deprecated("Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
    override fun cancel(cause: Throwable?): Boolean {
        inputChannel.cancel()
        return true
    }

    override fun cancel(cause: CancellationException?) {
        inputChannel.cancel(cause)
    }

    override fun iterator(): ChannelIterator<I> = inputChannel.iterator()

    override suspend fun receive(): I = inputChannel.receive()

    override suspend fun receiveCatching(): ChannelResult<I> = inputChannel.receiveCatching()

    override fun tryReceive(): ChannelResult<I> = inputChannel.tryReceive()
    // Input channel functions end

    // Output channel functions start
    override fun close(cause: Throwable?): Boolean = outputChannel.close(cause)

    override fun invokeOnClose(handler: (cause: Throwable?) -> Unit) {
        outputChannel.invokeOnClose(handler)
    }

    override suspend fun send(element: O) {
        outputChannel.send(element)
    }

    override fun trySend(element: O): ChannelResult<Unit> = outputChannel.trySend(element)
    // Output channel functions end

    private suspend fun receiveInput() {
        var input: I?
        do {
            input = if (mailbox.isEmpty) {
                inputChannel.receive()
            } else {
                inputChannel.tryReceive().getOrNull()
            }
            input?.let { message -> mailbox.offer(message) }
        } while (input != null)
    }

    private suspend fun sendOutput() {
        var output: O?
        do {
            if (mailbox.isFull) {
                output = mailbox.poll()
                output?.let { message ->
                    outputChannel.send(message)
                }
            } else {
                output = mailbox.peek()
                output?.let { message ->
                    if (outputChannel.trySend(message).isSuccess) {
                        mailbox.poll()
                    }
                }
            }
        } while (output != null)
    }
}