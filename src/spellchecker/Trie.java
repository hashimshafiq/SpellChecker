package spellchecker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

/**
 * A wrapper class for user interfacing with the trie.
 * Real functionality is abstracted in inner class Node.
 */
public class Trie {
	Node root;

	/**
	 * Each node in the trie represents a character. 
	 * Number of entries in the HashMap entries represents the number
	 * of children this node has. 
	 */
	private class Node {
		char charKey;
		boolean isWord;
		HashMap<Character, Node> suffixes;

		Node(char nodeKey, Node parent) {
			this.charKey = nodeKey;
			isWord = false;
			suffixes = new HashMap<Character, Node>();
		}

		/**
		 * Recursively adds a word to the trie, char by char.
		 * Note that the entire String is passed because in Java, Strings
		 * are immutable and just exist within a heap. It would be no more
		 * expensive once the String has been created.
		 * @param word
		 */
		void add(String word, int index) {
			if (word.length() == index) { // Recursion base condition
				this.isWord = true; // Allows to identify which prefixes are complete words
			}
			else { 
				char nodeKey = word.charAt(index);
				Node n = suffixes.get(nodeKey);

				if (n == null) { // If the child doesn't exist, create it
					n = new Node(nodeKey, this);
					suffixes.put(new Character(word.charAt(index)), n);
				} 

				n.add(word, index+1); // Recursive call by child node
			}
		}

		/**
		 * Returns whether or not a word is in the trie or not
		 * @param word Word to find in the dictionary
		 * @return true if word exists in the trie, else false
		 */
		boolean contains(String word, int index) {
			if (word.length() == index) {
				return this.isWord;
			}
			else {
				Node n = suffixes.get(word.charAt(index));
				if (n != null) {
					return n.contains(word, index+1);
				} else {
					return false;
				}
			}
		}

		/**
		 * Looks for the closest match in the trie of the word
		 * using DFS
		 */
		private String depthFirstSearch(String input, int index) {
			if (index == input.length()) {
				if (this.isWord) {
					return "" + charKey;
				}
				return null;
			} if (isInLastSegment(input, index-1) && this.isWord) { // index-1 because caller represents
																	// the char before the index
				return "" + charKey;
			} else {
				// TODO: wouldn't hurt to break this method down into smaller methods, like the stack part...
				String ret;
				
				// TODO: can't specify initial size for Stack... wasting memory here...
				Stack<Node> searchStack = new Stack<Node>(); // Each call gets its own stack, different from 
															 // typical DFS. This is because each call could
															 // have a different index, and we want to make sure
															 // it's calling with the correct one. Additionally, 
															 // because Strings are immutable, passing different
															 // substrings would take much more memory.
				char charAtOrigIndex = input.charAt(index);

				Stack<Character> permutations = createPermutations(charAtOrigIndex);

				for (char ch : permutations) {
					Node nodeToSearch = suffixes.get(ch);		// Faster than containsKey + get, 
																// since it's guaranteed no null V
					if (nodeToSearch != null) {
						searchStack.push(nodeToSearch);
					} 
				}
				
				while (!searchStack.isEmpty()) {
					ret = searchStack.pop().depthFirstSearch(input, index+1);
					if (ret != null) {
						return this.charKey + ret;
					}
				} // At this point, this search path got us nothing, try the other path. 

				if (index >= input.length()-1 || index < 1) { return null; } // If no next and prev char exists, stop here
				
				// Case for dealing with frame shifts caused by duplicate characters
				if (charAtOrigIndex == input.charAt(index-1)) {
					char nextDifferentChar;

					while (++index < input.length()) {
						nextDifferentChar = input.charAt(index);
						if (nextDifferentChar != charAtOrigIndex) {
							permutations = createPermutations(nextDifferentChar);

							for (char ch : permutations) {
								Node nodeToSearch = suffixes.get(ch);		// Faster than containsKey + get, 
																			// since it's guaranteed no null V
								if (nodeToSearch != null) {
									searchStack.push(nodeToSearch);
								} 
							}
							
							while (!searchStack.isEmpty()) {
								ret = searchStack.pop().depthFirstSearch(input, index+1);
								if (ret != null) {
									return this.charKey + ret;
								}
							}
							break;
						}
					}					
				}
				// If we make it this far, no suggestion was found
				return null;
			}
		}

