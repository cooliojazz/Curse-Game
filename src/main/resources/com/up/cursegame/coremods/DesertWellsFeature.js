function initializeCoreMod() {
	var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
	var Opcodes = Java.type("org.objectweb.asm.Opcodes");
	var IntInsnNode = Java.type("org.objectweb.asm.tree.IntInsnNode");
	
	return {
		"DesertWellsFeatureTransformer": {
			"target": {
				"type": "METHOD",
				"class": "net.minecraft.world.gen.feature.DesertWellsFeature",
//				"methodName": "place",
				"methodName": "func_241855_a",
				"methodDesc": "(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/world/gen/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/NoFeatureConfig;)Z",

			},
			"transformer": function(method) {
				print("Transforming method: " + method.name);
				var instructions = ASMAPI.listOf(
						new IntInsnNode(Opcodes.ALOAD, 0),
						new IntInsnNode(Opcodes.ALOAD, 1),
						new IntInsnNode(Opcodes.ALOAD, 4),
						ASMAPI.buildMethodCall("com/up/cursegame/asm/DesertWellsFeatureTransformer", "postPlace", "(Lnet/minecraft/world/gen/feature/DesertWellsFeature;Lnet/minecraft/world/ISeedReader;Lnet/minecraft/util/math/BlockPos;)V", ASMAPI.MethodType.STATIC));
				method.instructions.insertBefore(method.instructions.get(method.instructions.size() - 2), instructions);
				return method;
			}
		}
	};
}