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

package com.github.klikli_dev.occultism.crafting.recipe;

import com.github.klikli_dev.occultism.registry.OccultismRecipes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class WishingWellGrowingRecipe implements IRecipe<ItemStackFakeInventory> {
    //region Fields
    public static final NonNullList<Ingredient> EMPTY_INGREDIENTS = NonNullList.create();

    public static Serializer SERIALIZER = new Serializer();
    protected final ResourceLocation id;
    protected final Ingredient input;
    protected final Ingredient output;
    protected final int outputCount;
    protected final int growingTicks;
    protected final float earthEssence;
    protected final float airEssence;
    protected final float fireEssence;
    protected final float waterEssence;
    protected final float earthEssencePerTick;
    protected final float airEssencePerTick;
    protected final float fireEssencePerTick;
    protected final float waterEssencePerTick;

    //endregion Fields
    //region Initialization
    public WishingWellGrowingRecipe(ResourceLocation id, Ingredient input, int growingTicks, float earthEssence,
                                    float airEssence, float fireEssence, float waterEssence, Ingredient output,
                                    int outputCount) {
        this.id = id;
        this.input = input;

        this.growingTicks = growingTicks;
        this.earthEssence = earthEssence;
        this.airEssence = airEssence;
        this.fireEssence = fireEssence;
        this.waterEssence = waterEssence;
        this.output = output;
        this.outputCount = outputCount;

        this.earthEssencePerTick = this.earthEssence / this.growingTicks;
        this.airEssencePerTick = this.airEssence / this.growingTicks;
        this.fireEssencePerTick = this.fireEssence / this.growingTicks;
        this.waterEssencePerTick = this.waterEssence / this.growingTicks;
    }
    //endregion Initialization

    //region Getter / Setter
    public float getWaterEssence() {
        return this.waterEssence;
    }

    public float getFireEssence() {
        return this.fireEssence;
    }

    public float getAirEssence() {
        return this.airEssence;
    }

    public float getEarthEssence() {
        return this.earthEssence;
    }

    public int getGrowingTicks() {
        return this.growingTicks;
    }

    public float getWaterEssencePerTick() {
        return this.waterEssencePerTick;
    }

    public float getFireEssencePerTick() {
        return this.fireEssencePerTick;
    }

    public float getAirEssencePerTick() {
        return this.airEssencePerTick;
    }

    public float getEarthEssencePerTick() {
        return this.earthEssencePerTick;
    }

    public int getOutputCount() {
        return outputCount;
    }

    //endregion Getter / Setter

    //region Overrides
    @Override
    public boolean matches(ItemStackFakeInventory inv, World world) {
        return this.input.test(inv.getStackInSlot(0));
    }

    @Override
    public ItemStack getCraftingResult(ItemStackFakeInventory inv) {
        ItemStack result = this.getRecipeOutput().copy();
        result.setCount(this.outputCount);
        return result;
    }

    @Override
    public boolean canFit(int width, int height) {
        //we only ever use one slot, and we only support wishing wells, so return true.
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output.hasNoMatchingItems() ? ItemStack.EMPTY : this.output.getMatchingStacks()[0];
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return EMPTY_INGREDIENTS;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return OccultismRecipes.WISHING_WELL_GROWING_TYPE.get();
    }

    //endregion Overrides

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<WishingWellGrowingRecipe> {

        //region Overrides
        @Override
        public WishingWellGrowingRecipe read(ResourceLocation recipeId, JsonObject json) {
            JsonElement ingredientElement = JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json,
                    "ingredient") : JSONUtils.getJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.deserialize(ingredientElement);
            int growingTicks = JSONUtils.getInt(json, "growingTicks");
            float earthEssence = JSONUtils.getFloat(json, "earthEssence");
            float airEssence = JSONUtils.getFloat(json, "airEssence");
            float fireEssence = JSONUtils.getFloat(json, "fireEssence");
            float waterEssence = JSONUtils.getFloat(json, "waterEssence");

            JsonElement resultElement = JSONUtils.getJsonObject(json, "result");
            Ingredient result = Ingredient.deserialize(resultElement);
            int outputCount = JSONUtils.getInt(json, "outputCount", 1);

            return new WishingWellGrowingRecipe(recipeId, ingredient, growingTicks, earthEssence, airEssence,
                    fireEssence, waterEssence, result, outputCount);
        }

        @Override
        public WishingWellGrowingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            Ingredient ingredient = Ingredient.read(buffer);
            Ingredient result = Ingredient.read(buffer);
            int outputCount = buffer.readInt();
            int dissolveTicks = buffer.readInt();
            float earthEssence = buffer.readFloat();
            float airEssence = buffer.readFloat();
            float fireEssence = buffer.readFloat();
            float waterEssence = buffer.readFloat();

            return new WishingWellGrowingRecipe(recipeId, ingredient, dissolveTicks, earthEssence, airEssence,
                    fireEssence, waterEssence, result, outputCount);
        }

        @Override
        public void write(PacketBuffer buffer, WishingWellGrowingRecipe recipe) {
            recipe.input.write(buffer);
            recipe.output.write(buffer);
            buffer.writeInt(recipe.outputCount);
            buffer.writeInt(recipe.growingTicks);
            buffer.writeFloat(recipe.earthEssence);
            buffer.writeFloat(recipe.airEssence);
            buffer.writeFloat(recipe.fireEssence);
            buffer.writeFloat(recipe.waterEssence);
        }
        //endregion Overrides
    }
}