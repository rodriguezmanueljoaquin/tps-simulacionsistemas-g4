from math import ceil, floor
import seaborn as sns
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from constants import MAX_STEP, STEP, PARAM_GAP_SIZE, PARAM_PARTICLES_QTY, UMBRAL, SLOPE_LIMIT, SLOPE_STEP
from files import removeItemsOutOfStepAndMaxStep
from scalarObservableObject import ScalarObservableObject


def plotTemporalObservable(simulationResultsDict, observableParameter):
    # Las key del diccionario son de la forma (N, gap, v)
    if observableParameter == PARAM_PARTICLES_QTY:
        param_key =0
    elif observableParameter == PARAM_GAP_SIZE:
        param_key =1
    else: raise Exception("Invalid observable parameter")

    plt.rcParams.update({'font.size': 22})
    parameter = "Cantidad de partículas" if observableParameter == 'particles_number' else "Tamaño de abertura"

    data = {
        parameter:[],
        'Time':[],
        'Fraction':[]
    }

    simulationsResultsList = list(simulationResultsDict.items())
    simulationsResultsList.sort(key=lambda x: x[0][param_key])
    for simParams, simulationsResults in simulationsResultsList:
        # simParams es de la forma (N, gap, v)
        simulationResult = simulationsResults[0] 
        # solo nos interesa el primero, se podria tomar el promedio de todas las ejecuciones pero es innecesario y posiblemente no se visualize bien
        removeItemsOutOfStepAndMaxStep(simulationResult)
        for time, Fp in simulationResult.fpDict.items():
            data['Time'].append(ceil(time/STEP)*STEP)
            data['Fraction'].append(Fp)
            data[parameter].append(str(simParams[param_key]) + (" m" if observableParameter == PARAM_GAP_SIZE else ""))

    temporalObservableDataDF = pd.DataFrame(data)

    plt.figure(num="Temporal observable",figsize = (15,9))
    #plt.title(f"Temporal observable: Fracción de partículas vs {parameter}")
    plt.ylabel("Fracción de partículas")
    plt.xlabel("Tiempo (Pasos)")
    ax = sns.lineplot(data=temporalObservableDataDF, x="Time", y="Fraction",
                hue=parameter, legend="full",palette="pastel", ci=None)
    ax.axhline(y=0.5+UMBRAL, color='r', linestyle='--', label="Umbral ("+str(UMBRAL)+")")
    ax.axhline(y=0.5-UMBRAL, color='r', linestyle='--')
    ax.legend(title=parameter)
    sns.move_legend(ax,"upper right")
    plt.show()


def plotScalarObservable(simulationResultsDict, observableParameter):
    # Las key del diccionario son de la forma (N, gap, v)
    if observableParameter == PARAM_PARTICLES_QTY:
        param_key =0
    elif observableParameter == PARAM_GAP_SIZE:
        param_key =1
    else: raise Exception("Invalid observable parameter")

    plt.style.use('seaborn-pastel')
    plt.rcParams.update({'font.size': 22})

    simulationsByParameter = {}
    for simParams, simulationsResults in simulationResultsDict.items():
        dictionaryParameter = simParams[1-param_key]
        if dictionaryParameter not in simulationsByParameter:
            simulationsByParameter[dictionaryParameter] = [ScalarObservableObject(simParams[param_key],simulationsResults)]
        else: simulationsByParameter[dictionaryParameter].append(ScalarObservableObject(simParams[param_key],simulationsResults))

    parameter = "Cantidad de partículas" if observableParameter == PARAM_PARTICLES_QTY else "Tamaño de abertura"

    plt.figure(num="Scalar observable",figsize = (15,9))
    plt.ylabel("Tiempo de equilibrio (Pasos)")
    plt.xlabel(parameter)

    for dictParam, scalarObservableObjectList in simulationsByParameter.items():
        balanceTimeAverage = []
        parameterValues = []
        errors = []
        scalarObservableObjectList .sort(key=lambda x: x.param)
        

        for scalarObservableObject in scalarObservableObjectList:
            parameterValues.append(scalarObservableObject.param)
            balanceTimeList = [simulationResult.balanceTime for simulationResult in scalarObservableObject.simulationResults]
            balanceTimeAverage.append(np.mean(balanceTimeList))
            errors.append(np.std(balanceTimeList))

        plt.errorbar(parameterValues,balanceTimeAverage,yerr=errors,label=str(round(dictParam,2))+(" m" if observableParameter == PARAM_GAP_SIZE else ""))

    legendTitle = "Cantidad de partículas" if observableParameter == PARAM_GAP_SIZE else "Tamaño de abertura"
    plt.legend(title=legendTitle)
    plt.show()


