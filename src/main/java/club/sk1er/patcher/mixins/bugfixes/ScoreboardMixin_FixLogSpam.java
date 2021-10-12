package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin_FixLogSpam {
    @Shadow public abstract ScorePlayerTeam getTeam(String p_96508_1_);

    @Inject(method = "removeTeam", at = @At("HEAD"), cancellable = true)
    private void patcher$checkIfTeamIsNull(ScorePlayerTeam team, CallbackInfo ci) {
        if (team == null) ci.cancel();
    }

    @Redirect(method = "removeTeam", at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0, remap = false))
    private <K, V> V patcher$checkIfRegisteredNameIsNull(Map<K, V> instance, K o) {
        if (o != null) return instance.remove(o);
        return null;
    }

    @Inject(method = "removeObjective", at = @At("HEAD"), cancellable = true)
    private void patcher$checkIfObjectiveIsNull(ScoreObjective objective, CallbackInfo ci) {
        if (objective == null) ci.cancel();
    }

    @Redirect(method = "removeObjective", at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0, remap = false))
    private <K, V> V patcher$checkIfNameIsNull(Map<K, V> instance, K o) {
        if (o != null) return instance.remove(o);
        return null;
    }

    @Inject(method = "createTeam", at = @At(value = "CONSTANT", args = "stringValue=A team with the name '"), cancellable = true)
    private void patcher$returnExistingTeam(String name, CallbackInfoReturnable<ScorePlayerTeam> cir) {
        cir.setReturnValue(this.getTeam(name));
    }

    @Inject(method = "removePlayerFromTeam", at = @At(value = "CONSTANT", args = "stringValue=Player is either on another team or not on any team. Cannot remove from team '"), cancellable = true)
    private void patcher$silenceException(CallbackInfo ci) {
        ci.cancel();
    }
}
