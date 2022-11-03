import seaborn as sns
import pandas as pd
import numpy as np
import collections
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
                plt.errorbar(inputs[i], curves[i],yerr=errors,label=legends[i],color=colors[i])
            else:
                plt.errorbar(inputs[i], curves[i],yerr=errors,color=colors[i])

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
        "y_label": "Tiempo de contagio total (s)",
    }
    CONTAGION_SPEED = {
        "set_execution_observable_value": lambda execution: execution.set_mean_contagion_speed(),
        "get_execution_observable_value": lambda execution: execution.mean_contagion_speed,
        "y_label": "Velocidad de contagio media (z/s)",
    }

def plot_scalar_observable(simulation_results, variable, observable):
    if(observable == "zombie_fraction"):
        scalar_data = ScalarObservableTypeData.ZOMBIE_FRACTION
    else:
        scalar_data = ScalarObservableTypeData.CONTAGION_SPEED

    ##Primero, creamos un diccionario de la forma (variable a analizar;[observable])
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
    x_label = "Cantidad de humanos" if variable=='humans_initial_qty' else "Velocidad deseada del zombie (m/s)"
    plot_curves_with_legend(inputs,curves,None,x_label,y_label,errors)