def plotPressureVsTemperatureGraph(simulationResultsDict):
    plt.rcParams.update({'font.size': 22})
    
    data = {
        'pressure_average':[],
        'temperature':[]
    }

    plt.figure(num="Pressure vs Temperatur graph",figsize = (15,9))

    simulationsResultsList = list(simulationResultsDict.items())
    simulationsResultsList.sort(key=lambda x: x[0][2])
    for simParams, simulationsResults in simulationsResultsList:
        # simParams es de la forma (N, gap, v)
        pressureList = [simulationResult.pressure for simulationResult in simulationsResults]
        pressureAverage = np.mean(pressureList)
        data['pressure_average'].append(pressureAverage)
        data['temperature'].append(simulationsResults[0].getTemperature())
        plt.text(simParams[2], pressureAverage , '(v = {})'.format(simParams[2]))

    pressureGraphDataDF = pd.DataFrame(data)

    print(pressureGraphDataDF.head())

    plt.ylabel("Presion (N/m)")
    plt.xlabel("Temperatura (J)")
    ax = sns.lineplot(data=pressureGraphDataDF, x="temperature", y="pressure_average",
                legend="full",palette="pastel")

    plt.show()


def plotCuadraticErrorvsSlopeGraph(simulationResultsDict):
    
    plt.rcParams.update({'font.size': 22})
    
    data = {
        'error':[],
        'slope':[]
    }

    simulationsResultsList = list(simulationResultsDict.items())
    # print(simulationsResultsList)
    simulationsResultsList.sort(key=lambda x: x[0][2])

    print(simulationResultsDict)
    firstPressurePoint = __getPressureLinealAjustmentPoint(simulationsResultsList[0])
    secondPressurePoint = __getPressureLinealAjustmentPoint(simulationsResultsList[1])
    thirdPressurePoint = __getPressureLinealAjustmentPoint(simulationsResultsList[2])

    originalSlope = (secondPressurePoint[1]-firstPressurePoint[1])/(secondPressurePoint[0]-firstPressurePoint[0])

    minErrorPoint = (0,0)
    for currentSlope in range(originalSlope-SLOPE_LIMIT,originalSlope+SLOPE_LIMIT,SLOPE_STEP):
        linealPressure = currentSlope * thirdPressurePoint[0]
        error = ((linealPressure-thirdPressurePoint[1])**2)
        data['error'].append(error)
        data['slope'].append(currentSlope)
        if(len(data['error'])==1 or error<minErrorPoint[1]):
            minErrorPoint = (currentSlope,error)

    cuadraticErrorVsSlopeGraphDataDF = pd.DataFrame(data)
    
    plt.figure(num="Cuadratic error vs slope graph",figsize = (15,9))
    plt.ylabel("Error cuadratico")
    plt.xlabel("Pendiente")
    plt.text(minErrorPoint[0], minErrorPoint[1] , '({},{})'.format(minErrorPoint[0], minErrorPoint[1]))
    ax = sns.lineplot(data=cuadraticErrorVsSlopeGraphDataDF, x="slope", y="error",
                legend="full",palette="pastel")

    plt.show()
    


def __getPressureLinealAjustmentPoint(simulationResultListObject):
    # print(simulationResultListObject[1])
    temperature = simulationResultListObject[1][0].getTemperature()
    pressureList = [simulationResult.pressure for simulationResult in simulationResultListObject[1]]
    pressureAverage = np.mean(pressureList)
    return (temperature,pressureAverage)



    






    