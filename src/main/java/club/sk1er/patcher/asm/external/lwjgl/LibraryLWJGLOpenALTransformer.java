package club.sk1er.patcher.asm.external.lwjgl;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class LibraryLWJGLOpenALTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"paulscode.sound.libraries.LibraryLWJGLOpenAL"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("init")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) next).name.equals("create")) {
                        method.instructions.set(next, new MethodInsnNode(
                            Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/world/sound/audioswitcher/LibraryLWJGLOpenALImpl",
                            "createAL", "()V", false)
                        );
                        break;
                    }
                }

                break;
            }
        }
    }
}
