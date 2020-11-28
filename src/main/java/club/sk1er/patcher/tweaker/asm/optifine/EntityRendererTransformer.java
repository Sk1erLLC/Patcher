/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.tweaker.asm.optifine;

import club.sk1er.elementa.constraints.animation.Animations;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.EntityRendererHook;
import club.sk1er.patcher.tweaker.ClassTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.lwjgl.input.Mouse;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;
import java.util.ListIterator;

@SuppressWarnings("unused")
public class EntityRendererTransformer implements PatcherTransformer {

    private static final float normalModifier = 4f;
    private static float currentModifier = normalModifier;
    public static boolean zoomed = false;
    private static boolean hasScrolledYet = false;
    private static long lastMillis = System.currentTimeMillis();
    private static float smoothZoomProgress = 0f;
    private static float desiredModifier = currentModifier;

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.EntityRenderer"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "createdLightmap", "Z", null, null));

        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            switch (methodName) {
                case "getFOVModifier":
                case "func_78481_a": {
                    int zoomActiveIndex = -1;

                    for (LocalVariableNode var : methodNode.localVariables) {
                        if (var.name.equals("zoomActive")) {
                            zoomActiveIndex = var.index;
                            break;
                        }
                    }

                    Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    LabelNode ifne = new LabelNode();
                    while (iterator.hasNext()) {
                        AbstractInsnNode thing = iterator.next();
                        if (checkNode(thing)) {
                            methodNode.instructions.insertBefore(thing, new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "normalZoomSensitivity", "Z")); // False instead of true
                            methodNode.instructions.insertBefore(thing, new InsnNode(Opcodes.ICONST_1));
                            methodNode.instructions.insertBefore(thing, new InsnNode(Opcodes.IXOR));
                            methodNode.instructions.insert(thing, callResetAndSensChange());
                            methodNode.instructions.remove(thing);
                        } else if (checkDivNode(thing)) {
                            methodNode.instructions.remove(thing.getPrevious());
                            methodNode.instructions.insertBefore(thing, getDivisor());
                        } else if (checkZoomActiveNode(thing, zoomActiveIndex)) {
                            methodNode.instructions.insertBefore(thing, setZoomed(zoomActiveIndex));
                        } else if (thing instanceof MethodInsnNode && thing.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            String methodInsnName = mapMethodNameFromNode(thing);

                            if (methodInsnName.equals("getMaterial") || methodInsnName.equals("func_149688_o")) {
                                methodNode.instructions.insertBefore(thing.getPrevious(), createLabel(ifne));
                            }
                        } else if (thing instanceof LdcInsnNode && ((LdcInsnNode) thing).cst.equals(70.0f) && thing.getPrevious().getOpcode() == Opcodes.FMUL) {
                            methodNode.instructions.insert(thing.getNext().getNext().getNext(), setFOVLabelAndUpdateSmoothZoom(ifne));
                        } else if (thing.getOpcode() == Opcodes.INVOKESTATIC && (((MethodInsnNode) thing).name.equals("isKeyDown") || ((MethodInsnNode) thing).name.equals("func_100015_a"))) {
                            methodNode.instructions.insert(thing, modifyKeyDownIfToggleToZoom());
                        } else if (thing.getOpcode() == Opcodes.PUTSTATIC && ((FieldInsnNode) thing).owner.equals("Config") && ((FieldInsnNode) thing).name.equals("zoomMode") && thing.getPrevious().getOpcode() == Opcodes.ICONST_0) {
                            methodNode.instructions.insert(thing, new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/hooks/EntityRendererHook", "resetSensitivity", "()V", false));
                        }
                    }

                    break;
                }
                case "orientCamera":
                case "func_78467_g": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    int movingobjectpositionIndex = -1,
                        d0Index = -1,
                        f3Index = -1,
                        d1Index = -1,
                        f4Index = -1,
                        d2Index = -1,
                        f5Index = -1,
                        d6Index = -1,
                        d4Index = -1,
                        d5Index = -1;

                    boolean useNormalIndex = ClassTransformer.optifineVersion.equals("I7");

                    for (LocalVariableNode variable : methodNode.localVariables) {
                        switch (variable.name) {
                            case "movingobjectposition":
                                movingobjectpositionIndex = variable.index;
                                break;

                            case "d0":
                                d0Index = variable.index;
                                break;

                            case "f3":
                                f3Index = variable.index;
                                break;

                            case "d1":
                                d1Index = variable.index;
                                break;

                            case "f4":
                                f4Index = variable.index;
                                break;

                            case "d2":
                                d2Index = variable.index;
                                break;

                            case "f5":
                                f5Index = variable.index;
                                break;

                            case "d4":
                                d4Index = variable.index;
                                break;

                            case "d6":
                                d6Index = variable.index;
                                break;

                            case "d5":
                                d5Index = variable.index;
                                break;
                        }
                    }

                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();

                        if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals(-0.10000000149011612F)) {
                            methodNode.instructions.insertBefore(next, fixParallax());
                            methodNode.instructions.remove(next);
                        } else if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            final String methodDesc = mapMethodDescFromNode(next);
                            if ((methodInsnName.equals("rayTraceBlocks") || methodInsnName.equals("func_72933_a")) && methodDesc.equals("(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;")) {
                                methodNode.instructions.insertBefore(next.getNext().getNext().getNext(), changeCameraType(movingobjectpositionIndex, d0Index, d1Index,
                                    d2Index, d4Index, d5Index, d6Index, f3Index, f4Index, f5Index, useNormalIndex));
                            }
                        }
                    }
                    break;
                }

                case "updateLightmap":
                case "func_78472_g": {
                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), checkFullbright());

                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next.getOpcode() == Opcodes.INVOKEVIRTUAL && next instanceof MethodInsnNode) {
                            String methodInsnName = mapMethodNameFromNode(next);

                            if (methodInsnName.equals("endSection") || methodInsnName.equals("func_76319_b")) {
                                methodNode.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious(), assignCreatedLightmap());
                            } else if (methodInsnName.equals("isPotionActive") || methodInsnName.equals("func_70644_a")) {
                                AbstractInsnNode suspect = next.getNext().getNext();
                                if (suspect.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) suspect).owner.endsWith("CustomColors")) {
                                    continue;
                                }
                                methodNode.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious().getPrevious(), clampLightmap());
                            }
                        }
                    }
                    break;
                }
                case "renderStreamIndicator":
                case "func_152430_c":
                    clearInstructions(methodNode);
                    methodNode.instructions.insert(new InsnNode(Opcodes.RETURN));
                    break;

                case "renderWorldPass":
                case "func_175068_a": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            String methodInsnName = mapMethodNameFromNode(next);

                            if (methodInsnName.equals("renderEntities") || methodInsnName.equals("func_180446_a")) {
                                methodNode.instructions.insertBefore(next.getNext(), toggleCullingStatus(false));

                                for (int i = 0; i < 4; i++) {
                                    next = next.getPrevious();
                                }

                                methodNode.instructions.insertBefore(next, toggleCullingStatus(true));
                            }
                        }

                        switch (ClassTransformer.optifineVersion) {
                            case "I7": {
                                if (next instanceof TypeInsnNode) {
                                    if (FMLDeobfuscatingRemapper.INSTANCE.map(((TypeInsnNode) next).desc).equals("net/minecraft/client/renderer/culling/Frustum")) {
                                        while (true) {
                                            AbstractInsnNode insn = iterator.next();
                                            if (insn instanceof VarInsnNode) {
                                                methodNode.instructions.insert(insn, getStoreCameraInsn(((VarInsnNode) insn).var));
                                                break;
                                            }
                                        }
                                    }
                                }

                                break;
                            }

                            default: // vanilla & L6 will work for this
                            case "L5": {
                                int cameraVar = -1;

                                for (LocalVariableNode var : methodNode.localVariables) {
                                    if (var.name.equals("icamera")) {
                                        cameraVar = var.index;
                                        break;
                                    }
                                }

                                if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                    String methodInsnName = mapMethodNameFromNode(next);

                                    if (methodInsnName.equals("getRenderViewEntity") || methodInsnName.equals("func_175606_aa")) {
                                        next = next.getPrevious().getPrevious();

                                        methodNode.instructions.insertBefore(next, getStoreCameraInsn(cameraVar));
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    break;
                }

                case "func_78464_a":
                case "updateRenderer": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            String methodInsnName = mapMethodNameFromNode(next);

                            if (methodInsnName.equals("getLightBrightness")) {
                                ((MethodInsnNode) next.getPrevious()).desc = "(Lnet/minecraft/util/Vec3;)V";
                                methodNode.instructions.insertBefore(next.getPrevious(), getEyePosition());
                                break;
                            }
                        }
                    }

                    break;
                }

                case "func_78466_h":
                case "updateFogColor": {
                    // optifine already fixes this and i wasn't even aware!
                    if (!ClassTransformer.optifineVersion.equals("NONE")) {
                        return;
                    }

                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();


                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        int f6index = -1;

                        for (LocalVariableNode variable : methodNode.localVariables) {
                            if (variable.name.equals("f6")) {
                                f6index = variable.index;
                                break;
                            }
                        }

                        if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETFIELD) {
                            String fieldInsnName = mapFieldNameFromNode(next);

                            if (fieldInsnName.equals("fogColorBlue") || fieldInsnName.equals("field_175081_S")) {
                                if (next.getNext().getOpcode() == Opcodes.FDIV) {
                                    final AbstractInsnNode newInsn = next.getNext().getNext().getNext().getNext().getNext().getNext();
                                    if (newInsn instanceof VarInsnNode) {
                                        methodNode.instructions.insertBefore(newInsn, clampVariable(f6index));
                                    }
                                }
                            }
                        }
                    }

                    break;
                }

                case "func_78479_a":
                case "setupCameraTransform": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESPECIAL) {
                            final String methodInsnName = mapMethodNameFromNode(next);

                            if (methodInsnName.equals("setupViewBobbing") || methodInsnName.equals("func_78475_f")) {
                                LabelNode ifne = new LabelNode();
                                methodNode.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious(), removeViewBobbing(ifne));
                                methodNode.instructions.insertBefore(next.getNext(), ifne);
                            }
                        }
                    }
                }
            }
        }
    }

    private InsnList removeViewBobbing(LabelNode ifne) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "removeViewBobbing", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        return list;
    }

    private InsnList changeCameraType(int movingobjectpositionIndex, int d0Index, int d1Index, int d2Index, int d4Index, int d5Index, int d6Index, int f3Index, int f4Index, int f5Index, boolean useNormalIndex) {
        if (useNormalIndex) {
            movingobjectpositionIndex = 26;
            d0Index = 4;
            d1Index = 6;
            d2Index = 8;
            d4Index = 16;
            d5Index = 18;
            d6Index = 20;
            f3Index = 23;
            f4Index = 24;
            f5Index = 25;
        }

        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "betterCamera", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", isDevelopment() ? "mc" : "field_78531_r", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", isDevelopment() ? "theWorld" : "field_71441_e", "Lnet/minecraft/client/multiplayer/WorldClient;"));
        list.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/util/Vec3"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.DLOAD, d0Index));
        list.add(new VarInsnNode(Opcodes.FLOAD, f3Index));
        list.add(new InsnNode(Opcodes.F2D));
        list.add(new InsnNode(Opcodes.DADD));
        list.add(new VarInsnNode(Opcodes.DLOAD, d1Index));
        list.add(new VarInsnNode(Opcodes.FLOAD, f4Index));
        list.add(new InsnNode(Opcodes.F2D));
        list.add(new InsnNode(Opcodes.DADD));
        list.add(new VarInsnNode(Opcodes.DLOAD, d2Index));
        list.add(new VarInsnNode(Opcodes.FLOAD, f5Index));
        list.add(new InsnNode(Opcodes.F2D));
        list.add(new InsnNode(Opcodes.DADD));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/util/Vec3", "<init>", "(DDD)V", false));
        list.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/util/Vec3"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.DLOAD, d0Index));
        setupChanges(d4Index, f3Index, list);
        list.add(new VarInsnNode(Opcodes.FLOAD, f5Index));
        list.add(new InsnNode(Opcodes.F2D));
        list.add(new InsnNode(Opcodes.DADD));
        list.add(new VarInsnNode(Opcodes.DLOAD, d1Index));
        setupChanges(d6Index, f4Index, list);
        list.add(new VarInsnNode(Opcodes.DLOAD, d2Index));
        setupChanges(d5Index, f5Index, list);
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/util/Vec3", "<init>", "(DDD)V", false));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/multiplayer/WorldClient", isDevelopment() ? "rayTraceBlocks" : "func_147447_a", "(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;ZZZ)Lnet/minecraft/util/MovingObjectPosition;", false));
        list.add(new VarInsnNode(Opcodes.ASTORE, movingobjectpositionIndex));
        list.add(ifeq);
        return list;
    }

    private void setupChanges(int doubleIndex, int floatIndex, InsnList list) {
        list.add(new VarInsnNode(Opcodes.DLOAD, doubleIndex));
        list.add(new InsnNode(Opcodes.DSUB));
        list.add(new VarInsnNode(Opcodes.FLOAD, floatIndex));
        list.add(new InsnNode(Opcodes.F2D));
        list.add(new InsnNode(Opcodes.DADD));
    }

    private InsnList clampVariable(int f6index) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.FLOAD, f6index));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Float", "isInfinite", "(F)Z", false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.FLOAD, f6index));
        list.add(new InsnNode(Opcodes.DCONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Math", "nextAfter", "(FD)F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, f6index));
        list.add(ifeq);
        return list;
    }

    private InsnList clampLightmap() {
        // using srg name in dev crashes? Ok Forge
        final String clamp_float = isDevelopment() ? "clamp_float" : "func_76131_a";
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.FLOAD, 12));
        clampFloat(12, 13, clamp_float, list);
        clampFloat(13, 14, clamp_float, list);
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new InsnNode(Opcodes.FCONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/MathHelper", clamp_float, "(FFF)F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 14));
        return list;
    }

    private void clampFloat(int storeIndex, int loadIndex, String clamp_float, InsnList list) {
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new InsnNode(Opcodes.FCONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/MathHelper", clamp_float, "(FFF)F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, storeIndex));
        list.add(new VarInsnNode(Opcodes.FLOAD, loadIndex));
    }

    private InsnList getEyePosition() {
        // using srg name in dev crashes? Ok Forge
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.FCONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/entity/Entity",
            isDevelopment() ? "getPositionEyes" : "func_174824_e",
            "(F)Lnet/minecraft/util/Vec3;",
            false));
        return list;
    }

    private InsnList toggleCullingStatus(boolean status) {
        InsnList list = new InsnList();
        list.add(new InsnNode(status ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "club/sk1er/patcher/util/world/entity/culling/EntityCulling", "shouldPerformCulling", "Z"));
        return list;
    }

    private InsnList getStoreCameraInsn(int var) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, var));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC,
            "club/sk1er/patcher/util/world/particles/ParticleCulling",
            "camera",
            "Lnet/minecraft/client/renderer/culling/ICamera;"));
        return list;
    }

    private InsnList setFOVLabelAndUpdateSmoothZoom(LabelNode ifne) {
        InsnList list = new InsnList();
        list.add(ifne);
        if (!ClassTransformer.optifineVersion.equals("NONE")) {
            list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "smoothZoomAnimation", "Z"));
            LabelNode ifeq = new LabelNode();
            list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
            list.add(new VarInsnNode(Opcodes.FLOAD, 4));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/tweaker/asm/optifine/EntityRendererTransformer", "getSmoothModifier", "()F", false));
            list.add(new InsnNode(Opcodes.FMUL));
            list.add(new VarInsnNode(Opcodes.FSTORE, 4));
            list.add(ifeq);
        }
        return list;
    }

    private InsnList modifyKeyDownIfToggleToZoom() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "toggleToZoom", "Z"));
        LabelNode ifDisabled = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifDisabled));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/hooks/EntityRendererHook", "getZoomState", "(Z)Z", false));
        list.add(ifDisabled);
        return list;
    }

    private InsnList createLabel(LabelNode ifne) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "removeWaterFov", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        return list;
    }

    private InsnList assignCreatedLightmap() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/EntityRenderer", "createdLightmap", "Z"));
        return list;
    }

    private InsnList checkFullbright() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/world/FullbrightTicker", "isFullbright", "()Z", false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "createdLightmap", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }

    private InsnList fixParallax() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "parallaxFix", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new LdcInsnNode(0.05F));
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifeq);
        list.add(new LdcInsnNode(-0.10000000149011612F));
        list.add(gotoInsn);
        return list;
    }

    private boolean checkNode(AbstractInsnNode node) {
        if (node.getNext() == null) return false;
        if (node.getOpcode() == Opcodes.ICONST_1) {
            AbstractInsnNode next = node.getNext();
            if (next.getOpcode() == Opcodes.PUTFIELD) {
                FieldInsnNode fieldInsn = (FieldInsnNode) next;
                return fieldInsn.name.equals("smoothCamera") || fieldInsn.name.equals("field_74326_T");
            }
        }
        return false;
    }

    private boolean checkDivNode(AbstractInsnNode node) {
        if (node.getOpcode() == Opcodes.FDIV) {
            if (node.getPrevious().getOpcode() == Opcodes.LDC) {
                LdcInsnNode prev = (LdcInsnNode) node.getPrevious();
                if (prev.cst instanceof Float) {
                    Float f = (Float) prev.cst;
                    return f.equals(4f);
                }
            }
        }
        return false;
    }

    private boolean checkZoomActiveNode(AbstractInsnNode node, int zoomActiveIndex) {
        if (node.getOpcode() == Opcodes.ILOAD) {
            VarInsnNode n = (VarInsnNode) node;
            if (n.var == zoomActiveIndex) {
                return node.getNext().getOpcode() == Opcodes.IFEQ;
            }
        }
        return false;
    }

    private InsnList getDivisor() {
        InsnList list = new InsnList();
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/patcher/tweaker/asm/optifine/EntityRendererTransformer",
                "getModifier",
                "()F",
                false)); // Call my method
        return list;
    }

    private InsnList callResetAndSensChange() {
        InsnList list = new InsnList();
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/patcher/tweaker/asm/optifine/EntityRendererTransformer",
                "resetCurrent",
                "()V",
                false));
        list.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            "club/sk1er/patcher/hooks/EntityRendererHook",
            "reduceSensitivity",
            "()V",
            false));
        return list;
    }

    private InsnList setZoomed(int zoomActiveIndex) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ILOAD, zoomActiveIndex));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "club/sk1er/patcher/tweaker/asm/optifine/EntityRendererTransformer", "zoomed", "Z"));
        return list;
    }

    public static float getModifier() {
        if (!PatcherConfig.scrollToZoom) {
            return normalModifier;
        }
        long time = System.currentTimeMillis();
        long timeSinceLastChange = time - lastMillis;
        if (!zoomed) lastMillis = time;

        int moved = Mouse.getDWheel();

        if (moved > 0) {
            smoothZoomProgress = 0f;
            hasScrolledYet = true;
            desiredModifier += 0.25f * desiredModifier;
        } else if (moved < 0) {
            smoothZoomProgress = 0f;
            hasScrolledYet = true;
            desiredModifier -= 0.25f * desiredModifier;
            EntityRendererHook.fixMissingChunks();
        }

        if (desiredModifier < 0.8) {
            desiredModifier = 0.8f;
        }

        if (desiredModifier > 600) {
            desiredModifier = 600f;
        }
        if (PatcherConfig.smoothZoomAnimationWhenScrolling) {
            if (hasScrolledYet && smoothZoomProgress < 1) {
                EntityRendererHook.fixMissingChunks();
                smoothZoomProgress += 0.004F * timeSinceLastChange;
                smoothZoomProgress = smoothZoomProgress > 1 ? 1 : smoothZoomProgress;
                return currentModifier += (desiredModifier - currentModifier) * calculateEasing(smoothZoomProgress);
            }
        } else currentModifier = desiredModifier;
        return desiredModifier;
    }

    public static float getSmoothModifier() {
        long time = System.currentTimeMillis();
        long timeSinceLastChange = time - lastMillis;
        lastMillis = time;
        if (zoomed) {
            if (hasScrolledYet) return 1f;
            if (smoothZoomProgress < 1) {
                smoothZoomProgress += 0.005F * timeSinceLastChange;
                smoothZoomProgress = smoothZoomProgress > 1 ? 1 : smoothZoomProgress;
                return 4f - 3f * calculateEasing(smoothZoomProgress);
            }
        } else {
            if (hasScrolledYet) {
                hasScrolledYet = false;
                smoothZoomProgress = 1f;
            }
            if (smoothZoomProgress > 0) {
                smoothZoomProgress -= 0.005F * timeSinceLastChange;
                smoothZoomProgress = smoothZoomProgress < 0 ? 0 : smoothZoomProgress;
                EntityRendererHook.fixMissingChunks();
                float progress = 1 - smoothZoomProgress;
                float diff = PatcherConfig.scrollToZoom ? 1f / currentModifier : 0.25f;
                return diff + (1 - diff) * calculateEasing(progress);
            }
        }
        return 1f;
    }

    private static float calculateEasing(float x) {
        switch (PatcherConfig.smoothZoomAlgorithm) {
            case 0:
                return Animations.IN_OUT_QUAD.getValue(x);

            case 1:
                return Animations.IN_OUT_CIRCULAR.getValue(x);

            case 2:
                return Animations.OUT_QUINT.getValue(x);
        }

        // fallback
        return Animations.IN_OUT_QUAD.getValue(x);
    }

    public static void resetCurrent() {
        hasScrolledYet = false;
        currentModifier = normalModifier;
        desiredModifier = normalModifier;
        smoothZoomProgress = 0f;
    }
}
