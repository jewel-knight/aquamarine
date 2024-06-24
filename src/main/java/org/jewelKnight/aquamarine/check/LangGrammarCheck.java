package com.impactcn.aquamarine.check;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.markup.AnnotatedText;
import org.languagetool.markup.AnnotatedTextBuilder;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author impactCn
 * @date 2024/1/21 10:57
 */
public class LangGrammarCheck {

    private final static JLanguageTool LANG_TOOL = new JLanguageTool(new AmericanEnglish());

    public List<Range> check(String text) {
        List<Range> ranges = new ArrayList<>();
        try {
            List<RuleMatch> matches = LANG_TOOL.check(text);
            for (RuleMatch match : matches) {
                Range range = new Range(match.getFromPos(), match.getToPos());
                ranges.add(range);
            }
            return ranges;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws IOException {

        JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
        String error = "A sentence with a error in the Hitchhiker's Guide to th Galaxy";
//        String markdownText = "# Heading\n" +
//                "This is a **bol** _italic_ paragraph with [a link](http://a_sentence_with_a_error.com).\n" +
//                "1. The cat lost it's mind. **Its** a funny cat.\n" +
//                "2. Item 2";


        List<RuleMatch> matches = langTool.check(error);

        for (RuleMatch match : matches) {
            System.err.println(match.getPatternFromPos());
            System.err.println(match.getPatternToPos());
            System.err.println(match.getFromPosSentence());
            System.err.println(match.getToPosSentence());
            System.err.println("Potential error at characters " +
                    match.getFromPos() + "-" + match.getToPos() + ": " +
                    match.getMessage());
//            System.err.println(match.getSuggestedReplacements());
        }


    }


    public static class Range {

        private int startPos;

        private int endPos;

        public Range(int startPos, int endPos) {
            this.startPos = startPos;
            this.endPos = endPos;
        }


        public int getStartPos() {
            return startPos;
        }

        public void setStartPos(int startPos) {
            this.startPos = startPos;
        }

        public int getEndPos() {
            return endPos;
        }

        public void setEndPos(int endPos) {
            this.endPos = endPos;
        }


    }

}
