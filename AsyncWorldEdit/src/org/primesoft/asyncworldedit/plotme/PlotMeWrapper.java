package org.primesoft.asyncworldedit.plotme;

import com.google.common.base.Preconditions;
import com.worldcretornica.plotme.*;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.PluginMain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import static com.worldcretornica.plotme.PlotManager.*;
import static com.worldcretornica.plotme.PlotManager.isPlotWorld;
import static com.worldcretornica.plotme.PlotManager.isValidId;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public abstract class PlotMeWrapper {

    private final String LOG = "[" + PlotMe.NAME + " Event] ";
    private final boolean isAdv = PlotMe.advancedlogging;
    protected static final String[] SUPPORTED_COMMANDS = {"CommandClear", "CommandReset", "CommandMove"};
    protected static final String SUPPORTED_VERSION = "0.13b";
    private final PMCommand pmCommand;

    public PlotMeWrapper(PMCommand pmCommand) {
        this.pmCommand = pmCommand;
    }

    abstract void asyncClear(Player player, Plot plot);
    abstract void asyncMove(Player player, Plot plot1, Plot plot2);

    protected boolean handleClear(Player p) {
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

                                asyncClear(p, plot);
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

    protected boolean handlePlotReset(Player p) {
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

                        asyncClear(p, plot);

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

    protected boolean handlePlotMove(Player p, String[] args) {
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
                        if (move(p, plot1, plot2)) {
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

    private boolean move(Player p, String idFrom, String idTo) {
        Plot m1 = getPlotById(p, idFrom);
        Plot m2 = getPlotById(p, idTo);
        if(m1 == null || m2 == null) return false;
        asyncMove(p, m1, m2);
        World w = p.getWorld();
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
        return (String) invoke("f", types, price, showsign);
    }

    private Object invoke(String name, Class<?>[] types, Object... args) {
        Preconditions.checkArgument(types.length == args.length,
                "number of class types doesn't match number of arguments");
        try {
            Method method = pmCommand.getClass().getDeclaredMethod(name, types);
            method.setAccessible(true);
            return method.invoke(pmCommand, args);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            PluginMain.log("PlotMe reflection failed");
            e.printStackTrace();
            return null;
        }
    }

}
