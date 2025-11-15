package net.familiar_omen.redstone_ready_bundles.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecoratedPotBlock.class)
public class DecoratedPotBlockMixin {
    @Inject(at = @At("HEAD"), method = "getComparatorOutput", cancellable = true)
    protected void getComparatorOutputWhileBundleInPot(BlockState state, World world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (world.getBlockEntity(pos) instanceof DecoratedPotBlockEntity entity) {
            ItemStack itemStack = entity.getStack();

            if (itemStack.getItem() instanceof BundleItem bundle) {
                BundleContentsComponent bundleContents = itemStack.get(DataComponentTypes.BUNDLE_CONTENTS);

                Fraction occupancy = bundleContents.getOccupancy();
                int value = 1 + Math.ceilDiv(occupancy.getNumerator() * 15, occupancy.getDenominator());

                cir.setReturnValue(value);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "onUseWithItem", cancellable = true)
    protected void onUseWithItemWhileBundleInPot(ItemStack playerStack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.getBlockEntity(pos) instanceof DecoratedPotBlockEntity decoratedPotBlockEntity) {
            if (!world.isClient()) {
                ItemStack potStack = decoratedPotBlockEntity.getStack();

                if (potStack.getItem() instanceof BundleItem bundle) {
                    BundleContentsComponent bundleContents = potStack.get(DataComponentTypes.BUNDLE_CONTENTS);
                    BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContents);

                    ItemStack singleItem = playerStack.copyWithCount(1);

                    if (!singleItem.isEmpty() && builder.add(singleItem) > 0) {
                        playerStack.splitUnlessCreative(1, player);
                        decoratedPotBlockEntity.wobble(DecoratedPotBlockEntity.WobbleType.POSITIVE);
                        player.incrementStat(Stats.USED.getOrCreateStat(singleItem.getItem()));

                        bundleContents = builder.build();

                        potStack.set(DataComponentTypes.BUNDLE_CONTENTS, bundleContents);

                        world.playSound(null, pos, SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS, 1.0F, 0.7F + 0.5F * bundleContents.getOccupancy().floatValue());
                        if (world instanceof ServerWorld serverWorld) {
                            serverWorld.spawnParticles(ParticleTypes.DUST_PLUME, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 7, 0.0, 0.0, 0.0, 0.0);
                        }

                        decoratedPotBlockEntity.markDirty();
                        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                        cir.setReturnValue(ActionResult.SUCCESS);
                    }
                }
            }
        }
    }
}