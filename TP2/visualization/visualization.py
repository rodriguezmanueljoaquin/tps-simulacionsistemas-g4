import argparse
from files import readInputFiles
from graph import plotTemporalObservable
import exportOvito
import renderOvito

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

    #animate(simulationResults[0])


    ##Graficamos el observable temporal
    plotTemporalObservable(simulationResults)

def animate(result):
    exportOvito.exportOvito(result)
    renderOvito.animation()
    


if __name__ == "__main__":
    main()

