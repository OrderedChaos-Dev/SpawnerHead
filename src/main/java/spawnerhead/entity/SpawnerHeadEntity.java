package spawnerhead.entity;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import spawnerhead.SpawnerHeadConfig;

public class SpawnerHeadEntity extends MonsterEntity {
	
	private static final DataParameter<Boolean> FLAG = EntityDataManager.defineId(SpawnerHeadEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<String> SPAWNER_ENTITY_ID = EntityDataManager.defineId(SpawnerHeadEntity.class, DataSerializers.STRING);
	private static final DataParameter<Integer> BODY_TYPE = EntityDataManager.defineId(SpawnerHeadEntity.class, DataSerializers.INT);
	
	private AbstractSpawner spawner = new AbstractSpawner() {
		@Override
		public void broadcastEvent(int i) {
			SpawnerHeadEntity.this.level.broadcastEntityEvent(SpawnerHeadEntity.this, (byte) i);
		}

		@Override
		public World getLevel() {
			return SpawnerHeadEntity.this.level;
		}

		@Override
		public BlockPos getPos() {
			return new BlockPos(SpawnerHeadEntity.this.getEyePosition(1.0F));
		}

		@Override
		@javax.annotation.Nullable
		public Entity getSpawnerEntity() {
			return SpawnerHeadEntity.this;
		}
	};
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(FLAG, false);
		this.entityData.define(SPAWNER_ENTITY_ID, "");
		this.entityData.define(BODY_TYPE, 0);
	}

	public SpawnerHeadEntity(EntityType<? extends MonsterEntity> entity, World world) {
		super(entity, world);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(2, new RestrictSunGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MonsterEntity.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.23D).add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.ARMOR, 2.0D);
	}
	
	@Override
	public CreatureAttribute getMobType() {
		return CreatureAttribute.UNDEAD;
	}
	
	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		this.entityData.set(FLAG, nbt.getBoolean("flag"));
		this.entityData.set(SPAWNER_ENTITY_ID, nbt.getString("spawner_entity_id"));
		this.entityData.set(BODY_TYPE, nbt.getInt("type"));
		this.spawner.load(nbt);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putBoolean("flag", this.entityData.get(FLAG));
		nbt.putString("spawner_entity_id", this.entityData.get(SPAWNER_ENTITY_ID));
		nbt.putInt("type", this.entityData.get(BODY_TYPE));
		this.spawner.save(nbt);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleEntityEvent(byte b) {
		this.spawner.onEventTriggered(b);
	}
	
	@Override
	public void aiStep() {
		super.aiStep();
		
		//logic to set the spawner entity on mob spawn
		//this is because the spawner display entity cannot be changed once it is set during the game instance
		//and for some reason the display entity is set before anything else, so it always default to pig
		if(!this.level.isClientSide && !this.isSpawnerMobSet()) {
			EntityType<?> type = SpawnerHeadSpawns.SPAWN_POTENTIALS.getOne(this.getRandom());
			this.spawner.setEntityId(type);
			this.setSpawnerMobFlag(true);
			this.entityData.set(SPAWNER_ENTITY_ID, type.getRegistryName().toString());
		}
		if(this.level.isClientSide) {
			String clientEntity = this.getSpawner().getOrCreateDisplayEntity().getType().getRegistryName().toString();
			String dataEntity = this.entityData.get(SPAWNER_ENTITY_ID);
			if(!clientEntity.equals(dataEntity)) {
				Optional<EntityType<?>> entity = EntityType.byString(dataEntity);
				if(entity.isPresent()) {
					this.spawner.setEntityId(entity.get());
					this.spawner.displayEntity = entity.get().create(this.level);
				}
			}
		}

		if(SpawnerHeadConfig.burnsInSunlight.get()) {
			boolean flag = this.isSunBurnTick();
			if (flag) {
				this.setSecondsOnFire(8);
			}
		}
	}
	
	@Override
	public boolean canBeLeashed(PlayerEntity entity) {
		return SpawnerHeadConfig.canBeLeashed.get();
	}

	@Override
	public void tick() {
		super.tick();
		this.spawner.tick();
	}
	
	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		if(SpawnerHeadConfig.immuneToSkeletonArrows.get()) {
			if(source.isProjectile() && source instanceof IndirectEntityDamageSource) {
				Entity owner = ((IndirectEntityDamageSource) source).getEntity();
				if(owner != null && owner instanceof AbstractSkeletonEntity) {
					return true;
				}
			}
		}
		
		return super.isInvulnerableTo(source);
	}
	
	private boolean isSpawnerMobSet() {
		return this.entityData.get(FLAG);
	}
	
	private void setSpawnerMobFlag(boolean b) {
		this.entityData.set(FLAG, b);
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
				this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
			} else {
				this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
			}
		}
	}
	
	@Nullable
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT nbt) {
		data = super.finalizeSpawn(world, difficulty, reason, data, nbt);
		this.populateDefaultEquipmentSlots(difficulty);
		this.populateDefaultEquipmentEnchantments(difficulty);
		this.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(Blocks.SPAWNER));
		this.getItemBySlot(EquipmentSlotType.HEAD).enchant(Enchantments.ALL_DAMAGE_PROTECTION, this.getRandom().nextInt(3) + 1);
		this.setDropChance(EquipmentSlotType.HEAD, 0.0F);
		return data;
	}
	
	@Override
	protected float getStandingEyeHeight(Pose pose, EntitySize size) {
		return 1.74F;
	}
	
	@Override
	protected int getExperienceReward(PlayerEntity player) {
		return 35 + this.getRandom().nextInt(15) + this.getRandom().nextInt(15);
	}
	
	protected SoundEvent getStepSound() {
		return SoundEvents.ZOMBIE_STEP;
	}
	
	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}
	
	@Override
	protected ResourceLocation getDefaultLootTable() {
		if(SpawnerHeadConfig.dropSpecialLoot.get()) {
			Optional<EntityType<?>> entity = EntityType.byString(this.entityData.get(SPAWNER_ENTITY_ID));
			
			if(entity.isPresent()) {
				return entity.get().getDefaultLootTable();
			}
		} else {
			return EntityType.ZOMBIE.getDefaultLootTable();
		}

		return super.getDefaultLootTable();
	}
	
	public AbstractSpawner getSpawner() {
		return this.spawner;
	}
}
