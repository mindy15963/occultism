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

package com.github.klikli_dev.occultism.common.ritual.pentacle;

import com.github.klikli_dev.occultism.registry.OccultismBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import vazkii.patchouli.api.IMultiblock;

import java.util.Arrays;

public class DebugPentacle extends Pentacle {

    //region Fields
//    private final String[][] pattern = new String[][]{
//            {
//                    "  GCG  ",
//                    " G P G ",
//                    "G  P  G",
//                    "CPP0PPC",
//                    "G  P  G",
//                    " G P G ",
//                    "  GCG  "
//            }
//    };
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

    //endregion Fields

    //region Overrides
    @Override
    protected void setupMapping() {
        super.setupMapping();
        BlockState stairsBottom = OccultismBlocks.OTHERSTONE_STAIRS.get().getDefaultState();
        BlockState stairsTop =
                OccultismBlocks.OTHERSTONE_STAIRS.get().getDefaultState().with(StairsBlock.HALF, Half.TOP);
        this.mapping.addAll(Arrays.asList(
                '1', stairsBottom, //Bottom, North, straight
                '2', stairsBottom.with(StairsBlock.FACING, Direction.EAST), //Bottom, East, Straight
                'X', stairsBottom.with(StairsBlock.FACING, Direction.EAST).with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), //Bottom, East
                'W', stairsBottom.with(StairsBlock.FACING, Direction.EAST).with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), //Bottom, East
                'P', stairsBottom.with(StairsBlock.FACING, Direction.EAST).with(StairsBlock.SHAPE, StairsShape.INNER_RIGHT), //Bottom, East
                'N', stairsBottom.with(StairsBlock.FACING, Direction.EAST).with(StairsBlock.SHAPE, StairsShape.INNER_LEFT), //Bottom, East
                '3', stairsBottom.with(StairsBlock.FACING, Direction.SOUTH), //Bottom, South, Straight,
                '4', stairsBottom.with(StairsBlock.FACING, Direction.WEST), //Bottom, West, Straight
                'Z', stairsBottom.with(StairsBlock.FACING, Direction.WEST).with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), //Bottom, West
                'Y', stairsBottom.with(StairsBlock.FACING, Direction.WEST).with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), //Bottom, West
                'R', stairsBottom.with(StairsBlock.FACING, Direction.WEST).with(StairsBlock.SHAPE, StairsShape.INNER_RIGHT), //Bottom, West
                'Q', stairsBottom.with(StairsBlock.FACING, Direction.WEST).with(StairsBlock.SHAPE, StairsShape.INNER_LEFT), //Bottom, West
                '5', stairsTop, //Top, North, straight
                '6', stairsTop.with(StairsBlock.FACING, Direction.EAST), //Top, East, Straight
                'T', stairsTop.with(StairsBlock.FACING, Direction.EAST).with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), //Top, West
                'S', stairsTop.with(StairsBlock.FACING, Direction.EAST).with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), //Top, West
                '7', stairsTop.with(StairsBlock.FACING, Direction.SOUTH), //Top, South, Straight,
                '8', stairsTop.with(StairsBlock.FACING, Direction.WEST), //Top, West, Straight
                'V', stairsTop.with(StairsBlock.FACING, Direction.WEST).with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), //Top, West
                'U', stairsTop.with(StairsBlock.FACING, Direction.WEST).with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), //Top, West
                '0', this.api.displayOnlyMatcher(OccultismBlocks.WISTHING_WELL.get()),
                'B', this.api.looseBlockMatcher(Blocks.STONE_BRICK_WALL),
                'O', this.api.looseBlockMatcher(OccultismBlocks.OTHERSTONE.get()),
                ' ', this.api.anyMatcher()
        ));
    }

    @Override
    protected IMultiblock setupMultiblock() {
        return this.api.makeMultiblock(this.pattern, this.mapping.toArray()).setSymmetrical(true);
    }
    //endregion Overrides
}
