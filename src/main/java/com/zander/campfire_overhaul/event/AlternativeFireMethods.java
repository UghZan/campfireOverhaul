package com.zander.campfire_overhaul.event;

import com.zander.campfire_overhaul.config.CampfireOverhaulConfig;
import com.zander.campfire_overhaul.util.CampfireHelper;
import com.zander.campfire_overhaul.util.ICampfireExtra;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class AlternativeFireMethods {
    @SubscribeEvent
    public void fireStarterFlint(PlayerInteractEvent.RightClickBlock event) {

        World world = event.getWorld();
        PlayerEntity player = event.getPlayer();
        BlockPos blockPos = event.getPos();
        BlockState blockState = event.getWorld().getBlockState(event.getPos());

        if (event.getHand() == Hand.MAIN_HAND)
            if (player.getHeldItemMainhand().getItem() == Items.FLINT && player.getHeldItemOffhand().getItem() == Items.FLINT) {

                if (!CampfireOverhaulConfig.DOUBLE_FLINT_IGNITION.get())
                    return;

                if (!CampfireBlock.canBeLit(blockState))
                    return;

                Random rand = event.getWorld().rand;
                player.swingArm(Hand.MAIN_HAND);

                if (rand.nextFloat() < CampfireOverhaulConfig.FLINT_IGNITE_CHANCE.get() && !world.isRemote) {
                    world.setBlockState(blockPos, blockState.with(BlockStateProperties.LIT, Boolean.valueOf(true)), 3);
                }

                world.playSound(null, blockPos, SoundEvents.BLOCK_STONE_STEP, SoundCategory.BLOCKS, 1.0F, 2F + rand.nextFloat() * 0.4F);

                if (world.isRemote) {
                    for (int i = 0; i < 5; i++) {
                        world.addParticle(ParticleTypes.SMOKE, player.getPosX() + player.getLookVec().getX() + rand.nextFloat() * 0.25, player.getPosY() + 0.5f + rand.nextFloat() * 0.25, player.getPosZ() + player.getLookVec().getZ() + rand.nextFloat() * 0.25, 0, 0.01, 0);
                    }
                    world.addParticle(ParticleTypes.FLAME, player.getPosX() + player.getLookVec().getX() + rand.nextFloat() * 0.25, player.getPosY() + 0.5f + rand.nextFloat() * 0.25, player.getPosZ() + player.getLookVec().getZ() + rand.nextFloat() * 0.25, 0, 0.01, 0);

                }
            }
            else if(player.getHeldItemMainhand().getItem() == Items.DRAGON_BREATH)
            {
                if(!CampfireOverhaulConfig.DRAGON_BREATH_MAGIC.get())
                    return;

                if(!world.isRemote)
                {
                    world.setBlockState(blockPos, blockState.with(BlockStateProperties.LIT, Boolean.valueOf(true)), 3);
                    ((ICampfireExtra) world.getTileEntity(blockPos)).setLifeTime(-1337);
                }
                world.playSound(null, blockPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, 1F);
            }
    }


    @SubscribeEvent
    public void checkCampfireLifeTime(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        BlockPos pos = event.getPos();
        World world = event.getWorld();
        BlockState blockState = world.getBlockState(pos);

        if (!(blockState.getBlock() instanceof CampfireBlock) || !player.getHeldItemMainhand().isEmpty())
            return;

        if (event.getHand() == Hand.MAIN_HAND) {
            ICampfireExtra info = (ICampfireExtra) world.getTileEntity(pos);

            if (!world.isRemote)
                if (info.getLifeTime() != -1337)
                    player.sendMessage(new StringTextComponent("This campfire have " + info.getLifeTime() / 20 + " seconds of burn time left."), player.getUniqueID());
                else
                    player.sendMessage(new StringTextComponent("This campfire will burn forever!"), player.getUniqueID());

            //player.sendMessage(new StringTextComponent(blockState.getBlock().getRegistryName().toString()), player.getUniqueID());
        }
    }

    @SubscribeEvent
    public void campfireSet(BlockEvent.EntityPlaceEvent event)
    {
        BlockState placed = event.getPlacedBlock();
        BlockPos pos = event.getPos();

        if(!(event.getWorld().getTileEntity(pos) instanceof CampfireTileEntity))
            return;

        CampfireTileEntity tileEntity = (CampfireTileEntity) event.getWorld().getTileEntity(pos);
        ICampfireExtra lifeTime = (ICampfireExtra) tileEntity;

        if(CampfireHelper.isSoul(placed))
        {
            if(CampfireOverhaulConfig.SOUL_CAMPFIRE_INFINITE_LIFE_TIME.get())
                lifeTime.setLifeTime(-1337);
            else
                lifeTime.setLifeTime(CampfireOverhaulConfig.SOUL_CAMPFIRE_DEFAULT_LIFE_TIME.get());
        }
        else
        {
            if(CampfireOverhaulConfig.CAMPFIRE_INFINITE_LIFE_TIME.get())
                lifeTime.setLifeTime(-1337);
            else
                lifeTime.setLifeTime(CampfireOverhaulConfig.CAMPFIRE_DEFAULT_LIFE_TIME.get());
        }

    }
}
