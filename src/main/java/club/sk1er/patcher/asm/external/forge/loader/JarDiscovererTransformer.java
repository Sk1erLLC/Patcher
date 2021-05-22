package club.sk1er.patcher.asm.external.forge.loader;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class JarDiscovererTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.fml.common.discovery.JarDiscoverer"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("discover")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode) {
                        final MethodInsnNode insn = ((MethodInsnNode) next);
                        if (insn.name.equals("entries")) {
                            method.instructions.insertBefore(next, discoverCachedJar());
                        } else if (insn.name.equals("bindMetadata")) {
                            method.instructions.insert(insn, putCachedJar());
                        }
                    }
                }
            }
        }
    }

    private void getInstance(InsnList list) {
        list.add(new FieldInsnNode(
            Opcodes.GETSTATIC,
            "club/sk1er/patcher/util/forge/EntrypointCaching",
            "INSTANCE",
            "Lclub/sk1er/patcher/util/forge/EntrypointCaching;"
        ));
    }

    private InsnList discoverCachedJar() {
        InsnList list = new InsnList();

        getInstance(list);
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 4));
        list.add(new VarInsnNode(Opcodes.ALOAD, 6));
        list.add(new MethodInsnNode(
            Opcodes.INVOKEVIRTUAL,
            "club/sk1er/patcher/util/forge/EntrypointCaching",
            "discoverCachedEntrypoints",
            "(Lnet/minecraftforge/fml/common/discovery/ModCandidate;Lnet/minecraftforge/fml/common/discovery/ASMDataTable;Ljava/util/jar/JarFile;Lnet/minecraftforge/fml/common/MetadataCollection;)Ljava/util/List;",
            false
        ));
        list.add(new InsnNode(Opcodes.DUP));
        LabelNode normalDiscovery = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNULL, normalDiscovery));
        list.add(new InsnNode(Opcodes.ARETURN));
        list.add(normalDiscovery);
        list.add(new InsnNode(Opcodes.POP));

        return list;
    }

    private InsnList putCachedJar() {
        InsnList list = new InsnList();

        getInstance(list);
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 8));
        list.add(new MethodInsnNode(
            Opcodes.INVOKEVIRTUAL,
            "club/sk1er/patcher/util/forge/EntrypointCaching",
            "putCachedEntrypoints",
            "(Lnet/minecraftforge/fml/common/discovery/ModCandidate;Ljava/util/zip/ZipEntry;)V",
            false
        ));

        return list;
    }
}
