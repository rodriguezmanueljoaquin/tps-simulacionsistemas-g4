import seaborn as sns
import pandas as pd
import collections
import matplotlib.pyplot as plt
import math
from oscillator_helper import get_cuadratic_error,get_analytic

DELTA_T_POSITION_GRAPH = math.pow(10,-4) 

def plot_curves_with_legend(inputs,curves, legends, X_label = "X", Y_label = "Y", log_scale = False):
    # iters = range(1, len(curves[0]) + 1)
    colors = sns.color_palette("hls", len(legends))
    for i in range(len(curves)):
        plt.plot(inputs[i], curves[i], label=legends[i], color=colors[i])

    plt.legend()
    plt.xlabel(X_label)
    plt.ylabel(Y_label)
    if(log_scale):
        plt.yscale("log")
        plt.xscale("log")
    plt.show()

def plot_particle_evolution_by_simulation(simulation_results_dict):
    #Primero, creamos el nuevo diccionario a utilizar ( {deltaT: [[simulation_results],[simulation_results]])
    deltaT_dictionary = {}
    for key, value in simulation_results_dict.items():
        deltaT = key[0]
        simulation_result_list = value
        #Si el deltaT no esta en el diccionario, creemos su lista correspondiente con la lista de simulations_results
        if deltaT not in deltaT_dictionary:
            deltaT_dictionary[deltaT] = [simulation_result_list]
        #Sino, agregamos la lista de simulation_results a la existente
        else:
            deltaT_dictionary[deltaT].append(simulation_result_list)
    inputs = []
    curves = []
    legends = []
    #Luego, utilizamos un solo deltaT para el grafico correspondiente
    for simulation_executions in deltaT_dictionary[DELTA_T_POSITION_GRAPH]:
        # cada simulacion tiene multiples ejecuciones
        simulation_result = simulation_executions[0]
        position_evolution = []
        time_evolution = []
        for frame in simulation_result.particles_by_frame:
            position_evolution.append(frame.particles[0].getPosition()[0])
            time_evolution.append(frame.time)

        inputs.append(time_evolution)
        curves.append(position_evolution)
        legends.append(simulation_result.method_name + " deltaT= " + str(simulation_result.simulation_deltaT) + " error= "+ str(get_cuadratic_error(simulation_result)))

    #Finalmente, agregamos la data de la solucion analitica
    inputs.append(inputs[-1])
    curves.append([get_analytic(time,simulation_result.A,simulation_result.K,simulation_result.gamma,simulation_result.mass) for time in inputs[-1]])
    # print(curves[-1])
    legends.append("Analytic")

    plot_curves_with_legend(inputs,curves, legends, "Time(s)", "Position(m)")

def plot_error_graph(simulation_results_dict):
    #Primero, creamos el nuevo diccionario a utilizar ( {algoritmo: {deltaT : error}})
    error_dictionary = {}
    for key, value in simulation_results_dict.items():
        deltaT = key[0]
        method_name = key[1]
        simulation_result = value[0]
        #Si el algoritmo no esta en el diccionario, creemos su diccionario correspondiente con el deltaT utilizado y su error
        if method_name not in error_dictionary:
            error_dictionary[method_name] = {deltaT : get_cuadratic_error(simulation_result)}
        #Sino, agregamos el algoritmo y error al diccionario ya existente
        else:
            error_dictionary[method_name][deltaT] = get_cuadratic_error(simulation_result)
    #Una vez creado el diccionario, iteramos por el mismo para obtener la data correspondiente para el grafico
    inputs = []
    curves = []
    legends = []
    for method in error_dictionary:
        #Obtenemos el diccionario {deltaT: error} para cada algoritmo, ordenado por los deltaT
        deltaT_error_dictionary = collections.OrderedDict(sorted(error_dictionary[method].items()))
        #Agregamos los deltaT, errores y leyendas correspondientes
        inputs.append(list(deltaT_error_dictionary.keys()))
        curves.append(list(deltaT_error_dictionary.values()))
        legends.append(method)
    #Finalmente, graficamos los errores correspondientes
    # print(inputs)
    # print(curves)
    # print(legends)
    plot_curves_with_legend(inputs,curves, legends, "deltaT (s)", "Cuadratic error (m**2)",log_scale=True)

        


