package net.familiar_omen.redstone_ready_bundles;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;

public class BundleDispenseBehavior extends ItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        Position position = DispenserBlock.getOutputLocation(pointer);
        ItemStack stackToDrop = stack;

        if (stack.getItem() instanceof BundleItem bundle) {
            BundleContentsComponent bundleContents = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContents);

            if (bundleContents.isEmpty()) {
                stack = ItemStack.EMPTY;
            }
            else {
                stackToDrop = builder.removeSelected();
                stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
            }
        }

        spawnItem(pointer.world(), stackToDrop, 6, direction, position);

        return stack;
    }
}
