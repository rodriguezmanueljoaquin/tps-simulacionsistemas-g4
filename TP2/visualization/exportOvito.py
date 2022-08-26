
from cmath import pi
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.colors import ListedColormap, LinearSegmentedColormap
import math

def exportOvito(simulationResults,name):
    exportParticles(simulationResults.N,simulationResults.particlesDict,name)


def exportParticles(n,particlesDict,name):
    file = open(name, "w")
    for time in particlesDict:
        file.write("{}\ncomment\n".format(n))
        for idP in particlesDict[time]:
            particle = particlesDict[time][idP] 
            colorMap=cm.get_cmap('Pastel1', n)
            c = colorMap(math.atan2(particle.vely,particle.velx))
             
            file.write("{} {} {} {} {} {} {} {}\n".format(particle.x, particle.y, particle.velx, particle.vely, c[0],c[1],c[2],(math.atan2(particle.vely,particle.velx))/pi))
    file.close()

