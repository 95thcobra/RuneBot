package com.gmail.arhamjsiddiqui.runebot

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.gmail.arhamjsiddiqui.runebot.commands.HelpCommand
import com.gmail.arhamjsiddiqui.runebot.commands.TrainCommand
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.btobastian.sdcf4j.handler.JDA3Handler
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.User
import java.nio.file.FileSystems
import java.nio.file.Files


/**
 * @author Arham 4
 */
object RuneBot {

    @JvmStatic
    fun main(args: Array<String>) {
    }

    /**
     * Contains all the players of RuneBot in a neat hashmap to make looking up faster.
     * TODO save players periodically
     */
    val players = hashMapOf<User, Player>()

    val CONFIG: ConfigDto = let {
        val fileName = "config.yaml"
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule())

        Files.newBufferedReader(FileSystems.getDefault().getPath(fileName)).use { mapper.readValue(it, ConfigDto::class.java) }
    }

    val BOT: JDA = let {
        fun registerListeners(registrants: () -> Unit) {
            registrants.invoke()
        }
        fun registerCommands(registrants: () -> Unit) {
            registrants.invoke()
        }

        val jda = JDABuilder(AccountType.BOT).setToken(CONFIG.token).buildAsync()
        val cmd = JDA3Handler(jda)

        registerCommands {
            cmd.registerCommand(HelpCommand(cmd))
            cmd.registerCommand(TrainCommand())
        }

        jda
    }

    val DATASOURCE = let {
        val config = HikariConfig()

        config.jdbcUrl = CONFIG.jdbc.url
        config.username = CONFIG.jdbc.username
        config.password = CONFIG.jdbc.password
        config.isAutoCommit = true
        config.maximumPoolSize = 32

        HikariDataSource(config)
    }

    data class JDBCDto(val url: String, val driver: String, val username: String, val password: String)
    data class ConfigDto(val jdbc: JDBCDto, val token: String, val guildId: Long, val testChannelId: Long)
}

fun JDA.sendMessage(message: String) {
    getGuildById(RuneBot.CONFIG.guildId).getTextChannelById(RuneBot.CONFIG.testChannelId).sendMessage(message).queue()
}