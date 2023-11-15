import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayDeque;

public class DecisionTree extends BinaryTree<String> {

    // Constructors
    public DecisionTree(String data) {
        super(data);
    }

    public DecisionTree(String data, DecisionTree left, DecisionTree right) {
        super(data, left, right);
    }
    
    public DecisionTree getLeft() {
        return (DecisionTree) super.getLeft();
    }
    public DecisionTree getRight() {
        return (DecisionTree) super.getRight();
    }

    // Follow the path based on input string (Y for left, N for right)
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

    // Write the decision tree to a file
    public static void writeToFile(String filename, DecisionTree root) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            ArrayDeque<DecisionTree> nodeQueue = new ArrayDeque<>();
            ArrayDeque<String> pathQueue = new ArrayDeque<>();
            ArrayDeque<DecisionTree> newQueue = new ArrayDeque<>();
    
            nodeQueue.add(root);
            pathQueue.add("");
    
            while (!nodeQueue.isEmpty()) {
                DecisionTree currentNode = nodeQueue.poll();
                String currentPath = pathQueue.poll();
    
                out.println(currentPath + " " + currentNode.getData());
    
                if (currentNode.getLeft() != null) {
                    nodeQueue.add(currentNode.getLeft());
                    pathQueue.add(currentPath + "Y");
                    newQueue.add(currentNode.getLeft());
                }
                if (currentNode.getRight() != null) {
                    nodeQueue.add(currentNode.getRight());
                    pathQueue.add(currentPath + "N");
                    newQueue.add(currentNode.getRight());
                }
            }
            
            // Write to the file using the existing PrintWriter 'out'
            while (!newQueue.isEmpty()) {
                out.write(newQueue.poll() + "\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot load file.");
            System.exit(-1);
        }
    }
    
    
    // Read the decision tree from a file
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

    // Sample test code
    public static void main(String[] args) {
        // Building a sample decision tree
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

        // Accessing data at individual nodes
        System.out.println("Data at tree.getLeft().getRight(): " + tree.getLeft().getRight().getData());

        // Testing followPath method
        DecisionTree resultNode = tree.followPath("YN");
        System.out.println("Data at resultNode: " + resultNode.getData());
    }
}
