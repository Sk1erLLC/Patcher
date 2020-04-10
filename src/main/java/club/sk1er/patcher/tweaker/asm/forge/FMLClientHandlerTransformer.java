package club.sk1er.patcher.tweaker.asm.forge;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import java.util.ListIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class FMLClientHandlerTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.fml.client.FMLClientHandler"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        FieldNode disallowedCharMatcherField = new FieldNode(
                Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
                "DISALLOWED_CHAR_MATCHER",
                "Lcom/google/common/base/CharMatcher;",
                null,
                null
        );

        classNode.fields.add(disallowedCharMatcherField);

        for (MethodNode methodNode : classNode.methods) {
            String methodName = methodNode.name;
            if ("<init>".equals(methodName)) {
                methodNode.instructions.insert(initializeDisallowedChars());
            } else if ("stripSpecialChars".equals(methodName)) {
                methodNode.instructions.clear();
                methodNode.localVariables.clear();
                methodNode.instructions.insert(fasterSpecialChars());
            }
        }
    }

    private InsnList fasterSpecialChars() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/fml/client/FMLClientHandler", "DISALLOWED_CHAR_MATCHER",
                "Lcom/google/common/base/CharMatcher;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/StringUtils", "func_76338_a", // stripControlCodes
                "(Ljava/lang/String;)Ljava/lang/String;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/google/common/base/CharMatcher", "removeFrom",
                "(Ljava/lang/CharSequence;)Ljava/lang/String;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }

    private InsnList initializeDisallowedChars() {
        InsnList list = new InsnList();
        list.add(new LdcInsnNode("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/google/common/base/CharMatcher", "anyOf",
                "(Ljava/lang/CharSequence;)Lcom/google/common/base/CharMatcher;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/google/common/base/CharMatcher", "negate",
                "()Lcom/google/common/base/CharMatcher;", false));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "net/minecraftforge/fml/client/FMLClientHandler", "DISALLOWED_CHAR_MATCHER",
                "Lcom/google/common/base/CharMatcher;"));
        return list;
    }
}
