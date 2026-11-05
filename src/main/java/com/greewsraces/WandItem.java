package com.greewsraces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class WandItem extends Item {
    private final WandOrbItem orb;

    public WandItem(WandOrbItem orb) {
        super(new Item.Settings().maxCount(1));
        this.orb = orb;
    }

    public WandOrbItem getOrb() {
        return orb;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.addAll(WandTooltip.lines(orb));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient()) {
            if (user instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
                WandService.fire(serverPlayer, orb);
            }
        }
        return TypedActionResult.success(stack, world.isClient());
    }
}
