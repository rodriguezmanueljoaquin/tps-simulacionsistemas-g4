import argparse
from files import readInputFiles, removeItemsOutOfStepAndMaxStep
from graph import plotTemporalObservable, plotScalarObservable, plotCuadraticErrorvsSlopeGraph, plotPressureVsTemperatureGraph
import exportOvito
import renderOvito
from simulationResult import SimulationResult
from constants import PARAM_GAP_SIZE, PARAM_PARTICLES_QTY, PARAM_PRESSURE

def main():
    simulationResultsDict = dict()
    inputFilesDirectoryPath="../results"
    observableType = 'temporal'
    observableParam = PARAM_PARTICLES_QTY
    visualizationAction = 'graph'
    argsValid = True
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('-d','--InputFilesDirectory',dest='inputFilesDirectory')
        parser.add_argument('-v','--Variable',dest='variable')
        parser.add_argument('-o','--Observable',dest='observable')
        parser.add_argument('-a','--Action',dest='action')
        args = parser.parse_args()
        if(args.inputFilesDirectory is not None):
            inputFilesDirectoryPath = args.inputFilesDirectory
        if(args.action is not None):
            visualizationAction = args.action.lower().strip()
            if(visualizationAction != 'graph' and visualizationAction != 'animate'):
                argsValid = False
        if(args.variable is not None):
            observableParam = args.variable.lower().strip()
            if((observableParam!=PARAM_GAP_SIZE and observableParam!=PARAM_PARTICLES_QTY and observableParam!=PARAM_PRESSURE) or visualizationAction=='animate'):
                argsValid=False
        if(args.observable is not None):
            observableType = args.observable.lower().strip()
            if((observableType != 'scalar' and observableType != 'temporal' and observableType != 'temperature' and observableType != 'cuadratic_error' ) 
            or ((observableType == 'scalar' or observableType == 'temporal') and observableParam == 'pressure') or ((observableType == 'temperature' or observableType == 'cuadratic_error') and 
            (observableParam == PARAM_GAP_SIZE or observableParam == PARAM_PARTICLES_QTY)) or visualizationAction=='animate'):
                argsValid = False
        elif(observableParam=='pressure'):
            argsValid = False
    except Exception as e:
        print("Error in command line arguments")
        print(e)

    if(argsValid):

        ##Leemos los archivos de input
        readInputFiles(inputFilesDirectoryPath,simulationResultsDict)

        if(visualizationAction=='graph'):
            if(observableType == 'temporal'):
                plotTemporalObservable(simulationResultsDict, observableParam)
                return
            elif(observableType == 'scalar'):
                plotScalarObservable(simulationResultsDict, observableParam)
                return
            elif(observableType == 'temperature'):
                plotPressureVsTemperatureGraph(simulationResultsDict)
            else:
                plotCuadraticErrorvsSlopeGraph(simulationResultsDict)

        else:
        # ANIMACION:
            simulationResult = simulationResultsDict[(150, 0.01)][0]
            removeItemsOutOfStepAndMaxStep(simulationResult)
            exportOvito.exportOvito(simulationResult)
    
    else:
        print("Invalid command line arguments")

def animate(result):
    exportOvito.exportOvito(result)
    # renderOvito.animation()

if __name__ == "__main__":
    main()
