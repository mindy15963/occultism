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

package com.github.klikli_dev.occultism.common.item.spirit;

import com.github.klikli_dev.occultism.Occultism;
import com.github.klikli_dev.occultism.TranslationKeys;
import com.github.klikli_dev.occultism.api.common.data.GlobalBlockPos;
import com.github.klikli_dev.occultism.api.common.data.MachineReference;
import com.github.klikli_dev.occultism.api.common.data.WorkAreaSize;
import com.github.klikli_dev.occultism.api.common.item.IHandleItemMode;
import com.github.klikli_dev.occultism.api.common.item.IIngredientCopyNBT;
import com.github.klikli_dev.occultism.api.common.item.IIngredientPreventCrafting;
import com.github.klikli_dev.occultism.api.common.tile.IStorageController;
import com.github.klikli_dev.occultism.client.gui.GuiHelper;
import com.github.klikli_dev.occultism.common.entity.spirit.SpiritEntity;
import com.github.klikli_dev.occultism.common.job.ManageMachineJob;
import com.github.klikli_dev.occultism.util.EntityUtil;
import com.github.klikli_dev.occultism.util.ItemNBTUtil;
import com.github.klikli_dev.occultism.util.TextUtil;
import com.github.klikli_dev.occultism.util.TileEntityUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.*;

public class BookOfCallingItem extends Item implements IIngredientPreventCrafting, IIngredientCopyNBT, IHandleItemMode {

    //region Fields
    public static Map<UUID, Long> spiritDeathRegister = new HashMap<>();
    public String translationKeyBase;
    //endregion Fields

    //region Initialization
    public BookOfCallingItem(Properties properties, String translationKeyBase) {
        super(properties);
        this.translationKeyBase = translationKeyBase;
    }
    //endregion Initialization

    //region Getter / Setter

    /**
     * @return returns the base item translation including the spirit name, but excluding the job.
     */
    public String getTranslationKeyBase() {
        return this.translationKeyBase;
    }

    //endregion Getter / Setter
    //region Overrides
    @Override
    public boolean shouldCopyNBT(ItemStack itemStack, IRecipe recipe, CraftingInventory inventory) {
        return recipe.getRecipeOutput().getItem() instanceof BookOfBindingBoundItem;
    }

    @Override
    public CompoundNBT overrideNBT(ItemStack itemStack, CompoundNBT nbt, IRecipe recipe, CraftingInventory inventory) {
        CompoundNBT result = new CompoundNBT();
        //copy over only the spirit name
        if (nbt.contains(ItemNBTUtil.SPIRIT_NAME_TAG))
            result.putString(ItemNBTUtil.SPIRIT_NAME_TAG, nbt.getString(ItemNBTUtil.SPIRIT_NAME_TAG));
        return result;
    }

    @Override
    public boolean shouldPreventCrafting(ItemStack itemStack, IRecipe recipe, CraftingInventory inventory,
                                         World world) {
        CompoundNBT entityNBT = ItemNBTUtil.getSpiritEntityData(itemStack);
        if (entityNBT != null)
            return true; //entity stored in the book.

        UUID entityUUID = ItemNBTUtil.getSpiritEntityUUID(itemStack);
        MinecraftServer server = world.getServer();
        return entityUUID != null &&
               EntityUtil.getEntityByUuiDGlobal(server, entityUUID).isPresent(); //entity exists still in the world.
    }

    @Override
    public int getItemMode(ItemStack stack) {
        return ItemNBTUtil.getItemMode(stack);
    }

