package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class RenderGlobalTransformer implements PatcherTransformer {

  /**
   * The class name that's being transformed
   *
   * @return the class name
   */
  @Override
  public String[] getClassName() {
    return new String[] {"net.minecraft.client.renderer.RenderGlobal"};
  }

  /**
   * Perform any asm in order to transform code
   *
   * @param classNode the transformed class node
   * @param name the transformed class name
   */
  @Override
  public void transform(ClassNode classNode, String name) {
    for (MethodNode methodNode : classNode.methods) {
      String methodName = mapMethodName(classNode, methodNode);

      if (methodName.equals("renderClouds") || methodName.equals("func_180447_b")) {
        methodNode.instructions.insertBefore(
            methodNode.instructions.getFirst(), patcherCloudRenderer());
      } else if (methodName.equals("setWorldAndLoadRenderers")
          || methodName.equals("func_72732_a")) {
        methodNode.instructions.insertBefore(
            methodNode.instructions.getLast().getPrevious(), fixResourceLeak());
      }
    }
  }

  private InsnList patcherCloudRenderer() {
    InsnList list = new InsnList();
    list.add(
        new FieldInsnNode(
            Opcodes.GETSTATIC,
            "club/sk1er/patcher/Patcher",
            "instance",
            "Lclub/sk1er/patcher/Patcher;"));
    list.add(
        new MethodInsnNode(
            Opcodes.INVOKEVIRTUAL,
            "club/sk1er/patcher/Patcher",
            "getCloudHandler",
            "()Lclub/sk1er/patcher/util/cloud/CloudHandler;",
            false));
    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
    list.add(
        new FieldInsnNode(
            Opcodes.GETFIELD,
            "net/minecraft/client/renderer/RenderGlobal",
            "field_72773_u", // cloudTickCounter
            "I"));
    list.add(new VarInsnNode(Opcodes.FLOAD, 1));
    list.add(
        new MethodInsnNode(
            Opcodes.INVOKEVIRTUAL,
            "club/sk1er/patcher/util/cloud/CloudHandler",
            "renderClouds",
            "(IF)Z",
            false));
    LabelNode ifeq = new LabelNode();
    list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
    list.add(new InsnNode(Opcodes.RETURN));
    list.add(ifeq);
    return list;
  }

  private InsnList fixResourceLeak() {
    InsnList list = new InsnList();
    list.add(new VarInsnNode(Opcodes.ALOAD, 1));
    LabelNode ifnonnull = new LabelNode();
    list.add(new JumpInsnNode(Opcodes.IFNONNULL, ifnonnull));
    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
    list.add(
        new FieldInsnNode(
            Opcodes.GETFIELD,
            "net/minecraft/client/renderer/RenderGlobal",
            "field_175009_l", // chunksToUpdate
            "Ljava/util/Set;"));
    list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Set", "clear", "()V", true));
    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
    list.add(
        new FieldInsnNode(
            Opcodes.GETFIELD,
            "net/minecraft/client/renderer/RenderGlobal",
            "field_72755_R", // renderInfos
            "Ljava/util/List;"));
    list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "clear", "()V", true));
    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
    list.add(
        new FieldInsnNode(
            Opcodes.GETFIELD,
            "net/minecraft/client/renderer/RenderGlobal",
            "field_175008_n", // viewFrustum
            "Lnet/minecraft/client/renderer/ViewFrustum;"));
    LabelNode ifnull = new LabelNode();
    list.add(new JumpInsnNode(Opcodes.IFNULL, ifnull));
    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
    list.add(
        new FieldInsnNode(
            Opcodes.GETFIELD,
            "net/minecraft/client/renderer/RenderGlobal",
            "field_175008_n", // viewFrustum
            "Lnet/minecraft/client/renderer/ViewFrustum;"));
    list.add(
        new MethodInsnNode(
            Opcodes.INVOKEVIRTUAL,
            "net/minecraft/client/renderer/ViewFrustum",
            "func_178160_a", // deleteGlResources
            "()V",
            false));
    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
    list.add(new InsnNode(Opcodes.ACONST_NULL));
    list.add(
        new FieldInsnNode(
            Opcodes.PUTFIELD,
            "net/minecraft/client/renderer/RenderGlobal",
            "field_175008_n", // viewFrustum
            "Lnet/minecraft/client/renderer/ViewFrustum;"));
    list.add(ifnonnull);
    list.add(ifnull);
    return list;
  }
}
