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

package com.github.klikli_dev.occultism.common.block;

import com.github.klikli_dev.occultism.registry.OccultismBlocks;
import com.github.klikli_dev.occultism.registry.OccultismTiles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class WishingWellBaseBlock extends Block {
    //region Fields
    private final String[][] pattern = new String[][]{
            {
                    "T666S",
                    "7R4Q5",
                    "71 35",
                    "7N2P5",
                    "U888V",
            },
            {
                    "     ",
                    " O6O ",
                    " 7 5 ",
                    " O8O ",
                    "     ",
            },
            {
                    "     ",
                    " B B ",
                    "     ",
                    " B B ",
                    "     ",
            },
            {
                    "     ",
                    " B B ",
                    "     ",
                    " B B ",
                    "     ",
            },
            {
                    "     ",
                    " O2O ",
                    " 3 1 ",
                    " O4O ",
                    "     ",
            },
            {
                    "X222W",
                    "3OOO1",
                    "3O0O1",
                    "3OOO1",
                    "Y444Z",
            }
    };
    public IMultiblock blockMatcher;
    //endregion Fields

    //region Initialization
    public WishingWellBaseBlock(Properties properties) {
        super(properties);
    }
    //endregion Initialization

    //region Overrides
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return OccultismTiles.WISHING_WELL.get().create();
    }
    //endregion Overrides

    //region Methods
    public void registerMultiblock(ResourceLocation id) {
        PatchouliAPI.IPatchouliAPI api = PatchouliAPI.instance;

        BlockState stairsBottom = OccultismBlocks.OTHERSTONE_STAIRS.get().getDefaultState();
        BlockState stairsTop =
                OccultismBlocks.OTHERSTONE_STAIRS.get().getDefaultState().with(StairsBlock.HALF, Half.TOP);

        Predicate<BlockState> anyStairsPredicate =
                (state) -> state.getBlock() == OccultismBlocks.OTHERSTONE_STAIRS.get();
        List<Object> mapping = Arrays.asList(
                '1', api.predicateMatcher(stairsBottom, anyStairsPredicate), //Bottom, North, straight
                '2', api.predicateMatcher(stairsBottom.with(StairsBlock.FACING, Direction.EAST), anyStairsPredicate),
                //Bottom, East, Straight
                'X',
                api.predicateMatcher(stairsBottom.with(StairsBlock.FACING, Direction.EAST)
                                             .with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), anyStairsPredicate),
                //Bottom, East
                'W',
                api.predicateMatcher(stairsBottom.with(StairsBlock.FACING, Direction.EAST)
                                             .with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), anyStairsPredicate),
                //Bottom, East
                'P',
                api.predicateMatcher(stairsBottom.with(StairsBlock.FACING, Direction.EAST)
                                             .with(StairsBlock.SHAPE, StairsShape.INNER_RIGHT), anyStairsPredicate),
                //Bottom, East
                'N',
                api.predicateMatcher(stairsBottom.with(StairsBlock.FACING, Direction.EAST)
                                             .with(StairsBlock.SHAPE, StairsShape.INNER_LEFT), anyStairsPredicate),
                //Bottom, East
                '3', api.predicateMatcher(stairsBottom.with(StairsBlock.FACING, Direction.SOUTH), anyStairsPredicate),
                //Bottom, South, Straight,
                '4', api.predicateMatcher(stairsBottom.with(StairsBlock.FACING, Direction.WEST), anyStairsPredicate),
                //Bottom, West, Straight
                'Z',
                api.predicateMatcher(stairsBottom.with(StairsBlock.FACING, Direction.WEST)
                                             .with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), anyStairsPredicate),
                //Bottom, West
                'Y',
                api.predicateMatcher(stairsBottom.with(StairsBlock.FACING, Direction.WEST)
                                             .with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), anyStairsPredicate),
                //Bottom, West
                'R',
                api.predicateMatcher(stairsBottom.with(StairsBlock.FACING, Direction.WEST)
                                             .with(StairsBlock.SHAPE, StairsShape.INNER_RIGHT), anyStairsPredicate),
                //Bottom, West
                'Q',
                api.predicateMatcher(stairsBottom.with(StairsBlock.FACING, Direction.WEST)
                                             .with(StairsBlock.SHAPE, StairsShape.INNER_LEFT), anyStairsPredicate),
                //Bottom, West
                '5', api.predicateMatcher(stairsTop, anyStairsPredicate), //Top, North, straight
                '6',
                api.predicateMatcher(stairsTop.with(StairsBlock.FACING, Direction.EAST), anyStairsPredicate),
                //Top, East, Straight
                'T',
                api.predicateMatcher(stairsTop.with(StairsBlock.FACING, Direction.EAST)
                                             .with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT),
                        anyStairsPredicate),
                //Top, West
                'S', api.predicateMatcher(stairsTop.with(StairsBlock.FACING, Direction.EAST)
                                                  .with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT),
                        anyStairsPredicate),
                //Top, West
                '7',
                api.predicateMatcher(stairsTop.with(StairsBlock.FACING, Direction.SOUTH), anyStairsPredicate),
                //Top, South, Straight,
                '8',
                api.predicateMatcher(stairsTop.with(StairsBlock.FACING, Direction.WEST), anyStairsPredicate),
                //Top, West, Straight
                'V',
                api.predicateMatcher(stairsTop.with(StairsBlock.FACING, Direction.WEST)
                                             .with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT),
                        anyStairsPredicate),
                //Top, West
                'U', api.predicateMatcher(stairsTop.with(StairsBlock.FACING, Direction.WEST)
                                                  .with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT),
                        anyStairsPredicate),
                //Top, West
                '0', api.looseBlockMatcher(OccultismBlocks.WISTHING_WELL.get()),
                'B', api.looseBlockMatcher(Blocks.STONE_BRICK_WALL),
                'O', api.looseBlockMatcher(OccultismBlocks.OTHERSTONE.get()),
                ' ', api.anyMatcher()
        );
        this.blockMatcher =
                api.registerMultiblock(id,
                        api.makeMultiblock(this.pattern, mapping.toArray()).setSymmetrical(true));
    }
    //endregion Methods
}
