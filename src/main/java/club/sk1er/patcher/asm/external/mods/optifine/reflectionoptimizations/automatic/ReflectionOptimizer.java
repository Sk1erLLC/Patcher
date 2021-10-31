package club.sk1er.patcher.asm.external.mods.optifine.reflectionoptimizations.automatic;

import club.sk1er.patcher.optifine.OptiFineReflectorScraper;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.util.Bytecode;

public class ReflectionOptimizer implements PatcherTransformer {
    private static final String reflectorClass = "net/optifine/reflect/Reflector";
    private final OptiFineReflectorScraper.ReflectionData data = OptiFineReflectorScraper.readData();

    @Override
    public String[] getClassName() {
        if (data == null) return new String[0];
        return data.getClassesToTransform().toArray(new String[0]);
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        if (data == null) return;

        for (MethodNode methodNode : classNode.methods) {
            AbstractInsnNode insn = methodNode.instructions.getFirst();
            if (insn == null) continue;
            while (insn.getNext() != null) {
                insn = insn.getNext();
                if (insn instanceof FieldInsnNode && insn.getOpcode() == Opcodes.GETSTATIC) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insn;
                    if (fieldInsnNode.owner.equals(reflectorClass) && fieldInsnNode.desc.equals("Lnet/optifine/reflect/ReflectorMethod;")) {
                        if (insn.getNext() instanceof MethodInsnNode) {
                            MethodInsnNode next = (MethodInsnNode) insn.getNext();
                            if (next.name.equals("exists")) continue;
                        }
                        insn = transformMethodCall(methodNode.instructions, fieldInsnNode);
                    }
                }
            }
        }
    }

    private AbstractInsnNode transformMethodCall(InsnList insns, FieldInsnNode getReflectorNode) {
        OptiFineReflectorScraper.MethodData methodData = data.getReflectorMethodData(getReflectorNode.name);
        if (methodData == null) return getReflectorNode;
        Type returnType = Type.getReturnType(methodData.getDescriptor());
        Type[] parameterTypes = Type.getArgumentTypes(methodData.getDescriptor());
        int numberOfArrayStoresSeen = 0;
        AbstractInsnNode insn = getReflectorNode.getNext();
        while (insn.getNext() != null) {
            if (isReflectorCall(insn)) {
                if (insn.getNext().getOpcode() == Opcodes.POP && returnType == Type.VOID_TYPE) {
                    insns.remove(insn.getNext());
                }
                MethodInsnNode methodInsnNode = ((MethodInsnNode) insn);
                boolean isStatic = Type.getArgumentTypes(methodInsnNode.desc).length == 2;
                int opcode = isStatic ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL;
                String internalTargetName = methodData.getTargetClass().replace('.', '/');
                MethodInsnNode newCall = new MethodInsnNode(opcode, internalTargetName, methodData.getName(), methodData.getDescriptor(), false);
                insns.set(insn, newCall);
                if (returnType.getDescriptor().length() == 1 && returnType != Type.VOID_TYPE && methodInsnNode.desc.endsWith(")Ljava/lang/Object;")) {
                    // This method should return a primitive, but is expected to return an object. Box it
                    insns.insert(newCall, boxReturnValue(returnType));
                }
                if (!isStatic) {
                    // Cast to the desired type in case it isn't already known
                    insns.insertBefore(getReflectorNode, new TypeInsnNode(Opcodes.CHECKCAST, internalTargetName));
                }
                insns.remove(getReflectorNode);
                return newCall;
            }

            if (insn.getOpcode() == Opcodes.ANEWARRAY) {
                AbstractInsnNode lengthNode = insn.getPrevious();
                insns.remove(lengthNode);
                if (lengthNode.getOpcode() != Opcodes.ICONST_0) {
                    insns.remove(insn.getNext()); // DUP
                    insns.remove(insn.getNext()); // index of first item
                }
                AbstractInsnNode thisInsn = insn;
                insn = insn.getNext();
                insns.remove(thisInsn);
                continue;
            } else if (insn.getOpcode() == Opcodes.AASTORE) {
                if (insn.getNext().getOpcode() == Opcodes.DUP) {
                    insns.remove(insn.getNext()); // DUP
                    insns.remove(insn.getNext()); // index of next item
                }
                AbstractInsnNode thisInsn = insn;
                insn = insn.getNext();
                Type parameterType = parameterTypes[numberOfArrayStoresSeen];
                if (parameterType.getDescriptor().length() == 1) {
                    // Primitive, need to unbox
                    insns.set(thisInsn, unboxParameter(parameterType));
                } else {
                    // Cast to the desired type in case it isn't known by this point
                    insns.set(thisInsn, new TypeInsnNode(Opcodes.CHECKCAST, parameterType.getInternalName()));
                }
                numberOfArrayStoresSeen++;
                continue;
            }
            insn = insn.getNext();
        }
        return null;
    }

    private AbstractInsnNode unboxParameter(Type primitiveType) {
        org.spongepowered.asm.lib.Type type = org.spongepowered.asm.lib.Type.getType(primitiveType.getDescriptor());
        return new MethodInsnNode(
            Opcodes.INVOKEVIRTUAL,
            Bytecode.getBoxingType(type),
            Bytecode.getUnboxingMethod(type),
            "()" + primitiveType.getDescriptor(),
            false
        );
    }

    private AbstractInsnNode boxReturnValue(Type returnType) {
        org.spongepowered.asm.lib.Type type = org.spongepowered.asm.lib.Type.getType(returnType.getDescriptor());
        String boxingType = Bytecode.getBoxingType(type);
        return new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            boxingType,
            "valueOf",
            '(' + returnType.getDescriptor() + ")L" + boxingType + ';',
            false
        );
    }

    private boolean isReflectorCall(AbstractInsnNode insn) {
        if (insn instanceof MethodInsnNode) {
            MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
            return methodInsnNode.owner.equals(reflectorClass) && methodInsnNode.name.startsWith("call");
        }
        return false;
    }
}
