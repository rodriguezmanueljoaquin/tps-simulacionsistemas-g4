import argparse


from files import readInputFiles
from graph import makeParticlesGraph

def main():
    argsValid = False
    particlesDict = {}
    neighboursDict = {}
    graphPropertiesDict = {}
    neighbourhoodRadius = None
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('-s','--staticInputFilePath',dest='staticInputFilePath')
        parser.add_argument('-d','--dynamicInputFilePath',dest='dynamicInputFilePath')
        parser.add_argument('-n','--neighboursInputFilePath',dest='neighboursInputFilePath')
        parser.add_argument('-p','--particleId',dest='particleId')
        parser.add_argument('-r','--neighbourhoodRadius',dest='neighbourhoodRadius')
        args = parser.parse_args()
        if(args.staticInputFilePath is not None and args.dynamicInputFilePath is not None and args.neighboursInputFilePath and args.particleId is not None):
            staticInputFilePath = args.staticInputFilePath
            dynamicInputFilePath = args.dynamicInputFilePath
            neighboursInputFilePath = args.neighboursInputFilePath
            particleId = int(args.particleId)
            if(args.neighbourhoodRadius is not None):
                neighbourhoodRadius = float(args.neighbourhoodRadius)
            argsValid = True
    except Exception as e:
        print("Error in command line arguments")
        print(e)

    if(argsValid):
        
        ##Leemos los archivos de input
        readInputFiles(dynamicInputFilePath,staticInputFilePath,neighboursInputFilePath,particlesDict,neighboursDict,graphPropertiesDict)

        ##Si se paso el RC, lo seteamos en el graphProperties
        if(neighbourhoodRadius is not None):
            graphPropertiesDict['RC'] = neighbourhoodRadius

        ##Chequeamos que la particula exista
        if(particlesDict.get(particleId)):
            ##Imprimimos el grafico
            makeParticlesGraph(particlesDict,neighboursDict,graphPropertiesDict,particleId)
        else:
            print("Particle "+str(particleId)+" does not exist")


    else:
        print("Invalid command line arguments")

if __name__ == "__main__":
    main()

