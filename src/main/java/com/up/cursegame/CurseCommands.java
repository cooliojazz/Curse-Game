package com.up.cursegame;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.up.cursegame.util.DataUtil;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;

/**
 *
 * @author Ricky
 */
public class CurseCommands {
	public static final LiteralArgumentBuilder[] commands = {
			Commands.literal("start-curse")
					.then(Commands.argument("min-lives", IntegerArgumentType.integer())
						.then(Commands.argument("max-lives", IntegerArgumentType.integer())
							.then(Commands.argument("min-duration", IntegerArgumentType.integer())
								.then(Commands.argument("max-duration", IntegerArgumentType.integer())
									.then(Commands.argument("active", IntegerArgumentType.integer())
										.executes(context -> {
											CurseGame.game.start(context.getSource().getServer(),
													context.getArgument("min-lives", Integer.class),
													context.getArgument("max-lives", Integer.class),
													context.getArgument("min-duration", Integer.class) * 20,
													context.getArgument("max-duration", Integer.class) * 20,
													context.getArgument("active", Integer.class));
											return 1;
										})
									)
								)
							)
						)
					),
			Commands.literal("trade-lives")
					.then(Commands.argument("name", EntityArgument.player())
						.then(Commands.argument("amount", IntegerArgumentType.integer())
							.executes(context -> {
								DataUtil.tradeLives(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "name"), context.getArgument("amount", Integer.class));
								return 1;
							})
						)
					)
		};
}
