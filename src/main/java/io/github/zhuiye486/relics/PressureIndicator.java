package io.github.zhuiye486.relics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import basemod.abstracts.CustomRelic;
import io.github.zhuiye486.helpers.ModHelper;
import io.github.zhuiye486.events.ReplaceHeroCampfireOption;
import io.github.zhuiye486.powers.LuckyPower;

public class PressureIndicator extends CustomRelic{
    public static final String ID = ModHelper.makePath(PressureIndicator.class.getSimpleName());
    private static final String IMG_PATH = "SlayTheDungeonResources/img/relics/PressureIndicator.png";
    private static final RelicTier RELIC_TIER = RelicTier.STARTER;
    private static final LandingSound LANDING_SOUND = LandingSound.FLAT;

    public static final int TEST_AMOUNT = 100;
    public static final int DIE_AMOUNT = 200;
    public static float VirtueChance = 0.25f;
    public static float AfflictionChance = 0.75f;
    private static final String[] VIRTUE_CARD_IDS = new String[] {
        ModHelper.makePath("Courageous"),
        ModHelper.makePath("Focused"),
        ModHelper.makePath("Powerful"),
        ModHelper.makePath("Stalwart"),
        ModHelper.makePath("Vigorous")
    };
    private static final String[] AFFLICTION_CARD_IDS = new String[] {
        ModHelper.makePath("Abusive"),
        ModHelper.makePath("Fearful"),
        ModHelper.makePath("Hopeless"),
        ModHelper.makePath("Irrational"),
        ModHelper.makePath("Masochistic"),
        ModHelper.makePath("Paranoid"),
        ModHelper.makePath("Selfish")
    };

    private static Boolean pendingSavedTestTriggered;
    private boolean cardAddedAt100 = false;
    private boolean noBlockAtPlayerEndTurn = false;

