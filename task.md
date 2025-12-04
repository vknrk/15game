Programming Task: 15 Puzzle
Your task is to implement single user game 15 Puzzle (https://en.wikipedia.org/wiki/15_puzzle).
The 15-puzzle is a sliding puzzle that consists of a frame of numbered square tiles in random
order with one tile missing. Game is represented by 4x4 tiles board where 15 numbered tiles are
initially placed in random order and where 16th tile is missing. A tile can be moved to a
neighbour empty place. To succeed in the game you need to order tiles from 1 to 15, where tile
number 1 is at the top left corner and empty one is at the bottom right corner.
Implementation requirements:
Make sure to separate handling of presentation, input and game logic. Presentation and input
implemented by simple terminal I/O are good enough. Your solution should be easy to setup up
and run; please use standard build tools from your prefered platform.
Evaluation
You should value simplicity without sacrificing important design decisions that would allow game
to be easily maintained and extended in case you are asked to add additional features. We like
interesting solutions, but avoid overengineering. This task has very few requirements on game
representation and play; we encourage you to fill in with your own decisions where appropriate.
Good solutions should take into account:
● Testable design; unit tests and TDD are big pluses, but are optional
● Clean code
● Expressiveness in the design
● Correctness of random initial board generation algorithm implementation
