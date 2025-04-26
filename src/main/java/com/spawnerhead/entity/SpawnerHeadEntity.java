package com.spawnerhead.entity;

import com.spawnerhead.BlockInit;
import com.spawnerhead.EntityInit;
import com.spawnerhead.ItemInit;
import com.spawnerhead.SpawnerHeadConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

public class SpawnerHeadEntity extends Monster implements PowerableMob {
	public static final EntityDataAccessor<String> SPAWNER_ENTITY_ID = SynchedEntityData.defineId(SpawnerHeadEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> BODY_TYPE = SynchedEntityData.defineId(SpawnerHeadEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> IS_CHARGED = SynchedEntityData.defineId(SpawnerHeadEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> REFRESH_DISPLAY_ENTITY = SynchedEntityData.defineId(SpawnerHeadEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> DROP_SPAWNER_ON_DEATH = SynchedEntityData.defineId(SpawnerHeadEntity.class, EntityDataSerializers.BOOLEAN);
	
	private SpawnerHeadSpawner spawner = new SpawnerHeadSpawner(this);
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SPAWNER_ENTITY_ID, "");
		this.entityData.define(BODY_TYPE, 0);
		this.entityData.define(IS_CHARGED, false);
		this.entityData.define(REFRESH_DISPLAY_ENTITY, false);
		this.entityData.define(DROP_SPAWNER_ON_DEATH, false);
	}

	public SpawnerHeadEntity(EntityType<? extends Monster> entity, Level world) {
		super(entity, world);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(2, new RestrictSunGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(ZombifiedPiglin.class));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
	}
	
	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.23D).add(Attributes.MAX_HEALTH, 60.0D).add(Attributes.ARMOR, 2.0D);
	}
	
	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		this.entityData.set(SPAWNER_ENTITY_ID, nbt.getString("spawner_entity_id"));
		this.entityData.set(BODY_TYPE, nbt.getInt("type"));
		this.entityData.set(IS_CHARGED, nbt.getBoolean("is_charged"));
		this.entityData.set(REFRESH_DISPLAY_ENTITY, nbt.getBoolean("refresh_display_entity"));
		this.entityData.set(DROP_SPAWNER_ON_DEATH, nbt.getBoolean("drop_spawner_on_death"));
		this.spawner.load(this.level(), this.blockPosition(), nbt);
		Optional<EntityType<?>> type = EntityType.byString(this.entityData.get(SPAWNER_ENTITY_ID));
		if(type.isPresent()) {
			this.spawner.setEntityId(type.get(), this.level(), this.random, this.blockPosition());
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putString("spawner_entity_id", this.entityData.get(SPAWNER_ENTITY_ID));
		nbt.putInt("type", this.entityData.get(BODY_TYPE));
		nbt.putBoolean("is_charged", this.entityData.get(IS_CHARGED));
		nbt.putBoolean("refresh_display_entity", this.entityData.get(REFRESH_DISPLAY_ENTITY));
		nbt.putBoolean("drop_spawner_on_death", this.entityData.get(DROP_SPAWNER_ON_DEATH));
		this.spawner.save(nbt);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleEntityEvent(byte b) {
		this.spawner.onEventTriggered(this.level(), b);
	}
	
	@Override
	public void aiStep() {
		super.aiStep();

		if(SpawnerHeadConfig.burnsInSunlight.get()) {
			boolean flag = this.isSunBurnTick();
			if (flag) {
				this.setSecondsOnFire(8);
			}
		}
	}
	
	@Override
	public boolean canBeLeashed(Player entity) {
		return SpawnerHeadConfig.canBeLeashed.get();
	}

	@Override
	public void tick() {
		super.tick();
		if(this.level().isClientSide) {
			this.spawner.clientTick(this.level(), this.getSpawnerPos());
		} else {
			this.spawner.serverTick((ServerLevel) this.level(), this.getSpawnerPos());
		}
	}
	
	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		Entity sourceEntity = source.getEntity();
		if(SpawnerHeadConfig.immuneToSkeletonArrows.get()) {
			if (source.is(DamageTypes.ARROW))  {
				if (sourceEntity instanceof AbstractSkeleton) {
					return true;
				}
			}
		}
		if(SpawnerHeadConfig.immuneToCreeperExplosions.get()) {
			if (sourceEntity instanceof Creeper) {
				return true;
			}
		}
		
		return super.isInvulnerableTo(source);
	}

