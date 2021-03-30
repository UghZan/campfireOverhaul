package com.zander.campfire_overhaul.mixin;

import com.zander.campfire_overhaul.config.CampfireOverhaulConfig;
import com.zander.campfire_overhaul.util.ICampfireExtra;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

import com.zander.campfire_overhaul.util.CampfireHelper;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends ContainerBlock {

    @Shadow
    public static boolean isLit(BlockState state) {
        return false;
    }

    protected CampfireBlockMixin(Properties builder) {
        super(builder);
    }

    /*@Inject(at = @At("RETURN"), method = "<init>(ZILnet/minecraft/block/AbstractBlock$Properties;)V")
    protected void init(boolean smokey, int fireDamage, AbstractBlock.Properties properties, CallbackInfo callbackInfo) {

        if(isSoul(getDefaultState())) {
            this.(this.getDefaultState().with(CampfireBlock.LIT, !CampfireOverhaulConfig.SOUL_CAMPFIRE_CREATED_UNLIT.get()));
        }
        else
            this.setDefaultState(this.getDefaultState().with(CampfireBlock.LIT, !CampfireOverhaulConfig.CAMPFIRE_CREATED_UNLIT.get()));
    }*/

    @Inject(at = @At("RETURN"), method = "getStateForPlacement(Lnet/minecraft/item/BlockItemUseContext;)Lnet/minecraft/block/BlockState;", cancellable = true)
    protected void getStateForPlacement(BlockItemUseContext context, CallbackInfoReturnable<BlockState> callbackInfo) {

        if (CampfireHelper.isSoul(getDefaultState())) {
            callbackInfo.setReturnValue(callbackInfo.getReturnValue().with(CampfireBlock.LIT, callbackInfo.getReturnValue().get(CampfireBlock.LIT) & !CampfireOverhaulConfig.SOUL_CAMPFIRE_CREATED_UNLIT.get()));
        } else
            callbackInfo.setReturnValue(callbackInfo.getReturnValue().with(CampfireBlock.LIT, callbackInfo.getReturnValue().get(CampfireBlock.LIT) & !CampfireOverhaulConfig.CAMPFIRE_CREATED_UNLIT.get()));
    }

    @Inject(at = @At("HEAD"), method = "onEntityCollision(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V", cancellable = true)
    protected void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn, CallbackInfo callbackInfo) {
        if (entityIn instanceof ItemEntity) {
            Random rand = worldIn.rand;
            int rawBurnTime = ForgeHooks.getBurnTime(((ItemEntity) entityIn).getItem());
            if (worldIn.isRemote && isLit(state) && rawBurnTime > 0)
                worldIn.addParticle(ParticleTypes.SMOKE, entityIn.getPosX(), entityIn.getPosY() + 0.25D, entityIn.getPosZ(), 0, 0.05D, 0);

            if (rawBurnTime > 0) {
                if (((ItemEntity) entityIn).getThrowerId() != null && ((ICampfireExtra) worldIn.getTileEntity(pos)).getLifeTime() != -1337) {
                    if (!worldIn.isRemote) {
                        int burnTime = rawBurnTime * CampfireOverhaulConfig.CAMPFIRE_FUEL_MULTIPLIER.get() * ((ItemEntity) entityIn).getItem().getCount();
                        CampfireTileEntity tileEntity = (CampfireTileEntity) worldIn.getTileEntity(pos);
                        ICampfireExtra lifeTime = ((ICampfireExtra) tileEntity);
                        System.out.println(lifeTime.getLifeTime());
                        if (CampfireHelper.isSoul(state)) {
                            if (lifeTime.getLifeTime() < CampfireOverhaulConfig.SOUL_CAMPFIRE_MAX_LIFE_TIME.get() && lifeTime.getLifeTime() >= 0) {
                                lifeTime.addLifeTime(burnTime);
                                if (((ItemEntity) entityIn).getItem().getItem() == Items.LAVA_BUCKET)
                                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.BUCKET));
                                entityIn.remove();
                            }
                        } else {
                            if (lifeTime.getLifeTime() < CampfireOverhaulConfig.CAMPFIRE_MAX_LIFE_TIME.get() && lifeTime.getLifeTime() >= 0) {
                                lifeTime.addLifeTime(burnTime);
                                if (((ItemEntity) entityIn).getItem().getItem() == Items.LAVA_BUCKET)
                                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.BUCKET));
                                entityIn.remove();
                            }
                        }
                    }
                }
            }
        }
    }
}
