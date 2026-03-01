package com.ove.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.network.chat.Component;

public class InstantKillEnchantment extends Enchantment {
    
    public InstantKillEnchantment() {
        super(Rarity.VERY_RARE, CustomEnchantmentCategory.INSTANT_KILL, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public Component getFullname(int level) {
        return Component.translatable("enchantment.ove.instant_kill");
    }
    
    @Override
    public int getMinCost(int level) {
        return 30; // 高成本，难以获得
    }
    
    @Override
    public int getMaxCost(int level) {
        return 50;
    }
    
    @Override
    public int getMaxLevel() {
        return 1; // 只有1级
    }
    
    @Override
    public boolean canEnchant(ItemStack stack) {
<<<<<<< HEAD
        // 只能附魔在木棍、末地烛和烈焰棒上
        return stack.getItem() == Items.STICK || stack.getItem() == Items.END_ROD || stack.getItem() == Items.BLAZE_ROD;
=======
        // 只能附魔在木棍和末地烛上
        return stack.getItem() == Items.STICK || stack.getItem() == Items.END_ROD;
>>>>>>> 92730e58fb67986325b1b43031d4aa7dd72a0995
    }
    
    @Override
    public boolean isTreasureOnly() {
        return true; // 只能在宝藏中获得
    }
    
    @Override
    public boolean isTradeable() {
        return true; // 可以与村民交易
    }
    
    @Override
    public boolean isDiscoverable() {
        return true; // 可以在附魔台看到
    }
}