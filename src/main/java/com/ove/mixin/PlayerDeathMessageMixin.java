package com.ove.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ove.EnchantmentRegistry;

@Mixin(ServerPlayer.class)
public class PlayerDeathMessageMixin {
    
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void onPlayerDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        
        // 检查是否是玩家攻击造成的死亡
        if (damageSource.getEntity() instanceof Player attacker) {
            ItemStack heldItem = attacker.getMainHandItem();
            
            // 检查攻击者是否手持模组相关物品
            if (heldItem.getItem() == Items.END_ROD || 
                heldItem.getItem() == Items.STICK || 
                heldItem.getItem() == Items.BLAZE_ROD) {
                
                int instantKillLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.INSTANT_KILL, heldItem);
                
                // 如果是模组相关物品造成的死亡，取消原版死亡消息
                ci.cancel();
                return;
            }
        }
        
        // 检查是否是末地烛使用造成的自杀
        if (damageSource.getEntity() == player) {
            // 检查是否是末地烛使用造成的伤害
            // 在Minecraft 1.20.1中，末地烛使用造成的是魔法伤害
            if (damageSource.type().msgId().equals("magic")) {
                // 可能是末地烛使用造成的死亡，取消原版死亡消息
                ci.cancel();
                return;
            }
        }
        
        // 检查是否是残血使用末地烛造成的死亡
        if (damageSource.getEntity() == player && damageSource.type().msgId().equals("magic")) {
            // 可能是残血使用末地烛造成的死亡，取消原版死亡消息
            ci.cancel();
            return;
        }
    }
}