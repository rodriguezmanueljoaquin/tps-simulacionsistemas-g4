import seaborn as sns
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import math
from enum import Enum

def plot_curves_with_legend(inputs,curves, legends = None, X_label = "X", Y_label = "Y", errors = None, log_scale = False):
    colors = sns.color_palette("hls", len(curves))
    for i in range(len(curves)):
        if(errors is None):
            if(legends is not None):
                plt.plot(inputs[i], curves[i], label=legends[i], color=colors[i])
            else:
                plt.plot(inputs[i], curves[i], color=colors[i])
        else:
            if(legends is not None):
                plt.errorbar(inputs[i], curves[i],yerr=errors[i],label=legends[i],color=colors[i])
            else:
                plt.errorbar(inputs[i], curves[i],yerr=errors[i],color=colors[i])

    if legends is not None:
        plt.legend()
    plt.xlabel(X_label)
    plt.ylabel(Y_label)
    if(log_scale):
        plt.yscale("log")
        plt.xscale("log")
    plt.show()


##Funciones para observables escalares
class ScalarObservableTypeData(Enum):
    ZOMBIE_FRACTION = {
        "set_execution_observable_value": lambda execution: execution.set_total_contagion_time(),
        "get_execution_observable_value": lambda execution: execution.total_contagion_time,
        "y_label": "Tiempo de contagio total [s]",
    }
    CONTAGION_SPEED = {
        "set_execution_observable_value": lambda execution: execution.set_mean_contagion_speed(),
        "get_execution_observable_value": lambda execution: execution.mean_contagion_speed,
        "y_label": "Velocidad de contagio media [z/s]",
    }

def plot_scalar_observable(simulation_results, variable, observable):
    if(observable == "zombie_fraction"):
        scalar_data = ScalarObservableTypeData.ZOMBIE_FRACTION
    else:
        scalar_data = ScalarObservableTypeData.CONTAGION_SPEED

    ##Primero, creamos un diccionario de la forma {variable a analizar;[observable]}
    scalar_observable_dict = dict()
    ##Recorremos cada experimento, y por cada uno de ellos las distintas ejecuciones
    for experiment in simulation_results:
        for execution in experiment:
            scalar_data.value["set_execution_observable_value"](execution)
            ##Tomamos la variable a analizar como clave del diccionario
            dict_key = execution.humans_initial_qty if variable=='humans_initial_qty' else execution.zombie_desired_velocity
            
            ##Si no existe esa clave en el diccionario, la insertamos con una lista que contenga la variable observada
            ##Sino, lo agregamos a la lista ya existente
            if(dict_key not in scalar_observable_dict):
                scalar_observable_dict[dict_key] = \
                    [scalar_data.value["get_execution_observable_value"](execution)]
            else:
                scalar_observable_dict[dict_key]\
                    .append(scalar_data.value["get_execution_observable_value"](execution))

    ##Luego, ordenamos el diccionario por clave
    scalar_observable_dict = dict(sorted(scalar_observable_dict.items()))
    ##Luego, generamos la data para el observable escalar
    inputs = [list(scalar_observable_dict.keys())]
    curves = [[np.mean(observable_list) for observable_list in list(scalar_observable_dict.values())]]
    errors = [[np.std(observable_list) for observable_list in list(scalar_observable_dict.values())]]

    y_label = scalar_data.value["y_label"]
    x_label = "Cantidad de humanos" if variable=='humans_initial_qty' else "Velocidad deseada del zombie [m/s]"
    plot_curves_with_legend(inputs,curves,None,x_label,y_label,errors)


##Funciones para observables temporales
class TemporalObservableTypeData(Enum):
    ZOMBIE_FRACTION = {
        "get_particles_frame_observable_value": lambda particles_frame: particles_frame.get_zombie_fraction(),
        "y_label": "Fracción de zombies",
    }
    CONTAGION_SPEED = {
        "get_particles_frame_observable_value": lambda particles_frame: particles_frame.contagion_speed,
        "y_label": "Velocidad de contagio [z/s]",
    }

