package es.hugoalvarezajenjo.jellyfish.parser;

import java.util.Arrays;

public class FishboneParser {
    public static FishboneNode parse(final String content) {
        final String[] contentSplit = splitContent(content);
        FishboneNode root = null;
        FishboneNode current = null;
        int i = 0;
        for (i = i; i < contentSplit.length && root == null; i++) {
            if (getDepth(contentSplit[i]) > 0) {
                return null;
            } else {
                root = new FishboneNode(contentSplit[i], null, 0);
                current = root;
            }
        }
        while (i < contentSplit.length) {
            if (getDepth(contentSplit[i]) == 0) {
                return null;
            } else {
                if (getDepth(contentSplit[i]) > current.getDepth()) {
                    if (getDepth(contentSplit[i]) - current.getDepth() > 1) {
                        return null;
                    }
                    FishboneNode child = new FishboneNode(contentSplit[i], current, getDepth(contentSplit[i]));
                    current.addChild(child);
                    current = child;
                    i++;
                } else {
                    current = current.getParent();
                }
            }

        }
        return root;
    }

    private static int getDepth(final String line) {
        int count = 0;
        for (int i = 0; i < line.length() && line.charAt(i) == '-'; i++) {
            count++;
        }
        return count;
    }

    private static String[] splitContent(final String content) {
        return Arrays.stream(content.split("\n"))
                .filter(line -> !line.trim().isEmpty())
                .toArray(String[]::new);
    }
}
