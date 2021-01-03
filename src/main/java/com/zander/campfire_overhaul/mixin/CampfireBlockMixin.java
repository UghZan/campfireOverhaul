package com.zander.campfire_overhaul.mixin;

import com.zander.campfire_overhaul.config.CampfireOverhaulConfig;
import com.zander.campfire_overhaul.util.ICampfireExtra;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
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

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends ContainerBlock {

    @Shadow @Final public static BooleanProperty LIT;

    @Shadow
    public static boolean isLit(BlockState state) {
        return false;
    }

    protected CampfireBlockMixin(Properties builder) {
        super(builder);
    }

    @Inject(at = @At("RETURN"), method = "<init>(ZILnet/minecraft/block/AbstractBlock$Properties;)V")
    protected void init(boolean smokey, int fireDamage, AbstractBlock.Properties properties, CallbackInfo callbackInfo) {
        if(CampfireOverhaulConfig.CAMPFIRE_CREATED_UNLIT.get())
            this.setDefaultState(this.getDefaultState().with(CampfireBlock.LIT, false));
    }

    @Inject(at = @At("RETURN"), method = "getStateForPlacement(Lnet/minecraft/item/BlockItemUseContext;)Lnet/minecraft/block/BlockState;", cancellable = true)
    protected void getStateForPlacement(BlockItemUseContext context, CallbackInfoReturnable<BlockState> callbackInfo)
    {
        if(CampfireOverhaulConfig.CAMPFIRE_CREATED_UNLIT.get())
            callbackInfo.setReturnValue(callbackInfo.getReturnValue().with(CampfireBlock.LIT, false));
    }

    @Inject(at = @At("HEAD"), method = "onEntityCollision(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V", cancellable = true)
    protected void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn, CallbackInfo callbackInfo) {
        if (entityIn instanceof ItemEntity) {
            Random rand = worldIn.rand;
            int rawBurnTime = ForgeHooks.getBurnTime(((ItemEntity) entityIn).getItem());
            if(worldIn.isRemote && state.get(LIT) && rawBurnTime > 0)
                worldIn.addParticle(ParticleTypes.SMOKE, entityIn.getPosX(), entityIn.getPosY() + 0.25D, entityIn.getPosZ(),0 , 0.05D, 0);

            if(rawBurnTime > 0) {
                if(!worldIn.isRemote && ((ItemEntity)entityIn).getThrowerId() != null) {
                    int burnTime = rawBurnTime * CampfireOverhaulConfig.CAMPFIRE_FUEL_MULTIPLIER.get() * ((ItemEntity) entityIn).getItem().getCount();
                    CampfireTileEntity tileEntity = (CampfireTileEntity) worldIn.getTileEntity(pos);

                    ((ICampfireExtra) tileEntity).addLifeTime(burnTime);

                    entityIn.remove();
                }
                if(worldIn.isRemote && state.get(LIT)
                        && ((ItemEntity)entityIn).getThrowerId() != null)
                {
                    for (int i = 0; i < 4; ++i) {
                        worldIn.addParticle(ParticleTypes.LAVA, entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), rand.nextFloat() / 3.0F, (double) rand.nextFloat() * 0.25D, rand.nextFloat() / 3.0F);
                    }
                }
            }
        }
    }
}
