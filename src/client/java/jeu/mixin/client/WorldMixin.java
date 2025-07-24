package jeu.mixin.client;

import jeu.features.GlowingMushroomDetector;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class WorldMixin {
    @Inject(method = "setBlockState", at = @At("HEAD"))
    private void onBlockStateSet(BlockPos pos, BlockState newState, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        if((Object) this instanceof ClientWorld){
            ClientWorld world = (ClientWorld) (Object) this;
            BlockState oldState = world.getBlockState(pos);
            // on block break/set to air
            if (!oldState.isAir() && newState.isAir()) {
                GlowingMushroomDetector.INSTANCE.onBlockUpdate(pos);
            }
        }
    }
}

