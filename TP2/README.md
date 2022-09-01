## Authors
- [Igal Leonel Revich](https://github.com/irevich)
- [Manuel Joaquín Rodríguez](https://github.com/rodriguezmanueljoaquin)
- [Natali Lilienthal](https://github.com/Natu17)

## How to use the program
### Simulation
To generate the simulation output files, you should run the ´Manager.java´. The general parameters, such as the seed, particle velocity, neighbourhood radius, etc., can be modify on the file ´Constants.java´. Those output files are generated in the ´results´ folder, where a new directory is generated per simulation, with the name <i>out_N_noise_L</i>. 

Then, to change the parameters of each simulation (N, noise, etc), you should modify the ´simulationParameters´ structure inside the ´Manager.java´.

### Visualization (observables)
Once the simulation is made, to make the temporal observable of the output files which are in the ´results´ folder, you should run from the visualization directory the next instruction:

```python visualization.py```

Also, there are the following optional parameters:
- <i>-d</i>: Specifies the path of the input files (this one must have multiple directories, where every one must have a 'dynamic.txt' and a 'static.txt' file). By default uses the ´results´ folder.
- <i>-v</i>: Specifies the variable used for the x axis in the scalar observable and for the different curves in the observable temporal. The possible values are 'density' or 'noise'. By default uses 'noise'.
- <i>-o</i>: Specifies the observable to graph. The possible values are 'scalar' or 'temporal'. By default uses 'temporal'.

Some examples of executions are:

#### Scalar observable of density

```python visualization.py -v density -o scalar```

#### Temporal observable of noise from files in a specific path

```python visualization.py -d pathToSimulationOutputFiles```


