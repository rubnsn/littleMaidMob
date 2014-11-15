package mmm.littleMaidMob.mode;

import mmm.littleMaidMob.Statics;
import mmm.littleMaidMob.littleMaidMob;
import mmm.littleMaidMob.mode.ai.LMM_EntityAINearestAttackableTarget;
import mmm.littleMaidMob.sound.EnumSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class ModeRipper extends EntityModeBase {

    public static final int mmode_Ripper = 0x0081;
    public static final int mmode_TNTD = 0x00c1;
    public static final int mmode_Detonator = 0x00c2;

    public int timeSinceIgnited;
    public int lastTimeSinceIgnited;

    public ModeRipper(ModeController pEntity) {
        super(pEntity);
        timeSinceIgnited = -1;
    }

    @Override
    public int priority() {
        return 3100;
    }

    @Override
    public void init() {
        // 登録モードの名称追加
        /*
        ModLoader.addLocalization("littleMaidMob.mode.Ripper", "Ripper");
        ModLoader.addLocalization("littleMaidMob.mode.F-Ripper", "F-Ripper");
        ModLoader.addLocalization("littleMaidMob.mode.D-Ripper", "D-Ripper");
        ModLoader.addLocalization("littleMaidMob.mode.T-Ripper", "T-Ripper");
        ModLoader.addLocalization("littleMaidMob.mode.Ripper", "ja_JP", "毛狩り隊");
        ModLoader.addLocalization("littleMaidMob.mode.F-Ripper", "ja_JP", "毛狩り隊");
        ModLoader.addLocalization("littleMaidMob.mode.D-Ripper", "ja_JP", "毛狩り隊");
        ModLoader.addLocalization("littleMaidMob.mode.T-Ripper", "ja_JP", "毛狩り隊");
        ModLoader.addLocalization("littleMaidMob.mode.TNT-D", "TNT-D");
        ModLoader.addLocalization("littleMaidMob.mode.F-TNT-D", "TNT-D");
        ModLoader.addLocalization("littleMaidMob.mode.D-TNT-D", "TNT-D");
        ModLoader.addLocalization("littleMaidMob.mode.T-TNT-D", "TNT-D");
        //      ModLoader.addLocalization("littleMaidMob.mode.TNT-D", "ja_JP", "TNT-D");
        ModLoader.addLocalization("littleMaidMob.mode.Detonator", "Detonator");
        ModLoader.addLocalization("littleMaidMob.mode.F-Detonator", "F-Detonator");
        ModLoader.addLocalization("littleMaidMob.mode.D-Detonator", "D-Detonator");
        ModLoader.addLocalization("littleMaidMob.mode.T-Detonator", "T-Detonator");*/
    }

    @Override
    public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {

        // Ripper:0x0081
        EntityAITasks[] ltasks = new EntityAITasks[2];
        ltasks[0] = new EntityAITasks(controller.aiProfiler);
        ltasks[1] = new EntityAITasks(controller.aiProfiler);

        ltasks[0].addTask(1, controller.aiSwiming);
        ltasks[0].addTask(2, controller.aiSit);
        ltasks[0].addTask(3, controller.aiJumpTo);
        ltasks[0].addTask(4, controller.aiAttack);
        ltasks[0].addTask(5, controller.aiPanic);
        ltasks[0].addTask(6, controller.aiBeg);
        ltasks[0].addTask(7, controller.aiBegMove);
        ltasks[0].addTask(8, controller.aiAvoidPlayer);
        //      ltasks[0].addTask(7, pentitylittlemaid.aiCloseDoor);
        //      ltasks[0].addTask(8, pentitylittlemaid.aiOpenDoor);
        //      ltasks[0].addTask(9, pentitylittlemaid.aiCollectItem);
        ltasks[0].addTask(10, controller.aiFollow);
        //      ltasks[0].addTask(11, new EntityAILeapAtTarget(pentitylittlemaid, 0.3F));
        ltasks[0].addTask(11, controller.aiWander);
        ltasks[0].addTask(12, new EntityAIWatchClosest(getOwner(), EntityLivingBase.class, 10F));
        ltasks[0].addTask(12, new EntityAILookIdle(getOwner()));

        ltasks[1].addTask(1, new LMM_EntityAINearestAttackableTarget(getOwner(), EntityCreeper.class, 0, true));
        ltasks[1].addTask(2, new LMM_EntityAINearestAttackableTarget(getOwner(), EntityTNTPrimed.class, 0, true));
        ltasks[1].addTask(3, new LMM_EntityAINearestAttackableTarget(getOwner(), EntitySheep.class, 0, true));

        controller.addMaidMode(ltasks, "Ripper", mmode_Ripper);

        // TNT-D:0x00c1
        EntityAITasks[] ltasks2 = new EntityAITasks[2];
        ltasks2[0] = ltasks[0];
        ltasks2[1] = new EntityAITasks(controller.aiProfiler);
        ltasks2[1].addTask(1, new LMM_EntityAINearestAttackableTarget(getOwner(), EntityCreeper.class, 0, true));
        ltasks2[1].addTask(2, new LMM_EntityAINearestAttackableTarget(getOwner(), EntityTNTPrimed.class, 0, true));

        controller.addMaidMode(ltasks2, "TNT-D", mmode_TNTD);

        // Detonator:0x00c2
        EntityAITasks[] ltasks3 = new EntityAITasks[2];
        ltasks3[0] = pDefaultMove;
        ltasks3[1] = new EntityAITasks(controller.aiProfiler);
        ltasks2[1].addTask(1, new LMM_EntityAINearestAttackableTarget(getOwner(), EntityLivingBase.class, 0, true));

        controller.addMaidMode(ltasks2, "Detonator", mmode_Detonator);

    }

    @Override
    public void updateAITick(int pMode) {
        ItemStack litemstack = getOwner().inventory.getCurrentItem();
        if (litemstack != null && (getOwner().getAttackTarget() instanceof EntityCreeper || getOwner().getEntityToAttack() instanceof EntityTNTPrimed)) {
            if (pMode == mmode_Ripper) {
                controller.setMaidMode("TNT-D");
                getOwner().maidOverDriveTime.setEnable(true);
            } else if (controller.getMaidModeInt() == mmode_TNTD && litemstack.getItem() instanceof ItemShears) {
                getOwner().maidOverDriveTime.setEnable(true);
            }
        }
        if (!getOwner().maidOverDriveTime.isEnable() && pMode == mmode_TNTD) {
            controller.setMaidMode("Ripper");
            //          getNextEquipItem();
        }
    }

    @Override
    public void onUpdate(int pMode) {
        // 自爆モード
        if (pMode == mmode_Detonator && getOwner().isEntityAlive()) {
            if (timeSinceIgnited < 0) {
                if (lastTimeSinceIgnited != timeSinceIgnited) {
                    getOwner().getDataWatcher().updateObject(Statics.dataWatch_Free, Integer.valueOf(0));
                } else if (getOwner().getDataWatcher().getWatchableObjectInt(Statics.dataWatch_Free) == 1) {
                    lastTimeSinceIgnited = timeSinceIgnited = 0;
                }
            }
            lastTimeSinceIgnited = timeSinceIgnited;
            if (timeSinceIgnited > -1) {
                // 最期の瞬間はセツナイ
                if (getOwner().isMovementCeased() || timeSinceIgnited > 22) {
                    getOwner().getLookHelper().setLookPositionWithEntity(getOwner().getMaidMasterEntity(), 40F, 40F);
                }
                littleMaidMob.Debug(String.format("ID:%d(%s)-dom:%d(%d)", getOwner().getEntityId(), getOwner().worldObj.isRemote ? "C" : "W", getOwner().swingController.getDominantArm(), getOwner().inventory.currentItem));

                if (getOwner().inventory.isItemExplord(getOwner().inventory.currentItem) && timeSinceIgnited++ > 30) {
                    // TODO:自爆威力を対応させたいけど無理ぽ？
                    getOwner().inventory.decrStackSize(getOwner().inventory.currentItem, 1);
                    // インベントリをブチマケロ！
                    getOwner().inventory.dropAllItems(true);
                    timeSinceIgnited = -1;
                    getOwner().setDead();
                    // Mobによる破壊の是非
                    //                  boolean lflag = owner.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                    //                  owner.worldObj.createExplosion(owner, owner.posX, owner.posY, owner.posZ, 3F, lflag);
                }
            }
        }
    }

    @Override
    public boolean changeMode(EntityPlayer pentityplayer) {
        ItemStack litemstack = getOwner().inventory.getStackInSlot(0);
        if (litemstack != null) {
            if (litemstack.getItem() instanceof ItemShears) {
                controller.setMaidMode("Ripper");
                return true;
            }
            if (getOwner().inventory.isItemExplord(0)) {
                controller.setMaidMode("Detonator");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setMode(int pMode) {
        switch (pMode) {
        case mmode_Ripper:
            getOwner().setBloodsuck(false);
            return true;
        case mmode_TNTD:
            getOwner().setBloodsuck(false);
            return true;
        case mmode_Detonator:
            getOwner().setBloodsuck(true);
            //          owner.aiPanic.
            timeSinceIgnited = -1;

            return true;
        }

        return false;
    }

    @Override
    public int getNextEquipItem(int pMode) {
        int li;
        ItemStack litemstack;

        // モードに応じた識別判定、速度優先
        switch (pMode) {
        case mmode_Ripper:
        case mmode_TNTD:
            for (li = 0; li < getOwner().inventory.getInitInvSize(); li++) {
                litemstack = getOwner().inventory.getStackInSlot(li);
                if (litemstack == null)
                    continue;

                // はさみ
                if (litemstack.getItem() instanceof ItemShears) {
                    return li;
                }
            }
            break;
        case mmode_Detonator:
            for (li = 0; li < getOwner().inventory.getInitInvSize(); li++) {
                // 爆発物
                if (getOwner().inventory.isItemExplord(li)) {
                    return li;
                }
            }
            break;
        }

        return -1;
    }

    @Override
    public boolean attackEntityAsMob(int pMode, Entity pEntity) {
        if (pMode == mmode_Detonator) {
            // 通常殴り
            return false;
        }

        if (getOwner().swingController.getSwingStatusDominant().canAttack()) {
            ItemStack lis = getOwner().getCurrentEquippedItem();
            if (pEntity instanceof EntityCreeper) {
                // TODO:カットオフ
                // なんでPrivateにかえたし
                try {
                    lis.damageItem((Integer) ObfuscationReflectionHelper.getPrivateValue(EntityCreeper.class, (EntityCreeper) pEntity, 1), getOwner());
                    //                          (EntityCreeper)pEntity, 1), owner.maidAvatar);
                    ObfuscationReflectionHelper.setPrivateValue(EntityCreeper.class, (EntityCreeper) pEntity, 1, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //              ((EntityCreeper)pEntity).timeSinceIgnited = 0;
                getOwner().swingController.setSwing(20, EnumSound.attack_bloodsuck);
            } else if (pEntity instanceof EntityTNTPrimed) {
                pEntity.setDead();
                lis.damageItem(1, getOwner());
                //              lis.damageItem(1, owner.maidAvatar);
                getOwner().swingController.setSwing(20, EnumSound.attack_bloodsuck);
            } else {
                getOwner().avatar.interactWith(pEntity);
                getOwner().swingController.setSwing(20, EnumSound.attack);
            }
            if (lis.stackSize <= 0) {
                getOwner().inventory.setInventoryCurrentSlotContents(null);
                getOwner().getNextEquipItem();
            }
        }

        return true;
    }

    @Override
    public boolean isSearchEntity() {
        return true;
    }

    @Override
    public boolean checkEntity(Entity pEntity) {
        if (getOwner().inventory.currentItem < 0) {
            return false;
        }
        switch (controller.getMaidModeInt()) {
        case mmode_Detonator:
            return !getOwner().getIFF(pEntity);
        case mmode_Ripper:
            if (pEntity instanceof EntitySheep) {
                EntitySheep les = (EntitySheep) pEntity;
                if (!les.getSheared() && !les.isChild()) {
                    return true;
                }
            }
        case mmode_TNTD:
            if (pEntity instanceof EntityCreeper) {
                return true;
            }
            if (pEntity instanceof EntityTNTPrimed) {
                return true;
            }
            break;
        }

        return false;
    }

    protected float setLittleMaidFlashTime(float f) {
        // 爆発カウントダウン発光時間
        if (timeSinceIgnited > -1) {
            return ((float) this.lastTimeSinceIgnited + (float) (this.timeSinceIgnited - this.lastTimeSinceIgnited) * f) / 28.0F;
        } else {
            return 0F;
        }
    }

    @Override
    public int colorMultiplier(float pLight, float pPartialTicks) {
        float f2 = setLittleMaidFlashTime(pPartialTicks);

        if ((int) (f2 * 10F) % 2 == 0) {
            return 0;
        }
        int i = (int) (f2 * 0.2F * 255F);
        if (i < 0) {
            i = 0;
        }
        if (i > 255) {
            i = 255;
        }
        littleMaidMob.Debug(String.format("%2x", i));
        char c = '\377';
        char c1 = '\377';
        char c2 = '\377';
        return i << 24 | c << 16 | c1 << 8 | c2;
    }

    @Override
    public boolean damageEntity(int pMode, DamageSource par1DamageSource, float par2) {
        // 起爆
        if (pMode == mmode_Detonator && getOwner().inventory.isItemExplord(getOwner().getCurrentEquippedItem())) {
            if (timeSinceIgnited == -1) {
                getOwner().playSound("random.fuse", 1.0F, 0.5F);
                getOwner().getDataWatcher().updateObject(Statics.dataWatch_Free, Integer.valueOf(1));
            }
            //          if (owner.entityToAttack == null)
            getOwner().setMaidWait(true);
        }

        return false;
    }

    @Override
    public String getName() {
        return "ripper";
    }

}
