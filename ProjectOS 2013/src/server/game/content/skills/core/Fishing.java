package server.game.content.skills.core;

import server.game.content.skills.SkillHandler;
import server.game.players.Client;
import server.task.Task;
import server.util.Misc;
import server.*;

public class Fishing extends SkillHandler {

	public static final int FISHING_XP = FISHING_EXPERIENCE;

	private final static int[][] data = {
		{1, 1, 303, -1, 317, 10, 621, 321, 15, 30},		//SHRIMP + ANCHOVIES
		{2, 5, 309, 313, 327, 20, 622, 345, 10, 30},		//SARDINE + HERRING
		{3, 62, 303, -1, 7944, 100, 621, -1, -1, -1},		//MONKFISH
		{4, 20, 309, -1, 335, 50, 622, 331, 30, 70},		//TROUT
		{5, 23, 303, -1, 341, 45, 619, 363, 46, 100},		//BASS + COD
		{6, 25, 309, 313, 349, 60, 622, -1, -1, -1},		//PIKE
		{7, 35, 311, -1, 359, 80, 618, 371, 50, 100},		//TUNA + SWORDIE
		{8, 40, 301, -1, 377, 90, 619, -1, -1, -1},		//LOBSTER
		{9, 16, 303, -1, 353, 20, 620, -1, -1, -1},		//MACKEREL
		{10, 76, 311, -1, 383, 110, 618, -1, -1, -1},		//SHARK
		{11, 79, 303, -1, 395, 145, 619, -1, -1, -1},		//SEA TURTLE
		{12, 81, 303, -1, 389, 130, 621, -1, -1, -1},		//MANTA RAY
	};

	private static void attemptdata(final Client c, int npcId) {
		if(c.fishingProp[4] > 0) {
			c.playerIsFishing = false;
			c.fishingProp[4] = -1;
			return;
		}
		if (!noInventorySpace(c, "fishing")) {
			return;
		}
 		resetFishing(c);
		for (int[] aData : data) {
			if (npcId == aData[0]) {
				if (c.playerLevel[c.playerFishing] < aData[1]) {
					c.sendMessage("You don't meet the fishing requirement to use this spot.");
					c.sendMessage("You at least need a fishing level of " + aData[1] + ".");
					c.getDH().sendStatement("You need a fishing level of " + aData[1] + " to fish here.");
					return;
				}
				if (aData[3] > 0) {
					if (!c.getItems().playerHasItem(aData[3])) {
						c.sendMessage("You don't have any " + c.getItems().getItemName(aData[3]) + "!");
						c.sendMessage("You need " + c.getItems().getItemName(aData[3]) + " to fish here.");
						return;
					}
				}
				c.fishingProp[0] = aData[6]; //	ANIM
				c.fishingProp[1] = aData[4]; //	FISH
				c.fishingProp[2] = aData[5]; //	XP
				c.fishingProp[3] = aData[3]; //	BAIT
				c.fishingProp[4] = aData[2]; //	EQUIP
				c.fishingProp[5] = aData[7]; //	sFish
				c.fishingProp[6] = aData[8]; //	sLvl
				c.fishingProp[7] = aData[4]; //	FISH
				c.fishingProp[8] = aData[9]; //	sXP
				c.fishingProp[9] = Misc.random(1) == 0 ? 7 : 5;
				c.fishingProp[10] = aData[0]; //	INDEX
				if (!hasFishingEquipment(c, c.fishingProp[4])) {
					return;
				}
				c.sendMessage("You start fishing...");
				c.startAnimation(c.fishingProp[0]);
				c.stopPlayerSkill = true;

				if (c.playerIsFishing) {
					return;
				}

				c.playerIsFishing = true;
			    Server.getTaskScheduler().schedule(new Task(getTimer(0) + playerFishingLevel()) {
                    @Override
                    protected void execute() {
						if (c.fishingProp[5] > 0) {
							if (c.playerLevel[c.playerFishing] >= c.fishingProp[6]) {
								c.fishingProp[1] = c.fishingProp[Misc.random(1) == 0 ? 7 : 5];
							}
						}

						if (c.fishingProp[1] > 0) {
							c.sendMessage("You catch a " + c.getItems().getItemName(c.fishingProp[1]) + ".");
						}
						if (c.fishingProp[1] > 0) {
							c.getItems().addItem(c.fishingProp[1], 1);
							c.startAnimation(c.fishingProp[0]);
						}
						if (c.fishingProp[2] > 0) {
                         c.getPA().addSkillXP(c.fishingProp[2], c.playerFishing);
						}
						if (c.fishingProp[3] > 0) {
							c.getItems().deleteItem(c.fishingProp[3], c.getItems().getItemSlot(c.fishingProp[3]), 1);
							if (!c.getItems().playerHasItem(c.fishingProp[3])) {
								c.sendMessage("You don't have any " + c.getItems().getItemName(c.fishingProp[3]) + " left!");
								c.sendMessage("You need " + c.getItems().getItemName(c.fishingProp[3]) + " to fish here.");
								stop();
							}
						}
						if (!hasFishingEquipment(c, c.fishingProp[4])) {
							stop();
						}
						if (!noInventorySpace(c, "fishing")) {
							stop();
						}
						if (Misc.random(15) == 0) {
							stop();
						}
						if (!c.stopPlayerSkill) {
							stop();
						}
						if (!c.playerIsFishing) {
							stop();
						}
					}

					@Override
					public void stop() {
						resetFishing(c);
					}
				});
			}
		}
	}

