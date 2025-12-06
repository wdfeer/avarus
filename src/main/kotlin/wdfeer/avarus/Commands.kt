package wdfeer.avarus

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import wdfeer.avarus.CommandResult.*

fun initializeCommands(config: Config) {
    val buffs = config.buffs
    CommandRegistrationCallback.EVENT.register { commandDispatcher, _, _ ->
        registerMessageCommand(commandDispatcher, "avarus-help") {
            Text.of(
                "Avarus is a mod allowing to obtain stat increases (e.g. max hp) by \"buying\" them with a large amount of items with the \"avarus-get\" command, e.g.:\n/avarus cobblestone"
            )
        }
        registerMessageCommand(commandDispatcher, "avarus-status") { context ->
            val player = context.source.player ?: return@registerMessageCommand Text.of("No player found!")
            val (applied, notApplied) = buffs.partition { it.isApplied(player) }
            Text.of(
                "${applied.size}/${buffs.size} buffs applied.\n\n" +
                    "Available buffs: " +
                    notApplied.take(3).joinToString(", ") { it.item.toString() } +
                    if (notApplied.size <= 3) "" else ", +${notApplied.size - 3}"
            )
        }
        registerGetCommand(commandDispatcher, buffs)
        registerGetAllCommand(commandDispatcher, buffs)
        registerRemoveCommand(commandDispatcher, buffs)
    }
}

private fun registerGetCommand(
    dispatcher: CommandDispatcher<ServerCommandSource>,
    buffs: List<AttributeBuff>
) {
    var builder: LiteralArgumentBuilder<ServerCommandSource> = literal("avarus-get")

    for (buff in buffs) {
        builder = builder.then(
            literal(buff.item.toString().lowercase())
                .executes(toCommand { buff.tryApply(it) })
        )
    }

    dispatcher.register(builder)
}

private fun registerGetAllCommand(
    dispatcher: CommandDispatcher<ServerCommandSource>,
    buffs: List<AttributeBuff>
) {
    var builder: LiteralArgumentBuilder<ServerCommandSource> = literal("avarus-get-all")
    builder = builder.requires { it.hasPermissionLevel(2) }

    builder = builder.executes(toCommand { player ->
        if (player.isCreative) {
            val results = buffs.map { buff -> buff.tryApply(player) }
            val successCount = results.count { it.isSuccess() }
            if (successCount != 0) {
                Success("$successCount buffs applied.")
            } else Failure("All buffs already applied!")
        } else {
            Failure("You must be in creative mode!")
        }
    })

    dispatcher.register(builder)
}

private fun registerRemoveCommand(
    dispatcher: CommandDispatcher<ServerCommandSource>,
    buffs: List<AttributeBuff>
) {
    var builder: LiteralArgumentBuilder<ServerCommandSource> = literal("avarus-remove")
    builder = builder.requires { it.hasPermissionLevel(2) }

    for (buff in buffs) {
        builder = builder.then(
            literal(buff.item.toString().lowercase())
                .executes(toCommand {
                    buff.remove(it)
                    Success("${buff.name} buff removed.")
                })
        )
    }

    dispatcher.register(builder)
}
