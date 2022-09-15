import argparse
from files import readInputFiles
from graph import plotTemporalObservable, plotScalarObservable
import exportOvito
import renderOvito
from simulationResult import SimulationResult
from constants import PARAM_GAP_SIZE, PARAM_PARTICLES_QTY
def etaFunc(e):
  return e.eta

def main():
    simulationResultsDict = dict()
    inputFilesDirectoryPath="../results"
    observableType = 'temporal'
    observableParam = PARAM_PARTICLES_QTY
    argsValid = True
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('-d','--InputFilesDirectory',dest='inputFilesDirectory')
        parser.add_argument('-v','--Variable',dest='variable')
        parser.add_argument('-o','--Observable',dest='observable')
        args = parser.parse_args()
        if(args.inputFilesDirectory is not None):
            inputFilesDirectoryPath = args.inputFilesDirectory
        if(args.observable is not None):
            observableType = args.observable.lower().strip()
            if(observableType != 'scalar' and observableType != 'temporal'):
                argsValid = False

        if(args.variable is not None):
            observableParam = args.variable.lower().strip()
            if(observableParam!=PARAM_GAP_SIZE and observableParam!=PARAM_PARTICLES_QTY):
                argsValid=False
    except Exception as e:
        print("Error in command line arguments")
        print(e)

    if(argsValid):

        ##Leemos los archivos de input
        readInputFiles(inputFilesDirectoryPath,simulationResultsDict)

        # for item in simulationResultsDict.items():
        #     print(f"Gap : {item[0][1]}")
        #     print(f"N : {item[0][0]}")
        #     print(f"SimulationResults : {item[1]}")
        # print(simulationResultsDict[(20,0.01)])

        # simulationResults.sort(key=etaFunc)

        if(observableType == 'temporal'):
            plotTemporalObservable(simulationResultsDict, observableParam)
            return
        else:
            plotScalarObservable(simulationResults,noiseObservableParameter)

        simulationResults = simulationResultsDict[(20,0.01)]
        exportOvito.exportOvito(simulationResults[5])
    
    else:
        print("Invalid command line arguments")

def animate(result,name):
    exportOvito.exportOvito(result)
    # renderOvito.animation()

if __name__ == "__main__":
    main()
