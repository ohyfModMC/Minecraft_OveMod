package com.ove.enchantment;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class CustomEnchantmentCategory {
    // 使用现有的武器类别，然后在附魔类中重写canEnchant方法
    public static final EnchantmentCategory INSTANT_KILL = EnchantmentCategory.WEAPON;
}