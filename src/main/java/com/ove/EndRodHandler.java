package com.ove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndRodHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("Ove-EndRod");
    private static final int COOLDOWN_TICKS = 10;

    public static void register() {
        LOGGER.info("注册末地烛事件处理器...");

        UseItemCallback.EVENT.register((player, world, hand) -> {
            try {
                if (world.isClientSide()) {
                    return InteractionResultHolder.pass(player.getItemInHand(hand));
                }

                ItemStack heldItem = player.getItemInHand(hand);

                // 铁桶Shift+右键处理
                if (heldItem.getItem() == Items.BUCKET && player.isShiftKeyDown()) {
                    LOGGER.info("铁桶Shift+右键 - 直接获得牛奶桶");

                    if (player instanceof ServerPlayer serverPlayer) {
                        if (!serverPlayer.isCreative()) {
                            heldItem.shrink(1);
                        }

                        ItemStack milkBucket = new ItemStack(Items.MILK_BUCKET, 1);

                        if (player.getItemInHand(hand).isEmpty()) {
                            player.setItemInHand(hand, milkBucket);
                        } else {
                            if (!player.getInventory().add(milkBucket)) {
                                player.drop(milkBucket, false);
                            }
                        }

                        world.playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundEvents.BUCKET_FILL,
                                SoundSource.PLAYERS,
                                1.0f,
                                1.0f
                        );

                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 0));

                        LOGGER.info("铁桶转换完成");
                        return InteractionResultHolder.success(heldItem);
                    }
                }

                // 末地烛处理
                if (heldItem.getItem() != Items.END_ROD) {
                    return InteractionResultHolder.pass(heldItem);
                }

                if (player instanceof ServerPlayer serverPlayer) {
                    if (serverPlayer.getCooldowns().isOnCooldown(Items.END_ROD)) {
                        return InteractionResultHolder.pass(heldItem);
                    }

                    if (!player.isShiftKeyDown()) {
                        LOGGER.info("右键使用末地烛 - 触发事件, 当前数量: {}", heldItem.getCount());

                        world.playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundEvents.HONEY_BLOCK_BREAK,
                                SoundSource.BLOCKS,
                                1.0f,
                                1.0f
                        );

                        executeEndRodEffect(serverPlayer, hand, heldItem);
                        serverPlayer.getCooldowns().addCooldown(Items.END_ROD, COOLDOWN_TICKS);

                        return InteractionResultHolder.success(heldItem);
                    }
                }

                return InteractionResultHolder.pass(heldItem);

            } catch (Exception e) {
                LOGGER.error("UseItemCallback处理出错", e);
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            try {
                if (world.isClientSide()) {
                    return InteractionResult.PASS;
                }

                ItemStack heldItem = player.getItemInHand(hand);
                BlockPos pos = hitResult.getBlockPos();
                BlockState state = world.getBlockState(pos);

                if (heldItem.getItem() == Items.END_ROD && !player.isShiftKeyDown()) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        if (!serverPlayer.getCooldowns().isOnCooldown(Items.END_ROD)) {
                            LOGGER.info("手持末地烛点击方块 - 触发事件, 当前数量: {}", heldItem.getCount());

                            world.playSound(
                                    null,
                                    player.getX(),
                                    player.getY(),
                                    player.getZ(),
                                    SoundEvents.HONEY_BLOCK_BREAK,
                                    SoundSource.BLOCKS,
                                    1.0f,
                                    1.0f
                            );

                            executeEndRodEffect(serverPlayer, hand, heldItem);
                            serverPlayer.getCooldowns().addCooldown(Items.END_ROD, COOLDOWN_TICKS);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }

                if (state.is(Blocks.END_ROD)) {
                    if (player instanceof ServerPlayer serverPlayer && !player.isShiftKeyDown()) {
                        if (!serverPlayer.getCooldowns().isOnCooldown(Items.END_ROD)) {
                            LOGGER.info("右键点击已放置末地烛 - 触发事件");
                            world.playSound(
                                    null,
                                    player.getX(),
                                    player.getY(),
                                    player.getZ(),
                                    SoundEvents.HONEY_BLOCK_BREAK,
                                    SoundSource.BLOCKS,
                                    1.0f,
                                    1.0f
                            );
                            executeEndRodEffectWithoutConsume(serverPlayer);
                            serverPlayer.getCooldowns().addCooldown(Items.END_ROD, COOLDOWN_TICKS);
                            return InteractionResult.SUCCESS;
                        }
                    }
                    return InteractionResult.PASS;
                }

                return InteractionResult.PASS;

            } catch (Exception e) {
                LOGGER.error("UseBlockCallback处理出错", e);
                return InteractionResult.PASS;
            }
        });

        LOGGER.info("末地烛事件处理器注册完成");
        registerBucketHandler();
    }

    private static void registerBucketHandler() {
        LOGGER.info("注册铁桶事件处理器...");

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            try {
                if (world.isClientSide()) {
                    return InteractionResult.PASS;
                }

                ItemStack heldItem = player.getItemInHand(hand);

                if (heldItem.getItem() == Items.BUCKET && entity instanceof Player targetPlayer) {
                    // 检查是否是主手操作
                    if (hand != net.minecraft.world.InteractionHand.MAIN_HAND) {
                        return InteractionResult.PASS;
                    }
                    
                    if (player instanceof ServerPlayer serverPlayer) {
                        // 使用冷却系统防止重复触发
                        if (serverPlayer.getCooldowns().isOnCooldown(Items.BUCKET)) {
                            LOGGER.info("铁桶右键玩家 - 冷却中，跳过");
                            return InteractionResult.PASS;
                        }
                        
                        serverPlayer.getCooldowns().addCooldown(Items.BUCKET, 5);
                        
                        LOGGER.info("铁桶右键玩家触发 - 目标: {}, 手持物品数量: {}", 
                            targetPlayer.getName().getString(), heldItem.getCount());

                        if (!serverPlayer.isCreative()) {
                            heldItem.shrink(1);
                        }

                        ItemStack milkBucket = new ItemStack(Items.MILK_BUCKET, 1);

                        if (player.getItemInHand(hand).isEmpty()) {
                            player.setItemInHand(hand, milkBucket);
                        } else {
                            if (!player.getInventory().add(milkBucket)) {
                                player.drop(milkBucket, false);
                            }
                        }

                        world.playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundEvents.BUCKET_FILL,
                                SoundSource.PLAYERS,
                                1.0f,
                                1.0f
                        );

                        world.playSound(
                                null,
                                targetPlayer.getX(),
                                targetPlayer.getY(),
                                targetPlayer.getZ(),
                                SoundEvents.BUCKET_FILL,
                                SoundSource.PLAYERS,
                                1.0f,
                                1.0f
                        );

                        targetPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 0));

                        if (targetPlayer instanceof ServerPlayer targetServerPlayer) {
                            targetServerPlayer.displayClientMessage(Component.literal(""), true);
                        }

                        return InteractionResult.SUCCESS;
                    }
                }

                return InteractionResult.PASS;

            } catch (Exception e) {
                LOGGER.error("UseEntityCallback处理出错", e);
                return InteractionResult.PASS;
            }
        });

        LOGGER.info("铁桶事件处理器注册完成");
    }

    // ===== 修改为生命恢复V的版本 =====
    private static void executeEndRodEffect(ServerPlayer player, net.minecraft.world.InteractionHand hand, ItemStack heldItem) {
        int instantKillLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.INSTANT_KILL, heldItem);

        if (instantKillLevel > 0) {
            LOGGER.info("毙命附魔末地烛触发 - 玩家: {}", player.getName().getString());
            
            player.displayClientMessage(Component.literal("呜嗯♡~进去了~"), true);
            
            // 造成伤害
            player.hurt(player.damageSources().magic(), Float.MAX_VALUE);
            
            // 检查是否死亡并发送自定义消息
            if (player.isDeadOrDying()) {
                player.getServer().execute(() -> {
                    player.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal(player.getName().getString() + "被附魔的末地烛戳死了~"),
                        false
                    );
                });
            }
            return;
        }

        if (player.getHealth() <= 1.0f) {
            LOGGER.info("残血使用末地烛死亡 - 玩家: {}", player.getName().getString());
            
            player.displayClientMessage(Component.literal("呜嗯♡~进去了~"), true);
            
            // 造成伤害
            player.hurt(player.damageSources().magic(), Float.MAX_VALUE);
            
            // 检查是否死亡并发送自定义消息
            if (player.isDeadOrDying()) {
                player.getServer().execute(() -> {
                    player.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal(player.getName().getString() + "被末地烛戳死了~"),
                        false
                    );
                });
            }
            return;
        }

        // 普通使用（不致死）- 改为生命恢复V
        player.hurt(player.damageSources().magic(), 2.0f);
        player.displayClientMessage(Component.literal("呜嗯♡~进去了~"), true);
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 4)); // 4 = 生命恢复V

        LOGGER.info("末地烛使用 - 数量不变: {}", heldItem.getCount());

        // 同步客户端
        player.getInventory().setChanged();
        player.inventoryMenu.broadcastChanges();

        int slotId = (hand == net.minecraft.world.InteractionHand.MAIN_HAND) ?
                player.getInventory().selected : 40;
        player.connection.send(new ClientboundContainerSetSlotPacket(
                player.inventoryMenu.containerId,
                player.inventoryMenu.incrementStateId(),
                slotId,
                heldItem.copy()
        ));

        player.inventoryMenu.sendAllDataToRemote();

        LOGGER.info("效果已给予，扣血完成，物品数量保持不变: {}", heldItem.getCount());
    }

    private static void executeEndRodEffectWithoutConsume(ServerPlayer player) {
        player.hurt(player.damageSources().magic(), 2.0f);
        player.displayClientMessage(Component.literal("呜嗯♡~进去了~"), true);
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 4)); // 4 = 生命恢复V
    }
}