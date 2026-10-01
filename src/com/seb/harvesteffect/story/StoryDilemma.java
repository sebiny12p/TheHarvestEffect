package com.seb.harvesteffect.story;

import com.seb.harvesteffect.engine.GalacticState;

/**
 * Interactive narrative dilemma presenting player with high-stakes cosmic choices.
 */
public class StoryDilemma {

    public static class Choice {
        private final String label;
        private final String description;
        private final String outcomeNarrative;
        private final int eezoBonus;
        private final int biomassBonus;

        public Choice(String label, String description, String outcomeNarrative, int eezoBonus, int biomassBonus) {
            this.label = label;
            this.description = description;
            this.outcomeNarrative = outcomeNarrative;
            this.eezoBonus = eezoBonus;
            this.biomassBonus = biomassBonus;
        }

        public String getLabel() { return label; }
        public String getDescription() { return description; }
        public String getOutcomeNarrative() { return outcomeNarrative; }
        public int getEezoBonus() { return eezoBonus; }
        public int getBiomassBonus() { return biomassBonus; }

        public void apply(GalacticState state) {
            if (eezoBonus > 0) state.addEezo(eezoBonus);
            if (biomassBonus > 0) state.addBiomass(biomassBonus);
        }
    }

    private final String actId;
    private final String title;
    private final String speaker;
    private final String transmissionText;
    private final Choice choiceA;
    private final Choice choiceB;

    public StoryDilemma(String actId, String title, String speaker, String transmissionText, Choice choiceA, Choice choiceB) {
        this.actId = actId;
        this.title = title;
        this.speaker = speaker;
        this.transmissionText = transmissionText;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
    }

    public String getActId() { return actId; }
    public String getTitle() { return title; }
    public String getSpeaker() { return speaker; }
    public String getTransmissionText() { return transmissionText; }
    public Choice getChoiceA() { return choiceA; }
    public Choice getChoiceB() { return choiceB; }
}
