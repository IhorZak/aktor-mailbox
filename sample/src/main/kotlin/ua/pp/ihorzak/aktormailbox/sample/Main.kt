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

package ua.pp.ihorzak.aktormailbox.sample

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import kotlinx.cli.vararg
import kotlinx.coroutines.*
import ua.pp.ihorzak.aktormailbox.Mailbox
import ua.pp.ihorzak.aktormailbox.aktor
import ua.pp.ihorzak.aktormailbox.priority
import ua.pp.ihorzak.aktormailbox.transform
import kotlin.coroutines.coroutineContext

/**
 * Sample application entry point function.
 *
 * @param args Application start arguments.
 */
suspend fun main(args: Array<String>) {
    val scope = CoroutineScope(coroutineContext + Dispatchers.Default)
    processInput(
        input = prepareInput(args),
        scope = scope,
    )
}

private fun prepareInput(
    args: Array<String>,
): Input = with(ArgParser("aktor-mailbox-sample")) {
    val mailboxType by option(
        type = ArgType.Choice(
            toVariant = { s -> MailboxType.valueOf(s.uppercase()) },
        ),
        fullName = "mailbox",
        shortName = "m",
        description = "Type of aktor mailbox",
    ).required()
    val elementList by argument(
        type = ArgType.Int,
        fullName = "messages",
        description = "Messages to be sent to aktor mailbox",
    ).vararg()
    parse(args)
    Input(
        mailboxType = mailboxType,
        elements = elementList,
    )
}

private suspend fun processInput(
    scope: CoroutineScope,
    input: Input,
) {
    println("input = $input")
    val finishJob: CompletableJob = Job()
    val aktor = scope.aktor(
        mailbox = when (input.mailboxType) {
            MailboxType.PRIORITY -> Mailbox.priority(Int::compareTo)
            MailboxType.TRANSFORM -> Mailbox.transform { element -> element * element }
        }
    ) {
        var processedCount = 0
        for (message in channel) {
            println("Processing message: $message")
            delay(1000L)
            println("Processed message: $message")
            if (++processedCount == input.elements.size) {
                finishJob.complete()
            }
        }
    }
    input.elements.forEachIndexed { index, element ->
        println("Sending message: $element")
        aktor.send(element)
        println("Sent message: $element")
        if (index < input.elements.size - 1) {
            delay(400L)
        }
    }
    finishJob.join()
}

private enum class MailboxType {
    PRIORITY,
    TRANSFORM,
}

private data class Input(
    val mailboxType: MailboxType,
    val elements: Collection<Int>
)