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

    for result in simulationResults:
        animate(result, "particles-{}-{}-{}-{}.xyz\n".format(result.eta,result.N,result.v,result.L))

    ##Graficamos el observable temporal
    plotTemporalObservable(simulationResults)

def animate(result,name):
    exportOvito.exportOvito(result,name)
    #renderOvito.animation()
    


if __name__ == "__main__":
    main()

