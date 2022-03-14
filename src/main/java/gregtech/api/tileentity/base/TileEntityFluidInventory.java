package gregtech.api.tileentity.base;

import gregtech.api.capability.impl.FluidHandlerProxy;
import gregtech.api.capability.impl.FluidTankList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityFluidInventory extends TileEntityBaseCoverable {

    public static final String FLUID_INVENTORY_TAG = "FluidInventory";
    public static final String INVENTORY_FLUID_INPUTS_TAG = "ImportFluidInventory";
    public static final String INVENTORY_FLUID_OUTPUTS_TAG = "ExportFluidInventory";

    protected final boolean hasSeparateIO;

    protected IFluidHandler fluidInventory;
    protected FluidTankList importFluidInventory;
    protected FluidTankList exportFluidInventory;

    public boolean fluidInventoryChanged = false;

    public TileEntityFluidInventory(boolean hasSeparateIO) {
        super();
        this.hasSeparateIO = hasSeparateIO;
        initializeInventory();
    }

    protected void initializeInventory() {
        if (hasSeparateIO) {
            this.importFluidInventory = new FluidTankList(false);
            this.exportFluidInventory = new FluidTankList(false);
            this.fluidInventory = new FluidHandlerProxy(importFluidInventory, exportFluidInventory);
        } else {
            this.fluidInventory = new FluidTank(getDefaultFluidInventorySize());
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        if (this.hasSeparateIO) {
            data.setTag(INVENTORY_FLUID_INPUTS_TAG, importFluidInventory.serializeNBT());
            data.setTag(INVENTORY_FLUID_OUTPUTS_TAG, exportFluidInventory.serializeNBT());
        } else if (fluidInventory instanceof FluidTank) {
            data.setTag(FLUID_INVENTORY_TAG, ((FluidTank) fluidInventory).writeToNBT(new NBTTagCompound()));
        }

        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        if (this.hasSeparateIO) {
            importFluidInventory.deserializeNBT(data.getCompoundTag(INVENTORY_FLUID_INPUTS_TAG));
            exportFluidInventory.deserializeNBT(data.getCompoundTag(INVENTORY_FLUID_OUTPUTS_TAG));
        } else if (fluidInventory instanceof FluidTank) {
            ((FluidTank) fluidInventory).readFromNBT(data.getCompoundTag(FLUID_INVENTORY_TAG));
        }
    }

    public int getDefaultFluidInventorySize() {
        return 0;
    }

    @Override
    public void onTickResetChecks(long timer, boolean isServerSide) {
        super.onTickResetChecks(timer, isServerSide);
        fluidInventoryChanged = false;
    }

    /**
     * Override this if the MTE will keep its Fluid inventory on-break.
     * If this is overridden to return True, you MUST take care to handle
     * the Fluids in the MTE's inventory otherwise they will be voided on break.
     *
     * @return True if MTE fluid inventory is kept as an ItemStack, false otherwise
     */
    public boolean keepsFluidInventory() {
        return false;
    }

    @Override
    public void initFromNBT(NBTTagCompound compound, ResourceLocation multiTileEntityId, short itemStackMeta) {
        super.initFromNBT(compound, multiTileEntityId, itemStackMeta);
        if (keepsFluidInventory() && fluidInventory instanceof FluidTank && itemStack.hasKey(FluidHandlerItemStack.FLUID_NBT_KEY, Constants.NBT.TAG_COMPOUND)) {
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(itemStack.getCompoundTag(FluidHandlerItemStack.FLUID_NBT_KEY));
            ((FluidTank) fluidInventory).setFluid(fluidStack);
        }
    }

    @Override
    public NBTTagCompound writeItemNBT(NBTTagCompound nbtTagCompound) {
        if (keepsFluidInventory() && fluidInventory instanceof FluidTank) {
            FluidStack fluidStack = ((FluidTank) fluidInventory).getFluid();
            if (fluidStack != null && fluidStack.amount > 0) {
                NBTTagCompound tagCompound = new NBTTagCompound();
                fluidStack.writeToNBT(tagCompound);
                itemStack.setTag(FluidHandlerItemStack.FLUID_NBT_KEY, tagCompound);
            }
            return nbtTagCompound;
        }
        return nbtTagCompound;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        updateFluidInventory();
    }

    public void updateFluidInventory() {
        fluidInventoryChanged = true;
    }

    public IFluidHandler getFluidInventory() {
        return this.fluidInventory;
    }

    public FluidTankList getImportFluidInventory() {
        return importFluidInventory;
    }

    public FluidTankList getExportFluidInventory() {
        return exportFluidInventory;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        T result = super.getCapability(capability, facing);
        if (result != null) return result;

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && getFluidInventory().getTankProperties().length > 0) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getFluidInventory());
        }
        return null;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (super.hasCapability(capability, facing))
            return true;
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && getFluidInventory().getTankProperties().length > 0;
    }
}