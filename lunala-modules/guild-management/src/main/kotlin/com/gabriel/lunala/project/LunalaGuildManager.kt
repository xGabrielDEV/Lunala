package com.gabriel.lunala.project

import com.gabriel.lunala.project.commands.NotifyCommand
import com.gabriel.lunala.project.commands.SimpleCommand
import com.gabriel.lunala.project.module.DiscordModule
import com.gabriel.lunala.project.utils.modules.registerCommands
import com.gabriel.lunala.project.utils.modules.registerDependencies
import java.io.File

class LunalaGuildManager(name: String, file: File): DiscordModule(name, file) {

    override fun onStart() {
        registerCommands(NotifyCommand().create(), SimpleCommand(this).create())
    }

    override fun onStop() {

    }

}