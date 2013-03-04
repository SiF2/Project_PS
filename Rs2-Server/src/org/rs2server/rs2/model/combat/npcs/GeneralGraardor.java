package org.rs2server.rs2.model.combat.npcs;

import java.util.Random;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Hit.HitType;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.util.Misc;

public class GeneralGraardor extends AbstractCombatAction {

	private enum CombatStyle {

		MELEE, MAGIC
	}

	/**
	 * The singleton instance.
	 */
	private static final GeneralGraardor INSTANCE = new GeneralGraardor();

	/**
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	private static final String[] BANDOS_SHOUTS = { "Death to our enemies!",
			"Brargh!", "Break their bones!",
			"For the glory of the Big High War God!", "Split their skulls!",
			"We feast on the bones of our enemies tonight!", "CHAAARGE!",
			"Crush them underfoot!", "All glory to Bandos!", "GRAAAAAAAAAR!", };

	private static void randomMessage(NPC boss, String[] shouts) {
		if (Misc.random(4) != 0) {
			return;
		}
		String message = shouts[Misc.random(shouts.length - 1)];
		boss.forceChat(message);
	}

	@Override
	public int distance(Mob attacker, Mob victim) {
		return 7;
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}

		NPC npc = (NPC) attacker;
		randomMessage(npc, BANDOS_SHOUTS);
		CombatStyle style = CombatStyle.MAGIC;

		int maxHit;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;

		if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			switch (random.nextInt(2)) {
			default:
			case 0:
				style = CombatStyle.MELEE;
				break;
			case 1:
				style = CombatStyle.MAGIC;
				break;
			}
		}

		switch (style) {
		case MELEE:
			Animation anim = attacker.getAttackAnimation();
			attacker.playAnimation(anim);
			hitDelay = 1;
			blockAnimation = true;
			maxHit = npc.getCombatDefinition().getMaxHit();
			if (victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)) {
				maxHit = (int) (maxHit * 0.0);
			}
			randomHit = Misc.random(maxHit);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		default:
		case MAGIC:
			attacker.playAnimation(Animation.create(7063));
			victim.playGraphics(Graphic.create(1200, 60));

			hitDelay = 2;
			blockAnimation = false;
			if (victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = 19;
			} else {
				maxHit = 36;
			}
			randomHit = Misc.random(maxHit);
			hit = randomHit;
			World.getWorld().submit(new Tickable(2) {

				@Override
				public void execute() {
					stop();
					for (final Player near : World.getWorld().getPlayers()) {
						if (near != null
								&& near != attacker
								&& near != victim
								&& near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
							if (Misc.getDistance(near.getLocation(),
									attacker.getLocation()) < 10) {
								near.playGraphics(Graphic.create(1200));
								int hitz = hit;
								if (near.getCombatState().getPrayer(
										Prayers.PROTECT_FROM_MAGIC)) {
									hitz = (int) (hitz * 0.4);
								}
								hitz = Misc.random(hitz);
								near.inflictDamage(
										new Hit(hitz > 0 ? HitType.NORMAL_HIT
												: HitType.ZERO_DAMAGE_HIT, hitz),
										near);
							}
						}
					}
				}
			});
			if (victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = (int) (maxHit * 0.0);
			}
			randomHit = Misc.random(maxHit);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
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