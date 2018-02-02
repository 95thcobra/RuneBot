package com.gmail.arhamjsiddiqui.runebot.commands

import com.gmail.arhamjsiddiqui.runebot.DatabaseFunctions
import com.gmail.arhamjsiddiqui.runebot.RuneBot
import com.gmail.arhamjsiddiqui.runebot.player.SkillsData
import com.gmail.arhamjsiddiqui.runebot.sendMessage
import de.btobastian.sdcf4j.Command
import de.btobastian.sdcf4j.CommandExecutor
import net.dv8tion.jda.core.entities.User
import java.util.concurrent.ThreadLocalRandom

/**
 * The r.train command to train your RuneBot player!
 *
 * @author Arham 4
 */
class TrainCommand : CommandExecutor {

    @Command(aliases = ["r.train SKILL", "r.train"], description = "Trains your RuneBot player!")
    fun onTrainCommand(user: User, args: Array<String>) {
        if (args.isEmpty()) {
            RuneBot.BOT.sendMessage("Incorrect usage! Use as: `r.train SKILL`")
            return
        }
        val exp = ThreadLocalRandom.current().nextInt(0, 50)
        val player = DatabaseFunctions.fetchPlayer(user)
        val skillName = let {
            val input = args[0].toLowerCase()
            SkillsData.skills.skillNameForNickname[input] ?: input
        }
        val skillId = SkillsData.skills.skillIdFor[skillName]
        if (skillId != null) {
            player.skills.addExperience(skillId, exp)
            RuneBot.BOT.sendMessage("You have earned $exp EXP! Total EXP in ${skillName.capitalize()}: "
                    + player.skills.experiences[skillId])
        } else {
            RuneBot.BOT.sendMessage("Invalid skill name! Only the following can be used:\n```" +
                    "${SkillsData.skills.skillNameFor.toPresentableString()}```\nNicknames are available " +
                    "(for example: `r.train attack` can be `r.train atk`).")
        }
    }

    private fun Map<Int, String>.toPresentableString(): String {
        var output = ""
        keys.forEach { output += "r.train ${this[it]}\n" }
        return output
    }
}