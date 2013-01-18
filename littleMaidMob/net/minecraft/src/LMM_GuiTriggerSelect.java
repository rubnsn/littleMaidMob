﻿package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class LMM_GuiTriggerSelect extends GuiContainer {
	
	public static Map<String, List<Integer>> selector = new HashMap<String, List<Integer>>();
	
/*	
    public static List<Integer> ListSword = new ArrayList<Integer>();
    public static List<Integer> ListAxe = new ArrayList<Integer>();
    public static List<Integer> ListBow = new ArrayList<Integer>();
    public static String SwordIndex="";
    public static String AxeIndex="";
    public static String BowIndex="";
*/
	protected float scrolleWeaponset;
	protected float scrolleContainer;
    private static InventoryBasic inventory1 = new InventoryBasic("tmpsel", 40);
    private static InventoryBasic inventory2 = new InventoryBasic("tmpwep", 32);
    private int lastX;
    private int lastY;
    private boolean ismousePress;
    private int isScrolled;
    public LMM_GuiIFF owner;
    private GuiButton[] guiButton = new GuiButton[3]; 
    private LMM_ContainerTriggerSelect inventoryTrigger;
    private int selectPage;


	public LMM_GuiTriggerSelect(EntityPlayer entityplayer, LMM_GuiIFF guiowner) {
		super(new LMM_ContainerTriggerSelect(entityplayer));
        ySize = 216;
        owner = guiowner;
        inventoryTrigger = (LMM_ContainerTriggerSelect) inventorySlots;
	}

	@Override
	public void initGui() {
		super.initGui();
		
		guiButton[0] = new GuiButton(100, guiLeft + 7, guiTop +  193, 20, 20, "<");
		guiButton[1] = new GuiButton(101, guiLeft + 35, guiTop +  193, 106, 20, selector.keySet().toArray(new String[0])[0]);
		guiButton[2] = new GuiButton(102, guiLeft + 149, guiTop +  193, 20, 20, ">");
		controlList.add(guiButton[0]);
		controlList.add(guiButton[1]);
		controlList.add(guiButton[2]);
		guiButton[1].enabled = false;
		selectPage = 0;
	}
	
	
	@Override
    protected void keyTyped(char c, int i) {
        if(i == 1) {
        	mc.displayGuiScreen(owner);
        }
    }
	
	@Override
	public void onGuiClosed() {
		// 設定値のデコード
		setItemList();
		
		super.onGuiClosed();
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
	
	@Override
	protected void actionPerformed(GuiButton guibutton) {
		setItemList();
		if (guibutton.id == 100) {
			if (--selectPage < 0) {
				selectPage = selector.size() - 1;
			}
		}
		if (guibutton.id == 101) {
			// Sword Select
		}
		if (guibutton.id == 102) {
			if (++selectPage >= selector.size()) {
				selectPage = 0;
			}
		}
		String ls = selector.keySet().toArray(new String[0])[selectPage];
		guiButton[1].displayString = ls;
		inventoryTrigger.setWeaponSelect(ls);
		inventoryTrigger.setWeaponlist(0.0F);
	}
	
	@Override
    protected void handleMouseClick(Slot slot, int i, int j, int flag) {
    	
        if(slot != null) {
            if(slot.inventory == inventory1 && flag == 0) {
                InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
                ItemStack itemstack1 = inventoryplayer.getItemStack();
                ItemStack itemstack4 = slot.getStack();
                if(itemstack1 != null && itemstack4 != null && itemstack1.itemID == itemstack4.itemID) {
                	// 選択アイテムが空ではない時
                    if(j != 0) {
                        inventoryplayer.setItemStack(null);
                    }
                } else
                if(itemstack1 != null) {
                    inventoryplayer.setItemStack(null);
                } else
                if(itemstack4 == null) {
                    inventoryplayer.setItemStack(null);
                } else {
                    inventoryplayer.setItemStack(ItemStack.copyItemStack(itemstack4));
                }
            } else {
                inventorySlots.slotClick(slot.slotNumber, j, flag, mc.thePlayer);
                ItemStack itemstack = inventorySlots.getSlot(slot.slotNumber).getStack();
                mc.playerController.sendSlotPacket(itemstack, (slot.slotNumber - inventorySlots.inventorySlots.size()) + 9 + 36);
            }
        } else {
        	//Slot以外のところは捨てる
            InventoryPlayer inventoryplayer1 = mc.thePlayer.inventory;
            inventoryplayer1.setItemStack(null);
        }
    }

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRenderer.drawString("Item selection", 8, 6, 0x404040);
        fontRenderer.drawString("Trigger Items", 8, 110, 0x404040);
    }

	@Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        if(i != 0) {
        	if (lastY < height / 2) {
                int j = (inventoryTrigger.itemList.size() / 8 - 5) + 1;
                if(i > 0)
                {
                    i = 1;
                }
                if(i < 0)
                {
                    i = -1;
                }
                scrolleContainer -= (double)i / (double)j;
                if(scrolleContainer < 0.0F)
                {
                	scrolleContainer = 0.0F;
                }
                if(scrolleContainer > 1.0F)
                {
                	scrolleContainer = 1.0F;
                }
                inventoryTrigger.scrollTo(scrolleContainer);
        	} else {
                int j = (inventoryTrigger.weaponSelect.size() / 8 - 4) + 1;
                if(i > 0)
                {
                    i = 1;
                }
                if(i < 0)
                {
                    i = -1;
                }
                if (j > 0) {
                    scrolleWeaponset -= (double)i / (double)j;
                } else {
                    scrolleWeaponset = 0.0F;
                }
                if(scrolleWeaponset < 0.0F)
                {
                	scrolleWeaponset = 0.0F;
                }
                if(scrolleWeaponset > 1.0F)
                {
                	scrolleWeaponset = 1.0F;
                }
                inventoryTrigger.setWeaponlist(scrolleWeaponset);
        	}
        }
    }

	@Override
    public void drawScreen(int i, int j, float f)
    {
    	lastX = i;
    	lastY = j;
        boolean flag = Mouse.isButtonDown(0);
        int k = guiLeft;
        int l = guiTop;
        int i1 = k + 155;
        int j1 = l + 17;
        int k1 = i1 + 14;
        int l1 = j1 + 90;
        if(!flag)
        {
            isScrolled = 0;
        }
        if(!ismousePress && flag && i >= i1 && j >= j1 && i < k1 && j < l1)
        {
            isScrolled = 1;
        }
        if(isScrolled == 1)
        {
            scrolleContainer = (float)(j - (j1 + 8)) / ((float)(l1 - j1) - 16F);
            if(scrolleContainer < 0.0F)
            {
                scrolleContainer = 0.0F;
            }
            if(scrolleContainer > 1.0F)
            {
                scrolleContainer = 1.0F;
            }
            inventoryTrigger.scrollTo(scrolleContainer);
        }
        j1 = l + 120;
        l1 = j1 + 72;
        if(!ismousePress && flag && i >= i1 && j >= j1 && i < k1 && j < l1)
        {
            isScrolled = 2;
        }
        if(isScrolled == 2)
        {
        	scrolleWeaponset= (float)(j - (j1 + 8)) / ((float)(l1 - j1) - 16F);
            if(scrolleWeaponset < 0.0F)
            {
            	scrolleWeaponset = 0.0F;
            }
            if(scrolleWeaponset > 1.0F)
            {
            	scrolleWeaponset = 1.0F;
            }
            inventoryTrigger.setWeaponlist(scrolleWeaponset);
        }
        ismousePress = flag;
        super.drawScreen(i, j, f);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896 /*GL_LIGHTING*/);
    }

	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int k = mc.renderEngine.getTexture("/gui/littlemaidtrigger.png");
        mc.renderEngine.bindTexture(k);
        int l = guiLeft;
        int i1 = guiTop;
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        
        int j1 = l + 155;
        int k1 = i1 + 17;
        int l1 = k1 + 88 + 2;
