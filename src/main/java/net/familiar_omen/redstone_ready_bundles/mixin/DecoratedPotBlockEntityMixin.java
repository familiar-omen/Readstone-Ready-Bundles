package net.familiar_omen.redstone_ready_bundles.mixin;

import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DecoratedPotBlockEntity.class)
public abstract class DecoratedPotBlockEntityMixin implements SingleStackInventory {

    @Shadow
    public abstract ItemStack getStack();

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (getStack().getItem() instanceof BundleItem) {
            BundleContentsComponent bundleContents = getStack().get(DataComponentTypes.BUNDLE_CONTENTS);
            BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContents);

            return builder.add(stack.copy()) > 0;
        }
        return SingleStackInventory.super.isValid(slot, stack);
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        if (slot == 2) return false;
        return SingleStackInventory.super.canTransferTo(hopperInventory, slot, stack);
    }

    @Override
    public int size() {
        if (getStack().getItem() instanceof BundleItem) return 2;
        return SingleStackInventory.super.size();
    }

    @Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    public void setStack(ItemStack stack, CallbackInfo ci) {
        if (getStack().getItem() instanceof BundleItem) {
            BundleContentsComponent bundleContents = getStack().get(DataComponentTypes.BUNDLE_CONTENTS);
            BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContents);

            if (builder.add(stack) > 0) {
                getStack().set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                this.markDirty();
            }

            ci.cancel();
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.setStack(stack);
    }

}
