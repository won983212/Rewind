package com.won983212.rewind;

import com.won983212.rewind.client.ClientDist;
import com.won983212.rewind.recorder.Recorder;
import com.won983212.rewind.replayer.Replayer;
import com.won983212.rewind.server.ServerDist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RewindMod.MODID)
public class RewindMod {
    public static final String MODID = "rewind";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final Recorder RECORDER = new Recorder();
    public static final Replayer REPLAYER = new Replayer();
    private final CommonDist proxy;

    public RewindMod() {
        proxy = DistExecutor.safeRunForDist(() -> ClientDist::new, () -> ServerDist::new);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        proxy.onCommonSetup(event);
    }
}