//        scrolleWeaponset = 1.0F;
//        scrolleContainer = 0.5F;
        drawTexturedModalRect(l + 154, i1 + 17 + (int)((float)(l1 - k1 - 17) * scrolleContainer), 176, 0, 16, 16);
        drawTexturedModalRect(l + 154, i1 + 120 + (int)((float)(l1 - k1 - 35) * scrolleWeaponset), 176, 0, 16, 16);
	}

	
	private void setItemList() {
		List list1 = inventoryTrigger.getItemList();
		list1.clear();
		for (int i = 0; i < inventoryTrigger.weaponSelect.size(); i++) {
			ItemStack is = inventoryTrigger.weaponSelect.get(i);
			if (is != null && !list1.contains(is.getItem().itemID)) {
				list1.add(is.getItem().itemID);
			}
		}
	}
	
	
    public static InventoryBasic getInventory1() {
        return inventory1;
    }

    public static InventoryBasic getInventory2() {
        return inventory2;
    }

    public static void appendTriggerItem(String triggerstr, String indexstr) {
    	// トリガーアイテムの追加
    	List<Integer> llist = new ArrayList<Integer>();
    	appendWeaponsIndex(indexstr, llist);
    	selector.put(triggerstr, llist);
    }
    
	private static void appendWeaponsIndex(String indexstr, List<Integer> indexlist) {
		if (indexstr.isEmpty()) return;
        String[] s = indexstr.split(",");
        for (String t : s) {
           	indexlist.add(Integer.valueOf(t));
        }
	}
	
	/**
	 * アイテムが指定されたトリガーに登録されているかを判定
	 */
	public static boolean checkWeapon(String pName, ItemStack pItemStack) {
		if (!selector.containsKey(pName)) {
			return false;
		}
		
		return selector.get(pName).contains(pItemStack.itemID);
	}
	
	
}
