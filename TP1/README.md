## Authors
- [Igal Leonel Revich](https://github.com/irevich)
- [Manuel Joaquín Rodríguez](https://github.com/rodriguezmanueljoaquin)
- [Natali Lilienthal](https://github.com/Natu17)

## How to use the program
### Creation of the output file with the neighbours of each particle
On the same directory, you can either use the program with random input or take your simulation results for the analysis.

For the first case, you can modify the random generation constants, such as the seed, number of particles, dimension of the box, etc., on the file ´Constants.java´. Then to run both the generation of the random input and the program that analyzes those files execution of ´SimulationExample.java´ is needed.

For the second case, you have to enter the filepaths as command line parameters in the following order:
- Static filepath
- Dynamic filepath

If only one is passed, it will be interpreted as the static file and the dynamic will be randomly generated. Otherwise, if no arguments are passed, both files will be randomly generated, and if more than 2 arguments are passed, the program returns an error.

### Creation of the visualization graph for the neighbours of one particle
With the neighbours file created, which are named ´bruteForce_neighbours.txt´ and ´cellIndex_neighbours.txt´, denotating the method they used for the creation (under correct variables they should be the same), to create the visualization you should run, from the visualization directory, the next instruction:

```python3 visualization.py -s ./../Static.txt -d ./../Dynamic.txt -n ./../cellIndex_neighbours.txt -p 7```

Generalyzing it would be:

```python visualization.py -s pathToStaticFile -d pathToDynamicFile -n pathToNeighboursFile -p particleId```

An optional parameter can be passed to specify the neighbourhood radius (r<sub>c</sub>). If this one is passed, in the particles graph the neighbourhood perimeter with the current radius will be shown. To execute the program visualization with this parameter, you should run the next instruction:

```python3 visualization.py -s ./../Static.txt -d ./../Dynamic.txt -n ./../cellIndex_neighbours.txt -p 7 -r 0.5```

Generalyzing it would be:

```python visualization.py -s pathToStaticFile -d pathToDynamicFile -n pathToNeighboursFile -p particleId -r neighbourhoodRadius```