    @Override
    public void setItemMode(ItemStack stack, int mode) {
        ItemNBTUtil.setItemMode(stack, mode);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getItem();
        World world = context.getWorld();
        Direction facing = context.getFace();
        BlockPos pos = context.getPos();
        CompoundNBT entityData = ItemNBTUtil.getSpiritEntityData(itemStack);
        if (entityData != null) {
            //whenever we have an entity stored we can do nothing but release it
            if (!world.isRemote) {
                SpiritEntity entity = (SpiritEntity) EntityUtil.entityFromNBT(world, entityData);

                facing = facing == null ? Direction.UP : facing;

                BlockPos spawnPos = pos.toImmutable();
                if(!world.getBlockState(spawnPos).getCollisionShape(world, spawnPos).isEmpty())
                {
                    spawnPos = spawnPos.offset(facing);
                }

                entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(),
                        spawnPos.getZ(), world.rand.nextInt(360), 0);
                entity.setTamedBy(player);

                //refresh item nbt
                ItemNBTUtil.updateItemNBTFromEntity(itemStack, entity);

                world.addEntity(entity);

                itemStack.getTag().remove(ItemNBTUtil.SPIRIT_DATA_TAG); //delete entity from item
                player.container.detectAndSendChanges();
            }
        }
        else {
            //if there are no entities stored, we can either open the ui or perform the action
            if (player.isSneaking()) {
                //when sneaking, perform action based on mode
                return this.handleItemMode(player, world, pos, itemStack, facing);
            }
            else if(world.isRemote) {
                //if not sneaking, open general ui
                IItemModeSubset<?> subset = this.getItemModeSubset(itemStack);
                WorkAreaSize workAreaSize = ItemNBTUtil.getWorkAreaSize(itemStack);
                GuiHelper.openBookOfCallingGui(subset, workAreaSize);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (target.world.isRemote)
            return false;

        //Ignore anything that is not a spirit
        if (!(target instanceof SpiritEntity))
            return false;

        SpiritEntity entitySpirit = (SpiritEntity) target;

        //books can only control the spirit that is bound to them.
        if (!entitySpirit.getUniqueID().equals(ItemNBTUtil.getSpiritEntityUUID(stack))) {
            //Creative players can re-link the book.
            if (player.isCreative()) {
                ItemNBTUtil.setSpiritEntityUUID(stack, entitySpirit.getUniqueID());
                ItemNBTUtil.setBoundSpiritName(stack, entitySpirit.getName().getFormattedText());
            }
            else {
                player.sendStatusMessage(
                        new TranslationTextComponent(
                                TranslationKeys.BOOK_OF_CALLING_GENERIC + ".message_target_uuid_no_match"),
                        true);
                return false;

            }
        }

        //serialize entity
        ItemNBTUtil.setSpiritEntityData(stack, entitySpirit.serializeNBT());
        //show player swing anim
        player.swingArm(hand);
        player.setHeldItem(hand, stack); //need to write the item back to hand, otherwise we only modify a copy
        entitySpirit.remove(true);
        player.container.detectAndSendChanges();
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.getGameTime() % (20 * 60) == 0) {
            UUID spiritID = ItemNBTUtil.getSpiritEntityUUID(stack);
            if (spiritID != null) {
                Long deathTime = spiritDeathRegister.get(spiritID);
                if (deathTime != null && deathTime < worldIn.getGameTime()) {
                    spiritDeathRegister.remove(spiritID);
                    stack.getTag().remove(ItemNBTUtil.SPIRIT_UUID_TAG);
                }
            }
        }

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
                               ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent(this.getTranslationKeyBase() +
                                                 (ItemNBTUtil.getSpiritEntityUUID(stack) !=
                                                  null ? ".tooltip" : ".tooltip_dead"),
                TextUtil.formatDemonName(ItemNBTUtil.getBoundSpiritName(stack))));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return ItemNBTUtil.getSpiritEntityData(stack) != null;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return ItemNBTUtil.getSpiritEntityUUID(stack) != null ? Rarity.RARE : Rarity.COMMON;
    }
    //endregion Overrides

    //region Methods
    public IItemModeSubset<?> getItemModeSubset(ItemStack stack){
        return ItemMode.get(this.getItemMode(stack));
    }

    public boolean useWorkAreaSize(){
        return true;
    }

    public boolean setSpiritManagedMachine(PlayerEntity player, World world, BlockPos pos, ItemStack stack,
                                           Direction face) {
        UUID boundSpiritId = ItemNBTUtil.getSpiritEntityUUID(stack);
        if (boundSpiritId != null) {
            Optional<SpiritEntity> boundSpirit = EntityUtil.getEntityByUuiDGlobal(world.getServer(), boundSpiritId)
                                                         .map(e -> (SpiritEntity) e);
            TileEntity tileEntity = world.getTileEntity(pos);
            if (boundSpirit.isPresent() && tileEntity != null) {

                if (boundSpirit.get().getJob().isPresent() &&
                    boundSpirit.get().getJob().get() instanceof ManageMachineJob) {
                    ManageMachineJob manageMachine = (ManageMachineJob) boundSpirit.get().getJob().get();
                    MachineReference newReference = MachineReference.from(tileEntity);
                    if (manageMachine.getManagedMachine() == null ||
                        !manageMachine.getManagedMachine().globalPos.equals(newReference.globalPos)) {
                        //if we are setting a completely new machine, just overwrite the reference.
                        manageMachine.setManagedMachine(newReference);
                    }
                    else {
                        //otherwise just update the registry name in case the block type was switched out
                        manageMachine.getManagedMachine().registryName = newReference.registryName;
                    }

                    //write data into item nbt for client side usage
                    ItemNBTUtil.updateItemNBTFromEntity(stack, boundSpirit.get());

                    player.sendStatusMessage(
                            new TranslationTextComponent(
                                    TranslationKeys.BOOK_OF_CALLING_GENERIC + ".message_set_managed_machine",
                                    TextUtil.formatDemonName(boundSpirit.get().getName().getFormattedText())), true);
                    return true;
                }
            }
            else {
                player.sendStatusMessage(
                        new TranslationTextComponent(
                                TranslationKeys.BOOK_OF_CALLING_GENERIC + ".message_spirit_not_found"),
                        true);
            }
        }
        return false;
    }

    public boolean setSpiritStorageController(PlayerEntity player, World world, BlockPos pos, ItemStack stack,
                                              Direction face) {
        UUID boundSpiritId = ItemNBTUtil.getSpiritEntityUUID(stack);
        if (boundSpiritId != null) {
            Optional<SpiritEntity> boundSpirit = EntityUtil.getEntityByUuiDGlobal(world.getServer(), boundSpiritId)
                                                         .map(e -> (SpiritEntity) e);

            if (boundSpirit.isPresent() && boundSpirit.get().getJob().isPresent()) {
                if (boundSpirit.get().getJob().get() instanceof ManageMachineJob) {
                    ManageMachineJob job = (ManageMachineJob) boundSpirit.get().getJob().get();
                    job.setStorageControllerPosition(new GlobalBlockPos(pos, world));
                    //write data into item nbt for client side usage
                    ItemNBTUtil.updateItemNBTFromEntity(stack, boundSpirit.get());

                    String blockName = world.getBlockState(pos).getBlock().getTranslationKey();
                    player.sendStatusMessage(new TranslationTextComponent(
                            TranslationKeys.BOOK_OF_CALLING_GENERIC + ".message_set_storage_controller",
                            TextUtil.formatDemonName(boundSpirit.get().getName().getFormattedText()),
                            new TranslationTextComponent(blockName)), true);
                    return true;
                }
            }
            else {
                player.sendStatusMessage(
                        new TranslationTextComponent(
                                TranslationKeys.BOOK_OF_CALLING_GENERIC + ".message_spirit_not_found"),
                        true);
            }
        }
        return false;
    }

    public boolean setSpiritDepositLocation(PlayerEntity player, World world, BlockPos pos, ItemStack stack,
                                            Direction face) {
        UUID boundSpiritId = ItemNBTUtil.getSpiritEntityUUID(stack);
        if (boundSpiritId != null) {
            Optional<SpiritEntity> boundSpirit = EntityUtil.getEntityByUuiDGlobal(world.getServer(), boundSpiritId)
                                                         .map(e -> (SpiritEntity) e);

            if (boundSpirit.isPresent()) {
                //update properties on entity
                boundSpirit.get().setDepositPosition(pos);
                boundSpirit.get().setDepositFacing(face);
                //also update control item with latest data
                ItemNBTUtil.updateItemNBTFromEntity(stack, boundSpirit.get());

                String blockName = world.getBlockState(pos).getBlock().getTranslationKey();
                player.sendStatusMessage(
                        new TranslationTextComponent(TranslationKeys.BOOK_OF_CALLING_GENERIC + ".message_set_deposit",
                                TextUtil.formatDemonName(boundSpirit.get().getName().getFormattedText()),
                                new TranslationTextComponent(blockName), face.getName()), true);
                return true;
            }
            else {
                player.sendStatusMessage(
                        new TranslationTextComponent(
                                TranslationKeys.BOOK_OF_CALLING_GENERIC + ".message_spirit_not_found"),
                        true);
            }
        }
        return false;
    }

    public boolean setSpiritExtractLocation(PlayerEntity player, World world, BlockPos pos, ItemStack stack,
                                            Direction face) {
        UUID boundSpiritId = ItemNBTUtil.getSpiritEntityUUID(stack);
        if (boundSpiritId != null) {
            Optional<SpiritEntity> boundSpirit = EntityUtil.getEntityByUuiDGlobal(world.getServer(), boundSpiritId)
                                                         .map(e -> (SpiritEntity) e);

            if (boundSpirit.isPresent()) {
                //update properties on entity
                boundSpirit.get().setExtractPosition(pos);
                boundSpirit.get().setExtractFacing(face);
                //also update control item with latest data
                ItemNBTUtil.updateItemNBTFromEntity(stack, boundSpirit.get());

                String blockName = world.getBlockState(pos).getBlock().getTranslationKey();
                player.sendStatusMessage(
                        new TranslationTextComponent(TranslationKeys.BOOK_OF_CALLING_GENERIC + ".message_set_extract",
                                TextUtil.formatDemonName(boundSpirit.get().getName().getFormattedText()),
                                new TranslationTextComponent(blockName), face.getName()), true);
                return true;
            }
            else {
                player.sendStatusMessage(
                        new TranslationTextComponent(
                                TranslationKeys.BOOK_OF_CALLING_GENERIC + ".message_spirit_not_found"),
                        true);
            }
        }
        return false;
    }

    public boolean setSpiritBaseLocation(PlayerEntity player, World world, BlockPos pos, ItemStack stack,
                                         Direction face) {
        UUID boundSpiritId = ItemNBTUtil.getSpiritEntityUUID(stack);
        if (boundSpiritId != null) {
            Optional<SpiritEntity> boundSpirit = EntityUtil.getEntityByUuiDGlobal(world.getServer(), boundSpiritId)
                                                         .map(e -> (SpiritEntity) e);
            if (boundSpirit.isPresent()) {
                //update properties on entity
                boundSpirit.get().setWorkAreaPosition(pos);
                //also update control item with latest data
                ItemNBTUtil.updateItemNBTFromEntity(stack, boundSpirit.get());

                String blockName = world.getBlockState(pos).getBlock().getTranslationKey();
                player.sendStatusMessage(
                        new TranslationTextComponent(TranslationKeys.BOOK_OF_CALLING_GENERIC + ".message_set_base",
                                TextUtil.formatDemonName(boundSpirit.get().getName().getFormattedText()),
                                new TranslationTextComponent(blockName)), true);
                return true;
            }
            else {
                player.sendStatusMessage(
                        new TranslationTextComponent(
                                TranslationKeys.BOOK_OF_CALLING_GENERIC + ".message_spirit_not_found"),
                        true);
            }
        }
        return false;
    }

    public ActionResultType handleItemMode(PlayerEntity player, World world, BlockPos pos, ItemStack stack,
                                           Direction facing) {
        ItemMode itemMode = ItemMode.get(this.getItemMode(stack));
        TileEntity tileEntity = world.getTileEntity(pos);

        //handle the serverside item modes
        if (!world.isRemote) {
            switch (itemMode) {
                case SET_DEPOSIT:
                    if (tileEntity != null &&
                        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing).isPresent()) {
                        return this.setSpiritDepositLocation(player, world, pos, stack,
                                facing) ? ActionResultType.SUCCESS : ActionResultType.PASS;
                    }
                    break;
                case SET_EXTRACT:
                    if (tileEntity != null &&
                        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing).isPresent()) {
                        return this.setSpiritExtractLocation(player, world, pos, stack,
                                facing) ? ActionResultType.SUCCESS : ActionResultType.PASS;
                    }
                    break;
                case SET_BASE:
                    return this.setSpiritBaseLocation(player, world, pos, stack,
                            facing) ? ActionResultType.SUCCESS : ActionResultType.PASS;
                case SET_STORAGE_CONTROLLER:
                    if (tileEntity instanceof IStorageController) {
                        return this.setSpiritStorageController(player, world, pos, stack,
                                facing) ? ActionResultType.SUCCESS : ActionResultType.PASS;
                    }
                case SET_MANAGED_MACHINE:
                    if (tileEntity != null && TileEntityUtil.hasCapabilityOnAnySide(tileEntity,
                            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
                        this.setSpiritManagedMachine(player, world, pos, stack, facing);
                        return ActionResultType.SUCCESS;
                    }
                    break;
            }
        }
        else {
            switch (itemMode) {
                case SET_MANAGED_MACHINE:
                    if (tileEntity != null && TileEntityUtil.hasCapabilityOnAnySide(tileEntity,
                            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
                        MachineReference machine = ItemNBTUtil.getManagedMachine(stack);
                        if (machine != null) {
                            GuiHelper.openBookOfCallingManagedMachineGui(machine.insertFacing, machine.extractFacing,
                                    machine.customName);
                        }
                    }
                    break;
            }
        }


        return ActionResultType.PASS;
    }

    //endregion Methods

    public interface IItemModeSubset <T extends IItemModeSubset<T>>{
        ItemMode getItemMode();
        T next();
    }

    public enum ItemMode implements IItemModeSubset<ItemMode> {

        SET_DEPOSIT(0, "set_deposit"),
        SET_EXTRACT(1, "set_extract"),
        SET_BASE(2, "set_base"),
        SET_STORAGE_CONTROLLER(3, "set_storage_controller"),
        SET_MANAGED_MACHINE(4, "set_managed_machine");

        //region Fields
        private static final Map<Integer, ItemMode> lookup = new HashMap<Integer, ItemMode>();
        private static final String TRANSLATION_KEY_BASE =
                "enum." + Occultism.MODID + ".book_of_calling.item_mode";

        static {
            for (ItemMode itemMode : ItemMode.values()) {
                lookup.put(itemMode.getValue(), itemMode);
            }
        }

        private int value;
        private String translationKey;
        //endregion Fields

        //region Initialization
        ItemMode(int value, String translationKey) {
            this.value = value;
            this.translationKey = TRANSLATION_KEY_BASE + "." + translationKey;
        }
        //endregion Initialization

        //region Getter / Setter
        public int getValue() {
            return this.value;
        }

        public String getTranslationKey() {
            return this.translationKey;
        }
        //endregion Getter / Setter

        //region Static Methods
        public static ItemMode get(int value) {
            return lookup.get(value);
        }
        //endregion Static Methods

        @Override
        public ItemMode getItemMode() {
            return this;
        }

        //region Methods
        public ItemMode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        public boolean equals(int value) {
            return this.value == value;
        }
        //endregion Methods
    }
}
