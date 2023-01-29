package badasintended.slotlink.mixin;

import badasintended.slotlink.network.NetworkState;
import badasintended.slotlink.network.NetworkStateHolder;
import java.util.List;
import java.util.concurrent.Executor;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements NetworkStateHolder {

    @Shadow
    public abstract PersistentStateManager getPersistentStateManager();

    @Unique
    private NetworkState networkState;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void slotlink$initNetworkState(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<?> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<?> spawners, boolean shouldTickTime, CallbackInfo ci) {
        ServerWorld self = (ServerWorld) (Object) this;
        networkState = getPersistentStateManager().getOrCreate(nbt -> NetworkState.create(self, nbt), NetworkState::new, "slotlink");
    }

    @NotNull
    @Override
    public NetworkState slotlink$getNetworkState() {
        return networkState;
    }

}
