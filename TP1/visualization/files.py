from particle import Particle
import re

def readInputFiles(dynamicInputFilePath,staticInputFilePath,neighboursInputFilePath,particlesDict,neighboursDict,graphPropertiesDict):
    __readDynamicInputFile(dynamicInputFilePath,particlesDict)
    __readStaticInputFile(staticInputFilePath,particlesDict,graphPropertiesDict)
    __readNeighboursInputFile(neighboursInputFilePath,neighboursDict)

def readInputFiles(dynamicInputFilePath,staticInputFilePath,neighboursInputFilePath,particlesDict,neighboursDict,graphPropertiesDict):
    __readDynamicInputFile(dynamicInputFilePath,particlesDict)
    __readStaticInputFile(staticInputFilePath,particlesDict,graphPropertiesDict)
    __readNeighboursInputFile(neighboursInputFilePath,neighboursDict)

def __readDynamicInputFile(dynamicInputFilePath,particlesDict):
    lineCount = 0
    read = True
    
    file = open(dynamicInputFilePath , 'r')

    while read:
        line = file.readline()
        
        if not line:
            read = False
        else:
            if(lineCount!=0):
                position = line.split()
                particle = Particle(float(position[0]),float(position[1]))
                particlesDict[particle.id] = particle
            lineCount += 1

def __readNeighboursInputFile(neighboursInputFilePath,neighboursDict):
    read = True
    
    file = open(neighboursInputFilePath , 'r')

    while read:
        line = file.readline()
        line = re.sub(r"[\t\n]+", " ", line)
        line = re.sub(r"[\s]+" , " " , line )
        
        if not line:
            read = False
        else:
            ##Pedimos la particula y sus vecinos correspondientes
            currentParticleId = int(line.split(";")[0])
            neighbours = line.split(";")[1].split(",")
            neighbours[-1] = neighbours[-1].strip()
            if(len(neighbours)==1 and neighbours[0]==''):
                neighbours = list()
            else:
                neighbours = list(map(int,neighbours))

            ##Insertamos en el diccionario
            neighboursDict[currentParticleId] = neighbours


def __readStaticInputFile(staticInputFilePath,particlesDict,graphPropertiesDict):
    lineCount = 0
    read = True
    currentParticleId = 1
    
    file = open(staticInputFilePath , 'r')

    while read:
        line = file.readline()
        
        if not line:
            read = False
        else:
            if(lineCount==0):
                graphPropertiesDict['N'] = int(line)
            elif(lineCount==1):
                graphPropertiesDict['L'] = int(line)
            else:
                properties = line.split()
                particlesDict.get(currentParticleId).setRadius(float(properties[0]))
                particlesDict.get(currentParticleId).setProperty(float(properties[1]))
                currentParticleId += 1
            lineCount+=1





     