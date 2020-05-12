/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.config;

import club.sk1er.vigilance.Vigilant;

import java.io.File;

public class PatcherSoundConfig extends Vigilant {

    public PatcherSoundConfig() {
        super(new File("./config/patcher_sounds.toml"));
        initialize();
    }
}
