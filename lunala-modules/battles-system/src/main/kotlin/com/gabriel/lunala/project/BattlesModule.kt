package com.gabriel.lunala.project

import com.gabriel.lunala.project.commands.TestBattleCommand
import com.gabriel.lunala.project.module.DiscordModule
import com.gabriel.lunala.project.utils.modules.registerCommands
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import java.io.File

class BattlesModule(name: String, file: File) : DiscordModule(name, file) {

    override fun onStart() {
        loadKoinModules(module {
            single { this@BattlesModule }
        })
        registerCommands(TestBattleCommand().create())
    }

}