# SpellChecker Using Trie Prefix Tree

There are many datastructures used for SpellChecking and Spell Sugestions but by using them there comes time complexity.
Time taken by the other data structure is high because they do operation on the input word and then compre that input
with the Dictionary file. But here you see that dictionary file is very huge containing millions of lines of text so
searching againt that text is taking huge amount of time.

In this code I use Trie Tree to handle the dictionary file. Instead of doing operations on the Input, I have done
operation on the dictionary words and store them in the Trie Tree. The result is very shocking because the time
complexity reduces to length of the input string.
I also do little tweaks to make it more efficient by constructing tree only one time i.e start of the program. Now 
you can search of many words without creating the Prefix Tree again and again.

## What it can do ?
1. Consist of Graphicla User Interface
2. Support of Choosing File
3. Tree Constructed only one time
4. Very fast
5. Time Complexity reduces to length of the input String
6. Can handle multiple repeated words
7. Can handle vowels
8. Give word suggestion based on the input string
9. Automatically correct spelling
10. Light Weight

