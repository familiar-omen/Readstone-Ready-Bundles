package net.familiar_omen.redstone_ready_bundles.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
    //@Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/entity/HopperBlockEntity;isInventoryFull(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/util/math/Direction;)Z"), method = "insert", cancellable = true)
    @Inject(at = @At(value = "RETURN", ordinal = 1), method = "insert", cancellable = true)
    private static void insertIntoBundleInPot(World world, BlockPos pos, HopperBlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir, @Local LocalRef<Inventory> inventory,  @Local LocalRef<Direction> opposite_facing) {
        BlockPos target_pos = pos.offset(opposite_facing.get().getOpposite());

        if (world.getBlockEntity(target_pos) instanceof DecoratedPotBlockEntity entity) {
            ItemStack potStack = entity.getStack();

            if (potStack.getItem() instanceof BundleItem bundle) {
                BundleContentsComponent bundleContents = potStack.get(DataComponentTypes.BUNDLE_CONTENTS);
                BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContents);

                for (int i = 0; i < blockEntity.size(); i++) {
                    ItemStack hopperStack = blockEntity.getStack(i);
                    if (!hopperStack.isEmpty()) {
                        ItemStack singleItem = hopperStack.copyWithCount(1);

                        if (builder.add(singleItem) > 0) {
                            hopperStack.decrement(1);
                            potStack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                            inventory.get().markDirty();
                            cir.setReturnValue(true);
                        }
                    }
                }
            }
        }
    }
}
