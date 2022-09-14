
from cmath import pi
import random
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.colors import ListedColormap, LinearSegmentedColormap
import os
import math
ovitoFolderName = "ovito_input"

def exportOvito(simulationResults):
    if(not os.path.exists(ovitoFolderName)):
        os.makedirs(ovitoFolderName)
    print('Generating ovito wall file. . .')
    exportWalls(simulationResults.width,simulationResults.height, simulationResults.gap)
    print('Generating ovito particles file. . .')
    exportParticles(simulationResults.N,simulationResults.gap, simulationResults.particlesDict)
    print('Ovito files successfully generated')


def exportWalls(width, height, gap):
    file = open("{}/walls_{}.xyz".format(ovitoFolderName, gap), "w")
    output = "\n\n"
    qty = 0
    output += f"{0} {0}\n"
    output += f"{0} {height}\n"
    output += f"{width} {0}\n"
    output += f"{width} {height}\n"

    qty += 4

    amplification = 10000
    height_amplificated = round(height *amplification)
    for i in range(height_amplificated):
        true_height = i / amplification
        if(abs(true_height-height/2) >= gap/2):
            qty += 1
            output += f"{width/2} {true_height}\n"

    file.write(f"{qty}{output}")
    file.close()
    

def exportParticles(N,gap,particlesDict):
    file = open("{}/particles_{}_{}.xyz".format(ovitoFolderName, N, gap), "w")
    for time in particlesDict:
        file.write("{}\ncomment\n".format(N))
        for idP in particlesDict[time]:
            particle = particlesDict[time][idP] 
            colorMap=cm.get_cmap("gist_rainbow")
            c = colorMap((particle.id % N)/N)
             
            file.write("{} {} {} {} {} {} {}\n".format(particle.x, particle.y, particle.velx, particle.vely, c[0],c[1],c[2]))
    file.close()