	private static boolean hasFishingEquipment(Client c, int equipment) {
		if (!c.getItems().playerHasItem(equipment)) {
			if(equipment == 311) {
				if(!c.getItems().playerHasItem(311) && !c.getItems().playerHasItem(10129) && c.playerEquipment[3] != 10129) {
					c.sendMessage("You need a "+ c.getItems().getItemName(equipment) +" to fish here.");
					return false;
				}
			} else {
				c.sendMessage("You need a "+ c.getItems().getItemName(equipment) +" to fish here.");
				return false;
			}
		}
		return true;
	}

	public static void resetFishing(Client c) {
		c.startAnimation(65535);
		c.getPA().removeAllWindows();
		c.playerIsFishing = false;
		for(int i = 0; i < 11; i++) {
			c.fishingProp[i] = -1;
		}
	}

	private static int playerFishingLevel() {
		return (10 - (int)Math.floor(99 / 13));
	}

	private static int getTimer(int npcId) {
		switch (npcId) {
			case 1: return 2;
			case 2:	return 3;
			case 3:	return 4;
			case 4: return 4;
			case 5: return 4;
			case 6: return 5;
			case 7: return 5;
			case 8: return 5;
			case 9: return 5;
			case 10: return 5;
			case 11: return 9;
			case 12: return 9;
			default: return -1;
		}
	}

	public static void fishingNPC(Client c, int i, int l) {
		switch (i) {
			case 1:
				switch (l) {
					case 319:
					case 329:
					case 323:
					case 325:
					case 327:
					case 330:
					case 332:
					case 316: //NET + BAIT
						Fishing.attemptdata(c, 1);
						break;
					case 334:
					case 313: //NET + HARPOON
						Fishing.attemptdata(c, 3);
						break;
					case 322: //NET + HARPOON
						Fishing.attemptdata(c, 5);
						break;

					case 309: //LURE
					case 310:
					case 311:
					case 314:
					case 315:
					case 317:
					case 318:
					case 328:
					case 331:
						Fishing.attemptdata(c, 4);
						break;

					case 312:
					case 321:
					case 324: //CAGE + HARPOON
						Fishing.attemptdata(c, 8);
						break;
					case 326: //Manta Rays
						Fishing.attemptdata(c, 12);
					break;
				}
				break;
			case 2:
				switch (l) {
					case 327:
					case 330:
					case 332:
					case 316: //BAIT + NET
						Fishing.attemptdata(c, 2);
						break;
					case 319:
					case 323:
					case 325: //BAIT + NET
						Fishing.attemptdata(c, 9);
						break;
					case 310:
					case 311:
					case 314:
					case 315:
					case 317:
					case 318:
					case 328:
					case 329:
					case 331:
					case 309: //BAIT + LURE
						Fishing.attemptdata(c, 6);
						break;
					case 312:
					case 321:
					case 324://SWORDIES+TUNA-CAGE+HARPOON
						Fishing.attemptdata(c, 7);
						break;
					case 313:
					case 322:
					case 334:	//NET+HARPOON
						Fishing.attemptdata(c, 10);
						break;
					case 326: //SEA TURTLE
						Fishing.attemptdata(c, 11);
					break;
				}
				break;
		}
	}

	public static boolean fishingNPC(int npc) {
		for(int i = 308; i < 335; i++) {
			if(npc == i) {
				return true;
			}
		}
		return false;
	}
}