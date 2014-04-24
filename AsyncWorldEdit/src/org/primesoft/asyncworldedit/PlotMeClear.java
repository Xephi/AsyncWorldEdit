package org.primesoft.asyncworldedit;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.worldcretornica.plotme.*;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Jukebox;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.worldcretornica.plotme.PlotManager.*;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class PlotMeClear implements Listener {

    private PMCommand pmCommand;
    private WorldEditPlugin worldEditPlugin;
    private Map<String, Set<String>> aliases;
    private static final Pattern spacePattern = Pattern.compile(" ");

    public PlotMeClear(JavaPlugin plugin) {
        if (ConfigProvider.isPlotMeClearingEnabled()) {
            worldEditPlugin = Util.hook("WorldEdit", WorldEditPlugin.class);
            PlotMe plotMe = Util.hook("PlotMe", PlotMe.class);
            if (worldEditPlugin != null && plotMe != null) {
                pmCommand = new PMCommand(plotMe);
                aliases = new HashMap<>();
                aliases.put("CommandClear", new HashSet<String>());
                aliases.put("CommandReset", new HashSet<String>());
                aliases.put("CommandMove", new HashSet<String>());
                if (fetchAliases(plugin)) {
                    plugin.getServer().getPluginManager().registerEvents(this, plugin);
                    PluginMain.log("PlotMe clearing enabled.");
                }
            }
        }
    }

    private boolean fetchAliases(Plugin plugin) {
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
        if (args.length <= 1) {
            return new String[0];
        }
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private String getCommandBase(String command) {
        String[] args = spacePattern.split(command);
        if (args.length >= 2) {
            return (args[0] + " " + args[1]).toLowerCase();
        }
        return null;
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if (isCommand("CommandClear", event)) {
            handlePlotClear(event.getPlayer());
        } else if (isCommand("CommandReset", event)) {
            handlePlotReset(event.getPlayer());
        } else if (isCommand("CommandMove", event)) {
            handlePlotMove(event.getPlayer(), getArgs(event.getMessage()));
        }
    }

    private boolean handlePlotClear(Player p) {
        if (PlotMe.cPerms(p, "PlotMe.admin.clear") || PlotMe.cPerms(p, "PlotMe.use.clear")) {
            if (!isPlotWorld(p)) {
                Send(p, RED + C("MsgNotPlotWorld"));
            } else {
                String id = getPlotId(p.getLocation());
                if (id.equals("")) {
                    Send(p, RED + C("MsgNoPlotFound"));
                } else {
                    if (!isPlotAvailable(id, p)) {
                        Plot plot = getPlotById(p, id);

                        if (plot.protect) {
                            Send(p, RED + C("MsgPlotProtectedCannotClear"));
                        } else {
                            String playername = p.getName();

                            World w = p.getWorld();

                            PlotMapInfo pmi = getMap(w);

                            double price = 0;

                            if (isEconomyEnabled(w)) {
                                price = pmi.ClearPrice;
                                double balance = PlotMe.economy.getBalance(playername);

                                if (balance >= price) {
                                    EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

                                    if (!er.transactionSuccess()) {
                                        Send(p, RED + er.errorMessage);
                                        warn(er.errorMessage);
                                        return true;
                                    }
                                } else {
                                    Send(p, RED + C("MsgNotEnoughClear") + " " + C("WordMissing") + " " + RESET + (price - balance) + RED + " " + PlotMe.economy.currencyNamePlural());
                                    return true;
                                }
                            }

                            if (plot.owner.equalsIgnoreCase(playername) || PlotMe.cPerms(p, "PlotMe.admin.clear")) {

                                asyncClearPlot(p, plot);
                                Send(p, C("MsgPlotCleared"));

                                if (isAdv)
                                    PlotMe.logger.info(LOG + playername + " " + C("MsgClearedPlot") + " " + id);
                            } else {
                                Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedClear"));
                            }
                        }
                    } else {
                        Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
                    }
                }
            }
        } else {
            Send(p, RED + C("MsgPermissionDenied"));
        }
        return true;
    }

    private boolean handlePlotReset(Player p) {
        if (PlotMe.cPerms(p, "PlotMe.admin.reset")) {
            if (!isPlotWorld(p)) {
                Send(p, RED + C("MsgNotPlotWorld"));
            } else {
                Plot plot = getPlotById(p.getLocation());

                if (plot == null) {
                    Send(p, RED + C("MsgNoPlotFound"));
                } else {
                    if (plot.protect) {
                        Send(p, RED + C("MsgPlotProtectedCannotReset"));
                    } else {
                        String id = plot.id;
                        World w = p.getWorld();

                        setBiome(w, id, plot, Biome.PLAINS);
                        asyncClearPlot(p, plot);

                        if (isEconomyEnabled(p)) {
                            if (plot.auctionned) {
                                String currentbidder = plot.currentbidder;

                                if (!currentbidder.equals("")) {
                                    EconomyResponse er = PlotMe.economy.depositPlayer(currentbidder, plot.currentbid);

                                    if (!er.transactionSuccess()) {
                                        Send(p, er.errorMessage);
                                        warn(er.errorMessage);
                                    } else {
                                        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                                            if (player.getName().equalsIgnoreCase(currentbidder)) {
                                                Send(player, C("WordPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.owner + " " + C("MsgWasReset") + " " + f(plot.currentbid));
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            PlotMapInfo pmi = getMap(p);

                            if (pmi.RefundClaimPriceOnReset) {
                                EconomyResponse er = PlotMe.economy.depositPlayer(plot.owner, pmi.ClaimPrice);

                                if (!er.transactionSuccess()) {
                                    Send(p, RED + er.errorMessage);
                                    warn(er.errorMessage);
                                    return true;
                                } else {
                                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                                        if (player.getName().equalsIgnoreCase(plot.owner)) {
                                            Send(player, C("WordPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.owner + " " + C("MsgWasReset") + " " + f(pmi.ClaimPrice));
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if (!isPlotAvailable(id, p)) {
                            getPlots(p).remove(id);
                        }

                        String name = p.getName();

                        removeOwnerSign(w, id);
                        removeSellSign(w, id);

                        SqlManager.deletePlot(getIdX(id), getIdZ(id), w.getName().toLowerCase());

                        Send(p, C("MsgPlotReset"));

                        if (isAdv)
                            PlotMe.logger.info(LOG + name + " " + C("MsgResetPlot") + " " + id);
                    }
                }
            }
        } else {
            Send(p, RED + C("MsgPermissionDenied"));
        }
        return true;
    }

    private boolean handlePlotMove(Player p, String[] args) {
        if (PlotMe.cPerms(p, "PlotMe.admin.move")) {
            if (!isPlotWorld(p)) {
                Send(p, RED + C("MsgNotPlotWorld"));
            } else {
                if (args.length < 3 || args[1].equalsIgnoreCase("") || args[2].equalsIgnoreCase("")) {
                    Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandMove") + " <" + C("WordIdFrom") + "> <" + C("WordIdTo") + "> " +
                            RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandMove") + " 0;1 2;-1");
                } else {
                    String plot1 = args[1];
                    String plot2 = args[2];

                    if (!isValidId(plot1) || !isValidId(plot2)) {
                        Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandMove") + " <" + C("WordIdFrom") + "> <" + C("WordIdTo") + "> " +
                                RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandMove") + " 0;1 2;-1");
                        return true;
                    } else {
                        if (asyncMovePlot(p, plot1, plot2)) {
                            Send(p, C("MsgPlotMovedSuccess"));

                            if (isAdv)
                                PlotMe.logger.info(LOG + p.getName() + " " + C("MsgExchangedPlot") + " " + plot1 + " " + C("MsgAndPlot") + " " + plot2);
                        } else
                            Send(p, RED + C("ErrMovingPlot"));
                    }
                }
            }
        } else {
            Send(p, RED + C("MsgPermissionDenied"));
        }
        return true;
    }

    private boolean asyncMovePlot(Player p, String idFrom, String idTo) {
        World w = p.getWorld();
        Location plot1Bottom = getPlotBottomLoc(w, idFrom);
        Location plot2Bottom = getPlotBottomLoc(w, idTo);
        Location plot1Top = getPlotTopLoc(w, idFrom);

        int distanceX = plot1Bottom.getBlockX() - plot2Bottom.getBlockX();
        int distanceZ = plot1Bottom.getBlockZ() - plot2Bottom.getBlockZ();

        LocalPlayer lplayer = worldEditPlugin.wrapPlayer(p);
        LocalSession session = WorldEdit.getInstance().getSession(p.getName());
        EditSession editSession = session.createEditSession(lplayer);

        for (int x = plot1Bottom.getBlockX(); x <= plot1Top.getBlockX(); x++) {
            for (int z = plot1Bottom.getBlockZ(); z <= plot1Top.getBlockZ(); z++) {
                Block plot1Block = w.getBlockAt(new Location(w, x, 0, z));
                Block plot2Block = w.getBlockAt(new Location(w, x - distanceX, 0, z - distanceZ));

                String plot1Biome = plot1Block.getBiome().name();
                String plot2Biome = plot2Block.getBiome().name();

                plot1Block.setBiome(Biome.valueOf(plot2Biome));
                plot2Block.setBiome(Biome.valueOf(plot1Biome));

                for (int y = 0; y < w.getMaxHeight(); y++) {
                    plot1Block = w.getBlockAt(new Location(w, x, y, z));
                    int plot1Type = plot1Block.getTypeId();
                    byte plot1Data = plot1Block.getData();
                    BaseBlock baseBlock1 = new BaseBlock(plot1Type, plot1Data);
                    Vector v1 = BukkitUtil.toVector(plot1Block);

                    plot2Block = w.getBlockAt(new Location(w, x - distanceX, y, z - distanceZ));
                    int plot2Type = plot2Block.getTypeId();
                    byte plot2Data = plot2Block.getData();
                    BaseBlock baseBlock2 = new BaseBlock(plot2Type, plot2Data);
                    Vector v2 = BukkitUtil.toVector(plot2Block);

                    //plot1Block.setTypeId(plot2Type);
                    try {

                        editSession.setBlock(v1, baseBlock2);
                        //plot1Block.setTypeIdAndData(plot2Type, plot2Data, false);
                        //plot1Block.setData(plot2Data);

                        //net.minecraft.server.World world = ((org.bukkit.craftbukkit.CraftWorld) w).getHandle();
                        //world.setRawTypeIdAndData(plot1Block.getX(), plot1Block.getY(), plot1Block.getZ(), plot2Type, plot2Data);

                        editSession.setBlock(v2, baseBlock1);
                        //plot2Block.setTypeId(plot1Type);
                        //plot2Block.setTypeIdAndData(plot1Type, plot1Data, false);
                        //plot2Block.setData(plot1Data);
                        //world.setRawTypeIdAndData(plot2Block.getX(), plot2Block.getY(), plot2Block.getZ(), plot1Type, plot1Data);

                    } catch (MaxChangedBlocksException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        HashMap<String, Plot> plots = getPlots(w);

        if (plots.containsKey(idFrom)) {
            if (plots.containsKey(idTo)) {
                Plot plot1 = plots.get(idFrom);
                Plot plot2 = plots.get(idTo);

                int idX = getIdX(idTo);
                int idZ = getIdZ(idTo);
                SqlManager.deletePlot(idX, idZ, plot2.world);
                plots.remove(idFrom);
                plots.remove(idTo);
                idX = getIdX(idFrom);
                idZ = getIdZ(idFrom);
                SqlManager.deletePlot(idX, idZ, plot1.world);

                plot2.id = "" + idX + ";" + idZ;
                SqlManager.addPlot(plot2, idX, idZ, w);
                plots.put(idFrom, plot2);

                for (int i = 0; i < plot2.comments.size(); i++) {
                    SqlManager.addPlotComment(plot2.comments.get(i), i, idX, idZ, plot2.world);
                }

                for (String player : plot2.allowed()) {
                    SqlManager.addPlotAllowed(player, idX, idZ, plot2.world);
                }

                idX = getIdX(idTo);
                idZ = getIdZ(idTo);
                plot1.id = "" + idX + ";" + idZ;
                SqlManager.addPlot(plot1, idX, idZ, w);
                plots.put(idTo, plot1);

                for (int i = 0; i < plot1.comments.size(); i++) {
                    SqlManager.addPlotComment(plot1.comments.get(i), i, idX, idZ, plot1.world);
                }

                for (String player : plot1.allowed()) {
                    SqlManager.addPlotAllowed(player, idX, idZ, plot1.world);
                }

                setOwnerSign(w, plot1);
                setSellSign(w, plot1);
                setOwnerSign(w, plot2);
                setSellSign(w, plot2);

            } else {
                Plot plot = plots.get(idFrom);

                int idX = getIdX(idFrom);
                int idZ = getIdZ(idFrom);
                SqlManager.deletePlot(idX, idZ, plot.world);
                plots.remove(idFrom);
                idX = getIdX(idTo);
                idZ = getIdZ(idTo);
                plot.id = "" + idX + ";" + idZ;
                SqlManager.addPlot(plot, idX, idZ, w);
                plots.put(idTo, plot);

                for (int i = 0; i < plot.comments.size(); i++) {
                    SqlManager.addPlotComment(plot.comments.get(i), i, idX, idZ, plot.world);
                }

                for (String player : plot.allowed()) {
                    SqlManager.addPlotAllowed(player, idX, idZ, plot.world);
                }

                setOwnerSign(w, plot);
                setSellSign(w, plot);
                removeOwnerSign(w, idFrom);
                removeSellSign(w, idFrom);

            }
        } else {
            if (plots.containsKey(idTo)) {
                Plot plot = plots.get(idTo);

                int idX = getIdX(idTo);
                int idZ = getIdZ(idTo);
                SqlManager.deletePlot(idX, idZ, plot.world);
                plots.remove(idTo);

                idX = getIdX(idFrom);
                idZ = getIdZ(idFrom);
                plot.id = "" + idX + ";" + idZ;
                SqlManager.addPlot(plot, idX, idZ, w);
                plots.put(idFrom, plot);

                for (int i = 0; i < plot.comments.size(); i++) {
                    SqlManager.addPlotComment(plot.comments.get(i), i, idX, idZ, plot.world);
                }

                for (String player : plot.allowed()) {
                    SqlManager.addPlotAllowed(player, idX, idZ, plot.world);
                }

                setOwnerSign(w, plot);
                setSellSign(w, plot);
                removeOwnerSign(w, idTo);
                removeSellSign(w, idTo);
            }
        }

        return true;
    }

    private void asyncClearPlot(Player player, Plot plot) {
        Location bottom = getPlotBottomLoc(player.getWorld(), plot.id);
        Location top = getPlotTopLoc(player.getWorld(), plot.id);

        PlotMapInfo pmi = getMap(bottom);

        int bottomX = bottom.getBlockX();
        int topX = top.getBlockX();
        int bottomZ = bottom.getBlockZ();
        int topZ = top.getBlockZ();

        int minChunkX = (int) Math.floor((double) bottomX / 16);
        int maxChunkX = (int) Math.floor((double) topX / 16);
        int minChunkZ = (int) Math.floor((double) bottomZ / 16);
        int maxChunkZ = (int) Math.floor((double) topZ / 16);

        World w = bottom.getWorld();

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                Chunk chunk = w.getChunkAt(cx, cz);

                for (Entity e : chunk.getEntities()) {
                    Location eloc = e.getLocation();

                    if (!(e instanceof Player) && eloc.getBlockX() >= bottom.getBlockX() && eloc.getBlockX() <= top.getBlockX() &&
                            eloc.getBlockZ() >= bottom.getBlockZ() && eloc.getBlockZ() <= top.getBlockZ()) {
                        e.remove();
                    }
                }
            }
        }

        LocalPlayer lplayer = worldEditPlugin.wrapPlayer(player);
        LocalSession session = WorldEdit.getInstance().getSession(player.getName());
        EditSession editSession = session.createEditSession(lplayer);

        for (int x = bottomX; x <= topX; x++) {
            for (int z = bottomZ; z <= topZ; z++) {
                Block block = new Location(w, x, 0, z).getBlock();

                block.setBiome(Biome.PLAINS);

                for (int y = w.getMaxHeight(); y >= 0; y--) {
                    block = new Location(w, x, y, z).getBlock();

                    BlockState state = block.getState();

                    if (state instanceof InventoryHolder) {
                        InventoryHolder holder = (InventoryHolder) state;
                        holder.getInventory().clear();
                    }


                    if (state instanceof Jukebox) {
                        Jukebox jukebox = (Jukebox) state;
                        //Remove once they fix the NullPointerException
                        try {
                            jukebox.setPlaying(Material.AIR);
                        } catch (Exception e) {
                        }
                    }

                    Vector v = BukkitUtil.toVector(block);

                    try {

                        if (y == 0)
                            editSession.setBlock(v, new BaseBlock(pmi.BottomBlockId));
                        else if (y < pmi.RoadHeight)
                            editSession.setBlock(v, new BaseBlock(pmi.PlotFillingBlockId));
                        else if (y == pmi.RoadHeight)
                            editSession.setBlock(v, new BaseBlock(pmi.PlotFloorBlockId));
                        else {
                            if (y == (pmi.RoadHeight + 1) &&
                                    (x == bottomX - 1 ||
                                            x == topX + 1 ||
                                            z == bottomZ - 1 ||
                                            z == topZ + 1)) {
                                //block.setTypeId(pmi.WallBlockId);
                            } else {
                                editSession.setBlock(v, new BaseBlock(0));
                            }
                        }
                    } catch (MaxChangedBlocksException ex) {
                    }
                    ;
                }
            }
        }

        adjustWall(bottom);
    }

    private void Send(CommandSender cs, String text) {
        Class<?>[] types = new Class<?>[]{CommandSender.class, String.class};
        invoke("Send", types, cs, text);
    }

    private String C(String caption) {
        Class<?>[] types = new Class<?>[]{String.class};
        return (String) invoke("C", types, caption);
    }

    private String round(double money) {
        Class<?>[] types = new Class<?>[]{double.class};
        return (String) invoke("round", types, money);
    }

    private void warn(String msg) {
        Class<?>[] types = new Class<?>[]{String.class};
        invoke("warn", types, msg);
    }

    private String f(double price) {
        Class<?>[] types = new Class<?>[]{double.class};
        return (String) invoke("f", types, price);
    }

    private String f(double price, boolean showsign) {
        Class<?>[] types = new Class<?>[]{double.class, boolean.class};
        return (String) invoke("f", types, showsign);
    }

    private Object invoke(String name, Class<?>[] types, Object... args) {
        try {
            Object[] parameters = new Object[args.length];
            Method method = pmCommand.getClass().getDeclaredMethod(name, types);
            method.setAccessible(true);
            return method.invoke(pmCommand, args);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            PluginMain.log("PlotMe reflection failed");
            e.printStackTrace();
            return null;
        }
    }

    private final ChatColor BLUE = ChatColor.BLUE;
    private final ChatColor RED = ChatColor.RED;
    private final ChatColor RESET = ChatColor.RESET;
    private final ChatColor AQUA = ChatColor.AQUA;
    private final ChatColor GREEN = ChatColor.GREEN;
    private final ChatColor ITALIC = ChatColor.ITALIC;
    private final String PREFIX = PlotMe.PREFIX;
    private final String LOG = "[" + PlotMe.NAME + " Event] ";
    private final boolean isAdv = PlotMe.advancedlogging;

}
