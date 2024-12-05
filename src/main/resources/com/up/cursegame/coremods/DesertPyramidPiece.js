function initializeCoreMod() {
	var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
	var Opcodes = Java.type("org.objectweb.asm.Opcodes");
	var IntInsnNode = Java.type("org.objectweb.asm.tree.IntInsnNode");
	
	return {
		"DesertPyramidPieceTransformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.world.gen.feature.structure.DesertPyramidPiece"
			},
			"transformer": function(cls) {
				print("Tranforming DesertPyramidPiece!");
				var it = cls.methods.iterator();
				while (it.hasNext()) {
					method = it.next();
					if (method.name === "postProcess") {
						print("Found method to transform: " + method.name);
						var instructions = ASMAPI.listOf(
								new IntInsnNode(Opcodes.ALOAD, 0),
								new IntInsnNode(Opcodes.ALOAD, 1),
								new IntInsnNode(Opcodes.ALOAD, 5),
								ASMAPI.buildMethodCall("com/up/cursegame/asm/DesertPyramidPieceTransformer", "postPostProcess", "(Lnet/minecraft/world/gen/feature/structure/DesertPyramidPiece;Lnet/minecraft/world/ISeedReader;Lnet/minecraft/util/math/MutableBoundingBox;)V", ASMAPI.MethodType.STATIC));
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