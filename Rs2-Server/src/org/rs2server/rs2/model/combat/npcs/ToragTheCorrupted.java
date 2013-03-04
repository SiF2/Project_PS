package org.rs2server.rs2.model.combat.npcs;

import java.util.Random;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.util.Misc;

public class ToragTheCorrupted extends AbstractCombatAction {

	public enum CombatStyle {

		MELEE
	}

	/**
	 * The singleton instance.
	 */
	private static final ToragTheCorrupted INSTANCE = new ToragTheCorrupted();

	/**
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	private final Random random = new Random();

	/**
	 * Default private constructor.
	 */
	public ToragTheCorrupted() {
	}

	@Override
	public int distance(Mob attacker, Mob victim) {
		return 1;
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}

		NPC npc = (NPC) attacker;

		CombatStyle style = CombatStyle.MELEE;

		int maxHit;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;

		switch (style) {
		default:
		case MELEE:
			Animation anim = attacker.getAttackAnimation();
			attacker.playAnimation(anim);
			hitDelay = 1;
			blockAnimation = true;
			maxHit = npc.getCombatDefinition().getMaxHit();
			int torag = random.nextInt(8);
			if (torag == 3) {
				if (victim.getWalkingQueue().getEnergy() <= 4) {
					victim.getWalkingQueue().setEnergy(0);
				} else {
					victim.getWalkingQueue().setEnergy(
							victim.getWalkingQueue().getEnergy() - 4);
				}
				victim.playGraphics(Graphic.create(399, 0, 0));
				if (victim.getActionSender() != null) {
					victim.getActionSender().updateRunningConfig();
				}
			}
			if (victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)) {
				maxHit = (int) (maxHit * 0.0);
			}
			randomHit = Misc.random(maxHit);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		}
		attacker.getCombatState().setAttackDelay(5);
		attacker.getCombatState().setSpellDelay(5);

		World.getWorld().submit(new Tickable(hitDelay) {

			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit), attacker);
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				this.stop();
			}
		});

		vengeance(attacker, victim, hit, 1);

		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}
}
