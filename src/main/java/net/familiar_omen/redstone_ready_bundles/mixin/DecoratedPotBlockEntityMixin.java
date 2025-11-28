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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecoratedPotBlockEntity.class)
public abstract class DecoratedPotBlockEntityMixin implements SingleStackInventory {

    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    public void fitsInBundle(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (getStack().getItem() instanceof BundleItem) {
            BundleContentsComponent bundleContents = getStack().get(DataComponentTypes.BUNDLE_CONTENTS);
            BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContents);

            cir.setReturnValue(builder.add(stack.copy()) > 0);
        }
    }

    @Inject(method = "canTransferTo", at = @At("HEAD"), cancellable = true)
    public void dontTakeFromFakeSlot(Inventory hopperInventory, int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (slot == 2)
            cir.setReturnValue(false);
    }

    @Inject(method = "size", at = @At("HEAD"), cancellable = true)
    public void showAsNotFull(CallbackInfoReturnable<Integer> cir) {
        if (getStack().getItem() instanceof BundleItem)
            cir.setReturnValue(2);
    }

    @Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    public void rerouteToBundle(ItemStack stack, CallbackInfo ci) {
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
}