	@Override
	public void die(DamageSource damageSource) {
		super.die(damageSource);
		boolean flag = damageSource.getEntity() instanceof Creeper creeper && creeper.isPowered();
		if (this.getEntityData().get(DROP_SPAWNER_ON_DEATH) || flag) {
			this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
			if (!this.level().isClientSide()) {
				Vec3 eyePos = this.getEyePosition();
				BlockPos pos = new BlockPos((int)eyePos.x, (int)eyePos.y, (int)eyePos.z);
				BlockState state = BlockInit.FALLING_SPAWNER.get().defaultBlockState();
				FallingBlockEntity fallingblockentity = new FallingBlockEntity(this.level(), (double)pos.getX() + 0.5D, (double)pos.getY() + 0.25D, (double)pos.getZ() + 0.5D, state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)) : state) {
					@Override
					public boolean causeFallDamage(float p_149643_, float p_149644_, DamageSource p_149645_) {
						Predicate<Entity> predicate = (entity) -> entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.HUSK;
						Optional<Entity> targetEntity = this.level().getEntities(this, this.getBoundingBox(), predicate).stream().findAny();
						targetEntity.ifPresent((entity) -> {
							if (entity instanceof Zombie zombie && zombie.isBaby()) {
								return;
							}

							SpawnerHeadEntity spawnerHead = EntityInit.SPAWNER_HEAD.get().create(entity.level());
							spawnerHead.copyPosition(entity);
							spawnerHead.finalizeSpawn((ServerLevelAccessor) this.level(), this.level().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.CONVERSION, null, null);
							if(entity.getType() == EntityType.ZOMBIE)
								spawnerHead.setSpawnerHeadType(0);
							else
								spawnerHead.setSpawnerHeadType(1);
							spawnerHead.getEntityData().set(SPAWNER_ENTITY_ID, this.blockData.getString("id"));
							this.blockData.putBoolean("placeBlock", false);

							entity.level().addFreshEntity(spawnerHead);
							((Zombie)entity).getAllSlots().forEach(this::spawnAtLocation);
							entity.discard();
						});
						return false;
					}
				};

				CompoundTag tag = new SpawnData().getEntityToSpawn();
				tag.putString("id", this.entityData.get(SPAWNER_ENTITY_ID));
				tag.putBoolean("placeBlock", true);
				fallingblockentity.blockData = tag;

				double dx = (this.random.nextDouble() - this.random.nextDouble()) * 0.2;
				double dy = this.random.nextDouble() * 1.1;
				double dz = (this.random.nextDouble() - this.random.nextDouble()) * 0.2;
				if(flag) {
					dx *= 1.2;
					dy *= 1.5 + 0.3;
					dz *= 1.2;
				}

				Vec3 vec31 = new Vec3(dx, dy, dz);
				fallingblockentity.setDeltaMovement(fallingblockentity.getDeltaMovement().add(vec31));

				this.level().addFreshEntity(fallingblockentity);
			}
		}
	}

	public void setSpawnerHeadType(int i) {
		this.entityData.set(BODY_TYPE, i);
	}

	public int getSpawnerHeadType() {
		return this.entityData.get(BODY_TYPE);
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
		super.populateDefaultEquipmentSlots(randomSource, difficulty);
		if (this.random.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
			int i = this.random.nextInt(3);
			if (i == 0) {
				this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
			} else {
				this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
			}
		}
	}
	
	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag nbt) {
		data = super.finalizeSpawn(world, difficulty, reason, data, nbt);
		this.populateDefaultEquipmentSlots(this.random, difficulty);
		this.populateDefaultEquipmentEnchantments(this.random, difficulty);
		this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.SPAWNER));
		this.getItemBySlot(EquipmentSlot.HEAD).enchant(Enchantments.ALL_DAMAGE_PROTECTION, this.getRandom().nextInt(3) + 1);
		this.setDropChance(EquipmentSlot.HEAD, 0.0F);
		
		EntityType<?> type = EntityType.ZOMBIE;
		if(!SpawnerHeadSpawns.SPAWN_POTENTIALS.isEmpty()) {
			type = SpawnerHeadSpawns.SPAWN_POTENTIALS.getRandom(this.getRandom()).get().getData();
		}
		this.entityData.set(SPAWNER_ENTITY_ID, ForgeRegistries.ENTITY_TYPES.getKey(type).toString());
		this.spawner.setEntityId(type, world.getLevel(), this.random, this.blockPosition());
		
		if(reason == MobSpawnType.SPAWN_EGG) {
			this.setSpawnerHeadType(random.nextInt(2));
		}
		boolean shouldDropSpawnerOnDeath = this.random.nextInt(100) < SpawnerHeadConfig.dropSpawnerChance.get();
		this.entityData.set(DROP_SPAWNER_ON_DEATH, shouldDropSpawnerOnDeath);
		
		return data;
	}
	
	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		if(SpawnerHeadConfig.allowSpawnEggUse.get()) {
			ItemStack stack = player.getItemInHand(hand);
			Item item = stack.getItem();
			if(item instanceof SpawnEggItem && item != ItemInit.SPAWNER_HEAD_SPAWN_EGG.get()) {
				//TODO: make configurable blacklist
				EntityType<?> entity = ((SpawnEggItem)item).getType(null);
				if(!this.level().isClientSide) {
					this.entityData.set(SPAWNER_ENTITY_ID, ForgeRegistries.ENTITY_TYPES.getKey(entity).toString());
					this.spawner.setEntityId(entity, this.level(), this.random, this.blockPosition());
				}
				this.entityData.set(REFRESH_DISPLAY_ENTITY, true);
				if(!player.isCreative())
					stack.shrink(1);

				return InteractionResult.SUCCESS;
			}
		}
		
		return super.mobInteract(lastHurtByPlayer, hand);
	}
	
	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
		return 1.74F;
	}
	
	@Override
	public int getExperienceReward() {
		return 75 + this.getRandom().nextInt(30) + this.getRandom().nextInt(30);
	}
	
	protected SoundEvent getStepSound() {
		return SoundEvents.ZOMBIE_STEP;
	}
	
	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.BLAZE_HURT;
	}
	
	@Override
	protected ResourceLocation getDefaultLootTable() {
		if(SpawnerHeadConfig.dropSpecialLoot.get()) {
			return BuiltInLootTables.SIMPLE_DUNGEON;
		}

		return EntityType.ZOMBIE.getDefaultLootTable();
	}
	
	public BaseSpawner getSpawner() {
		return this.spawner;
	}
	
	public BlockPos getSpawnerPos() {
		return this.blockPosition().above();
	}

	@Override
	public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
		super.thunderHit(serverLevel, lightningBolt);

		if (SpawnerHeadConfig.canBeChargedByLightning.get()) {
			this.entityData.set(IS_CHARGED, true);
			this.entityData.set(REFRESH_DISPLAY_ENTITY, true);
			this.spawner.setMinSpawnDelay(SpawnerHeadConfig.chargedMinSpawnDelay.get());
			this.spawner.setMaxSpawnDelay(SpawnerHeadConfig.chargedMaxSpawnDelay.get());
		}
	}

	@Override
	public boolean isPowered() {
		return this.entityData.get(IS_CHARGED);
	}

	public boolean isUpsideDown() {
		if (this.hasCustomName()) {
			String name = this.getCustomName().getString();
			if(name.equals("Dinnerbone") || name.equals("Grumm")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setCustomName(Component component) {
		super.setCustomName(component);
		this.entityData.set(REFRESH_DISPLAY_ENTITY, true);
	}
}
