package com.ove;

import com.ove.enchantment.InstantKillEnchantment;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentRegistry {
    
    public static final Enchantment INSTANT_KILL = new InstantKillEnchantment();
    
    public static void register() {
        Registry.register(BuiltInRegistries.ENCHANTMENT, 
            new ResourceLocation("ove", "instant_kill"), 
            INSTANT_KILL
        );
    }
}