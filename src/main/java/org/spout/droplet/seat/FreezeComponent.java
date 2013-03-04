/*This file is part of DropletSeat.

Copyright (c) 2013 Spout LLC <http://www.spout.org/>

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.*/
package org.spout.droplet.seat;

import org.spout.api.component.type.EntityComponent;
import org.spout.api.entity.Player;

public class FreezeComponent extends EntityComponent {
    
    // This is our own component class which overrides the default positioning behaviour for sitting players and freezes them.
    @Override
    public void onTick(float dt) {
        // We need to check if the owner of the component, in which this code is executed, is a player.
        if(getOwner() instanceof Player) {
            Player player = (Player) getOwner();
            
            // If the player is sitting, we will go on and freeze stop him from moving.
            if(player.getData().get("sitting").equals(true)) {
                // The player's new position is set to his old position, so that it stays the same while he is sitting.
                if(player.getScene().isPositionDirty()) {
                    player.getScene().setPosition(getOwner().getScene().getPosition());
                    player.getNetworkSynchronizer().setPositionDirty();
                }
            }
        }
    }
    
}
