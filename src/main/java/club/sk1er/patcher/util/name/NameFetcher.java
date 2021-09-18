package club.sk1er.patcher.util.name;

import club.sk1er.patcher.Patcher;
import gg.essential.api.EssentialAPI;
import gg.essential.api.utils.Multithreading;
import gg.essential.api.utils.mojang.Name;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
                    CompletableFuture<UUID> uuid = EssentialAPI.getMojangAPI().getUUID(username);
                    if (uuid == null) return;
                    this.uuid = uuid.get();
                } catch (Exception e) {
                    Patcher.instance.getLogger().warn("Failed fetching UUID.", e);
                    return;
                }

                names.clear();
                if (uuid != null) {
                    List<Name> nameHistory = EssentialAPI.getMojangAPI().getNameHistory(uuid);
                    if (nameHistory == null || nameHistory.isEmpty()) return;
                    name = nameHistory.get(nameHistory.size() - 1).getName();

                    for (final Name history : nameHistory) {
                        final String name = history.getName();
                        //noinspection ConstantConditions
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
