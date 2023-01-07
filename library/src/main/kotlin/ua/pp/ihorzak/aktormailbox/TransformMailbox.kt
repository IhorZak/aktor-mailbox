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

import java.util.*

/**
 * Implementation of actor mailbox queue which transforms incoming messages to messages handled by the actor using
 * specified transformation function.
 *
 * @param I The type of incoming actor messages.
 * @param O The type of messages the actor handles
 *
 * @param transform A transformation function, which converts incoming messages to messages the actor handles.
 */
public class TransformMailbox<I, O>(
    private val transform: (I) -> O,
) : Mailbox<I, O> {
    private val queue: Queue<O> = LinkedList()

    override val isEmpty: Boolean
        get() = queue.isEmpty()

    override val isFull: Boolean = false

    override fun offer(message: I) {
        queue.add(transform(message))
    }

    override fun peek(): O? = queue.peek()

    override fun poll(): O? = queue.poll()
}