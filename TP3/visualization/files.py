import os
import copy
import numpy as np
from constants import STEP, UMBRAL
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
                    simulationResultsDict[(simulationResultBase.N,simulationResultBase.gap)] = simulationResultsList
        print('\tDirectory successfully read. . .')
        dirCount+=1

    print('Input files successfully read')


def __readDynamicInputFile(dynamicInputFilePath,simulationResult):
    lineCount = 0
    read = True
    N = simulationResult.N
    v = simulationResult.v
    width = simulationResult.width
    height = simulationResult.height
    
    file = open(dynamicInputFilePath , 'r')

    line = file.readline()
    lineCount += 1
    currentTime = float(line.strip())
    sectionSum = 0
    simulationResult.particlesDict[currentTime] = dict()
    while read:
        line = file.readline()
        
        if not line:
            read = False
        else:
            ##Si es una linea correspondiente a un tiempo, en caso de leer previamente la data de las particulas calculamos el Va en cuestion
            ##Luego, reinciamos el vector de velocidades y seteamos el tiempo correspondiente
            if(lineCount%(N+1)==0):
                Fp = sectionSum/N
                simulationResult.fpDict[currentTime] = Fp
                ##Si aun no llego al equilibrio, chequeamos que haya llegado a dicha condicion
                if(simulationResult.balanceTime is None):
                    if(Fp-0.5<UMBRAL):
                        simulationResult.setBalanceTime(currentTime)
                currentTime = float(line.strip())
                sectionSum = 0
                simulationResult.particlesDict[currentTime] = dict()

            else:
                ##Si no es una linea correspondiente a un tiempo, leemos y almacenamos la data de cada particula
                particleData = line.split(";")
                particle = Particle(int(particleData[0]),float(particleData[1]),float(particleData[2]),float(particleData[3]),float(particleData[4]))
                simulationResult.particlesDict[currentTime][particle.id] = particle
                sectionSum+=(particle.x<=width/2)

            lineCount += 1

    ##Calculamos el Fp correspondiente al tiempo actual
    Fp = sectionSum/N
    simulationResult.fpDict[currentTime] = Fp

    ## Saco los tiempos que no me interesan que son aquellos anteriores al ultimo cerca del STEP
    currentIterTime = 0
    removeKeys = list()
    for key in sorted(simulationResult.fpDict.keys()):
        if currentIterTime + STEP < key:
            for removeKey in removeKeys: 
                simulationResult.fpDict.pop(removeKey)

            currentIterTime += STEP
            removeKeys.clear()
        else:
            removeKeys.append(key)

    file.close()

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
            else:
                v = float(line.strip())
            lineCount+=1
    file.close()
    return SimulationResult(N,width,height,gap,v)
