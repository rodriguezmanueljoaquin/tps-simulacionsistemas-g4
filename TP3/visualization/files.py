import os
import copy
import numpy as np
from constants import MAX_STEP, STEP, UMBRAL
from simulationResult import SimulationResult
from particle import Particle

def readInputFiles(inputFilesDirectoryPath,simulationResultsDict):

    print('Reading input files. . .')

    ##Iteramos en el path de los archivos de input
    dirCount = 1
    for dir in os.listdir(inputFilesDirectoryPath):
        print('\tReading directory '+str(dirCount)+'. . .')
        simulationDirPath = inputFilesDirectoryPath +"/"+dir
        ##Por cada directorio, leemos los archivos estatico y dinamico correspondientes
        if(os.path.isdir(simulationDirPath)):
            simulationResultsList = list()
            simulationResultBase = None
            for inputFile in sorted(os.listdir(simulationDirPath),reverse=True):
                ##Primero, leemos el archivo estatico
                if(inputFile.lower().startswith('static')):
                    print('\t\tReading static file. . .')
                    simulationResultBase = __readStaticInputFile(simulationDirPath+"/"+inputFile)
                    print('\t\tStatic file successfully read')
                else:
                    ##Luego, leemos el directorio de archivos dinamicos
                    print('\t\tReading dynamic files directory. . .')
                    dynamicFilesDirPath = simulationDirPath+"/"+inputFile
                    if(os.path.isdir(dynamicFilesDirPath)):
                         for dynamicFilePath in os.listdir(dynamicFilesDirPath):
                            simulationResultToAdd = copy.deepcopy(simulationResultBase)
                            print('\t\t\tReading dynamic file. . .')
                            __readDynamicInputFile(dynamicFilesDirPath+"/"+dynamicFilePath,simulationResultToAdd)
                            print('\t\t\tDynamic file successfully read')
                            simulationResultsList.append(simulationResultToAdd)
                    print('\t\tDynamic files directory successfully read. . .')
                    simulationResultsDict[(simulationResultBase.N,simulationResultBase.gap,simulationResultBase.v)] = simulationResultsList
        print('\tDirectory successfully read. . .')
        dirCount+=1

    print('Input files successfully read')


def __readDynamicInputFile(dynamicInputFilePath,simulationResult):
    read = True
    N = simulationResult.N
    v = simulationResult.v
    width = simulationResult.width
    impulseSum = 0
    
    file = open(dynamicInputFilePath , 'r')

    # line = file.readline()
    # currentTime = float(line.strip())
    # sectionSum = 0
    # simulationResult.particlesDict[currentTime] = dict()
    while read:
        line = file.readline()
        
        if not line:
            read = False
            #Registramos el ultimo tiempo de la simulacion
            finalTime = currentTime
        else:
            ##Si aun no llego al equilibrio, chequeamos que haya llegado a dicha condicion
            currentTime = float(line.strip())
            if currentTime >= MAX_STEP * STEP:
                finalTime = currentTime
                read = False
                
            sectionSum = 0
            simulationResult.particlesDict[currentTime] = dict()
            for i in range(N):
                line = file.readline()
                particleData = line.split(";")
                id = float(particleData[0])
                x = float(particleData[1])
                y = float(particleData[2])
                vx = float(particleData[3])
                vy = float(particleData[4])
                lastCollisionType = int(particleData[5])
                particle = Particle(id,x,y,vx,vy,lastCollisionType)
                simulationResult.particlesDict[currentTime][id] = particle
                sectionSum += (x<=width/2)
                #Si se llego al equilibrio, realizamos los calculos correspondientes a la presion
                if(simulationResult.balanceTime is not None):
                    impulseSum += particle.getImpulse(simulationResult.mass)

            Fp = sectionSum/N
            simulationResult.fpDict[currentTime] = Fp
            if(simulationResult.balanceTime is None):
                if(Fp-0.5<UMBRAL):
                    simulationResult.setBalanceTime(currentTime)

    #Finalmente, calculamos la presion a partir de la suma de impulsos y el tiempo final de simulacion

    simulationResult.setPressure(impulseSum,finalTime)

    file.close()

def removeItemsOutOfStepAndMaxStep(simulationResult):
    ## Saco los tiempos que no me interesan que son aquellos anteriores al ultimo cerca del STEP
    currentIterTime = 0
    keysToRemove = list()
    stepKeysToRemove = list()
    for stepTime in sorted(simulationResult.fpDict.keys()):
        if stepTime > MAX_STEP:
            keysToRemove.append(stepTime)
        else:
            if currentIterTime < stepTime:
                keysToRemove += stepKeysToRemove[:-1] # el ultimo es el mas cercano al cambio del step asi que no lo borro
                stepKeysToRemove.clear()
                currentIterTime += STEP

            stepKeysToRemove.append(stepTime)

    keysToRemove += stepKeysToRemove[:-1]
    for removeKey in keysToRemove:
        simulationResult.fpDict.pop(removeKey)
        simulationResult.particlesDict.pop(removeKey)

    print("Unnecesary steps removed")

def __readStaticInputFile(staticInputFilePath):
    lineCount = 0
    read = True
    
    file = open(staticInputFilePath , 'r')

    while read:
        line = file.readline()
        
        if not line:
            read = False
        else:
            if(lineCount==0):
                N = int(line.strip())
            elif(lineCount==1):
                widthAndHeight = line.split()
                width = float(widthAndHeight[0].strip())
                height = float(widthAndHeight[1].strip())
            elif(lineCount==2):
                gap = float(line.strip())
            elif(lineCount==3):
                v = float(line.strip())
            else:
                mass = float(line.strip())
            lineCount+=1
    file.close()
    # return SimulationResult(N,width,height,gap,v,mass)
    return SimulationResult(N,width,height,gap,v,mass)
