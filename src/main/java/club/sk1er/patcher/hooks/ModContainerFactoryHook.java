package club.sk1er.patcher.hooks;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.discovery.asm.ASMModParser;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.util.Map;

@SuppressWarnings("unused")
public class ModContainerFactoryHook {

    public static ModContainer build(ASMModParser parser, ModCandidate container, Map<Type, Constructor<? extends ModContainer>> modTypes) {
        final String className = parser.getASMType().getClassName();
        for (ModAnnotation annotation : parser.getAnnotations()) {
            if (modTypes.containsKey(annotation.getASMType())) {
                FMLLog.fine("Identified a mod of type %s (%s) - loading", annotation.getASMType(), className);
                try {
                    final ModContainer ret = modTypes.get(annotation.getASMType()).newInstance(className, container, annotation.getValues());
                    if (!ret.shouldLoadInEnvironment()) {
                        FMLLog.fine("Skipping mod %s, container opted to not load.", className);
                        return null;
                    }

                    return ret;
                } catch (Exception e) {
                    FMLLog.log(Level.ERROR, e, "Unable to construct %s container", annotation.getASMType().getClassName());
                    return null;
                }
            }
        }

        return null;
    }
}