		/**
		 * Checks whether or not the given index is part of the last segment
		 * of a String, where segment is defined as repeating consecutive characters.
		 * (e.g. for abcddd, all d's are in the last segment, everything else is not)
		 * @param input user's original input
		 * @param index 
		 * @return 
		 */
		private boolean isInLastSegment(String input, int index) {
			int indexOfLastSectionHead = input.length()-1;
			while (indexOfLastSectionHead > 0) {
				if (input.charAt(indexOfLastSectionHead) != input.charAt(indexOfLastSectionHead-1)) {
					break;
				}
				--indexOfLastSectionHead;
			}
			return index >= indexOfLastSectionHead;
		}
	}

	/**
	 * Default constructor. Constructs a case-sensitive trie based on the
	 * packaged words file in the root dir of the project
	 */
	public Trie() {
		this("words");
	}

	/**
	 * Constructs a case-sensitive trie based on specified file directory
	 * @param fileDir File path, relative to project root. (e.g. "bin/words.txt") 
	 * @throws IOException if file directory was invalid. 
	 */
	public Trie(String fileDir) {
		root = new Node('\u0000', null);
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileDir));
			for ( String word = br.readLine(); word != null; word = br.readLine()) {
				root.add(word.trim(), 0);
			}
			br.close();
		}
		catch (FileNotFoundException fex) {
			System.out.println("Couldn't find dictionary file to parse; EXITING"); 
			System.exit(0);
		}
		catch (IOException ex) {
			System.out.println("Couldn't parse dictionary file; EXITING"); 
			System.exit(0);
		}
	}

	/** 
	 * Checks if the word exists in the trie
	 * @param word any String
	 * @return true if word was found
	 */
	public boolean contains(String word) {
		return root.contains(word, 0);
	}
	
	/**
	 * Suggests a correction to a user input by checking it with a trie-based dictionary
	 * @param input User's input that's potentially a typo
	 * @return NO SUGGESTION if nothing was found, otherwise a correction based on
	 * the fact that the user could have accidentally:
	 * 1) Repeated a character
	 * 2) Substituted a vowel
	 * 3) Substituted with upper case with lower case and vice versa
	 * It will to a DFS and return the first candidate. There is no kind of 
	 * probability weighing put on them.
	 */
	public String suggestCorrection(String input) {
		// If input was null, exit program, it means test code reached EOF
		//if (input == null) { System.exit(0); }
		// If input is empty, we have no idea what they meant...
		//if (input.isEmpty() ) {	return "NO SUGGESTION";	}
		
		// If original input was valid word, assume they meant it
		if (root.contains(input, 0)) { 
			return input; 
		}
		
		String ret = root.depthFirstSearch(input, 0);
		return ret != null ? ret : "NO SUGGESTION";
	}
	
	/**
	 * Creates permutations of any single character typos:
	 * 1) Any given character could be its lower/upper case form
	 * 2) Any vowel can be another vowel
	 *
	 * @param ch character to create permutations for
	 * @return Stack of the permutation. There is no significance to the fact that it's stack
	 */
	public static Stack<Character> createPermutations(char ch) {
		Stack<Character> ret = new Stack<Character>();

		if (isVowel(ch)) {
			ret.push('U'); ret.push('I'); ret.push('O'); ret.push('A'); ret.push('E');
			ret.push('u'); ret.push('i'); ret.push('o'); ret.push('a'); ret.push('e');
		} else if (Character.isAlphabetic(ch)) {
			ret.push(Character.toUpperCase(ch)); // push both the upper and lower case
			ret.push(Character.toLowerCase(ch)); 
								 
		} else {
			ret.push(ch);                        // For special character like '-'
		}
		return ret;
	}
	
	/**
	 * Evaluate whether a character is vowel
	 * @param c character to evaluate
	 * @return true if vowel (AEIOUaeiou)
	 */
	public static boolean isVowel(char c) {
		// Optimized by evaluating by frequency of letters
		return c == 'e' || c == 'a' || c == 'o' || c == 'i' || c == 'u'
			|| c == 'E' || c == 'A' || c == 'O' || c == 'I' || c == 'U';
	}
}