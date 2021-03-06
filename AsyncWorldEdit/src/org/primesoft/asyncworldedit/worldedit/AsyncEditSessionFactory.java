/*
 * The MIT License
 *
 * Copyright 2013 SBPrime.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primesoft.asyncworldedit.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.util.eventbus.EventBus;
import com.sk89q.worldedit.world.World;
import java.util.UUID;
import org.primesoft.asyncworldedit.ConfigProvider;
import org.primesoft.asyncworldedit.PluginMain;

/**
 *
 * @author SBPrime
 */
public class AsyncEditSessionFactory extends EditSessionFactory {

    private final PluginMain m_parent;
    private final EventBus m_eventBus;

    public AsyncEditSessionFactory(PluginMain parent, EventBus eventBus) {
        m_parent = parent;
        m_eventBus = eventBus;
    }

    @Override
    public EditSession getEditSession(World world, int maxBlocks) {
        return new AsyncEditSession(m_parent, ConfigProvider.DEFAULT_USER,
                m_eventBus, world, maxBlocks, null,
                new EditSessionEvent(world, null, maxBlocks, null));
    }

    @Override
    public EditSession getEditSession(World world, int maxBlocks, LocalPlayer player) {
        UUID uuid = BukkitPlayerWrapper.getUUID(player);
        AsyncEditSession result = new AsyncEditSession(m_parent, uuid,
                m_eventBus, world, maxBlocks, null,
                new EditSessionEvent(world, player, maxBlocks, null));
        m_parent.getPlotMeFix().setMask(PluginMain.getPlayer(uuid));

        return result;
    }

    @Override
    public EditSession getEditSession(World world, int maxBlocks, BlockBag blockBag) {
        return new AsyncEditSession(m_parent, ConfigProvider.DEFAULT_USER,
                m_eventBus, world, maxBlocks, blockBag,
                new EditSessionEvent(world, null, maxBlocks, null));
    }

    @Override
    public EditSession getEditSession(World world, int maxBlocks, BlockBag blockBag, LocalPlayer player) {
        UUID uuid = BukkitPlayerWrapper.getUUID(player);
        AsyncEditSession result = new AsyncEditSession(m_parent, uuid,
                m_eventBus, world, maxBlocks, blockBag,
                new EditSessionEvent(world, player, maxBlocks, null));
        m_parent.getPlotMeFix().setMask(PluginMain.getPlayer(uuid));

        return result;
    }
}