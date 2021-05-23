/*
 * Copyright © 2021 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.util.name;

import club.sk1er.patcher.Patcher;
import gg.essential.api.utils.Multithreading;
import me.kbrewster.mojangapi.MojangAPI;
import me.kbrewster.mojangapi.profile.Name;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NameFetcher {
    private final List<String> names = new ArrayList<>();
    private final DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    private UUID uuid = null;
    private String name = null;

    public void execute(String username) {
        execute(username, true);
    }

    public void execute(String username, boolean async) {
        try {
            if (username.isEmpty()) {
                return;
            }

            Runnable fetchNames = () -> {
                name = username;
                uuid = null;
                try {
                    uuid = MojangAPI.getUUID(username);
                } catch (Exception e) {
                    Patcher.instance.getLogger().warn("Failed fetching UUID.", e);
                }

                names.clear();
                if (uuid != null) {
                    ArrayList<Name> nh = MojangAPI.getNameHistory(uuid);
                    if (!nh.isEmpty()) {
                        name = nh.get(nh.size() - 1).getName();
                    }
                    for (final Name history : nh) {
                        final String name = history.getName();
                        if (history.getChangedToAt() == 0) {
                            names.add(name);
                        } else {
                            names.add(String.format("%s » %s", name, format.format(history.getChangedToAt())));
                        }
                    }
                } else {
                    names.add("Failed to fetch " + username + "'s names");
                }
            };

            if (async) {
                Multithreading.runAsync(fetchNames);
            } else {
                fetchNames.run();
            }
        } catch (Exception e) {
            Patcher.instance.getLogger().warn("User catch failed, tried fetching {}.", username, e);
        }
    }

    public List<String> getNames() {
        return names;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
