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
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.ThreadLocalRandom;

public class WishingWellTileEntity extends NetworkedTileEntity implements ITickableTileEntity {
    //region Fields
    /**
     * Init flag for water spawning
     */
    protected boolean hasEverSpawnedWater;
    /**
     * Init flag for multiblock check.
     */
    protected boolean hasEverValidatedMultiblock;
    /**
     * Can be outdated, is updated every 40-60 seconds.
     */
    public boolean hasValidMultiblock;

    /**
     * the ticks between multiblock check ticks.
     */
    protected int multiBlockTickTime;
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
        }
    }

    //endregion Overrides

    //region Methods
    public boolean hasValidMultiblock() {
        return OccultismBlocks.WISTHING_WELL.get().blockMatcher.validate(this.world, this.pos) != null;
    }

    protected void spawnFluidColumn() {
        BlockPos fluidPos = this.pos.add(0, 4, 0);
        if (this.world.getBlockState(fluidPos).getFluidState().getFluid() != Fluids.WATER) {
            this.world.setBlockState(fluidPos, Fluids.WATER.getDefaultState().getBlockState(), 11);
        }
    }
    //endregion Methods
}
