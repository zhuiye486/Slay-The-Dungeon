package io.github.zhuiye486.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;


public class ChooseHeroes extends AbstractImageEvent {
    public final static String ID = "SlayTheDungeon:ChooseHeros";
    private static final EventStrings EVENT_STRINGS = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = EVENT_STRINGS.NAME;
    private static final String[] DESCRIPTIONS = EVENT_STRINGS.DESCRIPTIONS;
    private static final String[] OPTIONS = EVENT_STRINGS.OPTIONS;

    private static Set<Integer> allUsedNumbers = new HashSet<>();
    private int choiceCount = 0;
    private List<Integer> selectedHeroes = new ArrayList<>();
    private List<Integer> currentSequence;

    public ChooseHeroes() {
        super(NAME, DESCRIPTIONS[0], "img/event/chooseHeros.jpg");

        this.currentSequence = generateUniqueRandomSequence(4, 1, 18, this.selectedHeroes);
        this.imageEventText.clearAllDialogs();
        this.imageEventText.clearRemainingOptions();

        for (int i = 0; i < this.currentSequence.size(); i++) {
            int number = this.currentSequence.get(i);
            this.imageEventText.setDialogOption(OPTIONS[number - 1]); 
        }
    }

    @Override
    public void onEnterRoom() {
        System.out.println("ChooseHeroes event entered room.");
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (this.choiceCount >= 4) {
            openMap();
            return;
        }

        // 记录选择
        int selectedHero = this.currentSequence.get(buttonPressed);
        this.selectedHeroes.add(selectedHero);
        this.choiceCount++;

        if (this.choiceCount < 4) {
            // 生成新的4个选项
            this.currentSequence = generateUniqueRandomSequence(4, 1, 18, this.selectedHeroes);
            this.imageEventText.clearAllDialogs();
            this.imageEventText.clearRemainingOptions();
            this.imageEventText.updateBodyText("这是第" + (this.choiceCount + 1) + "次选择，请从以下英雄中选择一个：");

            for (int i = 0; i < this.currentSequence.size(); i++) {
                int number = this.currentSequence.get(i);
                this.imageEventText.setDialogOption(OPTIONS[number - 1]);
            }
        } else {
            // 选择完成
            this.imageEventText.clearAllDialogs();
            this.imageEventText.clearRemainingOptions();
            this.imageEventText.updateBodyText("选择完成！");
            this.imageEventText.setDialogOption("离开");
            io.github.zhuiye486.characters.Heroes.updateStartingDeckForSelection(this.selectedHeroes);
            // Compose character image
            io.github.zhuiye486.characters.Heroes.composeCharacterImage(this.selectedHeroes);
        }
    }

    private static List<Integer> generateUniqueRandomSequence(int sequenceSize, int minNumber, int maxNumber, List<Integer> excluded) {
        Set<Integer> availableNumbers = new HashSet<>();

        for (int i = minNumber; i <= maxNumber; i++) {
            if (!allUsedNumbers.contains(i) && !excluded.contains(i))
                availableNumbers.add(i); 
        } 
        Random random = new Random(AbstractDungeon.miscRng.randomLong());
        ArrayList<Integer> numberList = new ArrayList<>(availableNumbers);
        Collections.shuffle(numberList, random);
        System.out.println("numberList" + numberList);
        if (numberList.size() >= sequenceSize) {
            List<Integer> list = new ArrayList<>(numberList.subList(0, sequenceSize));
            return list;
        } 
        List<Integer> sequence = new LinkedList<>();
        for (int i = 1; i <= sequenceSize; i++) {
            if (!allUsedNumbers.contains(i) && !excluded.contains(i)) {
                sequence.add(i);
            }
        }
        return sequence;
    }

}
