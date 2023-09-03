package thaumicboots.item.boots.comet;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.ISpecialArmor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.util.EMTConfigHandler;
import emt.util.EMTTextHelper;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumicboots.main.utils.TabThaumicBoots;

public class ItemNanoCometBoots extends ItemElectricCometBoots
        implements IRepairable, IRunicArmor, IElectricItem, IVisDiscountGear, ISpecialArmor {

    public IIcon icon;

    public int maxCharge = 1000000;
    public int energyPerDamage = 5000;
    public int visDiscount = 4;

    public double transferLimit = 1600;

    public ItemNanoCometBoots(ArmorMaterial par2EnumArmorMaterial, int par3, int par4) {
        super(par2EnumArmorMaterial, par3, par4);
        setCreativeTab(TabThaumicBoots.tabThaumicBoots);
        setUnlocalizedName("ItemNanoComet");
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
        ItemStack itemStack = new ItemStack(this, 1);
        if (getChargedItem(itemStack) == this) {
            ItemStack charged = new ItemStack(this, 1);
            ElectricItem.manager.charge(charged, 2147483647, 2147483647, true, false);
            itemList.add(charged);
        }
        if (getEmptyItem(itemStack) == this) {
            itemList.add(new ItemStack(this, 1, getMaxDamage()));
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.icon = ir.registerIcon("thaumicboots:nanoComet_16x");
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_77617_a(int par1) {
        return this.icon;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String layer) {
        return "thaumicboots:model/nanobootsComet.png";
    }

    @Override
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.rare;
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return par2ItemStack.isItemEqual(new ItemStack(Items.leather)) ? true
                : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    @Override
    protected float computeBonus(ItemStack itemStack, EntityPlayer player) {
        int ticks = player.inventory.armorItemInSlot(0).stackTagCompound.getInteger("runTicks");
        float bonus = (float) EMTConfigHandler.nanoBootsSpeed + 0.110F;
        bonus = bonus + ((ticks / 5) * 0.003F);
        if (ElectricItem.manager.getCharge(itemStack) == 0) {
            bonus = 0;
        } else if (player.isInWater()) {
            bonus /= 4.0F;
        }

        return bonus;
    }

    @Override
    public float getMinimumHeight() {
        return 6F;
    }

    @Override
    public float getPowerConsumptionMultiplier(float distance) {
        return (distance > EMTConfigHandler.nanoBootsMaxDrop) ? distance * 3 : distance;
    }

    public int getRunicCharge(ItemStack arg0) {
        return 0;
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source,
            double damage, int slot) {
        if (source.isUnblockable()) {
            return new net.minecraftforge.common.ISpecialArmor.ArmorProperties(0, 0.0D, 0);
        } else {
            double absorptionRatio = getBaseAbsorptionRatio() * getDamageAbsorptionRatio();
            int energyPerDamage = getEnergyPerDamage();
            double damageLimit = energyPerDamage <= 0 ? 0
                    : (25 * ElectricItem.manager.getCharge(armor)) / energyPerDamage;
            return new net.minecraftforge.common.ISpecialArmor.ArmorProperties(0, absorptionRatio, (int) damageLimit);
        }
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        if (ElectricItem.manager.getCharge(armor) >= getEnergyPerDamage()) {
            return (int) Math.round(20D * getBaseAbsorptionRatio() * getDamageAbsorptionRatio());
        } else {
            return 0;
        }
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        ElectricItem.manager.discharge(stack, damage * getEnergyPerDamage(), 0x7fffffff, true, false, false);
    }

    public double getDamageAbsorptionRatio() {
        return 1.5D;
    }

    private double getBaseAbsorptionRatio() {
        return 0.15D;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        list.add(EMTTextHelper.PURPLE + EMTTextHelper.localize("tooltip.EMT.visDiscount") + ": " + visDiscount + "%");
    }

    public int getEnergyPerDamage() {
        return energyPerDamage;
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return false;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return maxCharge;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return 3;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return transferLimit;
    }

    @Override
    public Item getChargedItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        return visDiscount;
    }
}
