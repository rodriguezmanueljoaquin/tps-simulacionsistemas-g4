import seaborn as sns
import pandas as pd
import numpy as np
import collections
import matplotlib.pyplot as plt
import math


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

###Tiempo de contagio total vs variable a analizar (Nh/Vz)
def plot_zombie_fraction_scalar_observable(simulation_results,variable):
    ##Primero, creamos un diccionario de la forma (variable a analizar;[tiempos de contagio total])
    scalar_observable_dict = dict()
    ##Recorremos cada experimento, y por cada uno de ellos las distintas ejecuciones
    for simulation_result_experiment in simulation_results:
        for simulation_result_execution in simulation_result_experiment:
            ##Tomamos la variable a analizar como clave del diccionario
            dict_key = simulation_result_execution.humans_initial_qty if variable=='humans_initial_qty' else simulation_result_execution.zombie_desired_velocity
            ##Si no existe esa clave en el diccionario, la insertamos con una lista que contenga el tiempo de contagio total correspondiente
            ##Sino, lo agregamos a la lista ya existente
            if(dict_key not in scalar_observable_dict):
                scalar_observable_dict[dict_key] = [simulation_result_execution.total_contagion_time]
            else:
                scalar_observable_dict[dict_key].append(simulation_result_execution.total_contagion_time)
    ##Luego, ordenamos el diccionario por clave
    scalar_observable_dict = dict(sorted(scalar_observable_dict.items()))
    ##Luego, generamos la data para el observable
    inputs = [list(scalar_observable_dict.keys())]
    curves = [[np.mean(contagion_time_list) for contagion_time_list in list(scalar_observable_dict.values())]]
    errors = [[np.std(contagion_time_list) for contagion_time_list in list(scalar_observable_dict.values())]]
    y_label = "Tiempo de contagio total (s)"
    x_label = "Cantidad de humanos" if variable=='humans_initial_qty' else "Velocidad deseada del zombie (m/s)"
    ##Finalmente, realizamos el observable correspondiente
    plot_curves_with_legend(inputs,curves,None,x_label,y_label,errors)


###Velocidad de contagio media vs variable a analizar (Nh/Vz)
def plot_contagion_speed_scalar_observable(simulation_results,variable):
    ##Primero, creamos un diccionario de la forma (variable a analizar;[velocidad de contagio media])
    scalar_observable_dict = dict()
    ##Recorremos cada experimento, y por cada uno de ellos las distintas ejecuciones
    for simulation_result_experiment in simulation_results:
        for simulation_result_execution in simulation_result_experiment:
            ##Tomamos la variable a analizar como clave del diccionario
            dict_key = simulation_result_execution.humans_initial_qty if variable=='humans_initial_qty' else simulation_result_execution.zombie_desired_velocity
            ##Si no existe esa clave en el diccionario, la insertamos con una lista que contenga la velocidad de contagio media correspondiente
            ##Sino, lo agregamos a la lista ya existente
            if(dict_key not in scalar_observable_dict):
                scalar_observable_dict[dict_key] = [simulation_result_execution.mean_contagion_speed]
            else:
                scalar_observable_dict[dict_key].append(simulation_result_execution.mean_contagion_speed)
    ##Luego, ordenamos el diccionario por clave
    scalar_observable_dict = dict(sorted(scalar_observable_dict.items()))
    ##Luego, generamos la data para el observable
    inputs = [list(scalar_observable_dict.keys())]
    curves = [[np.mean(mean_contagion_speed_list) for mean_contagion_speed_list in list(scalar_observable_dict.values())]]
    errors = [[np.std(mean_contagion_speed_list) for mean_contagion_speed_list in list(scalar_observable_dict.values())]]
    y_label = "Velocidad de contagio media (z/s)"
    x_label = "Cantidad de humanos" if variable=='humans_initial_qty' else "Velocidad deseada del zombie (m/s)"
    ##Finalmente, realizamos el observable correspondiente
    plot_curves_with_legend(inputs,curves,None,x_label,y_label,errors)