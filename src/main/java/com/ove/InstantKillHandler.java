package com.ove;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstantKillHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("Ove-InstantKill");
    
    public static void register() {
        LOGGER.info("注册毙命附魔事件处理器...");
        
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) {
                return InteractionResult.PASS;
            }
            
            ItemStack heldItem = player.getItemInHand(hand);
            
            // 检查武器是否有毙命附魔
            int instantKillLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.INSTANT_KILL, heldItem);
            
            if (instantKillLevel > 0) {
                // 检查目标是否是玩家
                if (entity instanceof Player targetPlayer) {
                    LOGGER.info("毙命附魔触发 - 玩家: {}, 目标: {}", 
                        player.getName().getString(), targetPlayer.getName().getString());
                    
                    // 显示击杀消息给所有玩家
                    String weaponName = (heldItem.getItem() == Items.STICK) ? "木棍" : "末地烛";
                    player.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal(targetPlayer.getName().getString() + " 被 " +
                            player.getName().getString() + " 的" + weaponName + "戳死了~"),
                        false
                    );
                    
                    // 一击必杀：直接设置目标生命值为0
                    targetPlayer.setHealth(0.0f);
                    
                    LOGGER.info("毙命附魔生效，目标已被一击必杀");
                    
                    // 返回成功，阻止原版伤害计算
                    return InteractionResult.SUCCESS;
                }
            }
            
            return InteractionResult.PASS;
        });
        
        LOGGER.info("毙命附魔事件处理器注册完成");
    }
}