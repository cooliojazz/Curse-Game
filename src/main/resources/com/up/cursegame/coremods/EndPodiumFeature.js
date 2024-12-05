function initializeCoreMod() {
	var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
	var Opcodes = Java.type("org.objectweb.asm.Opcodes");
	var IntInsnNode = Java.type("org.objectweb.asm.tree.IntInsnNode");
	
	return {
		"EndPodiumFeatureTransformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.world.gen.feature.EndPodiumFeature"
			},
			"transformer": function(cls) {
				print("Transforming class: " + cls.name);
				var it = cls.methods.iterator();
				while (it.hasNext()) {
					method = it.next();
					if (method.name === "place") {
						var instructions = ASMAPI.listOf(
								new IntInsnNode(Opcodes.ALOAD, 0),
								new IntInsnNode(Opcodes.ALOAD, 1),
								new IntInsnNode(Opcodes.ALOAD, 4),
								ASMAPI.buildMethodCall("com/up/cursegame/asm/EndPodiumFeatureTransformer", "postPlace", "(Lnet/minecraft/world/gen/feature/EndPodiumFeature;Lnet/minecraft/world/ISeedReader;Lnet/minecraft/util/math/BlockPos;)V", ASMAPI.MethodType.STATIC));
						method.instructions.insertBefore(method.instructions.get(method.instructions.size() - 2), instructions);
//						print(ASMAPI.methodNodeToString(method));
						break;
					}
				}
				
				return cls;
			}
		}
	};
}