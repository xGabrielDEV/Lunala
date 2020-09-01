package com.gabriel.lunala.project.command.handler

import com.gabriel.lunala.project.command.Command
import com.gabriel.lunala.project.command.CommandContext
import com.gabriel.lunala.project.command.snapshot.ShardCommand
import com.gabriel.lunala.project.entity.Profile
import com.gabriel.lunala.project.entity.Server
import com.gabriel.lunala.project.utils.client.reply
import com.gabriel.lunala.project.utils.message.LunaReply
import net.dv8tion.jda.api.entities.*
import org.koin.core.KoinComponent

class DiscordCommandContext(
        override val command: Command,
        override val label: String,
        override val profile: Profile,
        override val server: Server,
        override val args: List<String>,
        override val shard: ShardCommand,

        val message: Message,
        val member: Member,
        val guild: Guild,
        val channel: TextChannel
) : CommandContext, KoinComponent {

    suspend fun reply(reply: LunaReply): Message = channel.reply(reply)

}