import argparse


from files import readInputFiles

def main():
    argsValid = False
    particlesDict = {}
    neighboursDict = {}
    graphPropertiesDict = {}
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('-s','--staticInputFilePath',dest='staticInputFilePath')
        parser.add_argument('-d','--dynamicInputFilePath',dest='dynamicInputFilePath')
        parser.add_argument('-n','--neighboursInputFilePath',dest='neighboursInputFilePath')
        parser.add_argument('-p','--particleId',dest='particleId')
        args = parser.parse_args()
        if(args.staticInputFilePath is not None and args.dynamicInputFilePath is not None and args.neighboursInputFilePath and args.particleId is not None ):
            staticInputFilePath = args.staticInputFilePath
            dynamicInputFilePath = args.dynamicInputFilePath
            neighboursInputFilePath = args.neighboursInputFilePath
            particleId = args.particleId
            argsValid = True
    except Exception as e:
        print("Error in command line arguments")
        print(e)

    if(argsValid):
        readInputFiles(dynamicInputFilePath,staticInputFilePath,neighboursInputFilePath,particlesDict,neighboursDict,graphPropertiesDict)
        ##Imprimimos los dict
        ###Particles
        print(particlesDict)
        ###Neighbours
        print(neighboursDict)
        ###GraphProperties
        print(graphPropertiesDict)

    else:
        print("Invalid command line arguments")

if __name__ == "__main__":
    main()

