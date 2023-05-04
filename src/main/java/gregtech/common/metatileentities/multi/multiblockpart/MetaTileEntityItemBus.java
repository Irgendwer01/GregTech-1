package gregtech.common.metatileentities.multi.multiblockpart;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.*;
import gregtech.api.capability.impl.GhostCircuitItemStackHandler;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.capability.impl.NotifiableItemStackHandler;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.ModularUI.Builder;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.GhostCircuitSlotWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;
import gregtech.api.util.GTHashMaps;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.InventoryUtils;
import gregtech.api.util.ItemStackKey;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MetaTileEntityItemBus extends MetaTileEntityMultiblockNotifiablePart implements IMultiblockAbilityPart<IItemHandlerModifiable>, IControllable, IGhostSlotConfigurable {

    @Nullable
    protected GhostCircuitItemStackHandler circuitInventory;
    private IItemHandlerModifiable actualImportItems;

    private boolean workingEnabled;
    private boolean autoCollapse;

    public MetaTileEntityItemBus(ResourceLocation metaTileEntityId, int tier, boolean isExportHatch) {
        super(metaTileEntityId, tier, isExportHatch);
        this.workingEnabled = true;
        initializeInventory();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityItemBus(metaTileEntityId, getTier(), isExportHatch);
    }

    @Override
    protected void initializeInventory() {
        super.initializeInventory();
        if (this.hasGhostCircuitInventory()) {
            this.circuitInventory = new GhostCircuitItemStackHandler();
            this.circuitInventory.addNotifiableMetaTileEntity(this);
            this.actualImportItems = new ItemHandlerList(Arrays.asList(super.getImportItems(), this.circuitInventory));
        } else {
            this.actualImportItems = null;
        }
    }

    @Override
    public IItemHandlerModifiable getImportItems() {
        return this.actualImportItems == null ? super.getImportItems() : this.actualImportItems;
    }

    @Override
    public void addToMultiBlock(MultiblockControllerBase controllerBase) {
        super.addToMultiBlock(controllerBase);
        if (hasGhostCircuitInventory() && this.actualImportItems instanceof ItemHandlerList) {
            for (IItemHandler handler : ((ItemHandlerList) this.actualImportItems).getBackingHandlers()) {
                if (handler instanceof INotifiableHandler) {
                    INotifiableHandler notifiable = (INotifiableHandler) handler;
                    notifiable.addNotifiableMetaTileEntity(controllerBase);
                    notifiable.addToNotifiedList(this, handler, isExportHatch);
                }
            }
        }
    }

    @Override
    public void removeFromMultiBlock(MultiblockControllerBase controllerBase) {
        super.removeFromMultiBlock(controllerBase);
        if (hasGhostCircuitInventory() && this.actualImportItems instanceof ItemHandlerList) {
            for (IItemHandler handler : ((ItemHandlerList) this.actualImportItems).getBackingHandlers()) {
                if (handler instanceof INotifiableHandler) {
                    INotifiableHandler notifiable = (INotifiableHandler) handler;
                    notifiable.removeNotifiableMetaTileEntity(controllerBase);
                }
            }
        }
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote && getOffsetTimer() % 5 == 0) {
            if (workingEnabled) {
                if (isExportHatch) {
                    pushItemsIntoNearbyHandlers(getFrontFacing());
                } else {
                    pullItemsFromNearbyHandlers(getFrontFacing());
                }
            }
            // Only attempt to auto collapse the inventory contents once the bus has been notified
            if (isAutoCollapse()) {
                IItemHandlerModifiable inventory = (isExportHatch ? this.getExportItems() : this.getImportItems());
                if  (isExportHatch ? this.getNotifiedItemOutputList().contains(inventory) : this.getNotifiedItemInputList().contains(inventory)) {
                    collapseInventorySlotContents(inventory);
                }
            }
        }
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        this.workingEnabled = workingEnabled;
        World world = getWorld();
        if (world != null && !world.isRemote) {
            writeCustomData(GregtechDataCodes.WORKING_ENABLED, buf -> buf.writeBoolean(workingEnabled));
        }
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            SimpleOverlayRenderer renderer = isExportHatch ? Textures.PIPE_OUT_OVERLAY : Textures.PIPE_IN_OVERLAY;
            renderer.renderSided(getFrontFacing(), renderState, translation, pipeline);
            SimpleOverlayRenderer overlay = isExportHatch ? Textures.ITEM_HATCH_OUTPUT_OVERLAY : Textures.ITEM_HATCH_INPUT_OVERLAY;
            overlay.renderSided(getFrontFacing(), renderState, translation, pipeline);
        }
    }

    private int getInventorySize() {
        int sizeRoot = 1 + Math.min(9, getTier());
        return sizeRoot * sizeRoot;
    }

    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        return isExportHatch ? new NotifiableItemStackHandler(getInventorySize(), getController(), true) : new ItemStackHandler(0);
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return isExportHatch ? new ItemStackHandler(0) : new NotifiableItemStackHandler(getInventorySize(), getController(), false);
    }

    @Override
    public MultiblockAbility<IItemHandlerModifiable> getAbility() {
        return isExportHatch ? MultiblockAbility.EXPORT_ITEMS : MultiblockAbility.IMPORT_ITEMS;
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(workingEnabled);
        buf.writeBoolean(autoCollapse);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.workingEnabled = buf.readBoolean();
        this.autoCollapse = buf.readBoolean();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("workingEnabled", workingEnabled);
        data.setBoolean("autoCollapse", autoCollapse);
        if (this.circuitInventory != null && !this.isExportHatch) {
            this.circuitInventory.write(data);
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("workingEnabled")) {
            this.workingEnabled = data.getBoolean("workingEnabled");
        }
        if (data.hasKey("autoCollapse")) {
            this.autoCollapse = data.getBoolean("autoCollapse");
        }
        if (this.circuitInventory != null && !this.isExportHatch) {
            if (data.hasKey("CircuitInventory", Constants.NBT.TAG_COMPOUND)) {
                // legacy save support - move items in circuit inventory to importItems inventory, if possible
                ItemStackHandler legacyCircuitInventory = new ItemStackHandler();
                legacyCircuitInventory.deserializeNBT(data.getCompoundTag("CircuitInventory"));
                for (int i = 0; i < legacyCircuitInventory.getSlots(); i++) {
                    ItemStack stack = legacyCircuitInventory.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    stack = GTTransferUtils.insertItem(this.importItems, stack, false);
                    // If there's no space left in importItems, just set it as ghost circuit and void the item
                    this.circuitInventory.setCircuitValueFromStack(stack);
                }
            } else {
                this.circuitInventory.read(data);
            }
        }
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.TOGGLE_COLLAPSE_ITEMS) {
            this.autoCollapse = buf.readBoolean();
        }
    }

    @Override
    public void registerAbilities(List<IItemHandlerModifiable> abilityList) {
        if (this.hasGhostCircuitInventory() && this.actualImportItems != null) {
            abilityList.add(isExportHatch ? this.exportItems : this.actualImportItems);
        } else {
            abilityList.add(isExportHatch ? this.exportItems : this.importItems);
        }
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        int rowSize = (int) Math.sqrt(getInventorySize());
        return createUITemplate(entityPlayer, rowSize)
                .build(getHolder(), entityPlayer);
    }

    private ModularUI.Builder createUITemplate(EntityPlayer player, int gridSize) {
        int backgroundWidth = gridSize > 6 ? 176 + (gridSize - 6) * 18 : 176;
        int center = backgroundWidth / 2;

        int gridStartX = center - (gridSize * 9);

        int inventoryStartX = center - 9 - 4 * 18;
        int inventoryStartY = 18 + 18 * gridSize + 12;

        Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, backgroundWidth, 18 + 18 * gridSize + 94)
                .label(10, 5, getMetaFullName());

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                int index = y * gridSize + x;

                builder.widget(new SlotWidget(isExportHatch ? exportItems : importItems, index,
                        gridStartX + x * 18, 18 + y * 18, true, !isExportHatch)
                        .setBackgroundTexture(GuiTextures.SLOT));
            }
        }

        if (hasGhostCircuitInventory() && this.circuitInventory != null) {
            int circuitX = gridSize > 6 ? gridStartX + gridSize * 18 + 9 : inventoryStartX + 8 * 18;
            int circuitY = gridSize * 18;

            SlotWidget circuitSlot = new GhostCircuitSlotWidget(circuitInventory, 0, circuitX, circuitY)
                    .setBackgroundTexture(GuiTextures.SLOT, getCircuitSlotOverlay());
            builder.widget(getCircuitSlotTooltip(circuitSlot))
                    .widget(new ClickButtonWidget(circuitX - 9, circuitY, 9, 9, "",
                            click -> circuitInventory.addCircuitValue(click.isShiftClick ? 5 : 1))
                            .setShouldClientCallback(true)
                            .setButtonTexture(GuiTextures.BUTTON_INT_CIRCUIT_PLUS)
                            .setDisplayFunction(() -> circuitInventory.hasCircuitValue() && circuitInventory.getCircuitValue() < IntCircuitIngredient.CIRCUIT_MAX))
                    .widget(new ClickButtonWidget(circuitX - 9, circuitY + 9, 9, 9, "",
                            click -> circuitInventory.addCircuitValue(click.isShiftClick ? -5 : -1))
                            .setShouldClientCallback(true)
                            .setButtonTexture(GuiTextures.BUTTON_INT_CIRCUIT_MINUS)
                            .setDisplayFunction(() -> circuitInventory.hasCircuitValue() && circuitInventory.getCircuitValue() > IntCircuitIngredient.CIRCUIT_MIN));
        }

        return builder.bindPlayerInventory(player.inventory, GuiTextures.SLOT, inventoryStartX, inventoryStartY);
    }

    @Override
    public boolean hasGhostCircuitInventory() {
        return !this.isExportHatch;
    }

    // Method provided to override
    protected TextureArea getCircuitSlotOverlay() {
        return GuiTextures.INT_CIRCUIT_OVERLAY;
    }

    // Method provided to override
    protected SlotWidget getCircuitSlotTooltip(@Nonnull SlotWidget widget) {
        return widget.setTooltipText("gregtech.gui.configurator_slot.tooltip");
    }

    private static void collapseInventorySlotContents(IItemHandlerModifiable inventory) {

        // Gather a snapshot of the provided inventory
        Map<ItemStackKey, Integer> inventoryContents = GTHashMaps.fromItemHandler(inventory);

        List<ItemStack> inventoryItemContents = new ArrayList<>();

        // Populate the list of item stacks in the inventory with apportioned item stacks, for easy replacement
        for(Map.Entry<ItemStackKey, Integer> slot : inventoryContents.entrySet()) {
            ItemStack stack = slot.getKey().getItemStack();
            stack.setCount(slot.getValue());
            inventoryItemContents.addAll(InventoryUtils.apportionStack(stack, stack.getMaxStackSize()));
        }

        for(int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stackToMove;
            // Ensure that we are not exceeding the List size when attempting to populate items
            if(i >= inventoryItemContents.size()) {
                stackToMove = ItemStack.EMPTY;
            }
            else {
                stackToMove = inventoryItemContents.get(i);
            }

            // Populate the slots
            inventory.setStackInSlot(i, stackToMove);
        }

    }

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {

        boolean isAttached = false;
        if (this.isAttachedToMultiBlock()){
            setAutoCollapse(!this.autoCollapse);
            isAttached = true;
        }

        if(!getWorld().isRemote) {
            if (isAttached) {
                if(this.autoCollapse) {
                    playerIn.sendMessage(new TextComponentTranslation("gregtech.bus.collapse_true"));
                }
                else {
                    playerIn.sendMessage(new TextComponentTranslation("gregtech.bus.collapse_false"));
                }
            } else {
                playerIn.sendMessage(new TextComponentTranslation("gregtech.bus.collapse.error"));
            }
        }
        return true;
    }

    public boolean isAutoCollapse() {
        return autoCollapse;
    }

    public void setAutoCollapse(boolean inverted) {
        autoCollapse = inverted;
        if (!getWorld().isRemote) {
            writeCustomData(GregtechDataCodes.TOGGLE_COLLAPSE_ITEMS, packetBuffer -> packetBuffer.writeBoolean(autoCollapse));
            notifyBlockUpdate();
            markDirty();
        }
    }

    @Override
    public void setGhostCircuitConfig(int config) {
        if (this.circuitInventory == null || this.circuitInventory.getCircuitValue() == config) {
            return;
        }
        this.circuitInventory.setCircuitValue(config);
        if (!getWorld().isRemote) {
            markDirty();
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @Nonnull List<String> tooltip, boolean advanced) {
        if (this.isExportHatch)
            tooltip.add(I18n.format("gregtech.machine.item_bus.export.tooltip"));
        else
            tooltip.add(I18n.format("gregtech.machine.item_bus.import.tooltip"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.item_storage_capacity", getInventorySize()));
        tooltip.add(I18n.format("gregtech.universal.enabled"));
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.auto_collapse"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }
}
