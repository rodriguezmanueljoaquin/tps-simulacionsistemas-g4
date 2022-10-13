import seaborn as sns
import pandas as pd
import collections
import matplotlib.pyplot as plt
import matplotlib.dates as md
import math
import datetime

from Constants import PlanetIndexInDynamic,planet_index_dict

def plot_curves_with_legend(inputs,curves, legends = None, X_label = "X", Y_label = "Y", log_scale = False):
    # iters = range(1, len(curves[0]) + 1)
    colors = sns.color_palette("hls", len(curves)) 
    for i in range(len(curves)):
        if legends is not None:
            plt.plot(inputs[i], curves[i], label=legends[i], color=colors[i])
        else:
            plt.plot(inputs[i], curves[i], color=colors[i])

    if legends is not None:
        plt.legend()
    plt.xlabel(X_label)
    plt.ylabel(Y_label)
    if(log_scale):
        plt.yscale("log")
        plt.xscale("log")
    ax=plt.gca()
    # xfmt = md.DateFormatter('%H:%M:%S')
    xfmt = md.DateFormatter('%d/%m/%Y')
    ax.xaxis.set_major_formatter(xfmt)
    plt.show()

def get_min_distance_between_two_planets(simulation_result, planet1:PlanetIndexInDynamic, planet2:PlanetIndexInDynamic):
    distances = []

    index_of_departure = int(simulation_result.seconds_to_departure / \
        (simulation_result.particles_by_frame[1].time - simulation_result.particles_by_frame[0].time))

        # por si todavia no habia salido
    while(len(simulation_result.particles_by_frame[index_of_departure].particles) < 3):
        index_of_departure += 1

    for frame in simulation_result.particles_by_frame[index_of_departure: ]:
        planet1_data = frame.particles[planet1.value]
        planet2_data = frame.particles[planet2.value]
        distances.append((math.sqrt(
            (planet1_data.x  - planet2_data.x)**2 + (planet1_data.y - planet2_data.y)**2))-planet2_data.radius - planet1_data.radius)

    return min(distances)


def __get_departure_date(start_simulation_date,days_from_simulation_start_date):
    simulation_start_date = datetime.datetime.strptime(start_simulation_date,"%Y-%m-%d")
    # print(simulation_start_date)
    # return (simulation_start_date + datetime.timedelta(days=days_from_simulation_start_date)).strftime("%d/%m/%Y, %H:%M")
    return (simulation_start_date + datetime.timedelta(days=days_from_simulation_start_date))



def plot_minimum_distance_by_start_simulation_date(simulation_results):
    values = []

    start_simulation_date = simulation_results[0].start_simulation_date
    
    for simulation_result in simulation_results:
        values.append([
            simulation_result.seconds_to_departure,
            get_min_distance_between_two_planets(simulation_result, planet_index_dict[simulation_result.destiny_planet], PlanetIndexInDynamic.SPACESHIP)
        ])

    values.sort(key=lambda x: x[0])
    inputs = [__get_departure_date(start_simulation_date,x[0]/(60*60*24)) for x in values]
    curves = [x[1] for x in values]
    # TODO: DEBERIA DECIR LAS FECHAS EXACTAS Y EN EL LABEL ACORDARSE DE LAS UNIDADES
    plot_curves_with_legend([inputs], [curves], None, "Dias hasta despegue (días)", f"Distancia mínima entre la nave y {simulation_result.destiny_planet} (km)")

def plot_minimum_time_by_initial_velocity_module(simulation_results):
    values = []

    for simulation_result in simulation_results:
        values.append([
            simulation_result.initial_velocity_module,
            simulation_result.particles_by_frame[-1].time - simulation_result.seconds_to_departure
        ])

    values.sort(key=lambda x: x[0])
    inputs = [x[0] for x in values]
    curves = [x[1] for x in values]
    plot_curves_with_legend([inputs], [curves], None, "Modulo de la velocidad inicial (km/s)", f"Tiempo de viaje (s)")

def plot_velocity_evolution(simulation_result):
    velocities = []
    times = []
    index_of_departure = int(simulation_result.seconds_to_departure / \
        (simulation_result.particles_by_frame[1].time - simulation_result.particles_by_frame[0].time))

    # por si todavia no habia salido
    while(len(simulation_result.particles_by_frame[index_of_departure].particles) < 3):
        index_of_departure += 1

    spaceship_data = None
    for frame in simulation_result.particles_by_frame[index_of_departure:]:
        spaceship_data = frame.particles[PlanetIndexInDynamic.SPACESHIP.value]
        velocities.append(math.sqrt(spaceship_data.velx**2 + spaceship_data.vely**2))
        times.append(frame.time - simulation_result.seconds_to_departure)

    plot_curves_with_legend([times], [velocities], None, "Tiempo (s)", "Velocidad (km/s)")

    print("Velocidad cuando llega a un radio determinado de la superficie del planeta: ", velocities[-1], " km/s")
    print("Tiempo total de viaje: ", times[-1], " s")

    destiny_planet_on_last_frame = simulation_result.particles_by_frame[-1]\
        .particles[planet_index_dict[simulation_result.destiny_planet].value]
    velocity_relative_x = spaceship_data.velx - destiny_planet_on_last_frame.velx
    velocity_relative_y = spaceship_data.vely - destiny_planet_on_last_frame.vely
    print("Velocidad relativa de la nave al planeta destino cuando llega a un radio determinado de la superficie del destino",
             simulation_result.destiny_planet, ": vx=", velocity_relative_x, ": vy=", velocity_relative_y ," km/s")

def plot_trip_distance_evolution(simulation_result):
    distances = []
    times = []
    index_of_departure = int(simulation_result.seconds_to_departure / \
        (simulation_result.particles_by_frame[1].time - simulation_result.particles_by_frame[0].time))

    # por si todavia no habia salido
    while(len(simulation_result.particles_by_frame[index_of_departure].particles) < 3):
        index_of_departure += 1

    spaceship_data = None
    for frame in simulation_result.particles_by_frame[index_of_departure:]:
        spaceship_data = frame.particles[PlanetIndexInDynamic.SPACESHIP.value]
        destiny_data = frame.particles[planet_index_dict[simulation_result.destiny_planet].value]
        distances.append((math.sqrt(
            (spaceship_data.x  - destiny_data.x)**2 + (spaceship_data.y - destiny_data.y)**2)) - spaceship_data.radius - destiny_data.radius)
        times.append(frame.time - simulation_result.seconds_to_departure)

    plot_curves_with_legend([times], [distances], None, "Tiempo (s)", "Distancia (km)")
