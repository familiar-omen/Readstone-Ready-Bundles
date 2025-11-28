package net.familiar_omen.redstone_ready_bundles.mixin;

import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = DecoratedPotBlockEntity.class, priority = 500)
public abstract class DecoratedPotBlockEntityMixinBase implements SingleStackInventory {

    @Intrinsic
    public boolean isValid(int slot, ItemStack stack) {
        return SingleStackInventory.super.isValid(slot, stack);
    }

    @Intrinsic
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return SingleStackInventory.super.canTransferTo(hopperInventory, slot, stack);
    }

    @Intrinsic
    public int size() {
        return SingleStackInventory.super.size();
    }

    @Intrinsic
    public void setStack(int slot, ItemStack stack) {
        this.setStack(stack);
    }
}
