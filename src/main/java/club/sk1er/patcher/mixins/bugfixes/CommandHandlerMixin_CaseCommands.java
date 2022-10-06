package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.command.CommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Locale;

@Mixin(CommandHandler.class)
public class CommandHandlerMixin_CaseCommands {
    @ModifyArg(method = {"executeCommand", "getTabCompletionOptions"}, at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
    private Object patcher$makeLowerCaseForGet(Object s) {
        if (s instanceof String) {
            return ((String) s).toLowerCase(Locale.ENGLISH);
        }
        return s;
    }

    @ModifyArg(method = "registerCommand", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", remap = false), index = 0)
    private Object patcher$makeLowerCaseForPut(Object s) {
        if (s instanceof String) {
            return ((String) s).toLowerCase(Locale.ENGLISH);
        }
        return s;
    }

    @ModifyArg(method = "getTabCompletionOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/CommandBase;doesStringStartWith(Ljava/lang/String;Ljava/lang/String;)Z"), index = 0)
    private String patcher$makeLowerCaseForTabComplete(String s) {
        return s != null ? s.toLowerCase(Locale.ENGLISH) : null;
    }

}
