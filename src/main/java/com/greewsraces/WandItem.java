package com.greewsraces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WandItem extends Item {
    private final WandOrbItem orb;

    public WandItem(WandOrbItem orb, Item.Settings settings) {
        super(settings.maxCount(1));
        this.orb = orb;
    }

    public WandOrbItem getOrb() {
        return orb;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient()) {
            if (user instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
                WandService.fire(serverPlayer, orb);
            }
        }
        return ActionResult.SUCCESS;
    }
}
