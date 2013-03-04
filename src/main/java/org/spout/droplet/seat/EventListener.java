/*
 * This file is part of DropletSeat.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.spout.droplet.seat;

import org.spout.api.entity.Player;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.player.PlayerInteractEvent;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.event.player.PlayerJoinEvent;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;
import org.spout.api.scheduler.TaskPriority;
import org.spout.vanilla.component.entity.living.Human;

public class EventListener implements Listener {
    DropletSeat plugin;
    
    
    public EventListener(DropletSeat plugin) {
        this.plugin = plugin;
    }
    
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // When players join the game, some data is saved in their database which keeps track of whether they are sitting or not.
        player.getData().put("sitting", false);
        // We are going to attach our own component to them, which is necessary for sitting down.
        player.add(FreezeComponent.class);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // At first we check if the player has interacted with a block by right-clicking.
        if(event.getAction() == Action.RIGHT_CLICK) {
            Block block = event.getInteractedPoint().getBlock();
            String material = block.getMaterial().getDisplayName();
            
            // We will only proceed, if the material of the clicked block is listed in the plugin's configuration file.
            if(SeatConfig.BLOCKS.getKeys(true).contains(material)) {
                // We are going to get the 'Human' component of the player, which represents the player in Vanilla.
                final Player player = event.getPlayer();
                Human human = player.get(Human.class);
                
                // We need to check if the player really has such a component attached to him.
                if(human == null)
                    return;
                
                // If the player is already sitting on a block, he is brought into standing position and his data gets updated.
                if(player.getData().get("sitting").equals(true)) {
                    human.setRiding(false);
                    player.getData().put("sitting", false);
                }
                // If he isn't sitting, we are going to bring him into riding position.
                else {
                    human.setRiding(true);
                    
                    // The height in which the player should be sitting is loaded from the configuration file and saved in a variable.
                    Float height = block.getY() + SeatConfig.BLOCKS.getChild(material).getFloat();
                    
                    // After that, the player gets teleported to the clicked block in the configured height.
                    Point seat = new Point(player.getWorld(), block.getX() + 0.5f, height, block.getZ() + 0.5f);
                    player.teleport(seat);
                    
                    // We need to wait at least one tick, so that the player can be teleported to the block before we update his data and he gets frozen.
                    plugin.getEngine().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
			public void run() {
                            player.getData().put("sitting", true);
			}
                    }, 1, TaskPriority.NORMAL);
                }
                
                // After we're done with positioning the player, we need to send the metadata we've changed (riding position) to the client.
                human.sendMetaData();
            }
        }
    }
    
}
