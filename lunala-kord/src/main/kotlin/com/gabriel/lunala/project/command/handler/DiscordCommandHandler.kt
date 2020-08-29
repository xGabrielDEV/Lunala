package com.gabriel.lunala.project.command.handler

import com.gabriel.lunala.project.Lunala
import com.gabriel.lunala.project.command.Command
import com.gabriel.lunala.project.command.exception.FailException
import com.gabriel.lunala.project.table.LunalaProfiles
import com.gabriel.lunala.project.table.LunalaServers
import com.gabriel.lunala.project.utils.getLunalaPermissions
import com.gabriel.lunala.project.utils.luna.getProfileOrCreate
import com.gabriel.lunala.project.utils.message.LunaReply
import com.gabriel.lunala.project.utils.text.LevenshteinCalculator
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.on
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import java.util.*

class DiscordCommandHandler: CommandHandler<DiscordCommandContext>, KoinComponent {

    private val holder: CommandHolder by inject()
    private val lunala: Lunala by inject()

    override suspend fun startListening() = get<Kord>().on<MessageCreateEvent> {
        if (message.author == null) return@on

        if (!message.content.startsWith(PREFIX)) return@on
        if (message.content.length == PREFIX.length) return@on

        val content = message.content
                .substring(PREFIX.length)
                .trim()
                .split(" ")

        val args = content.drop(1).toMutableList()

        val command = get<CommandHolder>().commands.filter {
            it.key.contains(content.firstOrNull()?.toLowerCase())
        }.map { it.value }.firstOrNull() ?: return@on

        val shard = command.shards.filter {
            it.key.contains(content.drop(1).getOrNull(0)?.toLowerCase())
        }.map {
            it.value
        }.firstOrNull() ?: (command.shards[emptyList()] ?: error("Main shard was not defined."))

        if (shard.names.isNotEmpty())
            args.removeAt(0)

        val profile = lunala.getProfileOrCreate(message.author!!.id.longValue)
        val server = LunalaServers.findOrCreate(message.getAuthorAsMember()!!.guildId.longValue)

        dispatch(DiscordCommandContext(
                command = command,
                label = content[0],
                profile = profile,
                server = server,
                args = args,
                shard = shard,
                message = message,
                member = member!!,
                guild = getGuild()!!,
                channel = message.channel.asChannel() as TextChannel
        ))
    }.run { Unit }

    override suspend fun dispatch(context: DiscordCommandContext) = get<CoroutineScope>().launch {
        val hasPermission = context.member.getLunalaPermissions(context.channel).containsAll(context.shard.permissions)

        if (!hasPermission) {
            context.reply(LunaReply(
                    prefix = ":no_entry_sign:",
                    content = ", you need the following permissions to execute this command: `${context.shard.permissions.joinToString { it.name }}}`!",
                    mentionable = context.profile
            ))
            return@launch
        }

        val exception: Throwable = context.shard.runCatching {
            callback.invoke(context)
        }.exceptionOrNull() ?: return@launch

        if (exception is FailException) exception.callback()


        exception.printStackTrace()

        context.member.getDmChannel().runCatching {
            /**
            createMessage(LunaReply(
                    """B-beep boop! Apparently there was an error when you tried to execute the command `${context.label}`!
                        
                    `${exception.message}`
                        
                    Check if I have the correct permissions on your server, and try to execute the command
                    one more time, and if the error persists, you can contact our administration team: discord.gg/Error404

                    Thank you for the attention! :wink:
                    """.trimIndent()
            ).format())
            */
        }

    }.run { Unit }

    private fun similarest(input: String): Command {
        val levenshtein: MutableMap<Int, Command> = mutableMapOf()

        holder.commands.forEach {
            levenshtein[LevenshteinCalculator.levenshtein(input, it.value.labels[0])] = it.value
        }

        val highest = Collections.max(levenshtein.keys)

        return levenshtein[highest]!!
    }

    companion object {

        const val PREFIX = ">"

    }

}