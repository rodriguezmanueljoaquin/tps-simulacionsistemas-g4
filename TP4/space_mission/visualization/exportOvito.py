
from cmath import pi
import random
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.colors import ListedColormap, LinearSegmentedColormap
import os
import math
ovitoFolderName = "ovito_input"

def exportOvito(simulation_result):
    #Si no existe la carpeta de archivos de Ovito, la creamos
    if(not os.path.exists(ovitoFolderName)):
        os.makedirs(ovitoFolderName)
    print('Generating ovito file. . .')
    exportParticles(simulation_result.particles_by_frame,simulation_result.simulation_deltaT,simulation_result.seconds_to_departure, simulation_result.sun_id, simulation_result.sun_position[0],simulation_result.sun_position[1], simulation_result.sun_radius)
    print('Ovito file successfully generated')


def exportParticles(particles_by_frame,simulation_deltaT,seconds_to_departure, sun_id, sun_x, sun_y, sun_radius):
    file = open("{}/particles_{}_{}.xyz".format(ovitoFolderName, simulation_deltaT, seconds_to_departure), "w")
    for particle_frame in particles_by_frame:
        n = len(particle_frame.particles)
        file.write("{}\ncomment\n".format(n + 1))
        file.write("{} {} {} {} {} {}\n".format(sun_id,sun_x, sun_y, 0, 0, sun_radius/30))
        for particle in particle_frame.particles:
            file.write("{} {} {} {} {} {}\n".format(particle.id, particle.x, particle.y, particle.velx, particle.vely, particle.radius))
    file.close()

