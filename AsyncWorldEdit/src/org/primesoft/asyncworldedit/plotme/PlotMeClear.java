package org.primesoft.asyncworldedit.plotme;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitBiomeType;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.worldcretornica.plotme.PMCommand;
import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotMapInfo;
import com.worldcretornica.plotme.PlotMe;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.primesoft.asyncworldedit.ConfigProvider;
import org.primesoft.asyncworldedit.PluginMain;
import org.primesoft.asyncworldedit.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.worldcretornica.plotme.PlotManager.*;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class PlotMeClear extends PlotMeWrapper implements Listener {

    private static WorldEditPlugin worldEditPlugin;
    private static Map<String, Set<String>> aliases;
    private static final Pattern spacePattern = Pattern.compile(" ");

    private PlotMeClear(PMCommand pmCommand) {
        super(pmCommand);
    }

    public static void enable(JavaPlugin plugin) {
        if (ConfigProvider.isPlotMeClearingEnabled()) {
            worldEditPlugin = Util.hook("WorldEdit", WorldEditPlugin.class);
            PlotMe plotMe = Util.hook("PlotMe", PlotMe.class);
            if (worldEditPlugin != null && plotMe != null) {
                aliases = initializeAliases(SUPPORTED_COMMANDS);
                if (fetchAliases(plugin)) {
                    checkVersion(plotMe);
                    PlotMeClear plotMeClear = new PlotMeClear(new PMCommand(plotMe));
                    plugin.getServer().getPluginManager().registerEvents(plotMeClear, plugin);
                    PluginMain.log("PlotMe clearing enabled.");
                }
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (isCommand("CommandClear", event)) {
            handleClear(event.getPlayer());
        } else if (isCommand("CommandReset", event)) {
            handlePlotReset(event.getPlayer());
        } else if (isCommand("CommandMove", event)) {
            handlePlotMove(event.getPlayer(), getArgs(event.getMessage()));
        }
    }

    @Override
    public void asyncClear(Player player, Plot plot) {
        LocalPlayer lplayer = worldEditPlugin.wrapPlayer(player);
        LocalSession session = WorldEdit.getInstance().getSession(player.getName());
        EditSession editSession = session.createEditSession(lplayer);

        Region[] levels = getPlotLevels(player.getWorld(), plot);

        for(int i=0; i<levels.length; i++) {
            Region level = levels[i];
            setBiome(level, BukkitBiomeType.PLAINS);
        }

        PlotMapInfo pmi = getMap(player);

        try {
            editSession.setBlocks(levels[0], new BaseBlock(pmi.BottomBlockId, (int) pmi.BottomBlockValue));
            editSession.setBlocks(levels[1], new BaseBlock(pmi.PlotFillingBlockId, (int) pmi.PlotFillingBlockValue));
            editSession.setBlocks(levels[2], new BaseBlock(pmi.PlotFloorBlockId, (int) pmi.PlotFloorBlockValue));
            editSession.setBlocks(levels[3], new BaseBlock(BlockID.AIR));
        } catch (MaxChangedBlocksException e) {}

        Location loc = getPlotBottomLoc(player.getWorld(), plot.id);
        adjustWall(loc);
    }

    @Override
    // TODO Fix NPE and test
    public void asyncMove(Player player, Plot plot1, Plot plot2) {
        LocalPlayer lplayer = worldEditPlugin.wrapPlayer(player);
        LocalSession session = WorldEdit.getInstance().getSession(player.getName());
        EditSession editSession = session.createEditSession(lplayer);

        Region[] r1 = getPlotLevels(player.getWorld(), plot1);
        Region[] r2 = getPlotLevels(player.getWorld(), plot2);

        for(int i= 0; i<4; i++) {
            CuboidClipboard copy1 = copyLevel(r1[i]);
            CuboidClipboard copy2 = copyLevel(r2[i]);
            pasteLevel(editSession, copy1, copy2);
            pasteLevel(editSession, copy2, copy1);
        }
    }

    private static void checkVersion(PlotMe plotMe) {
        String version = plotMe.getDescription().getVersion();
        if(!version.equals(SUPPORTED_VERSION)) {
            PluginMain.log(Level.WARNING, "PlotMe clearing expected version '"+
                    SUPPORTED_VERSION + "' but found '" + version + "'");
        }
    }

    private static Map<String, Set<String>> initializeAliases(String... commands) {
        Map<String, Set<String>> map = new HashMap<>();
        for(String command : commands) {
            map.put(command, new HashSet<String>());
        }
        return map;
    }

    private static boolean fetchAliases(Plugin plugin) {
        YamlConfiguration yaml;
        File plugins = plugin.getDataFolder().getParentFile();
        File plotme = new File(plugins, "PlotMe");
        File config = new File(plotme, "config.yml");
        if (config.exists()) {
            yaml = YamlConfiguration.loadConfiguration(config);
            String language = yaml.getString("Language");
            File captions = new File(plugins, "PlotMe" + File.separator + "caption-" + language + ".yml");
            if (captions.exists()) {
                yaml = YamlConfiguration.loadConfiguration(captions);
                Map<String, String> commands = new HashMap<>();
                for (String key : aliases.keySet()) {
                    commands.put(key, yaml.getString(key));
                }
                File jar = new File(plugins, "PlotMe.jar");
                if (jar.exists()) {
                    try {
                        ZipFile zip = new ZipFile(jar);
                        ZipEntry in = zip.getEntry("plugin.yml");
                        InputStream stream = zip.getInputStream(in);
                        YamlConfiguration pluginYml = YamlConfiguration.loadConfiguration(stream);
                        List<String> plotMeAliases = pluginYml.getStringList("commands.plotme.aliases");
                        plotMeAliases.add("plotme");
                        String base = "/%s %s";
                        for (Map.Entry<String, String> entry : commands.entrySet()) {
                            for (String alias : plotMeAliases) {
                                aliases.get(entry.getKey())
                                        .add(String.format(base, alias, entry.getValue())
                                                .toLowerCase());
                            }
                        }
                        return true;
                    } catch (IOException e) {
                        PluginMain.log("Error fetching PlotMe aliases");
                    }
                }
            }
        }
        return false;
    }

    private boolean isCommand(String type, PlayerCommandPreprocessEvent event) {
        Set<String> commands = aliases.get(type);
        if (commands != null) {
            String base = getCommandBase(event.getMessage());
            if (base != null && commands.contains(base)) {
                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }

    private String[] getArgs(String command) {
        String[] args = spacePattern.split(command);
        if (args.length > 1) {
            return Arrays.copyOfRange(args, 1, args.length);
        }
        return new String[0];
    }

    private String getCommandBase(String command) {
        String[] args = spacePattern.split(command);
        if (args.length >= 2) {
            return (args[0] + " " + args[1]).toLowerCase();
        }
        return null;
    }

    private void setBiome(Region region, BiomeType biome) {
        HashSet<Long> alreadyVisited = new HashSet<Long>();
        for (Vector pt : region) {
            if (!alreadyVisited.contains((long) pt.getBlockX() << 32 | pt.getBlockZ())) {
                alreadyVisited.add(((long) pt.getBlockX() << 32 | pt.getBlockZ()));
                region.getWorld().setBiome(pt.toVector2D(), biome);
            }
        }
     }

    private CuboidClipboard copyLevel(Region region) {
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        LocalWorld world = region.getWorld();
        CuboidClipboard clipboard  = new CuboidClipboard(
                max.subtract(min).add(Vector.ONE),
                min, new Vector());
        LocalEntity[] entities = world.getEntities(region);
        for (LocalEntity entity : entities) {
            clipboard.storeEntity(entity);
        }
        world.killEntities(entities);
        return clipboard;
    }

    private void pasteLevel(EditSession editSession, CuboidClipboard clipboard1, CuboidClipboard clipboard2) {
        try {
            Vector pos = clipboard2.getOrigin();
            clipboard1.paste(editSession, pos, true);
            clipboard1.pasteEntities(pos);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    private Region[] getPlotLevels(World world, Plot plot) {
        PlotMapInfo pmi = getMap(world);
        LocalWorld lworld = BukkitUtil.getLocalWorld(world);
        Vector bottom = BukkitUtil.toVector(getPlotBottomLoc(world, plot.id));
        Vector top = BukkitUtil.toVector(getPlotTopLoc(world, plot.id));
        Region[] levels = new Region[4];
        Vector p1, p2;
        p1 = bottom.subtract(0, 1, 0);
        p2 = top.setY(bottom.getY());
        levels[0] = new CuboidRegion(lworld, p1, p2); // bedrock
        p1 = p1.add(0, 1, 0);
        p2 = p2.setY(pmi.RoadHeight - 1);
        levels[1] = new CuboidRegion(lworld, p1, p2); // dirt
        p2 = p2.add(0, 1, 0);
        p1 = p1.setY(p2.getY());
        levels[2] = new CuboidRegion(lworld, p1, p2); // grass
        p1 = p1.add(0, 1, 0);
        p2 = p2.setY(lworld.getMaxY());
        levels[3] = new CuboidRegion(lworld, p1, p2); // sky
        return levels;
    }

}
