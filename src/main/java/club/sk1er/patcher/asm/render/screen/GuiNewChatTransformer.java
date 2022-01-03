package club.sk1er.patcher.asm.render.screen;

import club.sk1er.patcher.tweaker.transform.CommonTransformer;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.ListIterator;

public class GuiNewChatTransformer implements CommonTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiNewChat"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            switch (methodName) {
                case "getChatComponent":
                case "func_146236_a": {
                    this.changeChatComponentHeight(methodNode);
                    break;
                }
            }
        }
    }
}
