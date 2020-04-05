/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.klikli_dev.occultism.common.tile;

import com.github.klikli_dev.occultism.registry.OccultismBlocks;
import com.github.klikli_dev.occultism.registry.OccultismTiles;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class WishingWellTileEntity extends NetworkedTileEntity implements ITickableTileEntity {
    //region Fields
    /**
     * Can be outdated, is updated every 40-60 seconds.
     */
    public boolean hasValidMultiblock;
    /**
     * Init flag for water spawning
     */
    protected boolean hasEverSpawnedWater;
    /**
     * Init flag for multiblock check.
     */
    protected boolean hasEverValidatedMultiblock;
    /**
     * the ticks between multiblock check ticks.
     */
    protected int multiBlockTickTime;

    /**
     * The queue of item stacks to dissolve over time.
     */
    protected Queue<ItemStack> itemsToDissolve = new ArrayDeque<>();

    protected ItemStack currentItemToDissolve;
    protected int currentItemTotalDissolveTicks;
    protected int currentItemDissolveTicks;

    //endregion Fields

    //region Initialization
    public WishingWellTileEntity() {
        super(OccultismTiles.WISHING_WELL.get());
        //randomize the tick time between 40-60 seconds to avoid all TEs checking in sync.
        this.multiBlockTickTime = ThreadLocalRandom.current().nextInt(40, 60) * 20;
    }
    //endregion Initialization

    //region Overrides
    @Override
    public void tick() {
        if (!this.hasEverValidatedMultiblock) {
            this.hasEverValidatedMultiblock = true;
            this.hasValidMultiblock = this.hasValidMultiblock();
        }
        if (!this.hasEverSpawnedWater && this.hasValidMultiblock) {
            this.hasEverSpawnedWater = true;
            this.spawnFluidColumn();
        }

        //check multiblock and water every 40-60 seconds.
        if (this.world.getGameTime() % this.multiBlockTickTime == 0) {
            this.hasValidMultiblock = this.hasValidMultiblock();
            if (this.hasValidMultiblock) {
                this.spawnFluidColumn();
            }
            else {

            }
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        ListNBT nbtList = compound.getList("itemsToDissolve", Constants.NBT.TAG_COMPOUND);
        this.itemsToDissolve = new ArrayDeque<>(
                nbtList.stream().map(CompoundNBT.class::cast).map(ItemStack::read).collect(Collectors.toList()));

        if (compound.contains("currentItemToDissolve")) {
            this.currentItemToDissolve = ItemStack.read(compound.getCompound("currentItemToDissolve"));
            this.currentItemTotalDissolveTicks =compound.getInt("currentItemTotalDissolveTicks");
            this.currentItemDissolveTicks = compound.getInt("currentItemDissolveTicks");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        //Store the items to dissolve
        ListNBT nbtList = new ListNBT();
        this.itemsToDissolve.forEach(itemStack -> nbtList.add(itemStack.serializeNBT()));
        compound.put("itemsToDissolve", nbtList);

        if (this.currentItemToDissolve != null) {
            compound.put("currentItemToDissolve", this.currentItemToDissolve.serializeNBT());
            compound.putInt("currentItemTotalDissolveTicks", this.currentItemTotalDissolveTicks);
            compound.putInt("currentItemDissolveTicks", this.currentItemDissolveTicks);
        }
        return super.write(compound);
    }

    @Override
    public void remove() {
        this.removeFluidColumn();
        super.remove();
    }
    //endregion Overrides

    //region Methods

    public void sacrificeItem(ItemEntity item) {
        //TOOD: check if recipe exists, if so store and remove.
        //this.itemsToDissolve.add(item.getItem());
    }

    public boolean hasValidMultiblock() {
        return OccultismBlocks.WISTHING_WELL.get().blockMatcher.validate(this.world, this.pos) != null;
    }

    protected void spawnFluidColumn() {
        BlockPos fluidPos = this.pos.add(0, 4, 0);
        if (this.world.getBlockState(fluidPos).getFluidState().getFluid() != Fluids.WATER) {
            this.world.setBlockState(fluidPos, Fluids.WATER.getDefaultState().getBlockState(), 11);
        }

        BlockPos sacrificePos = this.pos.add(0, 5, 0);
        if (this.world.getBlockState(sacrificePos).getBlock() != OccultismBlocks.WISHING_WELL_SACRIFICE.get()) {
            this.world.setBlockState(sacrificePos, OccultismBlocks.WISHING_WELL_SACRIFICE.get().getDefaultState(), 2);
        }
    }

    protected void removeFluidColumn() {
        BlockPos fluidPos = this.pos.add(0, 4, 0);
        if (this.world.getBlockState(fluidPos).getFluidState().getFluid() == Fluids.WATER) {
            this.world.removeBlock(fluidPos, false);
        }

        BlockPos sacrificePos = this.pos.add(0, 5, 0);
        if (this.world.getBlockState(sacrificePos).getBlock() == OccultismBlocks.WISHING_WELL_SACRIFICE.get()) {
            this.world.removeBlock(sacrificePos, false);
        }
    }
    //endregion Methods
}
