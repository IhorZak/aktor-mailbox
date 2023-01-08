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
 * Creates actor mailbox queue which sorts messages using specified comparison function.
 *
 * @param T The type of actor messages.
 *
 * @param comparator A comparison function, which imposes a total ordering on actor messages.
 *
 * @return Actor mailbox queue which sorts messages using specified comparison function.
 */
public fun <T> Mailbox.Companion.priority(
    comparator: Comparator<T>,
): Mailbox<T, T> = PriorityMailbox(comparator)

/**
 * Creates actor mailbox queue which transforms incoming messages to messages handled by the actor using specified
 * transformation function.
 *
 * @param I The type of incoming actor messages.
 * @param O The type of messages the actor handles.
 *
 * @param transform A transformation function, which converts incoming messages to messages the actor handles.
 *
 * @return Actor mailbox queue which transforms incoming messages to messages handled by the actor using specified
 * transformation function.
 */
public fun <I, O> Mailbox.Companion.transform(
    transform: (I) -> O,
): Mailbox<I, O> = TransformMailbox(transform)