    public PressureIndicator() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        this.counter = 0;
        this.cardAddedAt100 = false;
        this.noBlockAtPlayerEndTurn = false;
        if (pendingSavedTestTriggered != null) {
            this.cardAddedAt100 = pendingSavedTestTriggered;
            pendingSavedTestTriggered = null;
        }
        updateDescription();
    }

    public static boolean isTestTriggeredForSave() {
        if (AbstractDungeon.player == null) {
            return false;
        }
        AbstractRelic relic = AbstractDungeon.player.getRelic(ID);
        return relic instanceof PressureIndicator
            && ((PressureIndicator) relic).cardAddedAt100;
    }

    public static void loadTestTriggered(boolean triggered) {
        pendingSavedTestTriggered = triggered;
        if (AbstractDungeon.player != null) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(ID);
            if (relic instanceof PressureIndicator) {
                ((PressureIndicator) relic).cardAddedAt100 = triggered;
                pendingSavedTestTriggered = null;
            }
        }
    }

    @Override
    public void atTurnStart() {
        this.noBlockAtPlayerEndTurn = false;
        removeAfflictionsIfPressureZero();
    }

    @Override
    public void onPlayerEndTurn() {
        this.noBlockAtPlayerEndTurn = AbstractDungeon.player != null
            && AbstractDungeon.player.currentBlock == 0;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        removeAfflictionsIfPressureZero();

        if (info.type == DamageInfo.DamageType.THORNS || info.type == DamageInfo.DamageType.HP_LOSS) {
            return damageAmount;
        }

        AbstractCreature attacker = info.owner;
        int baseAmount = 7;
        if (attacker instanceof AbstractMonster && ((AbstractMonster) attacker).type == AbstractMonster.EnemyType.BOSS) {
            baseAmount = 10;
        }

        final int pressureBase = baseAmount;
        final boolean guaranteedPressure = noBlockAtPlayerEndTurn;
        final int unblockedDamage = damageAmount;
        final int hpBeforeAttack = AbstractDungeon.player.currentHealth;
        addToTop(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
            @Override
            public void update() {
                // Check HP after the attack action fully resolves (block/buffer/etc already applied).
                if (AbstractDungeon.player.currentHealth < hpBeforeAttack) {
                    addPressure(pressureBase, guaranteedPressure, unblockedDamage);
                }
                this.isDone = true;
            }
        });

        return damageAmount;
    }

    private void addPressure(int baseAmount, boolean guaranteed, int unblockedDamage) {
        int pressureAdded = guaranteed
            ? generateGuaranteedNumber(baseAmount)
            : generateRandomNumber(baseAmount);
        if (unblockedDamage > 20) {
            pressureAdded *= 2;
        } else if (unblockedDamage < 5) {
            pressureAdded = Math.round(pressureAdded / 2.0f);
        }
        BrightnessIndicator brightness = (BrightnessIndicator) AbstractDungeon.player
            .getRelic(BrightnessIndicator.ID);
        if (brightness != null) {
            pressureAdded = Math.max(0, Math.round(pressureAdded
                * BrightnessIndicator.getPressureMultiplier(brightness.getBrightness())));
        }
        changePressure(pressureAdded);
    }

    public void changePressure(int amount) {
        int previousPressure = counter;
        counter = Math.max(0, counter + amount);
        if (counter != previousPressure) {
            flash();
        }
        if (counter >= TEST_AMOUNT && !cardAddedAt100) {
            addRandomVirtueOrAffliction();
            cardAddedAt100 = true;
        }
        if (counter >= DIE_AMOUNT) {
            addToTop(new LoseHPAction(AbstractDungeon.player, null, 99999));
        }
        removeAfflictionsIfPressureZero();
    }

    @Override
    public String getUpdatedDescription() {
        return buildDescription();
    }

    private static float getVirtueChance() {
        return LuckyPower.getLuckyChance(VirtueChance);
    }

    private String buildDescription() {
        int virtueChance = Math.round(getVirtueChance() * 100.0f);
        int afflictionChance = 100 - virtueChance;
        return String.format(this.DESCRIPTIONS[0], virtueChance, afflictionChance);
    }

    private void updateDescription() {
        this.description = buildDescription();
    }

    @Override
    public void update() {
        super.update();
        String updatedDescription = buildDescription();
        if (!updatedDescription.equals(this.description)) {
            this.description = updatedDescription;
        }
    }

    @Override
    public void addCampfireOption(ArrayList<AbstractCampfireOption> options) {
        ReplaceHeroCampfireOption.addOptions(options);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new PressureIndicator();
    }

    public int generateRandomNumber(int baseNum) {
        // 30% chance to avoid pressure gain entirely.
        if (AbstractDungeon.miscRng.randomBoolean(0.3f)) {
            return 0;
        }

        // 70% chance to gain a random value in [1x, 2x] of baseNum.
        int minAdded = (int) Math.floor(baseNum * 1.0f);
        int maxAdded = (int) Math.ceil(baseNum * 2.0f);
        return AbstractDungeon.miscRng.random(minAdded, maxAdded);
    }

    private int generateGuaranteedNumber(int baseNum) {
        int minAdded = (int) Math.floor(baseNum * 1.0f);
        int maxAdded = (int) Math.ceil(baseNum * 2.0f);
        return AbstractDungeon.miscRng.random(minAdded, maxAdded);
    }

    private void addRandomVirtueOrAffliction() {
        boolean pickVirtue = AbstractDungeon.miscRng.randomBoolean(getVirtueChance());
        if (pickVirtue) {
            String cardId = VIRTUE_CARD_IDS[AbstractDungeon.miscRng.random(VIRTUE_CARD_IDS.length - 1)];
            AbstractCard cardToAdd = CardLibrary.getCopy(cardId);
            if (cardToAdd != null) {
                addToBot(new AddCardToDeckAction(cardToAdd));
            }
        } else {
            String cardId = AFFLICTION_CARD_IDS[AbstractDungeon.miscRng.random(AFFLICTION_CARD_IDS.length - 1)];
            AbstractCard cardToAdd = CardLibrary.getCopy(cardId);
            if (cardToAdd != null) {
                addToBot(new AddCardToDeckAction(cardToAdd));
            }
        }
    }

    public static boolean isTestCard(String cardId) {
        return Arrays.asList(VIRTUE_CARD_IDS).contains(cardId)
            || Arrays.asList(AFFLICTION_CARD_IDS).contains(cardId);
    }

    private void removeAfflictionsIfPressureZero() {
        if (this.counter != 0 || AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return;
        }

        Iterator<AbstractCard> iterator = AbstractDungeon.player.masterDeck.group.iterator();
        while (iterator.hasNext()) {
            AbstractCard card = iterator.next();
            if (Arrays.asList(AFFLICTION_CARD_IDS).contains(card.cardID)) {
                iterator.remove();
            }
        }
        // Reset the one-time trigger flag when pressure returns to 0
        this.cardAddedAt100 = false;
    }
}
