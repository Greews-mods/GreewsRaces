package com.greewsraces.mixin;

import com.greewsraces.PlayerDataManager;
import com.greewsraces.Race;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class FoodMixin {

    private static final java.util.Set<String> GHOUL_ALLOWED_FOODS = java.util.Set.of(
        "minecraft:rotten_flesh",
        "minecraft:beef",
        "minecraft:porkchop",
        "minecraft:chicken",
        "minecraft:mutton",
        "minecraft:rabbit",
        "minecraft:cod",
        "minecraft:salmon",
        "minecraft:tropical_fish"
    );

    /**
     * Blokuje konzumaci nepovolených potravin pro ghůla.
     * MC 1.21.10: use() vrací ActionResult místo TypedActionResult
     */
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onItemUse(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.get(DataComponentTypes.FOOD) == null) return;
        if (!PlayerDataManager.hasRaceSelected(player)) return;
        Race race = Race.fromId(PlayerDataManager.getRace(player));
        if (race != Race.GHOUL) return;

        String itemId = net.minecraft.registry.Registries.ITEM
            .getId(stack.getItem()).toString();

        if (!GHOUL_ALLOWED_FOODS.contains(itemId)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    /**
     * Po snědení hnilobného masa odstraníme nausea efekt.
     */
    @Inject(method = "finishUsing", at = @At("RETURN"))
    private void onFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = (ItemStack) (Object) this;

        if (!(user instanceof PlayerEntity player)) return;
        if (!PlayerDataManager.hasRaceSelected(player)) return;
        Race race = Race.fromId(PlayerDataManager.getRace(player));
        if (race != Race.GHOUL) return;

        String itemId = net.minecraft.registry.Registries.ITEM
            .getId(stack.getItem()).toString();

        if (itemId.equals("minecraft:rotten_flesh")) {
            player.removeStatusEffect(StatusEffects.NAUSEA);
        }
    }
}