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

package com.github.klikli_dev.occultism.registry;

import com.github.klikli_dev.occultism.Occultism;
import com.github.klikli_dev.occultism.crafting.recipe.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class OccultismRecipes {
    //region Fields
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPES = new DeferredRegister<>(
            ForgeRegistries.RECIPE_SERIALIZERS, Occultism.MODID);

    public static final NonNullLazy<IRecipeType<SpiritTradeRecipe>> SPIRIT_TRADE_TYPE =
            NonNullLazy.of(() -> IRecipeType.register(Occultism.MODID + ":spirit_trade"));
    public static final NonNullLazy<IRecipeType<SpiritFireRecipe>> SPIRIT_FIRE_TYPE =
            NonNullLazy.of(() -> IRecipeType.register(Occultism.MODID + ":spirit_fire"));
    public static final NonNullLazy<IRecipeType<CrushingRecipe>> CRUSHING_TYPE =
            NonNullLazy.of(() -> IRecipeType.register(Occultism.MODID + ":crushing"));
    public static final NonNullLazy<IRecipeType<MinerRecipe>> MINER_TYPE =
            NonNullLazy.of(() -> IRecipeType.register(Occultism.MODID + ":miner"));
    public static final NonNullLazy<IRecipeType<RitualFakeRecipe>> RITUAL_TYPE =
            NonNullLazy.of(() -> IRecipeType.register(Occultism.MODID + ":ritual"));
    public static final NonNullLazy<IRecipeType<WishingWellSacrificeRecipe>> WISHING_WELL_SACRIFICE_TYPE =
            NonNullLazy.of(() -> IRecipeType.register(Occultism.MODID + ":wishing_well_sacrifice"));
    public static final NonNullLazy<IRecipeType<WishingWellGrowingRecipe>> WISHING_WELL_GROWING_TYPE =
            NonNullLazy.of(() -> IRecipeType.register(Occultism.MODID + ":wishing_well_growing"));
    public static final NonNullLazy<IRecipeType<?>> CUSTOM_SHAPELESS_TYPE =
            NonNullLazy.of(() -> IRecipeType.CRAFTING);

    public static final RegistryObject<IRecipeSerializer<SpiritTradeRecipe>> SPIRIT_TRADE = RECIPES.register("spirit_trade",
            () -> SpiritTradeRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<SpiritFireRecipe>> SPIRIT_FIRE = RECIPES.register("spirit_fire",
            () -> SpiritFireRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<CrushingRecipe>> CRUSHING = RECIPES.register("crushing",
            () -> CrushingRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<MinerRecipe>> MINER = RECIPES.register("miner",
            () -> MinerRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<RitualFakeRecipe>> RITUAL = RECIPES.register("ritual",
            () -> RitualFakeRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<WishingWellSacrificeRecipe>> WISHING_WELL_SACRIFICE = RECIPES.register("wishing_well_sacrifice",
            () -> WishingWellSacrificeRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<WishingWellGrowingRecipe>> WISHING_WELL_GROWING = RECIPES.register("wishing_well_growing",
            () -> WishingWellGrowingRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<CustomShapelessRecipe>> CUSTOM_SHAPELESS = RECIPES.register("custom_shapeless",
            () -> CustomShapelessRecipe.SERIALIZER);

    //endregion Fields

}
