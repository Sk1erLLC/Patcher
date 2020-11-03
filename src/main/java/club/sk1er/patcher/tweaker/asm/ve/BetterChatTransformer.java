package club.sk1er.patcher.tweaker.asm.ve;

import club.sk1er.patcher.tweaker.transform.CommonTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class BetterChatTransformer implements CommonTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"com.orangemarshall.enhancements.modules.chat.BetterChat"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);

            if (methodName.equals("getChatComponent") || methodName.equals("func_146236_a")) {
                this.changeChatComponentHeight(method);
                break;
            }
        }
    }
}
