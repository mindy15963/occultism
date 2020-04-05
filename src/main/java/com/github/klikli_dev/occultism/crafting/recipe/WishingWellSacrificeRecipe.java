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

public class WishingWellSacrificeRecipe implements IRecipe<ItemStackFakeInventory> {
    //region Fields
    public static Serializer SERIALIZER = new Serializer();
    protected final ResourceLocation id;
    protected final Ingredient input;
    protected final int dissolveTicks;
    protected final float earthEssence;
    protected final float airEssence;
    protected final float fireEssence;
    protected final float waterEssence;

    //endregion Fields
    //region Initialization
    public WishingWellSacrificeRecipe(ResourceLocation id, Ingredient input, int dissolveTicks, float earthEssence,
                                      float airEssence, float fireEssence, float waterEssence) {
        this.input = input;
        this.id = id;
        this.dissolveTicks = dissolveTicks;
        this.earthEssence = earthEssence;
        this.airEssence = airEssence;
        this.fireEssence = fireEssence;
        this.waterEssence = waterEssence;
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

    public int getDissolveTicks() {
        return this.dissolveTicks;
    }

    //endregion Getter / Setter

    //region Overrides
    @Override
    public boolean matches(ItemStackFakeInventory inv, World world) {
        return this.input.test(inv.getStackInSlot(0));
    }

    @Override
    public ItemStack getCraftingResult(ItemStackFakeInventory inv) {
        //the output is essences, so no stuff here.
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        //we only ever use one slot, and we only support wishing wells, so return true.
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        //the output is essences, so no stuff here.
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.from(Ingredient.EMPTY, this.input);
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
        return OccultismRecipes.WISHING_WELL_SACRIFICE_TYPE.get();
    }

    //endregion Overrides

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<WishingWellSacrificeRecipe> {

        //region Overrides
        @Override
        public WishingWellSacrificeRecipe read(ResourceLocation recipeId, JsonObject json) {
            JsonElement ingredientElement = JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json,
                    "ingredient") : JSONUtils.getJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.deserialize(ingredientElement);
            int dissolveTicks = JSONUtils.getInt(json, "dissolveTicks");
            float earthEssence = JSONUtils.getFloat(json, "earthEssence");
            float airEssence = JSONUtils.getFloat(json, "airEssence");
            float fireEssence = JSONUtils.getFloat(json, "fireEssence");
            float waterEssence = JSONUtils.getFloat(json, "waterEssence");

            return new WishingWellSacrificeRecipe(recipeId, ingredient, dissolveTicks, earthEssence, airEssence,
                    fireEssence, waterEssence);
        }

        @Override
        public WishingWellSacrificeRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            Ingredient ingredient = Ingredient.read(buffer);
            Ingredient result = Ingredient.read(buffer);
            int dissolveTicks = buffer.readInt();
            float earthEssence = buffer.readFloat();
            float airEssence = buffer.readFloat();
            float fireEssence = buffer.readFloat();
            float waterEssence = buffer.readFloat();

            return new WishingWellSacrificeRecipe(recipeId, ingredient, dissolveTicks, earthEssence, airEssence,
                    fireEssence, waterEssence);
        }

        @Override
        public void write(PacketBuffer buffer, WishingWellSacrificeRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeInt(recipe.dissolveTicks);
            buffer.writeFloat(recipe.earthEssence);
            buffer.writeFloat(recipe.airEssence);
            buffer.writeFloat(recipe.fireEssence);
            buffer.writeFloat(recipe.waterEssence);
        }
        //endregion Overrides
    }
}