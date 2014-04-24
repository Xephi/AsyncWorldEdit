package org.primesoft.asyncworldedit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class Util {

    public static <T> T hook(String name, Class<T> clazz) {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(name);
        if (plugin != null && clazz.isInstance(plugin)) {
            return clazz.cast(plugin);
        }
        return null;
    }

    public static boolean pluginExists(String name, Class clazz) {
        return hook(name, clazz) != null;
    }

}
