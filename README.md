# NEAT
##### Authors: Chance Simmons and Brandon Townsend

Contained here is an implementation of NEAT in Java. It is a project built for learning more
 about genetic algorithms, specifically NEAT. It is a work-in-progress that contains a few bugs
  that should be looked after.
  
We have been executing the code using IntelliJ, so that is what we recommend for it.

Some Bugs:
- Coloring of the agents should be checked. It just looks funky right now.
- Crossover needs to be fully implemented and it might fix other issues such as:
    - The list of species in Population being reduced to zero.
- It could generally be more efficient in some areas.

Notes:
- Consider making both an overall champion (one that spans generations) and a generational
 champion for both Population and each Species. This could help alleviate some headache in areas.
