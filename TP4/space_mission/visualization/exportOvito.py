
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
    exportParticles(simulation_result.particles_by_frame,simulation_result.simulation_deltaT,simulation_result.method_name)
    print('Ovito file successfully generated')


def exportParticles(particles_by_frame,simulation_deltaT,method_name):
    file = open("{}/particles_{}_{}.xyz".format(ovitoFolderName, simulation_deltaT, method_name), "w")
    for particle_frame in particles_by_frame:
        n = len(particle_frame.particles)
        file.write("{}\ncomment\n".format(n))
        for particle in particle_frame.particles:
            colorMap=cm.get_cmap('Pastel1', n)
            c = colorMap(math.atan2(particle.vely,particle.velx))
             
            file.write("{} {} {} {} {} {} {} {}\n".format(particle.x, particle.y, particle.velx, particle.vely, c[0],c[1],c[2],(math.atan2(particle.vely,particle.velx))/pi))
    file.close()

