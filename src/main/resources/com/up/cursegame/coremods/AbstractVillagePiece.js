function initializeCoreMod() {
	var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
	var Opcodes = Java.type("org.objectweb.asm.Opcodes");
	var IntInsnNode = Java.type("org.objectweb.asm.tree.IntInsnNode");
	
	return {
		"AbstractVillagePieceTransformer": {
			"target": {
				"type": "METHOD",
				"class": "net.minecraft.world.gen.feature.structure.AbstractVillagePiece",
//				"methodName": "postProcess",
				"methodName": "func_230383_a_",
				"methodDesc": "(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/world/gen/feature/structure/StructureManager;Lnet/minecraft/world/gen/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/MutableBoundingBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z",

			},
			"transformer": function(method) {
				print("Transforming method: " + method.name);
				var instructions = ASMAPI.listOf(
						new IntInsnNode(Opcodes.ALOAD, 0),
						new IntInsnNode(Opcodes.ALOAD, 1),
						new IntInsnNode(Opcodes.ALOAD, 5),
						ASMAPI.buildMethodCall("com/up/cursegame/asm/AbstractVillagePieceTransformer", "postPostProcess", "(Lnet/minecraft/world/gen/feature/structure/AbstractVillagePiece;Lnet/minecraft/world/ISeedReader;Lnet/minecraft/util/math/MutableBoundingBox;)V", ASMAPI.MethodType.STATIC));
				method.instructions.insertBefore(method.instructions.get(method.instructions.size() - 2), instructions);
				return method;
			}
		}
	};
}