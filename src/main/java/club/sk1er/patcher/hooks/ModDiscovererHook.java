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

package club.sk1er.patcher.hooks;

import club.sk1er.patcher.jar.AsyncModDiscoverer;
import kotlin.Pair;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ModCandidate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModDiscovererHook {
    public static List<ModContainer> identifyModsAsync(List<ModCandidate> candidates, ASMDataTable dataTable, List<File> nonModLibs) {
        AsyncModDiscoverer asyncModDiscoverer = new AsyncModDiscoverer(candidates, nonModLibs, dataTable);
        Pair<List<ModContainer>, List<File>> pair = asyncModDiscoverer.discover();
        nonModLibs.addAll(pair.getSecond());
        return pair.getFirst();
    }
}
