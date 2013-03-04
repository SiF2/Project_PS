package org.rs2server.rs2.model.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.rs2server.rs2.action.impl.ObjectHarvestingAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.Sound;
import org.rs2server.util.Misc;

public class Woodcutting extends ObjectHarvestingAction {

	/**
	 * Represents types of axe hatchets.
	 * 
	 * @author Michael (Scu11)
	 * 
	 */
	public static enum Hatchet {

		/**
		 * Dragon axe.
		 */
		DRAGON(6739, 61, Animation.create(2846)),
		/**
		 * Rune axe.
		 */
		RUNE(1359, 41, Animation.create(867)),
		/**
		 * Adamant axe.
		 */
		ADAMANT(1357, 31, Animation.create(869)),
		/**
		 * Mithril axe.
		 */
		MITHRIL(1355, 21, Animation.create(871)),
		/**
		 * Black axe.
		 */
		BLACK(1361, 6, Animation.create(873)),
		/**
		 * Steel axe.
		 */
		STEEL(1353, 6, Animation.create(875)),
		/**
		 * Iron axe.
		 */
		IRON(1349, 1, Animation.create(877)),
		/**
		 * Bronze axe.
		 */
		BRONZE(1351, 1, Animation.create(879));
		/**
		 * The item id of this hatchet.
		 */
		private int id;
		/**
		 * The level required to use this hatchet.
		 */
		private int level;
		/**
		 * The animation performed when using this hatchet.
		 */
		private Animation animation;
		/**
		 * A list of hatchets.
		 */
		private static List<Hatchet> hatchets = new ArrayList<Hatchet>();

		/**
		 * Populates the hatchet map.
		 */
		static {
			for (Hatchet hatchet : Hatchet.values()) {
				hatchets.add(hatchet);
			}
		}

		/**
		 * Gets the list of hatchets.
		 * 
		 * @return The list of hatchets.
		 */
		public static List<Hatchet> getHatchets() {
			return hatchets;
		}

		private Hatchet(int id, int level, Animation animation) {
			this.id = id;
			this.level = level;
			this.animation = animation;
		}

		/**
		 * @return the animation
		 */
		public Animation getAnimation() {
			return animation;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * @return the level
		 */
		public int getRequiredLevel() {
			return level;
		}
	}

	/**
	 * Represents types of tree.
	 * 
	 * @author Michael
	 * 
	 */
	public static enum Tree {

		/**
		 * Normal tree.
		 */
		NORMAL(1511, 1, 5, 50, 1, new int[] { 1276, 1277, 1278, 1279, 1280,
				1282, 1283, 1284, 1285, 1286, 1289, 1290, 1291, 1315, 1316,
				1318, 1319, 1330, 1331, 1332, 1365, 1383, 1384, 3033, 3034,
				3035, 3036, 3881, 3882, 3883, 5902, 5903, 5904 }),
		/**
		 * Willow tree.
		 */
		WILLOW(1519, 30, 20, 22, 8, new int[] { 1308, 5551, 5552, 5553 }),
		/**
		 * Oak tree.
		 */
		OAK(1521, 15, 10, 22, 4, new int[] { 1281, 3037 }),
		/**
		 * Magic tree.
		 */
		MAGIC(1513, 75, 60, 310, 13, new int[] { 1306 }),
		/**
		 * Maple tree.
		 */
		MAPLE(1517, 45, 25, 100, 10, new int[] { 1307, 4677 }),
		/**
		 * Mahogany tree.
		 */
		MAHOGANY(6332, 25, 30, 22, 8, new int[] { 9034 }),
		/**
		 * Teak tree.
		 */
		TEAK(6333, 35, 22, 22, 8, new int[] { 9036 }),
		/**
		 * Achey tree.
		 */
		ACHEY(2862, 1, 5, 22, 8, new int[] { 2023 }),
		/**
		 * Yew tree.
		 */
		YEW(1515, 60, 40, 160, 8, new int[] { 1309 }),
		/**
		 * Dramen tree
		 */
		DRAMEN(771, 36, 0, 22, 8, new int[] { 1292 });
		/**
		 * The object ids of this tree.
		 */
		private int[] objects;
		/**
		 * The level required to cut this tree down.
		 */
		private int level;
		/**
		 * The log rewarded for each cut of the tree.
		 */
		private int log;
		/**
		 * The time it takes for this tree to respawn.
		 */
		private int respawnTimer;
		/**
		 * The amount of logs this tree contains.
		 */
		private int logCount;
		/**
		 * The experience granted for cutting a log.
		 */
		private double experience;
		/**
		 * A map of object ids to trees.
		 */
		private static Map<Integer, Tree> trees = new HashMap<Integer, Tree>();

		static {
			for (Tree tree : Tree.values()) {
				for (int object : tree.objects) {
					trees.put(object, tree);
				}
			}
		}

		/**
		 * Gets a tree by an object id.
		 * 
		 * @param object
		 *            The object id.
		 * @return The tree, or <code>null</code> if the object is not a tree.
		 */
		public static Tree forId(int object) {
			return trees.get(object);
		}

