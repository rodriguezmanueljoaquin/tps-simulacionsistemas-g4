import argparse
import os
from files import readInputFiles
from graph import plotObservables
import exportOvito
import renderOvito

def main():
    simulationResults = list()
    inputFilesDirectoryPath="../results"
    noiseObservableParameter = True
    argsValid = True
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('-d','--InputFilesDirectory',dest='inputFilesDirectory')
        parser.add_argument('-v','--Variable',dest='variable')
        args = parser.parse_args()
        if(args.inputFilesDirectory is not None):
            inputFilesDirectoryPath = args.inputFilesDirectory
        if(args.variable is not None):
            observableParam = args.variable.lower().strip()
            if(observableParam!="noise" and observableParam!="density"):
                argsValid=False
            elif(observableParam=="density"):
                noiseObservableParameter = False
    except Exception as e:
        print("Error in command line arguments")
        print(e)

    if(argsValid):

        ##Leemos los archivos de input
        readInputFiles(inputFilesDirectoryPath,simulationResults)

        ovitoFolderName = "ovito_input"

        ##Si no existe la carpeta para almacenar los archivos de ovito, la crea
        if(not os.path.exists(ovitoFolderName)):
            os.makedirs(ovitoFolderName)

        for result in simulationResults:
            animate(result, "{}/particles_{}_{}_{}.xyz".format(ovitoFolderName,result.N,result.eta,result.L))

        ##Graficamos los observables
        plotObservables(simulationResults,noiseObservableParameter)
    
    else:
        print("Invalid command line arguments")



def animate(result,name):
    exportOvito.exportOvito(result,name)
    # renderOvito.animation()
    


if __name__ == "__main__":
    main()

