
from cmath import pi
import random
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.colors import ListedColormap, LinearSegmentedColormap
import os
ovitoFolderName = "ovito_input"

def exportOvito(simulation_result):
    #Si no existe la carpeta de archivos de Ovito, la creamos
    if(not os.path.exists(ovitoFolderName)):
        os.makedirs(ovitoFolderName)
    print('Generating ovito files. . .')
    print('Generating circle ovito file. . .')
    exportCircle(simulation_result.circle_radius)
    print('Generating particles ovito file. . .')
    exportParticles(simulation_result.particles_by_frame, simulation_result.humans_initial_qty, simulation_result.zombie_desired_velocity)
    print('Ovito file successfully generated')

def exportCircle(circle_radius):
    file = open("{}/circle.xyz".format(ovitoFolderName), "w")

    n = 1000
    file.write("{}\ncomment\n".format(n))
    for i in range(0, n):
        angle = 2*pi*i/n
        file.write("{} {} {}\n".format(i,circle_radius*np.cos(angle), circle_radius*np.sin(angle)))
    file.close()

def exportParticles(particles_by_frame, humans_initial_qty, zombie_desired_velocity):
    file = open("{}/particles_{}_{}.xyz".format(ovitoFolderName, humans_initial_qty, zombie_desired_velocity), "w")

    for particle_frame in particles_by_frame:
        n = len(particle_frame.particles)
        file.write("{}\ncomment\n".format(n))
        for particle in particle_frame.particles:
            file.write("{} {} {} {} {} {} {}\n".format(particle.id, particle.x, particle.y, particle.velx, particle.vely, particle.radius, particle.state.value -2))
    
    file.close()

