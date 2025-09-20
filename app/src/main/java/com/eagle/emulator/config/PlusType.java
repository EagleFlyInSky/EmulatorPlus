package com.eagle.emulator.config;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlusType {


    AOPAOP("com.aopaop.app", true),
    MALDIVES("net.miririt.maldivesplayer", false),
    JOIPLAY("cyou.joiplay.joiplay", false),
    JOIPLAY_RUFFLE("cyou.joiplay.runtime.ruffle", false),
    JOIPLAY_RPGMAKER("cyou.joiplay.runtime.rpgmaker", false),
    TYRANOR("com.akira.tyranoemu", false),
    MOONLIGHT("com.limelight", false),
    MOONLIGHT_DEBUG("com.limelight.debug", false),
    GAME_DREAM_FACTORY("com.GameDreamFactoryAndroid", false),
    LUDASHI("com.ludashi.benchmark", false),
    EXAGEAR("com.ludashi.benchmara", false),
    ES_DE("org.es_de.frontend", false),
    BEACON("com.radikal.gamelauncher", false),
    NETHERSX2("xyz.aethersx2.android", false),
    DOLPHIN("org.dolphinemu.dolphinemu", false),
    AZAHAR("io.github.lime3ds.android", false),
    WINLATOR("com.winlator", false),
    M64("org.mupen64plusae.v3.fzurita.pro", false),
    SATURN_EMU("com.explusalpha.SaturnEmu", false),
    PPSSPP("org.ppsspp.ppsspp", false),
    EGG_GAME("com.xiaoji.egggame", false);


    private final String packageName;

    private final boolean dexkit;


    public static PlusType getType(String packageName) {
        return Arrays.stream(PlusType.values()).filter(type -> type.getPackageName().equals(packageName)).findFirst().orElse(null);
    }


}