def plot_temporal_observable(simulation_results, variable, observable):
    if(observable == "zombie_fraction"):
        temporal_data = TemporalObservableTypeData.ZOMBIE_FRACTION
    else:
        temporal_data = TemporalObservableTypeData.CONTAGION_SPEED
    
    ##Primero, creamos un diccionario de la forma {variable a analizar;[(tiempo;[observable])]}
    temporal_observable_dict = dict()
    ##Recorremos cada experimento, y por cada uno de ellos las distintas ejecuciones
    for experiment in simulation_results:
        for execution in experiment:
            ##Tomamos la variable a analizar como clave del diccionario principal
            dict_key = execution.humans_initial_qty if variable=='humans_initial_qty' else execution.zombie_desired_velocity

            ##Si no existe esa clave en el diccionario, la insertamos con su lista correspondiente
            if(dict_key not in temporal_observable_dict):
                temporal_observable_dict[dict_key] = list()
            
            ##Luego, recorremos los distintos frames de la ejecucion correspondiente
            for particle_frame in execution.particles_by_frame:
                ##Si no existe el tiempo del frame en la lista de tuplas correspondiente a la variable analizada, insertamos en la lista correspondiente una tupla con dicho tiempo
                ##y una lista que contenga la variable observada
                ##Sino, agregamos la variable observada a la lista existente de la tupla de dicho tiempo
                if(particle_frame.time not in list(map(lambda tuple: tuple[0],temporal_observable_dict[dict_key]))):
                    temporal_observable_dict[dict_key].append((particle_frame.time,[temporal_data.value["get_particles_frame_observable_value"](particle_frame)]))
                else:
                    current_time_tuple = list(filter(lambda tup: tup[0] == particle_frame.time,temporal_observable_dict[dict_key]))[0]
                    current_time_tuple[1].append(temporal_data.value["get_particles_frame_observable_value"](particle_frame))

            ##Luego, ordenamos la lista de tuplas por tiempo
            temporal_observable_dict[dict_key].sort(key=lambda x: x[0])
    
    ##Luego, ordenamos el diccionario por clave
    temporal_observable_dict = dict(sorted(temporal_observable_dict.items()))

    ##Luego, generamos la data para el observable temporal
    ###inputs = Tiempos medidos en cada ejecucion
    inputs = list(map(lambda time_tuples_list: list(map(lambda tuple: tuple[0],time_tuples_list)),list(temporal_observable_dict.values())))
    ###curves = Los valores medios de la variable observada en cada tiempo, por cada valor de variable analizada
    ####Primero, armamos una lista de la variable observada por tiempo, por cada variable analizada (de la forma [ [ [...], [...], [...] ], [ [...], [...], [...] ], ... ])
    observed_variable_per_time_per_variable_list = list(map(lambda time_tuples_list: list(map(lambda tuple: tuple[1],time_tuples_list)),list(temporal_observable_dict.values())))
    ####Luego, a partir de ella creamos una lista, que por cada variable analizada, tenga la variable observada promedio de cada tiempo (de la forma [ [ observedVariablePromt0, observedVariablePromt5 , ... ], [ observedVariablePromt0, observedVariablePromt5 , ... ], ... ])
    ####Dicha lista sera "curves"
    curves = list(map(lambda variable_list : list(map(lambda time_list: np.mean(time_list),variable_list)),observed_variable_per_time_per_variable_list))
    ###errors = Desvios estandar de los distintos valores de la variable observada por tiempo por variable analizada (se calcula igual que curves pero usando np.std() en vez de np.mean())
    errors = list(map(lambda variable_list : list(map(lambda time_list: np.std(time_list),variable_list)),observed_variable_per_time_per_variable_list))
    ###legends = Los distintos valores de la variable analizada
    legends = list(map(lambda dict_key: f"Cantidad de humanos = {dict_key}" if variable=='humans_initial_qty' else f"Velocidad deseada del zombie = {dict_key} m/s",temporal_observable_dict.keys()))
    
    y_label = temporal_data.value["y_label"]
    x_label = "Tiempo [s]"

    ##Finalmente, realizamos el observable correspondiente
    plot_curves_with_legend(inputs,curves,legends,x_label,y_label,errors)