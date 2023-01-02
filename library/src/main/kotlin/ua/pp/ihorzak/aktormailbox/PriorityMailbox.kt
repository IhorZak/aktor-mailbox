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

private const val INITIAL_CAPACITY = 16

/**
 * Implementation of actor mailbox queue which sorts messages using specified comparison function.
 *
 * @param T The type of actor messages.
 *
 * @param comparator A comparison function, which imposes a total ordering on actor messages.
 */
public class PriorityMailbox<T>(
    comparator: Comparator<T>
) : Mailbox<T, T> {
    private val queue: Queue<T> = PriorityQueue(INITIAL_CAPACITY, comparator)

    override val isEmpty: Boolean
        get() = queue.isEmpty()

    override val isFull: Boolean = false

    override fun offer(message: T) {
        queue.offer(message)
    }

    override fun peek(): T? = queue.peek()

    override fun poll(): T? = queue.poll()
}