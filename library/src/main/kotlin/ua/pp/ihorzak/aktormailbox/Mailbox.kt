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

/**
 * Represents the type of incoming messages queueing. Each actor has a single mailbox which acts as a queue.
 * It can be used to perform reordering of messages, messages type transformations etc.
 *
 * @param I The type of messages to be sent to actor.
 * @param O The type of messages actually handled by actor.
 */
public interface Mailbox<I, O> {
    public companion object;

    /**
     * Indicates if this actor mailbox queue has no messages to process by actor.
     */
    public val isEmpty: Boolean

    /**
     * Indicates if this actor mailbox queue has maximal number of messages and cannot accept more messages.
     */
    public val isFull: Boolean

    /**
     * Adds message to actor mailbox queue.
     *
     * @param message Message to add to actor mailbox queue.
     */
    public fun offer(message: I)

    /**
     * Retrieves, but does not remove a message which is next to be processed by actor from this mailbox queue.
     *
     * @return A message which is next to be processed by actor if this mailbox queue is not empty, otherwise null.
     */
    public fun peek(): O?

    /**
     * Retrieves and removes a message which is next to be processed by actor from this mailbox queue.
     *
     * @return A message which is next to be processed by actor if this mailbox queue is not empty, otherwise null.
     */
    public fun poll(): O?
}