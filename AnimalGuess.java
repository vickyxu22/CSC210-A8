import java.util.Scanner;

public class AnimalGuess {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Check if a filename is provided as a command line argument
        if (args.length != 1) {
            System.out.println("Usage: java AnimalGuess <filename>");
            System.exit(1);
        }

        String filename = args[0];

        // Read the decision tree from the file
        DecisionTree animalGuessTree = DecisionTree.readFromFile(filename);

        // Main game loop
        do {
            playGame(animalGuessTree, scanner);
        } while (playAgain(scanner));

        // Write the decision tree back to the file
        DecisionTree.writeToFile(filename, animalGuessTree);

        System.out.println("Thanks for playing!");
    }
    
    private static void playGame(DecisionTree currentNode, Scanner scanner) {
        System.out.println("Think of an animal...");
        while (!currentNode.isLeaf()) {
            boolean answer = getUserAnswer(currentNode.getData(), scanner);
            if (answer) {
                currentNode = (DecisionTree) currentNode.getLeft();
            } else {
                currentNode = (DecisionTree) currentNode.getRight();
            }
        }
        makeGuess(currentNode.getData(), scanner);
    }

    private static void makeGuess(String animal, Scanner scanner) {
        boolean correct = getUserAnswer("Is it " + animal + "?", scanner);
        if (correct) {
            System.out.println("I guessed it!");
        } else {
            learnNewAnimal(animal, scanner);
        }
    }

    private static void learnNewAnimal(String incorrectAnimal, Scanner scanner) {
        System.out.print("I give up. What animal were you thinking of? ");
        String newAnimal = scanner.nextLine().trim();
        System.out.print("Please provide a yes/no question to distinguish between "
                + incorrectAnimal + " and " + newAnimal + ": ");
        String newQuestion = scanner.nextLine().trim();

        DecisionTree newQuestionNode = new DecisionTree(newQuestion);
        DecisionTree newAnimalNode = new DecisionTree(newAnimal);
        DecisionTree oldAnimalNode = new DecisionTree(incorrectAnimal);

        System.out.print("Is the correct answer 'yes' for " + newAnimal + "? ");
        boolean answerYes = getUserAnswer("", scanner);

        if (answerYes) {
            newQuestionNode.setLeft(newAnimalNode);
            newQuestionNode.setRight(oldAnimalNode);
        } else {
            newQuestionNode.setLeft(oldAnimalNode);
            newQuestionNode.setRight(newAnimalNode);
        }

        System.out.println("Thanks! I'll remember that for next time.");
    }

    private static boolean getUserAnswer(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt + " (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("no") || input.equals("n")) {
                return false;
            } else {
                System.out.println("Invalid response. Please enter 'yes' or 'no'.");
            }
        }
    }

    private static boolean playAgain(Scanner scanner) {
        return getUserAnswer("Do you want to play again?", scanner);
    }

    private static DecisionTree buildSampleTree() {
        return new DecisionTree("Is it a mammal?",
                new DecisionTree("Does it have fur?",
                        new DecisionTree("Is it a cat?"),
                        new DecisionTree("Is it a bird?")
                ),
                new DecisionTree("Does it swim?",
                        new DecisionTree("Is it a fish?"),
                        new DecisionTree("Is it a reptile?",
                                new DecisionTree("Does it have scales?",
                                        new DecisionTree("Is it a snake?"),
                                        new DecisionTree("Is it a turtle?")
                                ),
                                new DecisionTree("Is it an insect?")
                        )
                )
        );
    }    
}
