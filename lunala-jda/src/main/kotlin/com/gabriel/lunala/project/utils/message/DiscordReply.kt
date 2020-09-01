package com.gabriel.lunala.project.utils.message

import com.gabriel.lunala.project.image.Image
import com.gabriel.lunala.project.utils.Mentionable
import net.dv8tion.jda.api.EmbedBuilder

class DiscordReply(
        prefix: String = ":large_blue_diamond:",
        content: String = "",
        mentionable: Mentionable? = null,
        image: Image? = null,
        val embed: EmbedBuilder? = null
): LunaReply(prefix, content, mentionable, image)