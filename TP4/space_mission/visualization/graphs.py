import seaborn as sns
import pandas as pd
import collections
import matplotlib.pyplot as plt
import math

from Constants import PlanetType

def plot_curves_with_legend(inputs,curves, legends = None, X_label = "X", Y_label = "Y", log_scale = False):
    # iters = range(1, len(curves[0]) + 1)
    colors = sns.color_palette("hls", len(curves)) 
    for i in range(len(curves)):
        label = legends[i] if legends is not None else ''
        plt.plot(inputs[i], curves[i], label=label, color=colors[i])

    plt.legend()
    plt.xlabel(X_label)
    plt.ylabel(Y_label)
    if(log_scale):
        plt.yscale("log")
        plt.xscale("log")
    plt.show()

def get_min_distance_between_two_planets(simulation_result, planet1:PlanetType, planet2:PlanetType):
    distances = []

    index_of_departure = int(simulation_result.seconds_to_departure / \
        (simulation_result.particles_by_frame[1].time - simulation_result.particles_by_frame[0].time))

    for frame in simulation_result.particles_by_frame[index_of_departure+1: ]:
        planet1_data = frame.particles[planet1.value]
        planet2_data = frame.particles[planet2.value]
        distances.append(math.sqrt((planet1_data.x - planet2_data.x)**2 + (planet1_data.y - planet2_data.y)**2))

    return min(distances)

def plot_minimum_distance_by_start_simulation_date(simulation_results):
    values = []

    for simulation_result in simulation_results:
        values.append([
            simulation_result.seconds_to_departure,
            get_min_distance_between_two_planets(simulation_result, PlanetType.VENUS, PlanetType.SPACESHIP)
        ])

    values.sort(key=lambda x: x[0])
    inputs = [x[0]/(60*60*24) for x in values]
    curves = [x[1] for x in values]
    plot_curves_with_legend([inputs], [curves], None, "Days to departure", "Minimum distance between spaceship and venus")
