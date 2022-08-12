package com.spawnerhead.entity;

import java.lang.reflect.Field;
import java.util.Optional;

import javax.annotation.Nullable;

import com.spawnerhead.ItemInit;
import com.spawnerhead.SpawnerHeadConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO: allow changing of spawner entity with spawn eggs
public class SpawnerHeadEntity extends Monster {
	
	public static final EntityDataAccessor<String> SPAWNER_ENTITY_ID = SynchedEntityData.defineId(SpawnerHeadEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> BODY_TYPE = SynchedEntityData.defineId(SpawnerHeadEntity.class, EntityDataSerializers.INT);
	
	private Entity displayEntity = null;
	
	private BaseSpawner spawner = new BaseSpawner() {
		
		@Override
		public void broadcastEvent(Level level, BlockPos pos, int i) {
			level.broadcastEntityEvent(SpawnerHeadEntity.this, (byte) i);
		}

		@Override
		@Nullable
		public Entity getSpawnerEntity() {
			return SpawnerHeadEntity.this;
		}
		
		@Override
		@Nullable
		public Entity getOrCreateDisplayEntity(Level level) {

			if (displayEntity == null) {

				Optional<EntityType<?>> type = EntityType.byString(entityData.get(SPAWNER_ENTITY_ID));
				//sync entity with basespawner on client
				if(type.isPresent()) {
					if(level.isClientSide) {
						this.setEntityId(type.get());
					}

					displayEntity = super.getOrCreateDisplayEntity(level);
				}
			} else if(displayEntity.getType() != EntityType.byString(entityData.get(SPAWNER_ENTITY_ID)).get()) {
				this.displayEntity = null;
				this.setEntityId(EntityType.byString(entityData.get(SPAWNER_ENTITY_ID)).get());
				displayEntity = super.getOrCreateDisplayEntity(level);
			}

			return displayEntity;
		}
	};
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SPAWNER_ENTITY_ID, "");
		this.entityData.define(BODY_TYPE, 0);
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
		this.spawner.load(this.level, this.blockPosition(), nbt);
		Optional<EntityType<?>> type = EntityType.byString(this.entityData.get(SPAWNER_ENTITY_ID));
		if(type.isPresent()) {
			this.spawner.setEntityId(type.get());
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putString("spawner_entity_id", this.entityData.get(SPAWNER_ENTITY_ID));
		nbt.putInt("type", this.entityData.get(BODY_TYPE));
		this.spawner.save(nbt);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleEntityEvent(byte b) {
		this.spawner.onEventTriggered(this.level, b);
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
		if(this.level.isClientSide) {
			this.spawner.clientTick(this.level, this.getSpawnerPos());
		} else {
			this.spawner.serverTick((ServerLevel) this.level, this.getSpawnerPos());
		}
	}
	
	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		if(SpawnerHeadConfig.immuneToSkeletonArrows.get()) {
			if(source.isProjectile() && source instanceof IndirectEntityDamageSource) {
				Entity owner = ((IndirectEntityDamageSource) source).getEntity();
				if(owner != null && owner instanceof AbstractSkeleton) {
					return true;
				}
			}
		}
		if(SpawnerHeadConfig.immuneToCreeperExplosions.get()) {
			if(source.isExplosion() && source instanceof EntityDamageSource) {
				Entity owner = ((EntityDamageSource) source).getEntity();
				if(owner != null && owner instanceof Creeper) {
					return true;
				}
			}
		}
		
		return super.isInvulnerableTo(source);
	}
	
	public void setSpawnerHeadType(int i) {
		this.entityData.set(BODY_TYPE, i);
	}

	public int getSpawnerHeadType() {
		return this.entityData.get(BODY_TYPE);
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
		super.populateDefaultEquipmentSlots(difficulty);
		if (this.random.nextFloat() < (this.level.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
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
		this.populateDefaultEquipmentSlots(difficulty);
		this.populateDefaultEquipmentEnchantments(difficulty);
		this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.SPAWNER));
		this.getItemBySlot(EquipmentSlot.HEAD).enchant(Enchantments.ALL_DAMAGE_PROTECTION, this.getRandom().nextInt(3) + 1);
		this.setDropChance(EquipmentSlot.HEAD, 0.0F);
		
		EntityType<?> type = EntityType.ZOMBIE;
		if(!SpawnerHeadSpawns.SPAWN_POTENTIALS.isEmpty()) {
			type = SpawnerHeadSpawns.SPAWN_POTENTIALS.getRandom(this.getRandom()).get().getData();
		}
		this.entityData.set(SPAWNER_ENTITY_ID, type.getRegistryName().toString());
		this.spawner.setEntityId(type);
		
		
		if(reason == MobSpawnType.SPAWN_EGG) {
			this.setSpawnerHeadType(random.nextInt(2));
		}
		
		return data;
	}
	
	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		if(SpawnerHeadConfig.allowSpawnEggUse.get()) {
			ItemStack stack = player.getItemInHand(hand);
			Item item = stack.getItem();
			if(item instanceof SpawnEggItem && item != ItemInit.spawnerhead_spawn_egg.get()) {
				//TODO: make configurable blacklist
				EntityType<?> entity = ((SpawnEggItem)item).getType(null);
				if(!this.level.isClientSide) {
					this.entityData.set(SPAWNER_ENTITY_ID, entity.getRegistryName().toString());
					this.spawner.setEntityId(entity);
				}
				
				if(!player.isCreative())
					stack.shrink(1);
				
				displayEntity = null;
				

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
	protected int getExperienceReward(Player player) {
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
//			Optional<EntityType<?>> entity = EntityType.byString(this.entityData.get(SPAWNER_ENTITY_ID));
//			
//			if(entity.isPresent()) {
//				return entity.get().getDefaultLootTable();
//			}
//		} else {
//			return EntityType.ZOMBIE.getDefaultLootTable();
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
}
