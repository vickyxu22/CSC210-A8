import java.io.*;
import java.util.*;
/**
 * Decision tree structure for a guessing game.
 */
public class DecisionTree extends BinaryTree<String> {
    /**
     * Constructs a DecisionTree.
     *
     * @param data The data for the node.
     */
    public DecisionTree(String data) {
        super(data);
    }

    /**
     * Constructs a DecisionTree, left, and right subtrees.
     *
     * @param data  The data for the node.
     * @param left  The left subtree.
     * @param right The right subtree.
     */
    public DecisionTree(String data, DecisionTree left, DecisionTree right) {
        super(data, left, right);
    }
    
    /**
     * Gets the left subtree as a DecisionTree.
     *
     * @return The left subtree as a DecisionTree.
     */
    public DecisionTree getLeft() {
        return (DecisionTree) super.getLeft();
    }

    /**
     * Gets the right subtree as a DecisionTree.
     *
     * @return The right subtree as a DecisionTree.
     */
    public DecisionTree getRight() {
        return (DecisionTree) super.getRight();
    }

    /**
     * Follows a given path string (Y for left, N for right) from the current node.
     *
     * @param path The path string.
     * @return The node at the end of the path.
     * @throws IllegalArgumentException if an invalid path character is encountered.
     */
    public DecisionTree followPath(String path) {
        DecisionTree currentNode = this;
        for (char direction : path.toCharArray()) {
            if (direction == 'Y') {
                currentNode = (DecisionTree) currentNode.getLeft();
            } else if (direction == 'N') {
                currentNode = (DecisionTree) currentNode.getRight();
            } else {
                throw new IllegalArgumentException("Invalid path character: " + direction);
            }

            if (currentNode == null) {
                throw new IllegalArgumentException("Invalid path: " + path);
            }
        }
        return currentNode;
    }

    /**
     * Finds a node containing specific data starting from the given node.
     *
     * @param node       The node to start the search from.
     * @param searchData The data to search for.
     * @return The found node containing the searchData, null if not found.
     */
    public DecisionTree findNode(DecisionTree node, String searchData) {
        if (node == null) {
            return null;
        }

        if (node.getData().equals(searchData)) {
            return node;
        }

        DecisionTree leftSearch = findNode(node.getLeft(), searchData);
        if (leftSearch != null) {
            return leftSearch;
        }

        return findNode(node.getRight(), searchData);
    }

    /**
     * Adds a new animal and a question to the decision tree.
     *
     * @param existingAnimal The existing animal incorrectly guessed.
     * @param newAnimal      The new animal introduced by the user.
     * @param question       The question to distinguish between existing and new animals.
     * @param answer         The user's answer ('yes' or 'no') to the question.
     * @param root           The root of the decision tree.
     */
    public void addNewAnimal(String existingAnimal, String newAnimal, String question, String answer, DecisionTree root) {
        DecisionTree existingNode = findNode(root, existingAnimal);
        if (existingNode != null) {
            DecisionTree newNode = new DecisionTree(newAnimal);
            DecisionTree originalAnimalNode = new DecisionTree(existingAnimal);
            DecisionTree newQuestionNode = new DecisionTree(question);

            if ("yes".equalsIgnoreCase(answer)) {
                existingNode.setLeft(newQuestionNode);
                newQuestionNode.setLeft(newNode);
                newQuestionNode.setRight(originalAnimalNode);
            } else {
                existingNode.setRight(newQuestionNode);
                newQuestionNode.setLeft(originalAnimalNode);
                newQuestionNode.setRight(newNode);
            }

            writeToFile("AnimalTree.txt", root);

            try (BufferedReader reader = new BufferedReader(new FileReader("AnimalTree.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Writes the decision tree structure to a file.
     *
     * @param filename The name of the file.
     * @param root     The root of the decision tree.
     */
    public static void writeToFile(String filename, DecisionTree root) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            ArrayDeque<DecisionTree> nodeQueue = new ArrayDeque<>();
            ArrayDeque<String> pathQueue = new ArrayDeque<>();
            ArrayDeque<String> outputQueue = new ArrayDeque<>();

            nodeQueue.add(root);
            pathQueue.add("");

            while (!nodeQueue.isEmpty()) {
                DecisionTree currentNode = nodeQueue.poll();
                String currentPath = pathQueue.poll();

                outputQueue.add(currentPath + " " + currentNode.getData());

                if (currentNode.getLeft() != null) {
                    nodeQueue.add(currentNode.getLeft());
                    pathQueue.add(currentPath + "Y");
                }
                if (currentNode.getRight() != null) {
                    nodeQueue.add(currentNode.getRight());
                    pathQueue.add(currentPath + "N");
                }
            }

            while (!outputQueue.isEmpty()) {
                String line = outputQueue.poll();
                out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot load file.");
            System.exit(-1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Reads the decision tree structure from a file.
     *
     * @param filename The name of the file.
     * @return The root of the decision tree.
     */
    public static DecisionTree readFromFile(String filename) {
        DecisionTree root = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                String path = parts[0];
                String data = parts[1];

                if (root == null) {
                    root = new DecisionTree(data);
                } else {
                    DecisionTree parentNode = root.followPath(path.substring(0, path.length() - 1));
                    DecisionTree newNode = new DecisionTree(data);
                    if (path.charAt(path.length() - 1) == 'Y') {
                        parentNode.setLeft(newNode);
                    } else {
                        parentNode.setRight(newNode);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    // sample test code
    public static void main(String[] args) {
        DecisionTree tree = new DecisionTree("Is it a mammal?",
                new DecisionTree("Does it have fur?",
                        new DecisionTree("Is it a cat?"),
                        new DecisionTree("Is it a bird?")
                ),
                new DecisionTree("Does it swim?",
                        new DecisionTree("Is it a fish?"),
                        new DecisionTree("Is it a reptile?")
                )
        );

        System.out.println("Data at tree.getLeft().getRight(): " + tree.getLeft().getRight().getData());

        DecisionTree resultNode = tree.followPath("YN");
        System.out.println("Data at resultNode: " + resultNode.getData());
    }
}