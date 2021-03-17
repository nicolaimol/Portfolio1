import java.util.Arrays;

public class WordAnalysing {

    private static String findExpression(String[] sentence, int from, int to) {
        String expression = "";
        for (int i = from; i < to; i++) {
            if (sentence[i].equals("to")) {
                if (sentence[i+1].equals("the")) {
                    expression += "to " + sentence[i+1] + " " + sentence[i +2];
                }
                else expression += "to " + sentence[i+1];
            }

            if (sentence[i].equals("for")) {
                expression += "for ";
            }

            if (sentence[i].equals("a")) {
                expression += "a " + sentence[i+1];
            }

            if (sentence[i].equals("in") || sentence[i].equals("at") || sentence[i].equals("on")) {
                if (sentence[i + 1].equals("the")) {
                    expression += " " + sentence[i] + " " + sentence[i + 1] + " " + sentence[i + 2];
                }
                else {
                    expression += sentence[i] + " " + sentence[i + 1];
                }
            }

            if (i < sentence.length - 1 && sentence[i].equals("some")) {
                expression += "some " + sentence[i + 1];
            }

            if (sentence[i].equals("of")) {
                expression += " of " + sentence[i + 1];
            }

            if (from == to -1) {
                expression = sentence[from];
            }
        }


        return expression;
    }

    static String[] findAction(String input) {
        return findAction(input, false);
    }

    private static String[] findAction(String input, boolean and) {
        input = input.replace("[?.,-!]", "");
        String user = input.split(":")[0];
        if (!user.equals("Server")) return new String[]{null, ""};
        String action = input.split(":")[1].trim();
        String[] sentence = action.split(" ");

        if (sentence.length == 1) {
            if (sentence[0].equals("sup")) {
                return new String[]{"greeting", "whats up"};
            }
            if (sentence[0].toLowerCase().contains("okay") || sentence[0].toLowerCase().contains("oki")) {
                return new String[]{"okay"};
            }
            return new String[]{"greeting", "hi"};
        } if (sentence.length == 2) {
            if (action.contains("hey") || action.contains("Hey")) {
                return new String[]{"greeting", "hi"};
            }
            if (sentence[0].equalsIgnoreCase("whats") && sentence[1].equalsIgnoreCase("up")) {
                return new String[]{"greeting", "whats up"};
            }
            if (sentence[0].equalsIgnoreCase("bye")) {
                return new String[]{"bye", sentence[1]};
            }
            if (sentence[0].equalsIgnoreCase("okay") || sentence[0].equalsIgnoreCase("oki")) {
                return new String[]{"okay"};
            }
        }


        String verb = null;
        String expression = "";

        boolean andIn = Arrays.asList(sentence).contains("and") || Arrays.asList(sentence).contains("or");

        String andOr = andIn &&  Arrays.asList(sentence).contains("and")? "and" : "or";

        int to =  andIn ? Arrays.asList(sentence).indexOf(andOr) : sentence.length;

        if (and || !andIn) {
            for (int i = 0; i < to; i++) {
                if (sentence[i].equalsIgnoreCase("lets")) {
                    verb = sentence[i + 1];
                    expression = findExpression(sentence, i + 2, to);
                    break;
                }
                if (sentence[i].equals("to")) {
                    verb = sentence[i+1];
                    expression = findExpression(sentence, i + 2, to);

                    for (int j = i + 2; j < sentence.length; j++) {

                        if (sentence[j].contains("ing") && !sentence[j].equals("morning")) {
                            verb = sentence[j].replaceAll("ing", "");
                        }

                    }
                    break;
                }
                if (sentence[i].contains("ing")) {
                    verb = sentence[i].replaceAll("ing", "");
                    expression = findExpression(sentence, i + 1, to);
                }
                if (sentence[i].equalsIgnoreCase("we")) {
                    if (!sentence[i + 1].equals("should") || !sentence[i + 1].equals("could")) {
                        verb = sentence[i + 1];
                        expression = findExpression(sentence, i + 2, to);
                    }
                    else  {
                        verb = sentence[i + 2];
                        expression = findExpression(sentence, i + 3, to);
                    }

                    break;
                }
                if (sentence[i].equalsIgnoreCase("wanna")) {
                    verb = sentence[i+1];
                    expression = findExpression(sentence, i + 2, to);
                    break;
                }
            }
        } else {

            String[] partOne = findAction(input, true);

            int next = sentence[to + 1].equals("maybe")? 2 : 1;
            String verb2 =  sentence[to + next];
            String expression2 = findExpression(sentence, to + next + 1, sentence.length);

            String[] twoActions = {partOne[0], partOne[1], verb2, expression2, andOr};
            return twoActions;
        }


        return new String[]{verb, expression.replaceAll("[?]", "")};
    }
}
