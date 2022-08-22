import argparse
from files import readInputFiles

def main():
    simulationResults = list()
    inputFilesDirectoryPath="./input"
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('-d','--InputFilesDirectory',dest='inputFilesDirectory')
        args = parser.parse_args()
        if(args.inputFilesDirectory is not None):
            inputFilesDirectoryPath = args.inputFilesDirectory
    except Exception as e:
        print("Error in command line arguments")
        print(e)
        
    ##Leemos los archivos de input
    readInputFiles(inputFilesDirectoryPath,simulationResults)

    ##Imprimimos los resultados
    for simulationResult in simulationResults:
        print(simulationResult.vaDict)


if __name__ == "__main__":
    main()

