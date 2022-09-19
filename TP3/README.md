## Authors
- [Igal Leonel Revich](https://github.com/irevich)
- [Manuel Joaquín Rodríguez](https://github.com/rodriguezmanueljoaquin)
- [Natali Lilienthal](https://github.com/Natu17)

## How to use the program
### Simulation
To generate the simulation output files, you should run the ´Manager.java´. The general parameters, such as the seed, particle velocity, width and height of the box, etc., can be modify on the file ´Constants.java´. Those output files are generated in the ´results´ folder, where a new directory is generated per simulation, with the name <i>out_N_gap_v</i>. 

Then, to change the parameters of each simulation (N, noise, etc), you should modify the ´simulationParameters´ structure inside the ´Manager.java´.

### Visualization (observables)
Once the simulation is made, to make the temporal observable of the output files which are in the ´results´ folder, you should run from the visualization directory the next instruction:

```python visualization.py```

Also, there are the following optional parameters:
- <i>-d</i>: Specifies the path of the input files (this one must have multiple directories, where every one must have a 'static.txt' file and a 'dynamics' folder with different txt for dynamic files). By default uses the ´results´ folder.
- <i>-a</i>: Specifies the action to be done. The possible values are 'graph' (to make the observables) and 'animate' to generate the files to make the animations in Ovito. By default uses 'graph'
- <i>-v</i>: Specifies the variable used for the x axis in the scalar observable and for the different curves in the observable temporal. The possible values are 'particles_number','gap_size' and 'pressure'. In case 'pressure' is the input, it will generate any of the pressure's observables (temperature or cuadratic error, depending on the -o parameter). By default uses 'particles_number'. This parameter cannot be passed at the same time with -a parameter when it has the value 'animate'.
- <i>-o</i>: Specifies the observable to graph. The possible values are 'scalar', 'temporal', 'temperature' and 'cuadratic_error'. By default uses 'temporal'. This parameter cannot be passed at the same time with -a parameter when it has the value 'animate', cannot pass the values 'scalar' or 'temporal' when -v parameter has the value 'pressure', cannot pass the values 'temperature' or 'cuadratic_error' when the -v parameter has the values 'particles_number' or 'gap_size', and in the default case, if the -v parameter has been passed with the value 'pressure', an error message will be shown.

Some examples of executions are:

#### Scalar observable of gap_size

```python visualization.py -v gap_size -o scalar```

#### Generate pressure temperature observable from files in a specific path

```python visualization.py -v pressure -o temperature -d pathToSimulationOutputFiles```