		/**
		 * Creates the tree.
		 * 
		 * @param log
		 *            The log id.
		 * @param level
		 *            The required level.
		 * @param experience
		 *            The experience per log.
		 * @param objects
		 *            The object ids.
		 */
		private Tree(int log, int level, double experience, int respawnTimer,
				int logCount, int[] objects) {
			this.objects = objects;
			this.level = level;
			this.experience = experience;
			this.respawnTimer = respawnTimer;
			this.logCount = logCount;
			this.log = log;
		}

		/**
		 * Gets the experience.
		 * 
		 * @return The experience.
		 */
		public double getExperience() {
			return experience;
		}

		/**
		 * @return the logCount
		 */
		public int getLogCount() {
			return logCount;
		}

		/**
		 * Gets the log id.
		 * 
		 * @return The log id.
		 */
		public int getLogId() {
			return log;
		}

		/**
		 * Gets the object ids.
		 * 
		 * @return The object ids.
		 */
		public int[] getObjectIds() {
			return objects;
		}

		/**
		 * Gets the required level.
		 * 
		 * @return The required level.
		 */
		public int getRequiredLevel() {
			return level;
		}

		/**
		 * @return the respawnTimer
		 */
		public int getRespawnTimer() {
			return respawnTimer;
		}
	}

	public static int[] getTreeIds() {
		return new int[] { 1276, 1277, 1278, 1279, 1280, 1282, 1283, 1284,
				1285, 1286, 1289, 1290, 1291, 1315, 1316, 1318, 1319, 1330,
				1331, 1332, 1365, 1383, 1384, 3033, 3034, 3035, 3036, 3881,
				3882, 3883, 5902, 5903, 5904, 1292, 1309, 2023, 9036, 9034,
				1307, 4677, 1306, 1281, 3037, 1308, 5551, 5552, 5553 };
	}

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	/**
	 * The tree we are cutting down.
	 */
	private GameObject object;

	/**
	 * The hatchet we are using.
	 */
	private Hatchet hatchet;

	/**
	 * The tree we are cutting down.
	 */
	private Tree tree;

	public Woodcutting(Mob mob, GameObject object) {
		super(mob);
		this.object = object;
		this.tree = Tree.forId(object.getId());
	}

	@Override
	public boolean canHarvest() {
		for (Hatchet hatchet : Hatchet.values()) {
			if ((getMob().getInventory().contains(hatchet.getId()) || getMob()
					.getEquipment().contains(hatchet.getId()))
					&& getMob().getSkills().getLevelForExperience(getSkill()) >= hatchet
							.getRequiredLevel()) {
				this.hatchet = hatchet;
				break;
			}
		}
		if (hatchet == null) {
			getMob().getActionSender().sendMessage(
					"You do not have an axe that you can use.");
			return false;
		}
		return true;
	}

	@Override
	public Animation getAnimation() {
		return hatchet.getAnimation();
	}

	@Override
	public int getCycleCount() {
		int skill = (int) (getMob().getSkills().getLevel(getSkill()) * 1.3);
		int level = tree.getRequiredLevel();
		int modifier = hatchet.getRequiredLevel();
		int randomAmt = random.nextInt(3);
		double cycleCount = 1;
		cycleCount = Math.ceil((level * 50 - skill * 10) / modifier * 0.25
				- randomAmt * 4);
		if (cycleCount < 1) {
			cycleCount = 1;
		}
		return (int) cycleCount;
	}

	@Override
	public double getExperience() {
		return tree.getExperience();
	}

	@Override
	public GameObject getGameObject() {
		return object;
	}

	@Override
	public int getGameObjectMaxHealth() {
		if (tree.equals(Tree.NORMAL)) {
			return 1;
		}
		int count = Misc.random(24) - Misc.random(tree.getLogCount()) * 2;
		if (count <= 0) {
			count = 1;
		}
		return count;
	}

	@Override
	public String getHarvestStartedMessage() {
		return "You swing your axe at the tree.";
	}

	@Override
	public String getInventoryFullMessage() {
		return "Your inventory is too full to hold any more "
				+ getReward().getDefinition().getName().toLowerCase() + ".";
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of "
				+ tree.getRequiredLevel() + " to cut this tree.";
	}

	@Override
	public int getObjectRespawnTimer() {
		return tree.getRespawnTimer();
	}

	@Override
	public GameObject getReplacementObject() {
		this.getMob().playSound(Sound.create(2734, (byte) 1, 0));
		return new GameObject(getGameObject().getLocation(), 1342, 10, 0, false);
	}

	@Override
	public int getRequiredLevel() {
		return tree.getRequiredLevel();
	}

	@Override
	public Item getReward() {
		return new Item(tree.getLogId(), 1);
	}

	@Override
	public int getSkill() {
		return Skills.WOODCUTTING;
	}

	@Override
	public String getSuccessfulHarvestMessage() {
		return "You get some "
				+ getReward().getDefinition().getName().toLowerCase() + ".";
	}

	@Override
	public String getUnsuccessfulAttemptMessage() {
		return "If this message appears, please make a bug report to administration.";
	}
}
