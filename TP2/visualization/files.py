import os
import numpy as np
from simulationResult import SimulationResult
from particle import Particle

def readInputFiles(inputFilesDirectoryPath,simulationResults):

    ##Iteramos en el path de los archivos de input
    for dir in os.listdir(inputFilesDirectoryPath):
        etaDirPath = inputFilesDirectoryPath +"/"+dir
        ##Por cada directorio, leemos los archivos estatico y dinamico correspondientes
        if(os.path.isdir(etaDirPath)):
            simulationResult = None
            for inputFile in sorted(os.listdir(etaDirPath),reverse=True):
                if(inputFile.lower().startswith('static')):
                    simulationResult = __readStaticInputFile(etaDirPath+"/"+inputFile)
                else:
                    __readDynamicInputFile(etaDirPath+"/"+inputFile,simulationResult)
            simulationResults.append(simulationResult)


def __readDynamicInputFile(dynamicInputFilePath,simulationResult):
    lineCount = 0
    read = True
    N = simulationResult.N
    v = simulationResult.v
    readingParticles = False
    
    file = open(dynamicInputFilePath , 'r')

    while read:
        line = file.readline()
        
        if not line:
            read = False
        else:
            ##Si es una linea correspondiente a un tiempo, en caso de leer previamente la data de las particulas calculamos el Va en cuestion
            ##Luego, reinciamos el vector de velocidades y seteamos el tiempo correspondiente
            if(lineCount%(N+1)==0):
                if(readingParticles):
                    readingParticles = False
                    Va = np.linalg.norm(velocitySum)/(N*v)
                    simulationResult.vaDict[currentTime] = Va
                currentTime = int(line.strip())
                velocitySum = np.array([0,0])
                simulationResult.particlesDict[currentTime] = dict()

            else:
                ##Si no es una linea correspondiente a un tiempo, leemos y almacenamos la data de cada particula
                if(not readingParticles):
                    readingParticles = True
                particleData = line.split(";")
                particle = Particle(int(particleData[0]),float(particleData[1]),float(particleData[2]),float(particleData[3]),float(particleData[4]))
                simulationResult.particlesDict[currentTime][particle.id] = particle
                velocitySum = np.add(velocitySum,np.array([particle.velx,particle.vely]))

            lineCount += 1

    ##Calculamos el Va correspondiente a la ultima particula
    if(readingParticles):
        readingParticles = False
        Va = np.linalg.norm(velocitySum)/(N*v)
        simulationResult.vaDict[currentTime] = Va


def __readStaticInputFile(staticInputFilePath):
    lineCount = 0
    read = True
    
    file = open(staticInputFilePath , 'r')

    while read:
        line = file.readline()
        
        if not line:
            read = False
        else:
            # print(lineCount)
            if(lineCount==0):
                eta = float(line.strip())
            elif(lineCount==1):
                N = int(line.strip())
            elif(lineCount==2):
                v = float(line.strip())
            else:
                L = int(line.strip())
            lineCount+=1
    
    return SimulationResult(eta,N,v,L)





     