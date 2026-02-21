package io.github.zhuiye486.events;

import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Random;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;


public class ChooseHeros extends AbstractImageEvent {
    public final static String ID = "SlayTheDungeon:ChooseHeros";
    private static final EventStrings EVENT_STRINGS = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = EVENT_STRINGS.NAME;
    private static final String[] DESCRIPTIONS = EVENT_STRINGS.DESCRIPTIONS;

    private static Set<Integer> allUsedNumbers = new HashSet<>();

    public ChooseHeros() {
        super(NAME, DESCRIPTIONS[0], "img/event/chooseHeros.jpg");

        List<Integer> currentSequence = generateUniqueRandomSequence(4, 1, 18);
    }

    public void onEnterRoom() {
        
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (buttonPressed) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            
        }
    }

    public static List<Integer> generateUniqueRandomSequence(int sequenceSize, int minNumber, int maxNumber) {
        Set<Integer> availableNumbers = new HashSet<>();

        for (int i = minNumber; i <= maxNumber; i++) {
            if (!allUsedNumbers.contains(Integer.valueOf(i)))
                availableNumbers.add(Integer.valueOf(i)); 
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
        sequence.add(Integer.valueOf(1));
        sequence.add(Integer.valueOf(2));
        sequence.add(Integer.valueOf(3));
        return sequence;
    }

}
