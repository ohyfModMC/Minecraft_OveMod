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
                    
<<<<<<< HEAD
                    // 造成足够伤害让目标死亡（一击必杀）
                    targetPlayer.hurt(targetPlayer.damageSources().playerAttack(player), Float.MAX_VALUE);
                    
                    // 检查目标是否真的死亡（没有触发不死图腾）
                    if (targetPlayer.isDeadOrDying()) {
                        // 显示击杀消息给所有玩家
                        String weaponName = "武器";
                        if (heldItem.getItem() == Items.STICK) {
                            weaponName = "木棍";
                        } else if (heldItem.getItem() == Items.END_ROD) {
                            weaponName = "末地烛";
                        } else if (heldItem.getItem() == Items.BLAZE_ROD) {
                            weaponName = "烈焰棒";
                        }
                        player.getServer().getPlayerList().broadcastSystemMessage(
                            Component.literal(targetPlayer.getName().getString() + "被" +
                                player.getName().getString() + "的" + weaponName + "戳死了~"),
                            false
                        );
                    }
                    
=======
                    // 显示击杀消息给所有玩家
                    String weaponName = (heldItem.getItem() == Items.STICK) ? "木棍" : "末地烛";
                    player.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal(targetPlayer.getName().getString() + " 被 " +
                            player.getName().getString() + " 的" + weaponName + "戳死了~"),
                        false
                    );
                    
                    // 造成足够伤害让目标死亡（一击必杀）
                    targetPlayer.hurt(targetPlayer.damageSources().playerAttack(player), Float.MAX_VALUE);
                    
>>>>>>> 92730e58fb67986325b1b43031d4aa7dd72a0995
                    LOGGER.info("毙命附魔生效，目标已被一击必杀");
                    
                    // 返回成功，阻止原版伤害计算
                    return InteractionResult.SUCCESS;
                }
            }
            
            return InteractionResult.PASS;
        });
        
        LOGGER.info("毙命附魔事件处理器注册完成");
<<<<<<< HEAD
        
        // 注册普通末地烛攻击玩家事件处理器
        registerEndRodAttackHandler();
    }
    
    private static void registerEndRodAttackHandler() {
        LOGGER.info("注册普通末地烛攻击事件处理器...");
        
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) {
                return InteractionResult.PASS;
            }
            
            ItemStack heldItem = player.getItemInHand(hand);
            
            // 检查是否手持普通末地烛（没有毙命附魔）
            if (heldItem.getItem() == Items.END_ROD) {
                int instantKillLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.INSTANT_KILL, heldItem);
                
                // 只有普通末地烛（没有附魔）才处理
                if (instantKillLevel == 0 && entity instanceof Player targetPlayer) {
                    LOGGER.info("普通末地烛攻击玩家 - 攻击者: {}, 目标: {}", 
                        player.getName().getString(), targetPlayer.getName().getString());
                    
                    // 造成普通末地烛伤害（2点伤害）
                    targetPlayer.hurt(targetPlayer.damageSources().playerAttack(player), 2.0f);
                    
                    // 检查目标是否真的死亡（没有触发不死图腾）
                    if (targetPlayer.isDeadOrDying()) {
                        // 显示自定义死亡消息给所有玩家
                        player.getServer().getPlayerList().broadcastSystemMessage(
                            Component.literal(targetPlayer.getName().getString() + "被" +
                                player.getName().getString() + "的末地烛戳死了~"),
                            false
                        );
                        
                        LOGGER.info("普通末地烛击杀玩家，显示自定义死亡消息");
                    }
                    
                    // 返回SUCCESS阻止原版攻击，避免重复伤害和原版死亡消息
                    return InteractionResult.SUCCESS;
                }
            }
            
            return InteractionResult.PASS;
        });
        
        LOGGER.info("普通末地烛攻击事件处理器注册完成");
=======
>>>>>>> 92730e58fb67986325b1b43031d4aa7dd72a0995
    }
}