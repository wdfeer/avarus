package wdfeer.avarus

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import wdfeer.avarus.CommandResult.*

//** Helper class containing state for building the commands. */
private class AvarusCommander(
    dispatcher: CommandDispatcher<ServerCommandSource>,
    private val parentCommand: LiteralArgumentBuilder<ServerCommandSource>,
    registerSubCommands: AvarusCommander.() -> Unit
) {
    init {
        registerSubCommands()
        dispatcher.register(parentCommand)
    }

    fun subcommand(
        name: String,
        block: LiteralArgumentBuilder<ServerCommandSource>.() -> Unit
    ) {
        parentCommand.then(literal(name).apply(block))
    }

    fun subcommand(name: String, executes: Command<ServerCommandSource>) {
        parentCommand.then(literal(name).executes(executes))
    }
}

fun initializeCommands(config: Config) {
    val buffs = config.buffs
    CommandRegistrationCallback.EVENT.register { commandDispatcher, _, _ ->
        AvarusCommander(commandDispatcher, literal("avarus")) {
            subcommand("help", toMessageCommand {
                "Avarus is a mod allowing to obtain stat increases (e.g. max hp) by \"buying\" them with a large amount of items with the \"avarus-get\" command, e.g.:\n/avarus cobblestone"
            })
            subcommand("status", toMessageCommand { player ->
                val (applied, notApplied) = buffs.partition { it.isApplied(player) }
                buildString {
                    append("${applied.size}/${buffs.size} buffs applied.\n\n")
                    append("Available buffs: ")
                    append(
                        notApplied.take(3).joinToString(", ") { it.item.toString() })
                    append(if (notApplied.size <= 3) "" else ", +${notApplied.size - 3}")
                }
            })
            subcommand("get") {
                for (b in buffs) {
                    then(
                        literal(b.name).executes(toCommand { b.tryApply(it) })
                    )
                }

                // TODO: make "all" an illegal buff name
                then(
                    literal("all").executes(
                        toCommand { player ->
                            if (player.isCreative) {
                                val results = buffs.map { buff -> buff.tryApply(player) }
                                val successCount = results.count { it.success }
                                if (successCount != 0) {
                                    Success("$successCount buffs applied.")
                                } else Failure("All buffs already applied!")
                            } else {
                                Failure("You must be in creative mode!")
                            }
                        }
                    ))
            }
            subcommand("remove") {
                requires { it.hasPermissionLevel(2) }

                for (buff in buffs) {
                    then(
                        literal(buff.name).executes(toCommand {
                            buff.remove(it)
                            Success("${buff.name} buff removed.")
                        })
                    )
                }

                then(literal("all").executes(toCommand { player ->
                    var count = 0
                    buffs.filter { it.isApplied(player) }.forEach {
                        it.remove(player)
                        count++
                    }
                    Success("$count buffs removed.")
                }))
            }
        }
    }
}
