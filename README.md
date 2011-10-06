Map That Set
============

Hans, Riddhi, Akiva, Najaf
--------------------------

10/4/11
-------
mapper: [1, 3, 3, 4, 1]
[1, 3, 4] -> [1,  3,4]

1 -> [1, 3, 4]
3 -> [1, 3, 4]
4 -> [1, 3, 4]

[2, 3] -> [3]
1 -> [1, 3 ,4]
2 -> [3]
3 -> [3]
4 -> [1, 3, 4]

Update rules
[1, 4] -> [1, 4]
1 -> [1, 4]
2 -> [3]
3 -> [3]
4 -> [1, 4]


Maintain a set of rules (Map<Set<Integer>,Set<Integer>>)
Maintain a set of possibilities (Map<Integer, Set<Integer>>)

Variables:
Rules = Map<Set<Integer>, Set<Integer>>
Possibilities = Map<Integer, Set<Integer>>

Methods:
Create guess (making a random guess, with some random # of possibilities from those keys where we do not yet know the value)
Update possibilities (removing elements from possibilities as seen by new guess)
Update rules <- (going through rules, removing element from sets if key->value found and equal # in key->value)
Check if solution reached <- trivial (going through the map, checking that each element maps to 1 thing)

10/6/11
-------

Things to improve with guesser:
* Craft better guesses
  - Work with disjoint sets
  - Then resolve
  - Create a random index
  - Overhaul of createGuess()
  - (Akiva)

* For the updateRules
  - Najaf's updateRules; using growth of knowledge base
  - Update rules history
  - (Riddhi)

* "I know, I infer" -- try to get this kind of performance
  - Implement this code
  - (Najaf)


Things to work on with the mapper:
* How many "many-to-one" sets
  - [1,2,3,4] -> [2]

* Strategies to try (Hans):
  - Pick random numbers
  - Maintain some history
  - Make sure not to pick too of the same random number
  
  List<Integer> history
  for(Integer i : possibilities.keySet())
    history.put(random) <-- random didn't appear too many times